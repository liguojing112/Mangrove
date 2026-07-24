import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

print("=== Frontend files ===")
print(run("ls -la /opt/mangrove/frontend/"))
print(run("ls /opt/mangrove/frontend/assets/ | head -10"))

print("=== Check index.html references ===")
print(run("cat /opt/mangrove/frontend/index.html"))

print("=== Test page routes ===")
for path in ["/", "/photos", "/admin", "/login", "/api/public/artists"]:
    code = run(f"curl -s -o /dev/null -w '%{{http_code}}' http://127.0.0.1{path}")
    print(f"  {path}: {code.strip()}")

print("=== Check JS/CSS 404s ===")
print(run(r"grep -i '404\|error\|fail' /var/log/nginx/access.log 2>/dev/null | tail -10 || echo 'no access log'"))
print(run(r"grep -i '404\|error' /var/log/nginx/error.log 2>/dev/null | tail -10 || echo 'no error log'"))

c.close()
