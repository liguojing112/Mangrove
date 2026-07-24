import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Check current password hash
print("=== Current hashes ===")
print(run("mariadb -u root -pmangrove_db_pass -e \"SELECT id, username, LEFT(password, 30) as pw_prefix FROM sys_user;\" mangrove"))

# Install bcrypt and generate hash
print("=== Install bcrypt ===")
print(run("pip3 install bcrypt -q 2>&1"))

print("=== Generate hash ===")
hash_result = run("python3 -c \"import bcrypt; print(bcrypt.hashpw(b'admin123', bcrypt.gensalt()).decode())\"")
print("Hash:", hash_result.strip())

# Write the update SQL properly
hash_val = hash_result.strip()
if hash_val.startswith("$"):
    sql = f"UPDATE sys_user SET password='{hash_val}';"
    sftp = c.open_sftp()
    with sftp.open("/tmp/fix.sql", "w") as f:
        f.write(sql)
    sftp.close()
    print("=== Update ===")
    print(run("mariadb -u root -pmangrove_db_pass mangrove < /tmp/fix.sql 2>&1"))
    print(run("rm -f /tmp/fix.sql"))

    # Verify
    print("=== Verify ===")
    print(run("mariadb -u root -pmangrove_db_pass -e \"SELECT id, username, LEFT(password, 30) as pw_prefix FROM sys_user;\" mangrove"))

    # Test login
    print("\n=== Test login ===")
    login_data = json.dumps({"username": "superadmin", "password": "admin123"})
    print(run(f"curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'"))
else:
    print("Failed to generate hash")

c.close()
