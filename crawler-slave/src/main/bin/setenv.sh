#!/bin/bash
#
# set enviroment for worker 
JAVA=java

port="-port=8989"  
master="-master=192.168.32.71:9999"

enable_search=" -enableSearch"
# when enable_search is not empty, set oracle
oracle="-oracle_url=jdbc:oracle:thin:@192.168.32.200:1521:yqjk -oracle_username=yqjk -oracle_passwd=yqjk#OLAP#2014"
       
JAVA_OPTS="-Xms512m -Xmx1536m  -server -XX:PermSize=64M -XX:MaxPermSize=256m"

WORKER_LOG_DIR=~/workerlog
WORKER_LOG_FILE=worker.log

WORKER_OPTS="$WORKER_OPTS -Dworker.log.dir=$WORKER_LOG_DIR"
WORKER_OPTS="$WORKER_OPTS -Dworker.log.file=$WORKER_LOG_FILE"