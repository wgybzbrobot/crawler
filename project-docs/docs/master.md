# Crawler Master

## 简介
  Cralwer Master从任务队列Redis中获取任务，根据评分分配任务给Slave进行抓取； 并监测Slave的运行状态。

![结构图](img/master.png)

## 任务分配

  Master分发任务给Slave, 并监测Slave运行状态。
  Master根据Slave的正在执行任务个数评分：

>score=1.0/(1.0+runningCount)


  分母单位是minute，不是millisecond, 否则score排序不正确。
  Master分配任务给score最大的Slave。

## 任务队列
用Redis zset 创建任务队列，评分：
> score=1.0/(currentTime+fetchInterval)

分母单位是minute，不是millisecond, 否则score排序不正确。
fetchInterval越小score越大，获取评分最高的任务作为下一个任务。

## REST 接口API说明

### 创建任务接口
1. URL: http://192.168.3.21:9999/master/slaves
2. 返回格式: json
3. HTTP请求方式: PUT
4. 请求参数
    4.1 创建网络巡检任务

 参数           | 必选   | 类型   | 说明       
 -------------|---------|---------|----------                     
 jobType       | true  | string| 任务类型, 值是NETWORK_INSPECT    
  url         | true  |string | 版块地址，必须是数据库中已存在 
prevFetchTime  | false | long  |上次抓取时间                  
comment        | false | string| 版块名称，用于任务抓取日志记录 

    
    示例:
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
-----------|---------|----------|---------                   
jobType        | true  | string| 任务类型, 值是NETWORK_SEARCH     
keyword        | true  |string | 版块地址，必须是数据库中已存在 
engineUrl      | true  | string|搜索引擎地址模板                

示例
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
    "slave":"192.168.3.23:8989",
    "msg":"success choose slave."
}
```


6. 返回字段说明

返回值字段 |字段类型 | 字段说明  
------------|---------------|----------
code         | int      | 代码        
slave        | string   |选择的slave
msg      | string   | 返回消息  

## 配置文件
文件名             | 说明                        | 文件在项目中的位置
----------              |------------                   |-------------
mysql.properties   | mysql数据库配置    |   mysql.properties
oracle.properties   | 全网搜索任务oracle数据库配置    |   oracle.properties
slaves.ini                  | slave节点位置   | slave.ini
redis.properties        | redis任务队列配置   |
log4j.xml           | 日志配置                  | log4j.xml

## Undo Task List