import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Test registration
print("=== Test registration ===")
reg_data = json.dumps({"username": "newfan100", "password": "test123456", "nickname": "新粉丝100"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}'", timeout=15)
print(o.read().decode().strip()[:300])

# Check error in logs
print("\n=== Full error ===")
print(run("journalctl -u mangrove --no-pager --since '30 sec ago' | grep -A 5 'Caused by' | tail -30"))

# Check the full stack trace
print("\n=== Stack trace ===")
print(run("journalctl -u mangrove --no-pager --since '30 sec ago' | grep -E 'Caused by|ConstraintViolation|cannot be null' | tail -10"))

c.close()
