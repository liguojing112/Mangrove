import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=15)
    return o.read().decode().strip()

# Test file access via nginx
print("=== File access via Nginx ===")
files = [
    "42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg",
    "a2dba733-abd8-4add-ac60-51323ea96056.jpeg",
    "e8f04423-68c6-4adb-bed4-f0adad76ee7b.jpeg",
]
for f in files:
    result = run(f"curl -s -o /dev/null -w '%{{http_code}} %{{size_download}}' http://127.0.0.1/uploads/{f}")
    print(f"  {f[:20]}... -> {result}")

# Test via external URL
print("\n=== External access test ===")
print(run("curl -sk -o /dev/null -w '%{http_code}' https://43.155.249.14/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg"))

# Check if the issue is CORS or something else
print("\n=== Check API response headers ===")
print(run("curl -sI http://127.0.0.1/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg | head -10"))

# Check if the frontend is using the right URL
print("\n=== Frontend photo API ===")
resp = run("curl -s http://127.0.0.1:8080/api/files/list")
print(resp[:500])

c.close()
