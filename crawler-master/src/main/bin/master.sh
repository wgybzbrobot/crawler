#!/bin/bash
#
CLASS=com.zxsoft.crawler.master.MasterServer       

# resovle links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done

# Get standard enviroment variables
PRGDIR=`dirname "$PRG"`
[ -z "$PRG_HOME" ] && PRG_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`
# Ensure that any user defined CLASSPATH variables are not used onstartup,
# but allow them to be specified in setenv.sh, in rare case when it is neede.
CLASSPATH=

if [ -r "$PRG_HOME/bin/setenv.sh" ]; then
        . "$PRG_HOME/bin/setenv.sh"
fi

CLASSPATH=$(echo "$PRG_HOME"/lib/*.jar | tr ' ' ':')
CLASSPATH="${CLASSPATH}":"${PRG_HOME}/conf"

if [ -z $PIDFILE ]
then PIDFILE=/tmp/master_server.pid
fi
    
MASTER_DAEMON_OUT="$MASTER_LOG_DIR/master.out"

case $1 in
    start)
        echo -n "Starting master ... "
        if [ -f $PIDFILE ]; then
                if kill -0 `cat $PIDFILE` > /dev/null 2>&1; then
                        echo $command already running as process `cat $PIDFILE`.
                        exit 0
                fi
        fi
        
        nohup $JAVA "-Dmaster.log.dir=${MASTER_LOG_DIR}" "-Dmaster.log.file=${MASTER_LOG_FILE}" \
         $JAVA_OPTS -cp $CLASSPATH $CLASS $port $enable_search > /dev/null 2>&1 &
        
        if [ $? -eq 0 ]; then
                echo $!
                if  echo -n $! > "$PIDFILE"  
                then
                        sleep 1
                        echo STARTED
                else 
                        echo FAILED TO WRITE PID
                        exit 1
                fi
        else
                echo SERVER DID NOT START
                exit 1
        fi
        ;;
    stop)
        echo -n "Stopping worker ... "
        if [ ! -f "$PIDFILE" ]
        then
                echo "no master to stop (could not find file $PIDFILE)"
        else
                echo $PIDFILE
                kill -9 $(cat "$PIDFILE")
                rm "$PIDFILE"
                echo STOPPED
        fi
        ;;
    restart)
        shift
        ./"$0" stop ${@}
        sleep 3
        ./"$0" start ${@}
        ;;
    status)
        if [ ! -f "$PIDFILE" ]
        then
                echo "no master is running."
        else
                echo "master is running (pid=$PIDFILE)"
        fi
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}" >&2
esac


