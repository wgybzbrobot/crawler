from fabric.api import env, parallel, task, run, put, get, cd

env.hosts=['192.168.3.21',
        '192.168.3.22',
        '192.168.3.23',
        '192.168.25.31',
        '192.168.25.37',
        '192.168.25.71',
        '192.168.25.72']
env.user='user2'
env.password='fljWO2AZfOtVocSHmJo3IA=='

@task
@parallel
def pushJdk(file):
	with cd('~'):
		put(file,'')
		run('tar -zxf %s' % (file))
		run('rm %s' % (file))

@task
@parallel
def configJdk():
	with cd('~'):
		run('source .bashrc')
		run('echo export PATH=~/jdk1.7.0_71/bin:$PATH >> .bashrc')
	
###################
# slave
###################

@task
@parallel
def push(file='crawler-slave.tar.gz'):
    code_dir='~'
    with cd(code_dir):
        put(file, '')
        run('tar -zxf crawler-slave.tar.gz')
        run('rm crawler-slave.tar.gz')

@task
@parallel
def start():
    with cd('~'):
        run("nohup sh crawler-slave/bin/slave.sh start >& /dev/null < /dev/null &", pty=False)

@task
@parallel
def restart():
    with cd('~'):
        run("nohup sh crawler-slave/bin/slave.sh restart >& /dev/null < /dev/null &", pty=False)

@task
@parallel
def stop():
    with cd('~/crawler-slave/'):
        run('sh bin/slave.sh stop')

@task
@parallel
def delete():
    stop()
    run('rm -r crawler-slave')

## backup log file


## restore log file
