import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    out = o.read().decode()
    err = e.read().decode()
    if err.strip():
        return f"{out}\n{err.strip()}"
    return out.strip()

mysql = "mariadb -u root -pmangrove_db_pass mangrove"

# 1. Announcement (column: status_flag not status)
print("=== Insert announcement ===")
print(run(f"{mysql} -e \"INSERT INTO announcement (title, content, status_flag) VALUES ('欢迎来到芒果种植园', '这里是粉丝专属的应援社区，一起守护最好的艺人！', 1);\""))

# 2. Schedule (schedule_type, start_time)
print("\n=== Insert schedule ===")
print(run(f"{mysql} -e \"INSERT INTO schedule (title, description, schedule_type, location, start_time, status) VALUES ('粉丝见面会', '与艺人近距离互动', 'FANMEETING', '北京', '2026-08-15 19:00:00', 1);\""))

# 3. Homepage item (content_type, section, target_id)
print("\n=== Insert homepage items ===")
items = [
    ("BANNER", "LONG_VIDEO", 1, 0),
    ("PHOTO", "PHOTO", 1, 1),
    ("VIDEO", "VIDEO", 1, 2),
]
for section, ctype, target, sort in items:
    r = run(f"{mysql} -e \"INSERT INTO homepage_item (section, content_type, target_id, sort_order) VALUES ('{section}', '{ctype}', {target}, {sort});\"")
    if r:
        print(f"  {section}: {r}")

# 4. Verify
print("\n=== Verify ===")
for t in ["artist", "announcement", "schedule", "homepage_item", "sys_config"]:
    r = run(f"{mysql} -e 'SELECT COUNT(*) as cnt FROM {t};'")
    print(f"  {t}: {r.strip()}")

# 5. Restart backend
print("\n=== Restart backend ===")
c2 = paramiko.SSHClient()
c2.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c2.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)
c2.exec_command("systemctl restart mangrove", timeout=30)
time.sleep(12)

# 6. Test APIs
print("\n=== Test APIs ===")
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
    _, o, _ = c2.exec_command(f"curl -s http://127.0.0.1:8080{api}", timeout=15)
    resp = o.read().decode().strip()[:200]
    ok = '"code":200' in resp
    print(f"  {'OK' if ok else 'ERR'} {api}: {resp[:120]}")

c.close()
c2.close()
