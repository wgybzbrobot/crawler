package com.zxsoft.crawler.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.zxsoft.carson.pojo.ListConf;
import com.zxsoft.carson.seed.ForumDetailConf;
import com.zxsoft.carson.seed.NewsDetailConf;

public interface ConfDao {

    /**
     * 方案1:需要将返回值改为map, 再根据type决定生成dom类型
     * 方案2: 添加接口方法,返回对应类型
     * 方案3: 将配置文件中的type类型对应表明,每次添加解析插件时,在该接口中
     * 添加相应方法,依次调用方法,返回有值时,设置缓存
     * @param url
     * @return
     */
	@Select("SELECT * FROM forumconf_detail WHERE host = #{host}")
	ForumDetailConf getForumDetailConf(@Param("host") String host);
	
	/**
	 * 获取新闻资讯列表页配置，包含种子
	 */
	@Select("SELECT * FROM conf_list WHERE url = #{url}")
	ListConf getListConf(@Param("url") String url);
	
	/**
	 * 获取列表页配置，包含种子
	 */
	@Select("SELECT * FROM conf_list")
	List<ListConf> getListConfs();
	
	/**
     * 获取新闻资讯详细页配置
     */
	@Select("SELECT * FROM newsconf_detail WHERE host = #{host}")
	NewsDetailConf getNewsDetailConf(@Param("host") String host);
	
	/*@Select("SELECT * FROM newsdom WHERE host = #{host}")
	NewsDom getNewsDom(@Param("host") String host);
	
	@Select("SELECT * FROM newsdom where url != ''")
	List<NewsDom> getNewsDoms();*/
}
