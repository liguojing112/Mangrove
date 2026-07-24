import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

# Check upload dir
print("=== Upload dir ===")
print(run("ls -la /opt/mangrove/uploads/ 2>&1 | head -10"))

# Test photos API
print("\n=== Photos API ===")
print(run("curl -s http://127.0.0.1:8080/api/files/list | python3 -m json.tool 2>/dev/null | head -40 || curl -s http://127.0.0.1:8080/api/files/list | head -300"))

# Check if file URLs work
print("\n=== File access test ===")
print(run("ls /opt/mangrove/uploads/ 2>/dev/null | head -5"))
# Check nginx uploads config
print("\n=== Nginx uploads config ===")
print(run("grep -A3 'uploads' /etc/nginx/sites-available/mangrove"))

# Check file controller logs
print("\n=== File controller errors ===")
print(run("journalctl -u mangrove --no-pager --since '5 min ago' | grep -i 'file\|upload\|photo\|image' | tail -10"))

c.close()
