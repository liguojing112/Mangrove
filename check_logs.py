import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Wait for backend to fully start
print("Waiting for backend...")
time.sleep(8)

# Check status
print("=== Status ===")
print(run("systemctl is-active mangrove"))

# Check recent logs for errors
print("\n=== Recent ERROR/WARN ===")
print(run("journalctl -u mangrove --no-pager --since '1 min ago' | grep -i 'error\|exception\|Caused by' | tail -20"))

# Test all APIs again
print("\n=== All APIs ===")
apis = {
    "artists": "/api/public/artists",
    "announcements": "/api/public/announcements",
    "schedule/latest": "/api/public/schedule/latest",
    "calendar/days": "/api/calendar/days?artistId=1",
    "homepage-items": "/api/public/homepage-items",
    "config/homepage": "/api/public/config/homepage",
    "config/photos-hero": "/api/public/config/photos-hero-cards",
}
for name, path in apis.items():
    _, o, _ = c.exec_command(f"curl -s -o /dev/null -w '%{{http_code}}' http://127.0.0.1:8080{path}", timeout=15)
    status = o.read().decode().strip()
    _, o2, _ = c.exec_command(f"curl -s http://127.0.0.1:8080{path}", timeout=15)
    resp = o2.read().decode().strip()[:150]
    ok = status == "200"
    print(f"  {'OK' if ok else 'ERR'} [{status}] {name}: {resp[:100]}")

# Check through Nginx too
print("\n=== Through Nginx ===")
for name, path in apis.items():
    _, o, _ = c.exec_command(f"curl -s -o /dev/null -w '%{{http_code}}' http://127.0.0.1{path}", timeout=15)
    status = o.read().decode().strip()
    print(f"  [{status}] {name}")

c.close()
