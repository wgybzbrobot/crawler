package com.zxsoft.crawler.web.controller.crawler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.web.service.crawler.ReptileService;

/**
 * 访问各个地区爬虫信息的控制器
 */
@Controller
@RequestMapping("reptile")
public class ReptileController {

    @Autowired
    private ReptileService reptileService;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "crawler/reptile";
    }
    
    /**
     * 获取各个地区爬虫地址信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value= "list",method = RequestMethod.GET)
    public List<Reptile> getReptiles () {
        List<Reptile> reptiles = reptileService.getReptiles();
        return reptiles;
    }
    /**
     * 添加地区爬虫的地址信息
     * @param reptile
     * @return
     */
    @RequestMapping(value= "add",method = RequestMethod.GET)
    public String addReptile(HttpServletRequest request,Reptile reptile) {
        reptileService.add(reptile);
        
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

}
