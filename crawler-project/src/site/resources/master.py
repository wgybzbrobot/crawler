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
# master
###################
def ls():
    run('ls crawler-master/conf')

def push():
    code_dir='~'
    with cd(code_dir):
        put('crawler-master.tar.gz', '')
        run('tar -zxf crawler-master.tar.gz')
        run('rm crawler-master.tar.gz')

def start():
    #run('cd ~/crawler-master-1.0.0/bin && sh start.sh', pty=True)
    with cd('~/crawler-master/'):
        run("nohup sh bin/master.sh start >& /dev/null < /dev/null &", pty=False)

def stop():
    with cd('~/crawler-master/'):
        run("sh bin/master.sh stop")

def delete():
    run("rm -r crawler-master")
