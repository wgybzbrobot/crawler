
# Cralwer Master

> **简介:**Crawler Master 是网络爬虫主控节点，负责分配任务给slave节点，并监控slave节点的运行状态。


## 如何安装配置？

> Master有三个配置文件，`master.properties`, `slave.ini`, `log4j.properties`.

1.master.properties

配置redis队列的地址

2.slaves.ini

配置slave节点的地址和描述

3.log4j.properties

配置日志

### MYSQL文件

> MySQL相关文件在src/test/resources/crawler.sql中，导入到crawler数据库中即可。

### Redis配置

> Redis配置为单台机器，启动端口为6379。

### 如何启动？

```bash
sh bin/master.sh start
```

> 注意：启动主控前先启动slave，貌似启动Master或这Slave都行？？

### 如何停止？

```bash
sh bin/master.sh stop
```





