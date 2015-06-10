# 网络舆情爬虫

**Crawler**是一个网络與情爬虫，其核心是利用`httpclient`和`htmlunit`下载网页，并利用`jsoup`解析网页。

Crawler包含一个`Master`、多个`Slave`，以及一个`WEB-UI`爬虫管理。

用户通过WEB-UI发送任务给Master，然后由Master分配任务给Slave， Slave进行抓取。
WEB-UI、Master、Slave都提供了创建任务的接口， 目前创建巡检任务只能调用WEB-UI
提供的接口创建。



目前支持网络巡检任务, 全网搜索任务, 具体见各个版块说明.

Crawler项目由Maven多模块构建,包含

- crawler-master,          master 节点
- crawler-slave,               slave节点
- crawler-web,                  爬虫规则配置及任务管理
- crawler-common,         Crawler通用模块
- crawler-protocol, 网络访问模块, 抓取网页, 包含httpclient和htmlunit
- crawler-dao,                   Crawler数据模块, 获取
- crawler-parse, 网页解析,利用jsoup
- crawler-assemblies, 打包
