import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    out = o.read().decode()
    err = e.read().decode()
    if err.strip() and "Warning" not in err:
        print(f"  ERR: {err.strip()[:100]}")
    return out.strip()

mysql = "mariadb -u root -pmangrove_db_pass mangrove"

# 1. Create sys_config table
print("=== Create sys_config ===")
print(run(f"""{mysql} -e "
CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
" """))

# 2. Insert artist
print("=== Insert artist ===")
print(run(f"""{mysql} -e "
INSERT IGNORE INTO artist (name, stage_name, birth_date, debut_date, nationality, bio, company, sort_order, status)
VALUES ('小芒', '小芒', '2000-05-20', '2020-06-15', '中国', '新生代艺人，热爱音乐与舞台', '芒果娱乐', 0, 1);
" """))

# 3. Insert homepage items
print("=== Insert homepage items ===")
items = [
    ("查看艺人主页", "", "艺人", "0"),
    ("浏览照片馆藏", "", "照片", "1"),
    ("观看视频精选", "", "视频", "2"),
]
for title, _, link, sort in items:
    run(f"""{mysql} -e "INSERT IGNORE INTO homepage_item (title, link_url, item_type, sort_order, status) VALUES ('{title}', '{link}', 'LINK', {sort}, 1);" """)

# 4. Insert schedule
print("=== Insert schedule ===")
run(f"""{mysql} -e "INSERT IGNORE INTO schedule (title, event_date, event_type, location, status) VALUES ('粉丝见面会', '2026-08-15 19:00:00', '活动', '北京', 1);" """)

# 5. Insert announcement
print("=== Insert announcement ===")
run(f"""{mysql} -e "INSERT IGNORE INTO announcement (title, content, status) VALUES ('欢迎来到芒果种植园', '这里是粉丝专属的应援社区，一起守护最好的艺人！', 1);" """)

# Verify
print("\n=== Verify ===")
print("Artists:", run(f"{mysql} -e 'SELECT COUNT(*) FROM artist;'"))
print("Homepage items:", run(f"{mysql} -e 'SELECT COUNT(*) FROM homepage_item;'"))
print("Schedule:", run(f"{mysql} -e 'SELECT COUNT(*) FROM schedule;'"))
print("Announcements:", run(f"{mysql} -e 'SELECT COUNT(*) FROM announcement;'"))
print("sys_config:", run(f"{mysql} -e 'SELECT COUNT(*) FROM sys_config;'"))

# Restart backend
print("\n=== Restart backend ===")
import time
c2 = paramiko.SSHClient()
c2.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c2.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)
_, _, _ = c2.exec_command("systemctl restart mangrove", timeout=30)
time.sleep(10)

# Test APIs
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
    resp = o.read().decode().strip()[:150]
    code = "200" if '"code":200' in resp else "ERR"
    print(f"  {code} {api}: {resp[:120]}")

c.close()
c2.close()
