package com.zxsoft.crawler.web.controller.crawler;

import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

import com.zxsoft.crawler.storage.ListConf;

@Controller
@RequestMapping("/crawlerInfo")
@SessionAttributes("CrawlerInfo")
public class CrawlerInfoController {

	
	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, @ModelAttribute("listConf") ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo) {
		return "/crawler/list";
	}
}
