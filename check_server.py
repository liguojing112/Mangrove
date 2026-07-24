import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=60)
    return o.read().decode() + e.read().decode()

print("=== OS ===")
print(run("cat /etc/os-release | head -5"))
print("=== Java ===")
print(run("java -version 2>&1 || echo 'no java'"))
print("=== apt-cache openjdk ===")
print(run("apt-cache search openjdk 2>/dev/null | head -10"))
print("=== MySQL/MariaDB ===")
print(run("mysql --version 2>&1 || echo 'no mysql'"))
print(run("apt-cache search mariadb-server 2>/dev/null | head -5"))
print(run("apt-cache search mysql-server 2>/dev/null | head -5"))
print("=== Nginx ===")
print(run("nginx -v 2>&1 || echo 'no nginx'"))
print(run("apt-cache search nginx 2>/dev/null | head -10"))
print("=== Node ===")
print(run("node --version 2>&1 || echo 'no node'"))
print("=== Nginx dir ===")
print(run("ls /etc/nginx/ 2>/dev/null || echo 'no nginx dir'"))
print("=== disk ===")
print(run("df -h / | tail -1"))

c.close()
