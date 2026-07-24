import paramiko
import time

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=120)
    return o.read().decode() + e.read().decode()

# 1. Install certbot
print("=== Install certbot ===")
print(run("apt-get update -qq && apt-get install -y -qq certbot python3-certbot-nginx 2>&1 | tail -3"))

# 2. Update Nginx config to use domain
print("\n=== Update Nginx config ===")
nginx_conf = """server {
    listen 80;
    server_name candice0728.com www.candice0728.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name candice0728.com www.candice0728.com;

    ssl_certificate /etc/nginx/ssl/mangrove.crt;
    ssl_certificate_key /etc/nginx/ssl/mangrove.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    root /opt/mangrove/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location ^~ /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
        client_max_body_size 50m;
    }

    location ^~ /uploads/ {
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

print(run("nginx -t"))
print(run("systemctl reload nginx"))

# 3. Run certbot
print("\n=== Request Let's Encrypt certificate ===")
result = run("certbot --nginx -d candice0728.com -d www.candice0728.com --non-interactive --agree-tos --email admin@candice0728.com 2>&1")
print(result[-500:])

# 4. Test SSL
print("\n=== Test SSL ===")
time.sleep(3)
_, o, _ = c.exec_command("curl -s -o /dev/null -w '%{http_code}' https://candice0728.com/", timeout=15)
print("HTTPS status:", o.read().decode().strip())

c.close()
print("\nDone!")
