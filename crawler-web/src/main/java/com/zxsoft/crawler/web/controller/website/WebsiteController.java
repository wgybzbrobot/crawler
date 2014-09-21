package com.zxsoft.crawler.web.controller.website;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.service.website.WebsiteService;
import com.zxsoft.crawler.web.service.website.impl.WebsiteServiceImpl;
import com.zxsoft.crawler.web.verification.ListConfigVerification;

@Controller
@RequestMapping("/website")
public class WebsiteController {

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(@ModelAttribute("website") Website website, Model model) {
		Page<Website> page = websiteServiceImpl.getWebsite(website, 1, 20);
		List<Website> list = page.getRes();
		model.addAttribute("page", page);
		return "index";
	}

	@Autowired
	private WebsiteService websiteServiceImpl;
	
	/**
	 * get list configuration infomation
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public List<Website> list(
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
	        Website website) {
		Page<Website> page = websiteServiceImpl.getWebsite(website, pageNo, pageSize);
		List<Website> list = page.getRes();
		return list;
	}

}
