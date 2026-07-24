import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

mysql = "mariadb -u root -proot123 mangrove"

# 1. Add status column to file_meta
print("=== Add status column ===")
c.exec_command(f"""{mysql} -e "ALTER TABLE file_meta ADD COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '0=待审 1=通过 2=拒绝';" """, timeout=10)

# 2. Verify
_, o, _ = c.exec_command(f"{mysql} -e 'DESCRIBE file_meta;'", timeout=10)
print(o.read().decode())

c.close()
print("Done")
