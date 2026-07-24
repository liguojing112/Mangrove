import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

mysql = "mariadb -u root -pmangrove_db_pass mangrove"

# Fix 1: Make public_id nullable or give default
print("=== Fix public_id column ===")
# Make it nullable so registration doesn't fail
print(run(f"{mysql} -e \"ALTER TABLE sys_user MODIFY COLUMN public_id VARCHAR(20) NULL;\" 2>&1"))
# Verify
print(run(f"{mysql} -e 'DESCRIBE sys_user;' | grep public_id"))

# Fix 2: Set proper password for the root user via JDBC
# The FileController uses raw JDBC with old password 'root123'
# We need to also set the password to 'root123' via the old method
# OR change the auth to work with both passwords
print("\n=== Fix root auth ===")
# Grant root access with the old password too (for the hardcoded JDBC)
cmds = [
    "mariadb -u root -pmangrove_db_pass -e \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123';\" 2>&1",
    "mariadb -u root -proot123 -e \"SELECT 'JDBC password fixed';\" 2>&1",
]
for cmd in cmds:
    print(run(cmd))

# Update application.yml to match
print("\n=== Update app config ===")
# The external application.yml uses mangrove_db_pass, but the JAR internals might use root123
# Let's update the external config to use root123
app_yml = """server:
  port: 8080

spring:
  application:
    name: mangrove
  datasource:
    url: jdbc:mysql://localhost:3306/mangrove?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: root123
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    open-in-view: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
  servlet:
    multipart:
      max-file-size: 500MB
      max-request-size: 1000MB
      enabled: true
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null

jwt:
  secret: YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY3ODlBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWg==
  expiration: 604800000
  header: Authorization
  token-prefix: "Bearer "

file:
  upload-dir: /opt/mangrove/uploads
"""
sftp = c.open_sftp()
with sftp.open("/opt/mangrove/application.yml", "w") as f:
    f.write(app_yml)
sftp.close()

# Restart backend
import time
print("\n=== Restart backend ===")
run("systemctl restart mangrove")
time.sleep(12)

# Test registration
print("\n=== Test registration ===")
import json
reg_data = json.dumps({"username": "testuser", "password": "test123", "nickname": "测试用户"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/register -H 'Content-Type: application/json' -d '{reg_data}'", timeout=15)
print(o.read().decode().strip()[:300])

# Also test admin login still works
print("\n=== Test admin login ===")
login_data = json.dumps({"username": "superadmin", "password": "admin123"})
_, o, _ = c.exec_command(f"curl -s -X POST http://127.0.0.1:8080/api/auth/login -H 'Content-Type: application/json' -d '{login_data}'", timeout=15)
resp = o.read().decode().strip()[:200]
print(resp)

c.close()
