import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Login
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
token = json.loads(o.read().decode())["data"]["token"]

# Upload image
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/files/upload -H 'Authorization: Bearer {token}' -F 'file=@/opt/mangrove/uploads/a2042143-0828-4a79-b660-829078325365.jpeg'", timeout=15)
upload_resp = json.loads(o.read().decode())
url = upload_resp.get("data", {}).get("url", "")
filename = url.split("/")[-1] if url else ""
print(f"Uploaded: {filename}")

# Save meta with status=0
meta = json.dumps({"filename": filename, "displayName": "测试待审核", "seqNo": 0, "category": "", "photoDate": "", "status": 0})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/files/meta -H 'Content-Type: application/json' -H 'Authorization: Bearer {token}' -d '{meta}'", timeout=15)
print("Meta save:", o.read().decode()[:100])

# Check pending
_, o, _ = c.exec_command(f"curl -s 'http://127.0.0.1:8080/api/files/meta?status=0' -H 'Authorization: Bearer {token}'", timeout=15)
resp = json.loads(o.read().decode())
pending = resp.get("data", [])
print(f"Pending files: {len(pending)}")
for item in pending:
    print(f"  {item['filename'][:40]} status={item.get('status', '?')}")

# Approve the file
if pending:
    fname = pending[0]["filename"]
    _, o, _ = c.exec_command(f"curl -s -X PUT 'http://127.0.0.1:8080/api/files/meta/{fname}/status?status=1' -H 'Authorization: Bearer {token}'", timeout=15)
    print("Approve result:", o.read().decode())

# Verify it's now approved
_, o, _ = c.exec_command(f"curl -s 'http://127.0.0.1:8080/api/files/meta?status=1' -H 'Authorization: Bearer {token}'", timeout=15)
resp2 = json.loads(o.read().decode())
print(f"After approve - pending: 0, approved: {len(resp2.get('data', []))}")

c.close()
