import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

mysql = "mariadb -u root -proot123 mangrove"

# Check current SQL mode
print("=== Current SQL_MODE ===")
print(run(f"{mysql} -e 'SELECT @@sql_mode;'"))

# Make points nullable (simplest fix)
print("\n=== Make columns nullable ===")
cols = ["points", "total_points"]
for col in cols:
    print(run(f"{mysql} -e \"ALTER TABLE user_tree MODIFY COLUMN {col} INT NULL DEFAULT 0;\" 2>&1"))

# Verify
print("\n=== Verify ===")
print(run(f"{mysql} -e 'DESCRIBE user_tree;' | grep -E \"points|total_points\""))

# Test registration
print("\n=== Test registration ===")
reg_data = json.dumps({"username": "newfan200", "password": "test123456", "nickname": "芒果粉丝"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}'", timeout=15)
print(o.read().decode().strip()[:300])

# Check users
print("\n=== Users ===")
print(run(f"{mysql} -e 'SELECT id, username, nickname, role FROM sys_user;'"))

c.close()
