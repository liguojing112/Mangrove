import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Login
login_data = json.dumps({"username": "admin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
token = json.loads(o.read().decode())["data"]["token"]

# Check-in
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/tree/checkin -H 'Authorization: Bearer {token}'", timeout=15)
print("Check-in:", o.read().decode()[:300])

# Recent
_, o, _ = c.exec_command("curl -s http://127.0.0.1:8080/api/tree/recent-checkins", timeout=15)
print("Recent:", o.read().decode()[:300])

c.close()
