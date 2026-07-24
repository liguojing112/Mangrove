import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=60)
    return o.read().decode() + e.read().decode()

# Check firewall
print("=== Firewall ===")
print(run("iptables -L -n 2>/dev/null | head -10 || ufw status 2>/dev/null || echo 'no firewall found'"))

# Check if port 443 is open
print("=== Port 443 ===")
print(run("ss -tlnp | grep 443 || echo 'port 443 not listening'"))

# Generate self-signed SSL cert
print("=== Generate self-signed cert ===")
cmds = [
    "mkdir -p /etc/nginx/ssl",
    "openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/nginx/ssl/mangrove.key -out /etc/nginx/ssl/mangrove.crt -subj '/CN=43.155.249.14'",
]
for cmd in cmds:
    print(run(cmd))

# Write HTTPS Nginx config
nginx_conf = """server {
    listen 80;
    server_name _;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name _;

    ssl_certificate /etc/nginx/ssl/mangrove.crt;
    ssl_certificate_key /etc/nginx/ssl/mangrove.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    root /opt/mangrove/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
        client_max_body_size 50m;
    }

    location /uploads/ {
        alias /opt/mangrove/uploads/;
        expires 30d;
    }

    location ~* \\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
"""

sftp = c.open_sftp()
with sftp.open("/etc/nginx/sites-available/mangrove", "w") as f:
    f.write(nginx_conf)
sftp.close()

# Test and reload Nginx
print("=== Nginx test ===")
print(run("nginx -t"))
print(run("systemctl reload nginx"))

# Verify
import time
time.sleep(2)
print("\n=== Verify HTTP→HTTPS redirect ===")
_, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' -L http://localhost/", timeout=15)
print(f"HTTP redirect: {o.read().decode().strip()}")
_, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' -k https://localhost/", timeout=15)
print(f"HTTPS direct: {o.read().decode().strip()}")

print("\n=== Done ===")
print("Access: https://43.155.249.14")
print("Note: self-signed cert, browsers will show 'Not Secure' warning - click 'Advanced' → 'Proceed'")

c.close()
