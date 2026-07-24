import paramiko
import os
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=120)
    return o.read().decode() + e.read().decode()

# 1. Check MariaDB auth and fix
print("=== Fix MariaDB auth ===")
# Try connecting without password first (unix_socket)
print(run("mariadb -e 'SELECT 1;' 2>&1"))
# Check current auth plugin
print(run("mariadb -e \"SELECT user, host, plugin FROM mysql.global_priv WHERE user='root';\" 2>&1 || true"))

# 2. Set password properly
print("=== Set password ===")
print(run("mariadb -e \"ALTER USER 'root'@'localhost' IDENTIFIED VIA mysql_native_password USING PASSWORD('mangrove_db_pass');\" 2>&1 || true"))
print(run("mariadb -e \"FLUSH PRIVILEGES;\" 2>&1 || true"))

# 3. Test password auth
print("=== Test password ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SELECT 1;' 2>&1"))

# 4. Import SQL with password
print("=== Import SQL ===")
sql_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), "docs", "mangrove-database.sql")
sftp = c.open_sftp()
sftp.put(sql_file, "/tmp/mangrove_init.sql")
sftp.close()
print(run("mariadb -u root -pmangrove_db_pass mangrove < /tmp/mangrove_init.sql 2>&1"))

# 5. Verify tables
print("=== Tables ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SHOW TABLES;' mangrove 2>&1 | head -30"))

# 6. Restart backend
print("=== Restart backend ===")
print(run("systemctl restart mangrove"))
time.sleep(12)

# 7. Test API
print("=== API test ===")
print(run("curl -s http://localhost:8080/api/public/artists"))
print(run("curl -s http://localhost/api/public/artists"))

c.close()
