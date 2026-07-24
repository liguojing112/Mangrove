import paramiko
import os
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=300)
    return o.read().decode() + e.read().decode()

# Import SQL with --force to skip "table already exists" errors
print("=== Import SQL with --force ===")
sql_file = os.path.join(os.path.dirname(os.path.abspath(__file__)), "docs", "mangrove-database.sql")
sftp = c.open_sftp()
sftp.put(sql_file, "/tmp/mangrove_init.sql")
sftp.close()
result = run("mariadb -u root -pmangrove_db_pass mangrove --force < /tmp/mangrove_init.sql 2>&1")
# Only show last 20 lines to see summary
lines = result.strip().split('\n')
print('\n'.join(lines[-20:]))

# Check row counts
print("\n=== Row counts ===")
tables = ["sys_user", "artist", "comment", "fan_work", "letter_note", "merchandise", "long_video", "game_level", "homepage_item", "announcement"]
for t in tables:
    result = run(f"mariadb -u root -pmangrove_db_pass -e \"SELECT '{t}' AS tbl, COUNT(*) AS cnt FROM {t};\" mangrove 2>&1")
    for line in result.strip().split('\n'):
        if t in line or 'cnt' in line:
            print(line)

# Restart backend
print("\n=== Restart backend ===")
run("systemctl restart mangrove")
time.sleep(12)

# Test API with data
print("=== API test ===")
print(run("curl -s http://localhost:8080/api/public/artists"))
print(run("curl -s http://localhost/api/public/artists"))

c.close()
