import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=15)
    return o.read().decode().strip()

print("=== Full Nginx config ===")
print(run("cat /etc/nginx/sites-available/mangrove"))

print("\n=== HTTPS file test ===")
# Test via HTTPS directly
print(run("curl -sk -o /dev/null -w '%{http_code}' https://127.0.0.1/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg"))

# Check file existence
print("\n=== File check ===")
print(run("ls -la /opt/mangrove/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg"))

# Check nginx error log
print("\n=== Nginx recent errors ===")
print(run("tail -20 /var/log/nginx/error.log"))

# Check if the uploads location works in HTTPS block
print("\n=== Test with trace ===")
print(run("curl -skv https://127.0.0.1/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg 2>&1 | tail -20"))

c.close()
