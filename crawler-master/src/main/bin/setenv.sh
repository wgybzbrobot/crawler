# !/bin/bash
#
# set enviroment for MASTER 
JAVA=java

port="-port=9999"
redis_host=" -redis_host=192.168.32.72"
enable_search=" -enableSearch"
# when enable_search is not empty, set oracle
oracle="-oracle_url=jdbc:oracle:thin:@192.168.32.200:1521:yqjk -oracle_username=yqjk -oracle_passwd=yqjk#OLAP#2014"
      

  
JAVA_OPTS="-Xms512m -Xmx1024m  -server -XX:PermSize=64M -XX:MaxPermSize=256m"

MASTER_LOG_DIR=~/masterlog
MASTER_LOG_FILE=master.log

MASTER_OPTS="$MASTER_OPTS -Dmaster.log.dir=$MASTER_LOG_DIR"
MASTER_OPTS="$MASTER_OPTS -Dmaster.log.file=$MASTER_LOGFILE"