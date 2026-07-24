import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

print("=== Backend status ===")
print(run("systemctl status mangrove --no-pager | head -15"))

print("=== Backend logs (recent) ===")
print(run("journalctl -u mangrove --no-pager -n 20 --since '1 min ago'"))

print("=== Port check ===")
print(run("ss -tlnp | grep -E '80|8080'"))

print("=== Direct API test ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:8080/api/public/artists"))

print("=== Nginx API test ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1/api/public/artists"))

c.close()
