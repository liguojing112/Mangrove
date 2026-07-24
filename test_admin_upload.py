import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Login as regular admin
login_data = json.dumps({"username": "admin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
resp = json.loads(o.read().decode())
if resp.get("code") != 200:
    print("Login failed:", resp)
    c.close()
    exit(1)
token = resp["data"]["token"]
role = resp["data"]["role"]
print(f"Logged in as: {resp['data']['username']} (role: {role})")

# Upload image
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/files/upload -H 'Authorization: Bearer {token}' -F 'file=@/opt/mangrove/uploads/a2042143-0828-4a79-b660-829078325365.jpeg'", timeout=15)
upload_resp = json.loads(o.read().decode())
url = upload_resp.get("data", {}).get("url", "")
filename = url.split("/")[-1] if url else ""
print(f"Uploaded: {filename}")

# Save meta with status based on role (simulating what ResourceManager does)
status = 1 if role == "SUPER_ADMIN" else 0
meta = json.dumps({"filename": filename, "displayName": "普通管理员测试", "seqNo": 0, "category": "", "photoDate": "", "status": status})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/files/meta -H 'Content-Type: application/json' -H 'Authorization: Bearer {token}' -d '{meta}'", timeout=15)
print(f"Meta save (status={status}):", o.read().decode()[:100])

# Check pending
_, o, _ = c.exec_command(f"curl -s 'http://127.0.0.1:8080/api/files/meta?status=0' -H 'Authorization: Bearer {token}'", timeout=15)
resp = json.loads(o.read().decode())
print(f"Pending files: {len(resp.get('data', []))}")

c.close()
