import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

print("=== Recent errors ===")
print(run("journalctl -u mangrove --no-pager --since '5 min ago' | grep -i 'error\|exception\|upload\|file' | tail -20"))

print("\n=== Upload dir permissions ===")
print(run("ls -la /opt/mangrove/uploads/ | head -5"))

c.close()
