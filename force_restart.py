import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode().strip()

# Force kill Java process
print("=== Kill Java ===")
print(run("pkill -9 java 2>/dev/null; echo done"))

time.sleep(3)

# Start backend
print("=== Start backend ===")
print(run("systemctl start mangrove"))

time.sleep(15)

# Check
print("=== Status ===")
print(run("systemctl is-active mangrove"))

print("=== API ===")
print(run("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:8080/api/public/artists"))

c.close()
