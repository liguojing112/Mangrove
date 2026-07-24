import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=60)
    return o.read().decode() + e.read().decode()

print("Waiting for backend...")
time.sleep(15)

print("=== mangrove service ===")
print(run("systemctl status mangrove --no-pager | head -15"))
print("=== Recent logs ===")
print(run("journalctl -u mangrove --no-pager -n 30"))
print("=== API test ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/public/artists"))
print("=== Frontend ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://localhost/"))

c.close()
