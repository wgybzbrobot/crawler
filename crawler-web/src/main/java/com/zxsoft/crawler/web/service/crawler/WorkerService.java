package com.zxsoft.crawler.web.service.crawler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.WorkerConf;

@Service
public interface WorkerService {

    List<WorkerConf> getWorkers() throws CrawlerException;
    
}
