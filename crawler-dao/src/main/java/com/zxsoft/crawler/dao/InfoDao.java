package com.zxsoft.crawler.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.zxsoft.carson.pojo.Forum;
import com.zxsoft.carson.pojo.News;
import com.zxsoft.carson.pojo.Reply;
import com.zxsoft.carson.pojo.Seed;

/**
 * 操作数据表
 */
public interface InfoDao {

    @Select("SELECT * FROM seed WHERE remain = 0 limit 1")
    Seed getSeed();
    
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
    
/*    @Insert("INSERT INTO seed (url, remain, type) VALUES (#{url}, #{remain}, #{type})")
    void addSeed(List<Seed> seeds);*/
    
    @Insert("INSERT INTO forum (url, title, releasedate, content, author, replyNum, reviewNum, imgUrl, audioUrl, videoUrl) "
            + " VALUES (#{url}, #{title}, #{releasedate}, #{content}, #{author}, #{replyNum}, #{reviewNum}, #{imgUrl}, #{audioUrl}, #{videoUrl})")
    void addForum(Forum forum);
    
    @Select("SELECT * FROM forum WHERE url = #{url}")
    Forum getForum(@Param("url") String url);
    
    @Update("UPDATE forum set replyNum=#{replyNum}, reviewNum=#{reviewNum} where url=#{url}")
    void updateForum(Forum forum);

    @Insert("INSERT INTO reply (id, parentId, mainUrl, authorAccount, content, videoUrl, imgUrl, audioUrl, title, releasedate, address, md5, currentUrl) "
            + "VALUES (#{id}, #{parentId}, #{mainUrl}, #{authorAccount}, #{content}, #{videoUrl}, #{imgUrl}, #{audioUrl}, #{title}, #{releasedate}, #{address}, #{md5}, #{currentUrl})")
    void addReply(Reply reply);

    @Insert("INSERT INTO news (url, title, content, sources, img, audio, video, author, releaseDate, fetchDate, replyNum, forwardNum, reviewNum) "
            + " VALUES (#{url}, #{title}, #{content}, #{sources}, #{img}, #{audio}, #{video}, #{author}, #{releaseDate}, #{fetchDate}, #{replyNum}, #{forwardNum}, #{reviewNum})")
    void addNews(News news);
    

    
}
