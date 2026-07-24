import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode().strip()

# Check status
print("=== Status ===")
print(run("systemctl is-active mangrove"))

# If not active, restart
status = run("systemctl is-active mangrove")
if status != "active":
    print("Restarting...")
    c.exec_command("systemctl restart mangrove")
    time.sleep(15)
    print("Status:", run("systemctl is-active mangrove"))

# Test API
print("\n=== API test ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:8080/api/public/artists"))

c.close()
