package com.zxsoft.crawler.web.controller.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 */
@Controller
@RequestMapping("worker")
public class WorkerController {

    private static Logger LOG = LoggerFactory.getLogger(WorkerController.class);

    /**
     * 
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        // TODO: 请求master获取worker节点
        return "/worker/list";
    }

}
