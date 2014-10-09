package com.zxsoft.crawler.web.service.crawler;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface SlaveService {

	List<Map<String, Object>> slaves() throws IOException ;
}
