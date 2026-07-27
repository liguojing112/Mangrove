#!/usr/bin/env python3
"""芒果种植园自动化部署脚本 - Debian 13 (trixie)"""
import paramiko
import os
import sys
import time

SERVER = "43.155.249.14"
USER = "root"
PASSWORD = "Sukidayou01."
PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))
JAR_PATH = os.path.join(PROJECT_DIR, "target", "mangrove-1.0.0.jar")
FRONTEND_DIST = os.path.join(PROJECT_DIR, "frontend", "dist")
SQL_FILE = os.path.join(PROJECT_DIR, "docs", "mangrove-database.sql")
REMOTE_APP_DIR = "/opt/mangrove"

def ssh_connect():
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(SERVER, username=USER, password=PASSWORD, timeout=30)
    return client

def run(client, cmd, sudo=False, ignore_error=False):
    if sudo:
        cmd = f"echo '{PASSWORD}' | sudo -S bash -c '{cmd}'"
    stdin, stdout, stderr = client.exec_command(cmd, timeout=300)
    out = stdout.read().decode()
    err = stderr.read().decode()
    code = stdout.channel.recv_exit_status()
    if code != 0 and not ignore_error:
        print(f"  [WARN] {err.strip()[:200]}")
    return out, err, code

def upload_file(client, local_path, remote_path):
    sftp = client.open_sftp()
    sftp.put(local_path, remote_path)
    sftp.close()

def upload_dir(client, local_dir, remote_dir):
    sftp = client.open_sftp()
    for root, dirs, files in os.walk(local_dir):
        rel = os.path.relpath(root, local_dir)
        remote_base = os.path.join(remote_dir, rel).replace("\\", "/")
        try:
            sftp.mkdir(remote_base)
        except:
            pass
        for f in files:
            local_file = os.path.join(root, f)
            remote_file = os.path.join(remote_base, f).replace("\\", "/")
            sftp.put(local_file, remote_file)
    sftp.close()

def main():
    print("=" * 60)
    print("  芒果种植园 - 自动化部署")
    print("=" * 60)

    # Step 0: 检查本地文件
    print("\n[0/8] 检查构建产物...")
    if not os.path.exists(JAR_PATH):
        print(f"  ERROR: JAR 不存在: {JAR_PATH}")
        sys.exit(1)
    if not os.path.exists(os.path.join(FRONTEND_DIST, "index.html")):
        print(f"  ERROR: 前端 dist 不存在: {FRONTEND_DIST}")
        sys.exit(1)
    print(f"  JAR: {os.path.getsize(JAR_PATH) / 1024 / 1024:.1f} MB")
    print(f"  前端 dist: OK")

    # Step 1: 连接服务器
    print("\n[1/8] 连接服务器...")
    client = ssh_connect()
    print(f"  已连接 {SERVER}")

    # Step 2: 安装系统依赖
    print("\n[2/8] 安装系统依赖...")
    run(client, "apt-get update -qq", sudo=True)
    # Java - Debian 13 默认是 OpenJDK 21
    run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq default-jdk-headless", sudo=True)
    # MariaDB (Debian 13 的 MySQL 兼容方案)
    run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mariadb-server", sudo=True)
    run(client, "systemctl enable mariadb", sudo=True)
    run(client, "systemctl start mariadb", sudo=True)
    # Nginx
    run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq nginx || true", sudo=True, ignore_error=True)
    # FFmpeg：为短视频生成静态 JPG 封面，兼容 iOS 微信浏览器
    run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq ffmpeg", sudo=True)
    # 如果 nginx 装不上，用 python3 http server 作为临时方案
    out, _, _ = run(client, "which nginx", ignore_error=True)
    if not out.strip():
        print("  [INFO] nginx 不可用，使用 Python HTTP server 作为静态文件服务")
    else:
        run(client, "systemctl enable nginx", sudo=True)
        run(client, "systemctl start nginx", sudo=True)
    print("  系统依赖安装完成")

    # 验证 Java
    out, _, _ = run(client, "java -version 2>&1 | head -1")
    print(f"  Java: {out.strip()}")

    # Step 3: 配置数据库
    print("\n[3/8] 配置数据库...")
    run(client, "mariadb -e \"CREATE DATABASE IF NOT EXISTS mangrove CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\"", sudo=True)
    run(client, "mariadb -e \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'mangrove_db_pass'; FLUSH PRIVILEGES;\" 2>/dev/null || true", sudo=True)
    run(client, "mariadb -u root -pmangrove_db_pass -e \"SELECT 1;\" 2>/dev/null || mariadb -e \"SELECT 1;\"", sudo=True, ignore_error=True)
    # 上传并导入 SQL
    remote_sql = "/tmp/mangrove_init.sql"
    upload_file(client, SQL_FILE, remote_sql)
    run(client, f"mariadb -u root mangrove < {remote_sql}", sudo=True, ignore_error=True)
    run(client, f"rm -f {remote_sql}", sudo=True)
    print("  数据库 mangrove 已创建并导入")

    # Step 4: 创建应用目录并上传文件
    print("\n[4/8] 上传项目文件...")
    run(client, f"mkdir -p {REMOTE_APP_DIR}", sudo=True)
    upload_file(client, JAR_PATH, f"{REMOTE_APP_DIR}/mangrove-1.0.0.jar")
    run(client, f"mkdir -p {REMOTE_APP_DIR}/frontend", sudo=True)
    upload_dir(client, FRONTEND_DIST, f"{REMOTE_APP_DIR}/frontend")
    run(client, f"mkdir -p {REMOTE_APP_DIR}/uploads", sudo=True)
    run(client, f"chmod +x {REMOTE_APP_DIR}/mangrove-1.0.0.jar", sudo=True)
    print("  JAR + 前端文件已上传")

    # Step 5: 写入配置文件
    print("\n[5/8] 写入配置文件...")
    app_yml = """server:
  port: 8080

spring:
  application:
    name: mangrove
  datasource:
    url: jdbc:mysql://localhost:3306/mangrove?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: mangrove_db_pass
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
    sftp = client.open_sftp()
    with sftp.open(f"{REMOTE_APP_DIR}/application.yml", "w") as f:
        f.write(app_yml)
    sftp.close()
    print("  application.yml 已写入")

    # Step 6: 创建 systemd 服务
    print("\n[6/8] 配置系统服务...")
    service_unit = f"""[Unit]
