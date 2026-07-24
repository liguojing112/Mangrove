import paramiko
import json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

mysql = "mariadb -u root -proot123 mangrove"

# Check latest error
print("=== Latest error ===")
print(run("journalctl -u mangrove --no-pager --since '20 sec ago' | grep 'Caused by' | tail -5"))

# Make ALL potentially problematic columns nullable or give defaults
print("\n=== Fix all columns ===")
all_fixes = [
    "ALTER TABLE user_tree MODIFY COLUMN points INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN total_points INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN total_checkins INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN total_likes INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN total_uploads INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN consecutive_days INT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN experience BIGINT NULL DEFAULT 0",
    "ALTER TABLE user_tree MODIFY COLUMN level INT NULL DEFAULT 1",
]
for sql in all_fixes:
    r = run(f"{mysql} -e \"{sql}\" 2>&1")
    if r.strip():
        print(f"  {r.strip()[:80]}")

# Also check other potentially problematic tables
print("\n=== Check other tables ===")
for table in ["checkin_record", "birthday_wish", "favorite", "like_record", "letter_note", "user_note"]:
    r = run(f"{mysql} -e 'DESCRIBE {table};' 2>&1 | head -5")
    if "doesn't exist" not in r:
        print(f"\n--- {table} ---")
        print(r[:300])

# Test registration again
print("\n=== Test registration ===")
reg_data = json.dumps({"username": "newfan300", "password": "test123456", "nickname": "芒果粉丝300"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}'", timeout=15)
resp = o.read().decode().strip()
print(resp[:300])

if "500" in resp or "error" in resp.lower():
    print("\n=== New error ===")
    print(run("journalctl -u mangrove --no-pager --since '15 sec ago' | grep -E 'Caused by|cannot be null|ConstraintViolation' | head -5"))

c.close()
