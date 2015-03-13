#!/bin/bash
#
CLASS=com.zxsoft.crawler.api.SlaveServer         
                                                       
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


JAVA_OPTS="-Xms512m -Xmx1024m  -server -XX:PermSize=64M -XX:MaxPermSize=256m"

# init pid
pid=0

checkPid() {
    jps=`jps -l | grep $CLASS`

    if [ -n "$jps" ]; then
        pid=`echo $jps | awk '{print $1}'`
    else
        pid=0
    fi
}

PORT=8989
start() {
    checkPid

    if [ $pid -ne 0 ]; then
        echo "warn: $CLASS already started on port $PORT.(pid=$pid)"
    else
        if [ $# = 3 ]; then
            $PORT = $2
        fi
        echo "Starting $CLASS"
        `nohup java $JAVA_OPTS -classpath $CLASSPATH $CLASS $PORT enableSearchTask >/dev/null 2>&1 &`
       # exec $CMD
        checkPid
        if [ $pid -ne 0 ]; then
            echo "$CLASS is started on port $PORT (pid=$pid)[OK]"
        else
            echo "Start $CLASS failed"
        fi
    fi
}


stop() {
    checkPid

    if [ $pid -ne 0 ]; then
        echo  "Stopping $CLASS ... (pid=$pid)"
        exec kill -9 $pid
        checkPid
        if [ $pid -ne 0; ]; then
            echo "Stop failed."
        else
            echo "$CLASS stopped."
        fi
    else
        echo "$CLASS is not running."
    fi
}

status() {
    checkPid

    if [ $pid -ne 0 ]; then
        echo "$CLASS is running.(pid=$pid)"
    else
        echo "$CLASS is not running"
    fi
}


help() {
    echo "Usage: $0 {start [port] | stop | restart | help}"
    echo "$0 start [port]: the default port is $PORT"
    echo "$0 help: display this info."
}


if [ "$1" = "start" ]; then
    start
elif [ "$1" = "stop" ]; then
    stop
elif [ "$1" = "restart" ]; then
    stop
    start
elif [ "$1" = "status" ]; then
    status
elif [ "$1" = "--help" ]; then
    help
else
    help
fi
