package com.zxsoft.carson.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
public class RedisService {

    private static final String MD5 = "md5";
    public void addMd5(String md5) {
        redisTemplate.opsForSet().add(MD5, md5);
    }
    public boolean md5Exist(String md5) {
        return redisTemplate.opsForSet().isMember(MD5, md5);
    }
    
    // 判度帖子是否变动或新闻是否已抓取
    private static final String JUDGE_MD5 = "judgemd5";
    public void addJudgeMd5(String md5) {
    	redisTemplate.opsForSet().add(JUDGE_MD5, md5);
    }
    public boolean judgeMd5Exist(String md5) {
    	return redisTemplate.opsForSet().isMember(JUDGE_MD5, md5);
    }
    

	private StringRedisTemplate redisTemplate;
    /*
     * getter, setter, constructor
     */
    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisService() {
    }

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
