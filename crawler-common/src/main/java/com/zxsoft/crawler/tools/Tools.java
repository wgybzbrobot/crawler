package com.zxsoft.crawler.tools;

import com.zxsoft.carson.dao.ConfService;
import com.zxsoft.carson.dao.InfoService;
import com.zxsoft.carson.redis.RedisService;

/**
 * 服务工具集合
 */
public class Tools {

    private RedisService redisSerivice;
    private ConfService domService;
    private InfoService infoService;

    public Tools() {
    }

    public Tools(RedisService redisSerivice, ConfService domService, InfoService infoService) {
        super();
        this.redisSerivice = redisSerivice;
        this.domService = domService;
        this.infoService = infoService;
    }

    public RedisService getRedisSerivice() {
        return redisSerivice;
    }

    public void setRedisSerivice(RedisService redisSerivice) {
        this.redisSerivice = redisSerivice;
    }

    public ConfService getDomService() {
        return domService;
    }

    public void setDomService(ConfService domService) {
        this.domService = domService;
    }

    public InfoService getInfoService() {
        return infoService;
    }

    public void setInfoService(InfoService infoService) {
        this.infoService = infoService;
    }

}
