import paramiko
import os
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=120)
    return o.read().decode() + e.read().decode()

# 1. Re-upload and import SQL
print("=== Re-import SQL ===")
sql_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), "docs", "mangrove-database.sql")
sftp = c.open_sftp()
sftp.put(sql_file, "/tmp/mangrove_init.sql")
sftp.close()
print(run("mariadb -u root mangrove < /tmp/mangrove_init.sql 2>&1"))
print(run("rm -f /tmp/mangrove_init.sql"))

# 2. Verify tables exist
print("=== Tables ===")
print(run("mariadb -u root -e 'SHOW TABLES;' mangrove 2>&1 | head -30"))

# 3. Check Nginx config and status
print("=== Nginx status ===")
print(run("systemctl status nginx --no-pager | head -10"))
print(run("cat /etc/nginx/sites-available/mangrove 2>/dev/null | head -20 || echo 'no nginx config'"))
print(run("nginx -t 2>&1 || true"))

# 4. Test from external via Nginx
print("=== External test ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://localhost/"))
print(run("curl -s -o /dev/null -w '%{http_code}' http://localhost/api/public/artists"))

# 5. Restart backend to pick up tables
print("=== Restart backend ===")
print(run("systemctl restart mangrove"))
time.sleep(10)
print(run("systemctl is-active mangrove"))

# 6. Final API test
print("=== Final API test ===")
print(run("curl -s http://localhost:8080/api/public/artists"))
print(run("curl -s http://localhost/api/public/artists"))

# 7. Check if frontend loads
print("=== Frontend ===")
print(run("curl -s http://localhost/ | head -5"))

c.close()
print("\n=== DONE ===")
print("Visit: http://43.155.249.14")
