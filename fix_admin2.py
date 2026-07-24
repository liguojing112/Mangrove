import paramiko
import subprocess

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Generate BCrypt hash for 'admin123' using Python on the server
print("=== Generate hash ===")
hash_result = run("python3 -c \"import bcrypt; print(bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode())\" 2>&1 || pip3 install bcrypt -q && python3 -c \"import bcrypt; print(bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode())\" 2>&1")
print("Hash:", hash_result.strip())

# If bcrypt not available, use a known hash
# BCrypt hash for 'admin123' = $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7jqL9V0Hu
if "Traceback" in hash_result or not hash_result.strip().startswith("$"):
    hash_val = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7jqL9V0Hu"
    print("Using preset hash")
else:
    hash_val = hash_result.strip()

# Write SQL to a temp file and execute
sql_content = f"UPDATE sys_user SET password='{hash_val}' WHERE username='superadmin';"
sftp = c.open_sftp()
with sftp.open("/tmp/fix_pass.sql", "w") as f:
    f.write(sql_content)
sftp.close()

print("=== Update password ===")
print(run("mariadb -u root -pmangrove_db_pass mangrove < /tmp/fix_pass.sql 2>&1"))
print(run("rm -f /tmp/fix_pass.sql"))

# Verify
print("=== Users ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SELECT id, username, nickname, role FROM sys_user;' mangrove"))

# Test login
print("\n=== Test login ===")
import json
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'"))

# Also try admin/admin123
login_data2 = json.dumps({"username": "admin", "password": "admin123"})
print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data2}'"))

c.close()
