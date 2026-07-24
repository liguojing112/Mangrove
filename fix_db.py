import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=60)
    out = o.read().decode()
    err = e.read().decode()
    return out + err

# Check current auth method
print("=== Current root auth ===")
print(run("mariadb -e 'SELECT user, host, plugin FROM mysql.user WHERE user=\"root\";'"))

# MariaDB on Debian uses unix_socket by default - root can connect without password via socket
# We need to switch to password auth
print("=== Switch to password auth ===")
cmds = [
    "mariadb -e \"UPDATE mysql.user SET plugin='mysql_native_password' WHERE user='root';\"",
    "mariadb -e \"FLUSH PRIVILEGES;\"",
    "mariadb -e \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'mangrove_db_pass';\"",
    "mariadb -e \"FLUSH PRIVILEGES;\"",
]
for cmd in cmds:
    print(run(cmd))

# Test password auth
print("=== Test password auth ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SELECT password_auth_ok;'"))

# Re-import SQL
print("=== Re-import SQL ===")
print(run("mariadb -u root -pmangrove_db_pass mangrove < /tmp/mangrove_init.sql 2>&1 || true"))

# Restart backend
print("=== Restart backend ===")
print(run("systemctl restart mangrove"))
time.sleep(12)
print(run("systemctl status mangrove --no-pager | head -10"))

# Test API
print("=== Test API ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/public/artists"))

c.close()
