import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Check current pending
_, o, _ = c.exec_command("mariadb -u root -proot123 mangrove -e \"SELECT COUNT(*) FROM file_meta WHERE status=0;\"", timeout=10)
count = o.read().decode().strip().split('\n')[-1].strip()
print(f"Current pending: {count}")

# If 0, insert one directly
if count == "0":
    filename = "test_pending_" + str(int(__import__('time').time())) + ".jpg"
    c.exec_command(f"mariadb -u root -proot123 mangrove -e \"INSERT INTO file_meta (filename, display_name, status) VALUES ('{filename}', '待审核测试图片', 0);\"", timeout=10)
    
    # Verify
    _, o, _ = c.exec_command("mariadb -u root -proot123 mangrove -e \"SELECT COUNT(*) FROM file_meta WHERE status=0;\"", timeout=10)
    count2 = o.read().decode().strip().split('\n')[-1].strip()
    print(f"After insert: {count2}")

# Check API
_, o, _ = c.exec_command("curl -s 'http://127.0.0.1:8080/api/files/meta?status=0'", timeout=15)
resp = json.loads(o.read().decode())
print(f"API pending: {len(resp.get('data', []))}")
if resp.get('data'):
    print(f"  First: {resp['data'][0].get('displayName', '?')}")

c.close()
