#任务管理设计


## 任务分类
任务类型分为网络巡检(`NETWOR_INSPECT`)、全网搜索(`NETWORK_SEARCH`)，
这两种任务类型的抓取控制分别定义为`NetworkInspectParserController`、`NetworkSearchParserController`。

## 网络巡检


## 全网搜索
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





## 任务队列
用Redis zset 创建任务队列，评分：
> score=1.0/(currentTime+fetchInterval)

分母单位是minute，不是millisecond, 否则score排序不正确。
fetchInterval越小score越大，获取评分最高的任务作为下一个任务。

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

解析器需调用RestOutput.write写出数据

**注：**版块类型是mysql数据库配置表category中的字段