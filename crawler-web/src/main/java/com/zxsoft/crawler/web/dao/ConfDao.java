package com.zxsoft.crawler.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.web.model.ForumDetailConf;
import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.model.NewsDetailConf;
import com.zxsoft.crawler.web.model.Seed;

@Repository
public interface ConfDao {

	@Insert("INSERT INTO conf_list (comment, url, category, ajax, fetchinterval, pageNum,"
			+ "filterurl, listdom, linedom, urldom, datedom, updatedatedom) VALUES (#{comment},"
			+ "#{url}, #{category}, #{ajax}, #{fetchinterval}, #{pageNum}, #{filterurl},"
			+ "#{listdom}, #{linedom}, #{urldom}, #{datedom},#{updatedatedom})")
	void saveListConf(ListConf listConf);
	
	@Delete("DELETE FROM conf_list WHERE url = #{url}")
	void delListConf(ListConf listConf);
    
	@Insert("INSERT INTO forumconf_detail (host, replyNum, reviewNum, forwardNum, fetchorder,"
			+ "master, masterAuthor, masterDate, masterContent, reply, replyAuthor, replyDate,"
			+ "replyContent, subReply, subReplyAuthor, subReplyDate, subReplyContent)"
			+ " VALUES (#{host}, #{replyNum}, #{reviewNum}, #{forwardNum}, #{fetchorder},"
			+ "#{master}, #{masterAuthor}, #{masterDate}, #{masterContent}, #{reply}, #{replyAuthor},"
			+ "#{replyDate}, #{replyContent}, #{subReply}, #{subReplyAuthor}, #{subReplyDate}, "
			+ "#{subReplyContent})")
	void saveForumDetailConf(ForumDetailConf detailConf);
	
	@Delete("DELETE FROM forumconf_detail WHERE host = #{host}")
	void delForumDetailConf(ForumDetailConf detailConf);
	
	@Insert("INSERT INTO newsconf_detail (host, title, content, sources, author, releaseDate,"
			+ "replyNum, forwardNum, reviewNum) VALUES (#{host}, #{title}, #{content}, #{sources},"
			+ "#{author}, #{releaseDate}, #{replyNum}, #{forwardNum}, #{reviewNum})")
	void saveNewsDetailConf(NewsDetailConf detailConf);
	
	@Delete("DELETE FROM newsconf_detail WHERE host = #{host}")
	void delNewsDetailConf(NewsDetailConf detailConf);
	
	
	
	
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
	
	@Select("SELECT * FROM conf_list WHERE comment LIKE CONCAT('%',#{comment},'%') LIMIT #{pageNum}, #{pageSize}")
	List<ListConf> findListConfs(@Param("comment") String comment, @Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
	@Select("SELECT * FROM conf_list WHERE 1=1 limit #{pageNum}, #{pageSize}")
	List<ListConf> getListConfs(@Param("pageNum") int pageNum, @Param("pageSize") int pageSize);
	/**
     * 获取新闻资讯详细页配置
     */
	@Select("SELECT * FROM newsconf_detail WHERE host = #{host}")
	NewsDetailConf getNewsDetailConf(@Param("host") String host);
	
	/*@Select("SELECT * FROM newsdom WHERE host = #{host}")
	NewsDom getNewsDom(@Param("host") String host);
	
	@Select("SELECT * FROM newsdom where url != ''")
	List<NewsDom> getNewsDoms();*/
	
	@Select("SELECT * FROM seed WHERE url=#{url} limit 1")
    Seed getSeed(Seed seed);
    
    @Delete("DELETE FROM seed WHERE url = #{url}")
    void delSeed(Seed seed);
    
    @Delete("DELETE FROM seed")
    void delAllSeed();
    
//    @Update("UPDATE seed set remain = fetchinterval, lastfetchtime = (select now()) WHERE url = #{url}")
    @Update("UPDATE seed set remain = fetchinterval, lastfetchtime = #{lastfetchtime} WHERE url = #{url}")
    void updateSeed(Seed seed);
    								
    @Insert("INSERT INTO seed (url, indexUrl, fetchinterval, remain, type, lose, mainUrl, title, releasedate) " +
    		"VALUES (#{url}, #{indexUrl}, #{fetchinterval}, #{remain}, #{type}, #{lose}, #{mainUrl}, #{title}, #{releasedate})")
    void addSeed(Seed seed);
}
