package com.zxsoft.crawler.web.controller.crawler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.WorkerConf;
import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.web.service.crawler.ReptileService;
import com.zxsoft.crawler.web.service.crawler.WorkerService;

/**
 * 访问各个地区爬虫信息的控制器
 */
@Controller
@RequestMapping("worker")
public class WorkerController {

    private static Logger LOG = LoggerFactory.getLogger(WorkerController.class);

    @Autowired
    private WorkerService workerService;
    @Autowired
    private ReptileService reptileService;
    /**
     * 
     * @param model
     * @return
     * @throws CrawlerException
     */
    @RequestMapping(value = "{reptileId}", method = RequestMethod.GET)
    public String index(@PathVariable(value = "reptileId") Integer reptileId, Model model)
                    throws CrawlerException {
        Reptile reptile = reptileService.getReptile(reptileId);
        model.addAttribute("reptile", reptile);
        
        List<WorkerConf> workers = workerService.getWorkers(reptileId);
        model.addAttribute("workers", workers);

        return "crawler/worker";
    }

}
