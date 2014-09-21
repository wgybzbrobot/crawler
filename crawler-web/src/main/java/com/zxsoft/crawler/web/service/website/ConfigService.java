package com.zxsoft.crawler.web.service.website;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface ConfigService {

	/**
	 * 获取配置配置，包含列表页和详细页配置
	 * @param url 版块URL
	 */
	Map<String, Object> getConfig(String url);
}
