package com.zxsoft.carson.core;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.RedisConnectionFailureException;

import com.zxsoft.carson.dao.ConfService;
import com.zxsoft.carson.dao.InfoService;
import com.zxsoft.carson.parse.Category;
import com.zxsoft.carson.pojo.ListConf;
import com.zxsoft.carson.pojo.Seed;
import com.zxsoft.carson.redis.RedisService;
import com.zxsoft.carson.util.CarsonConfiguration;
import com.zxsoft.carson.util.Tool;
import com.zxsoft.carson.util.Tools;

public class Injector implements Tool {

	private static Logger LOG = LoggerFactory.getLogger(Injector.class);
	
    private Tools tools;
    
    /**
     * 初始化注入种子(都是列表页)
     */
    public void inject(List<ListConf> list) {
    	tools.getInfoService().delAllSeed();
        for (ListConf conf : list) {
        	Seed seed = new Seed(conf.getUrl(), Category.LIST_PAGE, conf.getUrl(), conf.getFetchinterval(), 0, true);
        	tools.getInfoService().addSeed(seed);
        }
    }

    
    public static void main(String[] args) {
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:*applicationContext.xml");
    	RedisService redisSerivice = (RedisService) context.getBean("redisService");
        ConfService confService = (ConfService) context.getBean("confService");
        InfoService infoService = (InfoService) context.getBean("infoService");

        Configuration conf = CarsonConfiguration.create();

        Tools tools = new Tools(redisSerivice, confService, infoService);
        
        try {
        List<ListConf> list = confService.getListConfs();
        Injector injector = new Injector();
        injector.setTools(tools);
        injector.inject(list);
        } catch (RedisConnectionFailureException e) {
        	LOG.warn("Cannot connect Redis, Crawler will exit.");
        	System.exit(1);
        }
        
        context.close();
        System.exit(0);
    }
    
    
    
    public Tools getTools() {
        return tools;
    }

    public void setTools(Tools tools) {
        this.tools = tools;
    }
}
