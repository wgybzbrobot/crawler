# 网络爬虫

crawler redis maven

---


[TOC]
## 1. 爬虫设计
！[爬虫的设计图]()
### 1.1 任务管理设计
用Redis zset 创建任务队列，评分：
> score = 1.0 / (currentTime + fetchInterval)

fetchInterval越小score越大，获取评分最高的任务作为下一个任务。

### 1.2 任务分配设计
爬虫由一个Master和多个Slave组成，Master分发任务给Slave, 并监测Slave运行状态。
Master根据Slave的正在执行任务个数评分：
> score = 1.0 / (1.0 + runningCount)

分配任务给score最大的Slave。
任务参数：

字段      | 含义    |示例
----------|---------|-------
site    |网站地址   |http://www.sina.com.cn
url     |版块地址   |http://roll.news.sina.com.cn/s/channel.php
jobType |任务类型   |NETWORK_INSPECT或者NETWORK_SEARCH
fetchInterval   |抓取时间间隔 |单位分钟, 用于网络巡检NETWORK_INSPECT

## 2. 开发

 1. 任务类型分为网络巡检、全网搜索，每个任务类型有对应的抓取方式； 新增任务需要新添加抓取方式。
 2. 每个网站有多个版块，版块类型分为论坛、新闻资讯、博客、微博等，每种类型有对应的解析器；新增类型需要添加新的解析器。
 3. 爬虫解析网页内容后存入ES索引接口，若写入失败则写入数据库mysql

## 3. 安装与配置

### 3.1 安装mysql
Mysql存储爬虫抓取网页的规则
配置Mysql编码是UTF-8, 防止乱码
### 3.2 安装redis
Redis用于存储URL
### 3.3 安装爬虫



## 4. 打包发布
打包(http://www.infoq.com/cn/news/2011/06/xxb-maven-9-package)





