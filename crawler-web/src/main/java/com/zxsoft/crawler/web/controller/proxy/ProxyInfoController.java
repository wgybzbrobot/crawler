package com.zxsoft.crawler.web.controller.proxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Proxy;
import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.web.service.proxy.ProxyService;
import com.zxsoft.crawler.web.service.website.DictService;

@Controller
@RequestMapping("/proxy")
public class ProxyInfoController {

	@Autowired
	private ProxyService proxyService;
	
	@Autowired
	private DictService dictService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model) {
		Page<Proxy> page = proxyService.getProxies(1, 50, null);
		model.addAttribute("page", page);
		
		List<SiteType> siteTypes = dictService.getSiteTypes();
		model.addAttribute("siteTypes", siteTypes);
		
		return "/proxy/list";
	}

	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.POST)
	public List<Proxy> list(
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
	        Proxy param) {

		return null;
	}

	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String add(Proxy proxy) {
		proxyService.addOrUpdateProxy(proxy);
		return "redirect:/proxy";
	}

}
