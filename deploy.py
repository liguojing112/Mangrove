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
    print("  芒果种植园 - 自动化部署脚本")
    print("  ⚠️  项目已完工，主要功能已禁用，防止误操作")
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

    # Step 1: 连接服务器（仅验证连通性）
    print("\n[1/8] 连接服务器...")
    client = ssh_connect()
    print(f"  已连接 {SERVER}")

    # Step 2: 安装系统依赖
    print("\n[2/8] 安装系统依赖... [已禁用]")
    # run(client, "apt-get update -qq", sudo=True)
    # run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq default-jdk-headless", sudo=True)
    # run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq mariadb-server", sudo=True)
    # run(client, "systemctl enable mariadb", sudo=True)
    # run(client, "systemctl start mariadb", sudo=True)
    # run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq nginx || true", sudo=True, ignore_error=True)
    # run(client, "DEBIAN_FRONTEND=noninteractive apt-get install -y -qq ffmpeg", sudo=True)
    # out, _, _ = run(client, "which nginx", ignore_error=True)
    # if not out.strip():
    #     print("  [INFO] nginx 不可用，使用 Python HTTP server 作为静态文件服务")
    # else:
    #     run(client, "systemctl enable nginx", sudo=True)
    #     run(client, "systemctl start nginx", sudo=True)
    print("  跳过（项目已完工）")

    # Step 3: 配置数据库
    print("\n[3/8] 配置数据库... [已禁用]")
    # run(client, "mariadb -e \"CREATE DATABASE IF NOT EXISTS mangrove CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;\"", sudo=True)
    # run(client, "mariadb -e \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'mangrove_db_pass'; FLUSH PRIVILEGES;\" 2>/dev/null || true", sudo=True)
    # remote_sql = "/tmp/mangrove_init.sql"
    # upload_file(client, SQL_FILE, remote_sql)
    # run(client, f"mariadb -u root mangrove < {remote_sql}", sudo=True, ignore_error=True)
    print("  跳过（项目已完工）")

    # Step 4: 上传项目文件
    print("\n[4/8] 上传项目文件... [已禁用]")
    # run(client, f"mkdir -p {REMOTE_APP_DIR}", sudo=True)
    # upload_file(client, JAR_PATH, f"{REMOTE_APP_DIR}/mangrove-1.0.0.jar")
    # upload_dir(client, FRONTEND_DIST, f"{REMOTE_APP_DIR}/frontend")
    # run(client, f"mkdir -p {REMOTE_APP_DIR}/uploads", sudo=True)
    print("  跳过（项目已完工）")

    # Step 5: 写入配置文件
    print("\n[5/8] 写入配置文件... [已禁用]")
    # app_yml = ...
    print("  跳过（项目已完工）")

    # Step 6: 创建 systemd 服务
    print("\n[6/8] 配置系统服务... [已禁用]")
    # run(client, "systemctl daemon-reload", sudo=True)
    # run(client, "systemctl enable mangrove", sudo=True)
    # run(client, "systemctl restart mangrove", sudo=True)
    print("  跳过（项目已完工）")

    # Step 7: 配置 Nginx
    print("\n[7/8] 配置 Web 服务器... [已禁用]")
    # run(client, "systemctl reload nginx", sudo=True)
    print("  跳过（项目已完工）")

    # Step 8: 验证
    print("\n[8/8] 验证部署...")
    time.sleep(2)
    out, _, _ = run(client, "systemctl is-active mangrove")
    print(f"  mangrove 服务: {out.strip()}")
    out, _, _ = run(client, "systemctl is-active nginx 2>/dev/null || echo unknown")
    print(f"  nginx 服务: {out.strip()}")

    client.close()
    print("\n" + "=" * 60)
    print("  ⚠️  项目已完工，部署脚本主要功能已禁用")
    print(f"  访问地址: https://candice0728.com")
    print(f"  管理后台: https://candice0728.com/admin")
    print("=" * 60)

if __name__ == "__main__":
    main()
