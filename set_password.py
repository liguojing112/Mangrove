import paramiko
import json

HASH = "$2b$12$PpcnW3F33GDSeGRUvtoi0ObY2ccoKxkyE86E4pgH7LRL1GkIq.sNy"

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Write SQL
sql = f"UPDATE sys_user SET password='{HASH}';"
sftp = c.open_sftp()
with sftp.open("/tmp/fix.sql", "w") as f:
    f.write(sql)
sftp.close()

print("=== Update passwords ===")
print(run("mariadb -u root -pmangrove_db_pass mangrove < /tmp/fix.sql"))
print(run("rm -f /tmp/fix.sql"))

# Verify
print("=== Verify ===")
print(run("mariadb -u root -pmangrove_db_pass -e \"SELECT id, username, LEFT(password, 30) as pw FROM sys_user;\" mangrove"))

# Test login
print("\n=== Test login superadmin/admin123 ===")
data = json.dumps({"username": "superadmin", "password": "admin123"})
print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{data}'"))

print("\n=== Test login admin/admin123 ===")
data2 = json.dumps({"username": "admin", "password": "admin123"})
print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{data2}'"))

c.close()
