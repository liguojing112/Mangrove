import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode()

# Fix: Add ^~ to /uploads/ location so it takes priority over regex locations
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

print("=== Test config ===")
print(run("nginx -t"))

print("=== Reload ===")
print(run("systemctl reload nginx"))

print("\n=== Test upload access ===")
import time
time.sleep(1)
_, o, _ = c.exec_command("curl -sk -o /dev/null -w '%{http_code} %{size_download}' https://127.0.0.1/uploads/42c25936-9488-4f8d-a98e-b6935ee4f13a.jpeg", timeout=15)
print(o.read().decode().strip())

c.close()
print("\nDone! Photos should now display correctly.")
