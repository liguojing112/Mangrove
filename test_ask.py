import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Write JSON body to a temp file to avoid escaping issues
sftp = c.open_sftp()
with sftp.open("/tmp/ask_body.json", "w") as f:
    f.write(json.dumps({"question": "你最喜欢什么颜色？"}))
sftp.close()

# Test with file body
_, o, _ = c.exec_command("curl -s -X POST http://127.0.0.1:8080/api/artist-bio/ask -H 'Content-Type: application/json' -d @/tmp/ask_body.json", timeout=15)
print("Result:", o.read().decode()[:300])

c.close()
