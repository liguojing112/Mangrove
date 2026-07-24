import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=60)
    return o.read().decode() + e.read().decode()

# Check what port the backend is on
print("=== Port check ===")
print(run("ss -tlnp | grep 8080"))
print(run("ss -tlnp | grep java"))

# Check backend logs for startup
print("=== Backend logs ===")
print(run("journalctl -u mangrove --no-pager -n 50 --since '2 min ago'"))

# Try direct curl to backend
print("=== Direct curl ===")
print(run("curl -v http://127.0.0.1:8080/api/public/artists 2>&1 | head -30"))

# Check if the application.yml was picked up
print("=== Config check ===")
print(run("cat /opt/mangrove/application.yml | head -10"))

c.close()
