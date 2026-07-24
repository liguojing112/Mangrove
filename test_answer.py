import paramiko, json, time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Wait for backend
for i in range(15):
    _, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:8080/api/public/artists", timeout=10)
    if o.read().decode().strip() == "200":
        print("Backend ready")
        break
    time.sleep(3)

# Login
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
token = json.loads(o.read().decode())["data"]["token"]

# Answer a question
body = json.dumps({"question": "test", "answer": "回答成功了！"})
sftp = c.open_sftp()
with sftp.open("/tmp/answer_body.json", "w") as f:
    f.write(body)
sftp.close()

_, o, _ = c.exec_command(f"curl -s -X PUT http://127.0.0.1:8080/api/admin/artist-bio/24 -H 'Content-Type: application/json' -H 'Authorization: Bearer {token}' -d @/tmp/answer_body.json", timeout=15)
print("Answer:", o.read().decode()[:300])

c.close()
