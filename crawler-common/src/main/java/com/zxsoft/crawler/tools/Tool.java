package com.zxsoft.crawler.tools;

/**
 * class implements this interface to configure tools 
 * which contain service objects such as redisService, 
 * infoService, domService.
 */
public interface Tool {

    
	Tools getTools();
	
	void setTools(Tools tools);
}
