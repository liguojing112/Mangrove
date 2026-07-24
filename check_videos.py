import paramiko
c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=15)
    return o.read().decode().strip()

# Check what video files exist
print("=== File list ===")
print(run("curl -s http://127.0.0.1:8080/api/files/list | python3 -m json.tool | head -50"))

print("\n=== Long videos API ===")
print(run("curl -s http://127.0.0.1:8080/api/long-videos | head -200"))

c.close()
