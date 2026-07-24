import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

# Check registration error in logs
print("=== Backend errors ===")
print(run("journalctl -u mangrove --no-pager --since '5 min ago' | grep -i 'error\|exception\|register\|register' | tail -20"))

# Check sys_user table structure
print("\n=== sys_user table ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'DESCRIBE sys_user;' mangrove"))

# Check if sys_config table exists
print("\n=== sys_config ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SHOW TABLES LIKE \"%sys%\";' mangrove"))

# List ALL tables
print("\n=== All tables ===")
print(run("mariadb -u root -pmangrove_db_pass -e 'SHOW TABLES;' mangrove"))

c.close()