Description=Mangrove Backend Service
After=network.target mariadb.service

[Service]
Type=simple
User=root
WorkingDirectory={REMOTE_APP_DIR}
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar {REMOTE_APP_DIR}/mangrove-1.0.0.jar --spring.config.location={REMOTE_APP_DIR}/application.yml
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
"""
    sftp = client.open_sftp()
    with sftp.open("/etc/systemd/system/mangrove.service", "w") as f:
        f.write(service_unit)
    sftp.close()
    run(client, "systemctl daemon-reload", sudo=True)
    run(client, "systemctl enable mangrove", sudo=True)
    run(client, "systemctl restart mangrove", sudo=True)
    print("  mangrove.service 已创建并启动")

    # Step 7: 配置 Nginx 或 Python HTTP Server
    print("\n[7/8] 配置 Web 服务器...")
    out, _, _ = run(client, "which nginx", ignore_error=True)
    if out.strip():
        # Nginx 可用
        nginx_conf = """server {
    listen 80;
    server_name candice0728.com www.candice0728.com;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl;
    server_name candice0728.com www.candice0728.com;

    ssl_certificate /etc/letsencrypt/live/candice0728.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/candice0728.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    root /opt/mangrove/frontend;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location ~* \\.html$ {
        add_header Cache-Control "no-cache, no-store, must-revalidate";
        add_header Pragma "no-cache";
        add_header Expires "0";
    }

    location ^~ /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
        proxy_connect_timeout 60s;
        proxy_read_timeout 300s;
        client_max_body_size 500m;
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
        sftp = client.open_sftp()
        try:
            sftp.mkdir("/etc/nginx/sites-available")
        except:
            pass
        with sftp.open("/etc/nginx/sites-available/mangrove", "w") as f:
            f.write(nginx_conf)
        sftp.close()
        run(client, "ln -sf /etc/nginx/sites-available/mangrove /etc/nginx/sites-enabled/mangrove", sudo=True)
        run(client, "rm -f /etc/nginx/sites-enabled/default", sudo=True, ignore_error=True)
        run(client, "nginx -t", sudo=True)
        run(client, "systemctl reload nginx", sudo=True)
        print("  Nginx 已配置并重载")
    else:
        # 使用 Python HTTP server 作为静态文件服务
        print("  使用 Python HTTP server 作为前端静态文件服务")
        # 创建一个简单的 Python HTTP server 脚本
        server_script = '''import http.server
import socketserver
import os
import urllib.request

PORT = 80
FRONTEND_DIR = "/opt/mangrove/frontend"
BACKEND_URL = "http://127.0.0.1:8080"

class ProxyHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=FRONTEND_DIR, **kwargs)

    def do_GET(self):
        # API 请求代理到后端
        if self.path.startswith("/api/"):
            self.proxy_request("GET")
        else:
            # 静态文件，尝试本地文件，不存在则返回 index.html (SPA)
            file_path = os.path.join(FRONTEND_DIR, self.path.lstrip("/"))
            if os.path.isfile(file_path):
                super().do_GET()
            else:
                self.path = "/index.html"
                super().do_GET()

    def do_POST(self):
        if self.path.startswith("/api/"):
            self.proxy_request("POST")
        else:
            self.send_error(404)

    def do_PUT(self):
        if self.path.startswith("/api/"):
            self.proxy_request("PUT")
        else:
            self.send_error(404)

    def do_DELETE(self):
        if self.path.startswith("/api/"):
            self.proxy_request("DELETE")
        else:
            self.send_error(404)

    def proxy_request(self, method):
        url = BACKEND_URL + self.path
        content_length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(content_length) if content_length > 0 else None
        headers = {k: v for k, v in self.headers.items() if k.lower() not in ("host", "connection")}
        req = urllib.request.Request(url, data=body, headers=headers, method=method)
        try:
            with urllib.request.urlopen(req, timeout=120) as resp:
                self.send_response(resp.status)
                for k, v in resp.headers.items():
                    if k.lower() not in ("transfer-encoding", "connection"):
                        self.send_header(k, v)
                self.end_headers()
                self.wfile.write(resp.read())
        except Exception as e:
            self.send_response(502)
            self.end_headers()
            self.wfile.write(str(e).encode())

    def log_message(self, format, *args):
        pass

with socketserver.TCPServer(("0.0.0.0", PORT), ProxyHandler) as httpd:
    print(f"Serving on port {PORT}")
    httpd.serve_forever()
'''
        sftp = client.open_sftp()
        with sftp.open("/opt/mangrove/server.py", "w") as f:
            f.write(server_script)
        sftp.close()

        # 创建 systemd 服务 for Python server
        py_service = """[Unit]
Description=Mangrove Frontend Server
After=network.target

[Service]
Type=simple
User=root
ExecStart=/usr/bin/python3 /opt/mangrove/server.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
"""
        sftp = client.open_sftp()
        with sftp.open("/etc/systemd/system/mangrove-web.service", "w") as f:
            f.write(py_service)
        sftp.close()
        run(client, "systemctl daemon-reload", sudo=True)
        run(client, "systemctl enable mangrove-web", sudo=True)
        run(client, "systemctl restart mangrove-web", sudo=True)
        print("  Python HTTP server 已启动")

    # Step 8: 验证
    print("\n[8/8] 验证部署...")
    time.sleep(8)
    out, _, _ = run(client, "curl -s -o /dev/null -w '%{http_code}' http://localhost/")
    print(f"  前端状态码: {out.strip()}")
    out, _, _ = run(client, "curl -s -o /dev/null -w '%{http_code}' http://localhost/api/public/artists")
    print(f"  后端 API 状态码: {out.strip()}")
    out, _, _ = run(client, "systemctl is-active mangrove")
    print(f"  mangrove 服务: {out.strip()}")
    out, _, _ = run(client, "systemctl is-active mangrove-web 2>/dev/null || systemctl is-active nginx 2>/dev/null || echo unknown")
    print(f"  Web 服务: {out.strip()}")

    client.close()
    print("\n" + "=" * 60)
    print("  部署完成！")
    print(f"  访问地址: http://{SERVER}")
    print(f"  管理后台: http://{SERVER}/admin")
    print("=" * 60)

if __name__ == "__main__":
    main()
