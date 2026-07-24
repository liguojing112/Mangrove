import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    out = o.read().decode()
    err = e.read().decode()
    if err.strip():
        return f"{out}{err.strip()}"
    return out.strip()

mysql = "mariadb -u root -pmangrove_db_pass mangrove"

# Check column names for failed tables
tables = ["homepage_item", "schedule", "announcement"]
for t in tables:
    print(f"\n=== {t} ===")
    print(run(f"{mysql} -e 'DESCRIBE {t};'"))

c.close()
