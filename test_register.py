import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Test registration properly
print("=== Test registration ===")
reg_data = json.dumps({"username": "newfan1", "password": "test123456", "nickname": "新粉丝"})
_, o, e = c.exec_command(f"curl -sv -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}' 2>&1", timeout=15)
out = o.read().decode()
err = e.read().decode()
print("Response:", out[-300:] if out else "empty")
print("Error:", err[-300:] if err else "none")

# Check logs
print("\n=== Recent logs ===")
print(run("journalctl -u mangrove --no-pager --since '1 min ago' | grep -i 'register\|public_id\|error\|exception' | tail -15"))

# Test with different endpoint
print("\n=== Test auth paths ===")
paths = [
    ("POST", "/api/auth/register", json.dumps({"username":"test2","password":"test123","nickname":"test2"})),
    ("POST", "/api/auth/signup", json.dumps({"username":"test2","password":"test123","nickname":"test2"})),
]
for method, path, data in paths:
    _, o, _ = c.exec_command(f"curl -s -o /dev/null -w '%{{http_code}}' -X {method} http://127.0.0.1:8080{path} -H 'Content-Type: application/json' -d '{data}'", timeout=15)
    status = o.read().decode().strip()
    print(f"  {method} {path}: {status}")

c.close()
