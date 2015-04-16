package com.zxsoft.crawler.web.controller.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.web.service.crawler.WorkerService;

/**
 *
 */
@Controller
@RequestMapping("worker")
public class WorkerController {

    private static Logger LOG = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private WorkerService workerService;
    
    /**
     * 
     * @param model
     * @return
     * @throws CrawlerException 
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) throws CrawlerException {

        List<WorkerConf> workers = workerService.getWorkers();
        
        model.addAttribute("workers", workers);
        
        return "crawler/worker";
    }

}
