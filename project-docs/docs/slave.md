# Crawler Slave

![结构图](img/slave.png)

##任务管理设计

### 任务分类
任务类型分为网络巡检(`NETWOR_INSPECT`)、全网搜索(`NETWORK_SEARCH`)，
这两种任务类型的抓取控制分别定义为`NetworkInspectParserController`、`NetworkSearchParserController`。

###网络巡检

###全网搜索
Master启动后
1. 读取视图`SELECT_TASK_EXECUTE_LIST`（读取一次），全部读取
2. 读取任务列表`JHRW_RWLB`（循环读取），

[Master创建全网搜索任务接口定义](crawler-master/interface.html)

Slave启动后，将任务执行表`JHRW_RWZX`中机器号为自身的且ZT字段置为２，ZSZT置为１．（执行一次）

分发任务给Slave，Slave接受到任务后，
1. 将任务列表`JHRW_RWLB`中对应任务记录删除
2. 将任务执行表`JHRW_RWZX`中对应任务记录的机器号字段置为本机器

Slave执行完任务后，
1. 如果执行成功，将任务执行表`JHRW_RWZX`中对应任务记录ZT字段置为２，ZSZT置为２.
2.  如果执行失败，将任务执行表`JHRW_RWZX`中对应任务记录ZT字段置为２，ZSZT置为1.


##规则及解析
#### 规则
爬虫是从网站的一个版块开始抓取，版块呈现的是一个列表页面，列表页面中的链接是详细页面。
规则就是分为列表页面规则和详细页面规则， 分别配置在数据库表`conf_list`和`conf_detail`。
规则包含以下信息：

1. 规则中定义抽取已下载的网页中包含的数据，如标题、内容、时间、作者等。
2. 爬虫根据规则中定义的更新时间和上次抓取时间进行比较，判断是否继续抓取；
    若版块没有更新时间，则爬虫抓取两页（可配置）后停止抓取。
3. 定义版块所属类别，如论坛、新闻咨询，爬虫通过类别用对应的解析器解析网页。

#### 解析
爬虫根据目标版块的类别进行解析，目前支持论坛、新闻资讯、百度贴吧等解析器。
若新增解析器, 则需继承`com.zxsoft.crawler.parse.Parser`类, 并在`parse-plugin.xml`中配置新增的解析器类地址， 如

```xml
<plugin>
    <type>forum</type>
    <class>com.zxsoft.crawler.plugin.parse.ForumParser</class>
    <description>论坛解析器</description>
</plugin>
```

解析器需调用Output.write写出数据

**注：**版块类型是mysql数据库配置表category中的字段

##  Slave节点REST API说明
###创建任务接口
1. URL: http://192.168.3.23:8989/slave/jobs
2. 返回格式: json
3. HTTP请求方式PUT
4. 请求参数
    4.1 创建网络巡检任务

参数 1    |   扩展参数       | 必选   | 类型   |说明                            
------------|-----------------|----------|---------|----------------
jobType  |                    | true  | string| 任务类型, 包括NETWORK_INSPECT, NETWORK_SEARCH, NETWORK_FOCUS
args        |                     | true  | map | 包含以下参数
                |url                     | true  |string | 版块地址，必须是数据库中已存在         
                |comment        | false | string| 版块名称，用于任务抓取日志记录 
                |prevFetchTime  | false | long  |上次抓取时间, 毫秒       
                |source_id          | true    | String  | 网站id
                |country_code    | true  | int       | 境内(1)境外(0);若为境外, 则以下provinceId, cityId不用填写, 否则需要.
                |province_code      | true  |   int     | 省份代码, 如果没有则为0
                |city_code               | true |   int     | 城市代码, 如果没有则为0
                | server_id             | true  | int       | 前置机代码
                | source_type         | true    | int       | 1-全网搜索，2-网络巡检，3-重点关注


JSON示例

```json
{
    "jobType":"NETWORK_INSPECT",
    "args":{
        "prevFetchTime":1417830912810,
        "comment":"新浪新闻滚动",
        "url":"http://roll.news.sina.com.cn/s/channel.php"
    }
}
```

4.2 创建全网搜索任务

  参数      |必选    | 类型   |说明                             
---------------|-------|-------|--------------------------------
jobType        | true  | string| 任务类型, 值是NETWORK_SEARCH     
keyword        | true  |string | 版块地址，必须是数据库中已存在 
engineUrl      | true  | string|搜索引擎地址模板                

JSON示例
```json
{
    "jobType":"NETWORK_SEARCH",
    "args": {
        "keyword":"我要爆料 ",
        "engineUrl":"http://www.baidu.com/s?wd=%s&ie=utf-8"
    }
}
```

5. 返回结果JSON示例
```json
{
    "code":"22",
    "msg":"create job success."
}
```

6. 返回字段说明

返回值字段 |字段类型  | 字段说明 
------------|----------|-----------
code        | int      | 代码        
  msg      | string   | 返回消息  


##开发

#### 增加新的任务
任务定义在类`RAMJobManager`,

```java
    static {
        typeToClass.put(JobType.NETWORK_INSPECT, NetworkInspectJob.class);
        typeToClass.put(JobType.NETWORK_SEARCH, NetworkSearchJob.class);
    }
```

**已定义任务:**

- 网络巡检 `NETWORKINSPECTJOB`, 调用`NetworkInpectParserController`控制抓取流程
- 全网搜索 `NETWORKSEARCHJOB`, 调用`NetworkSearchParserController`控制抓取流程
- 重点关注 'FOCUSJOB'

**如何添加新的任务类别?**
1. 继承类`CrawlTool`.
2. 定义自己的抓取流程控制类*Controller, 任务类调用Controller.
3. 在类`RAMJobManager`中typeToClass添加类类型定义.

###解析器
  
  解析器定义在配置文件`parse-plugins.xml`
**已定义解析器:**

- ForumParser  
- TieBaParser  
- NewsParser  
- BlogParser  
  
####添加新的解析器

1. 继承Parser类, 利用ConfDao读取规则, 抽取数据后利用RestOutput将数据写出.
2. 在 `parse-plugins.xml` 中添加解析类配置

## 配置文件
文件名             | 说明                        | 文件在项目中的位置
----------              |------------                   |-------------
db.properties   | 规则mysql数据库配置    |   db.properties
master.properties   | master节点的位置   | master.properties
protocol.properties | 网络配置, 如代理     |
restoutput.properties        | 索引服务地址   |
log4j.xml           | 日志配置                  | log4j.xml
cache-sentiment.properties  | 索引服务队列地址  | 
oracle.properties                       |  全网搜索任务       |


##Undo Task List

- [x] 详细页ajax处理
- [x] 获取 字段  "location": "上海市 电信张江机房",
        "source_name": "搜狐新闻搜索",
        "source_type": 1,       "source_id": 925,  "server_id": 3174
- [ ] 重点关注 'FOCUSJOB' 未完成
- [ ] htmlunit性能优化
- [ ] 提供登陆功能，如西祠胡同
