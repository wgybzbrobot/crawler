#开发

## 任务
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

**如何添加新的任务类别?**
1. 继承类`CrawlTool`.
2. 定义自己的抓取流程控制类*Controller, 任务类调用Controller.
3. 在类`RAMJobManager`中typeToClass添加类类型定义.

##解析器
  
  解析器定义在配置文件`parse-plugins.xml`
**已定义解析器:**

- ForumParser  
- TieBaParser  
- NewsParser  
- BlogParser  
  
**如何添加新的解析器?**
1. 继承Parser类, 利用ConfDao读取规则, 抽取数据后利用RestOutput将数据写出.
2. 在 `parse-plugins.xml` 中添加解析类配置



