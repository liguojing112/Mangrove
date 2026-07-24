import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Get list of actual files on disk
_, o, _ = c.exec_command("ls /opt/mangrove/uploads/", timeout=10)
existing = set(o.read().decode().strip().split('\n'))
existing = {f for f in existing if f}  # remove empty strings
print(f"Files on disk: {len(existing)}")

# Get all filenames from file_meta
_, o, _ = c.exec_command("mariadb -u root -proot123 mangrove -e \"SELECT filename FROM file_meta;\"", timeout=10)
meta_files = o.read().decode().strip().split('\n')[1:]  # skip header
meta_filenames = [line.strip() for line in meta_files if line.strip()]
print(f"Meta entries: {len(meta_filenames)}")

# Find orphans (in meta but not on disk)
orphans = [f for f in meta_filenames if f not in existing]
print(f"Orphans to delete: {len(orphans)}")

if orphans:
    # Delete orphans in batches
    batch_size = 50
    for i in range(0, len(orphans), batch_size):
        batch = orphans[i:i+batch_size]
        quoted = ", ".join(["'" + f + "'" for f in batch])
        c.exec_command(f"mariadb -u root -proot123 mangrove -e \"DELETE FROM file_meta WHERE filename IN ({quoted});\"", timeout=10)
        print(f"  Deleted batch {i//batch_size + 1} ({len(batch)} records)")

# Verify
_, o, _ = c.exec_command("mariadb -u root -proot123 mangrove -e \"SELECT COUNT(*) as remaining FROM file_meta;\"", timeout=10)
remaining = o.read().decode().strip().split('\n')[-1].strip()
print(f"\nRemaining meta entries: {remaining}")

c.close()
print("Done")
