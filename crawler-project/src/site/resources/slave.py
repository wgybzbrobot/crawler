from fabric.api import env
from fabric.api import run
from fabric.api import put
from fabric.api import get
from fabric.api import cd

env.hosts=['192.168.3.21',
        '192.168.3.22',
        '192.168.3.23']
env.user='user2'
env.password='fljWO2AZfOtVocSHmJo3IA=='


def uname():
    run('uname -a')

###################
# slave
###################
def ls():
    run('ls crawler-slave/conf')

def push():
    code_dir='~'
    with cd(code_dir):
        put('crawler-slave.tar.gz', '')
        run('tar -zxf crawler-slave.tar.gz')
        run('rm crawler-slave.tar.gz')
        run('cd crawler-slave/conf')
        run('ls')

def start():
    with cd('~/crawler-slave/'):
        run("nohup sh bin/slave.sh start >& /dev/null < /dev/null &", pty=False)

def stop():
    with cd('~/crawler-slave/'):
        run('sh bin/slave.sh stop')

def delete():
    run('rm -r crawler-slave')

## backup log file


## restore log file
