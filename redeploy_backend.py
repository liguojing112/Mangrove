import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Upload new JAR
print("=== Upload JAR ===")
sftp = c.open_sftp()
sftp.put("target/mangrove-1.0.0.jar", "/opt/mangrove/mangrove-1.0.0.jar")
sftp.close()
print("Done")

# Restart backend
print("=== Restart backend ===")
_, o, _ = c.exec_command("systemctl restart mangrove", timeout=30)
print(o.read().decode())

time.sleep(12)

# Test
print("=== Test ===")
_, o, _ = c.exec_command("systemctl is-active mangrove", timeout=10)
print("Status:", o.read().decode().strip())

_, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' http://127.0.0.1:8080/api/public/artists", timeout=15)
print("API:", o.read().decode().strip())

c.close()
print("\nDone!")
