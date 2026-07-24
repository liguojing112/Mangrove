import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    out = o.read().decode()
    err = e.read().decode()
    if err.strip() and "Warning" not in err:
        return f"ERR: {err.strip()}" 
    return out.strip()

mysql = "mariadb -u root -pmangrove_db_pass mangrove"

# Check table structures
print("=== Check artist table ===")
print(run(f"{mysql} -e 'DESCRIBE artist;'"))

# Check sys_config table
print("\n=== Check sys_config table ===")
print(run(f"{mysql} -e 'DESCRIBE sys_config;'"))

# Check if tables exist
print("\n=== Check tables ===")
tables_result = run(f"{mysql} -e 'SHOW TABLES;'")
if "schedule" not in tables_result and "Schedule" not in tables_result:
    print("Searching for schedule table...")
    print(run(f"{mysql} -e \"SHOW TABLES LIKE '%sche%';\""))

if "homepage" not in tables_result.lower():
    print("Searching for homepage table...")
    print(run(f"{mysql} -e \"SHOW TABLES LIKE '%home%';\""))

# List all tables
print("\n=== All tables ===")
print(run(f"{mysql} -e 'SHOW TABLES;'"))

c.close()
