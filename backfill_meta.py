import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

mysql = "mariadb -u root -proot123 mangrove"

# Get all media files from uploads dir
_, o, _ = c.exec_command("ls /opt/mangrove/uploads/ | grep -iE '\\.(jpg|jpeg|png|gif|webp|mp4|webm|mov|avi)$'", timeout=15)
files = o.read().decode().strip().split('\n')
print(f"Found {len(files)} media files")

# Insert into file_meta (skip existing)
for fname in files:
    if not fname.strip():
        continue
    c.exec_command(f"""{mysql} -e "INSERT IGNORE INTO file_meta (filename, display_name, seq_no) VALUES ('{fname}', '', 0);" """, timeout=5)

# Verify
_, o, _ = c.exec_command(f"{mysql} -e 'SELECT COUNT(*) as cnt FROM file_meta;'", timeout=5)
print("Meta rows:", o.read().decode().strip())

c.close()
