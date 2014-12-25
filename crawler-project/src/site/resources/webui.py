from fabric.api import env
from fabric.api import run
from fabric.api import put
from fabric.api import get
from fabric.api import cd

env.hosts='192.168.3.21'
env.user='user2'
env.password='fljWO2AZfOtVocSHmJo3IA=='


def uname():
    run('uname -a')

###################
# web ui
###################
def ls():
    run('ls crawler-master-1.0.0/conf')

def pushTomcat():
    code_dir='~'
    with cd(code_dir):
        put('apache-tomcat-7.0.56.tar.gz', '')
        run('tar -zxf apache-tomcat-7.0.56.tar.gz')
        run('rm apache-tomcat-7.0.56.tar.gz')

def pushWebUI():
    code_dir='~'
    with cd(code_dir):
        put('crawler-web.war', '~/apache-tomcat-7.0.56/webapps/')

def startTomcat():
    with cd('~/apache-tomcat-7.0.56/'):
        run("nohup sh bin/startup.sh >& /dev/null < /dev/null &", pty=False)

## bakeup log file

## restore log file
