import paramiko, json

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

# Write JSON to temp file
sftp = c.open_sftp()
with sftp.open("/tmp/test_ask.json", "w") as f:
    f.write(json.dumps({"question": "你最喜欢的水果是什么？"}))
sftp.close()

# Test ask endpoint
_, o, _ = c.exec_command("curl -s -X POST http://127.0.0.1:8080/api/artist-bio/ask -H 'Content-Type: application/json' -d @/tmp/test_ask.json", timeout=15)
print("Ask result:", o.read().decode()[:300])

# Test bio sections (public endpoint)
_, o, _ = c.exec_command("curl -s http://127.0.0.1:8080/api/public/artists/1/bio", timeout=15)
print("Bio sections:", o.read().decode()[:300])

c.close()
