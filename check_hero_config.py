import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Login
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
token = json.loads(o.read().decode())["data"]["token"]

# Check hero cards config
_, o, _ = c.exec_command(f'curl -s http://127.0.0.1:8080/api/admin/config/photos_hero_cards -H "Authorization: Bearer {token}"', timeout=15)
resp = o.read().decode()
print("Response:", resp[:300])

# Check DB directly
_, o, _ = c.exec_command("mariadb -u root -proot123 mangrove -e \"SELECT config_key, LEFT(config_value, 100) FROM sys_config WHERE config_key='photos_hero_cards';\"", timeout=10)
print("\nDB:", o.read().decode().strip())

c.close()
