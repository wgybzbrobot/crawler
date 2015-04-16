package com.zxsoft.crawler.web.service.crawler.impl;

import java.util.ArrayList;
import java.util.List;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.web.service.crawler.WorkerService;

@Service
public class WorkerServiceImpl implements WorkerService {

    @Value("${master}")
    private String master;
    
    @Override
    public List<WorkerConf> getWorkers() throws CrawlerException {
        
        List<WorkerConf> workers = new ArrayList<WorkerConf>();
        
        ClientResource cli= null;
        try {
            cli = new ClientResource("http://" + master + "/master/worker");
            cli.setRetryOnError(false);
            workers = cli.get(workers.getClass());
        } catch (Exception e) {
            throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "获取worker节点信息失败,msg:" + e.getMessage());
        } finally {
            if (cli != null) {
                cli.release();
            }
        }
        
        return workers;
    }

}
