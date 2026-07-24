import paramiko, time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)
_, o, _ = c.exec_command("cat /opt/mangrove/frontend/index.html", timeout=10)
html = o.read().decode()

# Add version timestamp to JS and CSS URLs
import re
ts = str(int(time.time()))
html = re.sub(r'src="(/assets/[^"]+)"', f'src="\\1?v={ts}"', html)
html = re.sub(r'href="(/assets/[^"]+\.css)"', f'href="\\1?v={ts}"', html)

sftp = c.open_sftp()
with sftp.open("/opt/mangrove/frontend/index.html", "w") as f:
    f.write(html)
sftp.close()

_, o, _ = c.exec_command("cat /opt/mangrove/frontend/index.html | grep -o 'script src.*'", timeout=10)
print("Updated HTML:", o.read().decode().strip())

c.close()
print("Done")
