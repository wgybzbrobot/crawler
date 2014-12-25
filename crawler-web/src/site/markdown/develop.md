#开发

## Crawler Web架构
Crawler Web 是基于Spring MVC开发的J2EE架构, 若对Spring MVC不了解, 请阅读<<Spring In Action>>.
DAO层是利用hibernate处理的,hibernate是对象关系映射框架. 
由于在开发初期, 没有考虑到将列表页和详细页归结到一个网站类,如百度类, 所以列表页表`conf_list`和详细页表`conf_detial`没有设计成和表`website`, 
表`section`有关联关系. 这样带来的不利是操作表`website`或表`section`时不能影响表`conf_list`,`conf_detail`,
需要手动操作表`conf_list`,`conf_detail`.

修改办法: 在表`conf_list`和`conf_detail`添加id主键. 不过影响爬虫如何获取到规则,影响较大.



##如何快速开发?

1. Debug某个功能点
2. 看crawler web提供了哪些请求映射
在eclipse sts编辑器中,鼠标放在crawler-web工程中,右击选择spring tools --> request mapping, 即刻查看哪些映射.

