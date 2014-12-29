from fabric.api import env, parallel, task, run, put, get, cd

env.hosts=['192.168.3.21',
        '192.168.3.22',
        '192.168.3.23']
env.user='user2'
env.password='fljWO2AZfOtVocSHmJo3IA=='

###################
# slave
###################

@task
@parallel
def push():
    code_dir='~'
    with cd(code_dir):
        put('crawler-slave.tar.gz', '')
        run('tar -zxf crawler-slave.tar.gz')
        run('rm crawler-slave.tar.gz')

@task
@parallel
def start():
    with cd('~'):
        run("nohup sh crawler-slave/bin/slave.sh start >& /dev/null < /dev/null &", pty=False)

@task
@parallel
def stop():
    with cd('~/crawler-slave/'):
        run('sh bin/slave.sh stop')

@task
@parallel
def delete():
    run('rm -r crawler-slave')

## backup log file


## restore log file
