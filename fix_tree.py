import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

mysql = "mariadb -u root -proot123 mangrove"

# Check user_tree table structure
print("=== user_tree table ===")
print(run(f"{mysql} -e 'DESCRIBE user_tree;'"))

# Fix NOT NULL columns that need defaults
print("\n=== Fix columns ===")
fixes = [
    "ALTER TABLE user_tree MODIFY COLUMN points INT NOT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN total_points INT NOT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN experience INT NOT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN level INT NOT NULL DEFAULT 1",
]
for sql in fixes:
    print(run(f"{mysql} -e \"{sql}\" 2>&1"))

# Verify
print("\n=== Verify ===")
print(run(f"{mysql} -e 'DESCRIBE user_tree;' | grep -E 'points|total_points|experience|level'"))

# Test registration again
print("\n=== Test registration ===")
reg_data = json.dumps({"username": "newfan99", "password": "test123456", "nickname": "新粉丝"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}'", timeout=15)
resp = o.read().decode().strip()[:300]
print(resp)

# Check users
print("\n=== Users ===")
print(run(f"{mysql} -e 'SELECT id, username, nickname, role FROM sys_user;'"))

c.close()
