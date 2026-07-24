import paramiko
import os

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Clear old frontend
print("=== Clear old frontend ===")
print(run("rm -rf /opt/mangrove/frontend/* 2>&1"))

# Upload new dist
local_dist = os.path.join(os.path.dirname(os.path.abspath(__file__)), "frontend", "dist")
sftp = c.open_sftp()
for root, dirs, files in os.walk(local_dist):
    rel = os.path.relpath(root, local_dist)
    remote_base = os.path.join("/opt/mangrove/frontend", rel).replace("\\", "/")
    try:
        sftp.mkdir(remote_base)
    except:
        pass
    for f in files:
        local_file = os.path.join(root, f)
        remote_file = os.path.join(remote_base, f).replace("\\", "/")
        sftp.put(local_file, remote_file)
sftp.close()

# Verify
print("=== Verify ===")
print(run("ls /opt/mangrove/frontend/"))
print(run("ls /opt/mangrove/frontend/assets/ | head -5"))

# Test
_, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' http://localhost/tree", timeout=15)
print(f"\nPage /tree: {o.read().decode().strip()}")

c.close()
print("\nDone! Refresh https://43.155.249.14/tree")
