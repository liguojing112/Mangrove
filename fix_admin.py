import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Check existing users
print("=== Users ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SELECT id, username, nickname, role FROM sys_user;' mangrove"))

# Reset superadmin password to admin123
# BCrypt hash for 'admin123': $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7jqL9V0Hu
print("\n=== Reset admin password ===")
print(run("mariadb -u root -pmangrove_db_pass mangrove -e \"UPDATE sys_user SET password=\\'$2a\\$10\\$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7jqL9V0Hu\\' WHERE username='superadmin';\" 2>&1"))

# Verify
print(run("mariadb -u root -pmangrove_db_pass -e 'SELECT id, username, nickname, role FROM sys_user;' mangrove"))

# Test login
print("\n=== Test login ===")
import json
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'"))

c.close()
