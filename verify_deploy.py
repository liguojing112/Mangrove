import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

print("=== Service Status ===")
print("mangrove:", run("systemctl is-active mangrove").strip())
print("nginx:", run("systemctl is-active nginx").strip())
print("mariadb:", run("systemctl is-active mariadb").strip())

print("\n=== Port Check ===")
print(run("ss -tlnp | grep -E '80|8080|3306'"))

print("\n=== Frontend (via Nginx) ===")
print("Status:", run("curl -s -o /dev/null -w '%{http_code}' http://localhost/").strip())
print("Title:", run("curl -s http://localhost/ | grep -o '<title>.*</title>'").strip())

print("\n=== Backend API (via Nginx) ===")
print("Status:", run("curl -s -o /dev/null -w '%{http_code}' http://localhost/api/public/artists").strip())
print("Response:", run("curl -s http://localhost/api/public/artists").strip())

print("\n=== Backend API (direct) ===")
print("Status:", run("curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/public/artists").strip())

print("\n=== Admin Login ===")
login_result = run("""curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"username":"superadmin","password":"admin123"}'""")
print("Login:", login_result.strip()[:200])

print("\n=== External Access ===")
print("Frontend:", run("curl -s -o /dev/null -w '%{http_code}' http://43.155.249.14/").strip())
print("API:", run("curl -s -o /dev/null -w '%{http_code}' http://43.155.249.14/api/public/artists").strip())

c.close()
