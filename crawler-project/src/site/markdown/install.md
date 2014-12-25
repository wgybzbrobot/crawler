#安装与配置

## 安装mysql

**用途:** Mysql存储爬虫抓取网页的规则.

1. 下载[Mysql](http://dev.mysql.com/downloads/mysql/)，配置Mysql编码是UTF-8, 防止中文乱码.

2. 创建数据库`crawler`

3. 创建表, 下载脚本

	- [表website](./crawler_website.sql)
	- [表section](./crawler_section.sql)
	- [表conf_list](./crawler_conf_list.sql)
	- [表conf_detail](./crawler_conf_detail.sql)
	- [表account](./crawler_account.sql)

4. 创建用户，并授权用户访问数据库`crawler`.


## 安装redis
用途:Redis用于构建URL任务队列。
下载[redis](http://redis.io/download)。
设置Redis开机启动。

## 配置爬虫WEB-UI
用途：WEB-UI(crawler-web)用于管理爬虫。


1. 修改配置mysql数据库的地址

 ```bash   
 $ cd webapps/crawler-web/WEB-INF/classes/spring/
 $ vim db.properties
 ```

2. 修改配置主控master地址

```bash
$ cd webapps/crawler-web/WEB-INF/classes/spring
$ vim master.properties
```

## 配置 crawler-slave

```bash
$ cd crawler-slave-${project.version}
$ vim conf/db.properties 	# 配置mysql地址
$ vim conf/output.properties# 配置索引地址
$ vim conf/log4j.properties # 配置日志
$ sh bin/slave.sh start  # 启动Slave, 默认port是8989
```

## 配置 Crawler-Master

```bash
$ cd crawler-master-${project.version}
$ vim conf/slaves.ini   # 配置slave,按格式填写slave,id是唯一的
$ vim conf/master.properties # 配置redis地址等信息
$ vim conf/log4j.properties # 配置日志, 运行稳定时将root > level debug换成info
$ sh bin/master.sh   # 启动Master, 默认port是9999
```

## 部署

**机器要求:**
1. 爬虫运行机器操作启动是linux, 支持ssh登陆
2. 主控和Web部署在一台机器, 内存大于2G.
3. Slave机器内存大于1G, 若小于1G, 需要调整爬虫启动脚本中的java运行环境
3. 操作机器是linux, 安装python版本>2.7, 安装fabic
4. 下载python脚本
	-  [Master部署脚本](./master.py)
	-  [Slave部署脚本](./slave.py)
	-  [WebUI部署脚本](./webui.py)
	
这里提供一个部署例子.

部署到机器192.168.3.21/22/23.









