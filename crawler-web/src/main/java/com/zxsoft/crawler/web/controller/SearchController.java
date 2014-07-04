package com.zxsoft.crawler.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.service.ConfService;
import com.zxsoft.framework.utils.Page;

@Controller
@RequestMapping("/search")
public class SearchController {

	private Page page;
	private int pageNo = 1;
	private int pageSize = 10;
	
	@Autowired
	private ConfService confService;
	
	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		 model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model,@ModelAttribute("listConf") ListConf listConf,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		this.page = confService.getListConfs(null, pageNo, pageSize);
		model.addAttribute("page", page);
		model.addAttribute("pageSize", pageSize);
		return "/search";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(Model model,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest, 
			@ModelAttribute("listConf") ListConf listConf,
			@RequestParam(value="pageNo",defaultValue="1",required=false) Integer pageNo) {
		if(pageNo==null){
			pageNo = 1;
		}
		
		this.page = confService.getListConfs(listConf, pageNo, pageSize);
		
		model.addAttribute("page", page);
		model.addAttribute("pageSize", 1);
		return "/search";
	}

}
