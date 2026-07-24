import paramiko

c = paramiko.SSHClient()
c.set_missing_host_key_policy(paramiko.AutoAddPolicy())
c.connect("43.155.249.14", username="root", password="Sukidayou01.", timeout=30)

def run(cmd):
    _, o, e = c.exec_command(cmd, timeout=30)
    return o.read().decode() + e.read().decode()

print(run("mariadb -u root -proot123 mangrove -e \"UPDATE sys_user SET bio='超级管理员' WHERE username='superadmin';\""))

print(run("mariadb -u root -proot123 mangrove -e \"SELECT id, username, nickname, bio FROM sys_user;\""))
c.close()
