import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Check if JS/CSS assets load
print("=== Check JS/CSS assets ===")
import re
index = run("cat /opt/mangrove/frontend/index.html")
# Find asset URLs
js_file = re.search(r'src="(/assets/[^"]+)"', index)
css_file = re.search(r'href="(/assets/[^"]+)"', index)
if js_file:
    code = run(f"curl -s -o /dev/null -w '%{{http_code}}' http://127.0.0.1{js_file.group(1)}")
    print(f"JS: {js_file.group(1)} -> {code}")
if css_file:
    code = run(f"curl -s -o /dev/null -w '%{{http_code}}' http://127.0.0.1{css_file.group(1)}")
    print(f"CSS: {css_file.group(1)} -> {code}")

# Check key API endpoints
print("\n=== API endpoints ===")
apis = [
    "/api/public/artists",
    "/api/public/announcements",
    "/api/public/schedule/latest",
    "/api/calendar/days?artistId=1",
    "/api/public/homepage-items",
    "/api/public/config/homepage",
    "/api/public/config/photos-hero-cards",
]
for api in apis:
    resp = run(f"curl -s http://127.0.0.1:8080{api}")
    print(f"\n{api}:")
    print(f"  {resp.strip()[:200]}")

c.close()
