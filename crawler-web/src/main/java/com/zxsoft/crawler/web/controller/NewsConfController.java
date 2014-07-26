package com.zxsoft.crawler.web.controller;

import java.net.MalformedURLException;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;

import com.zxsoft.crawler.util.Utils;
import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.model.NewsConf;
import com.zxsoft.crawler.web.model.NewsDetailConf;
import com.zxsoft.crawler.web.service.ConfService;
import com.zxsoft.crawler.web.verification.ListConfigVerification;
import com.zxsoft.crawler.web.verification.NewsDetailConfigVerification;

@Controller
@RequestMapping("/newsConf")
@SessionAttributes("newsConf")
public class NewsConfController {

	@Autowired
	private ListConfigVerification listConfigVerification;
	@Autowired
	private NewsDetailConfigVerification newsDetailConfigVerification;
	@Autowired
	private ConfService confService;
	
	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		 model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("newsConf")
	public NewsConf createFormBean() {
		return new NewsConf();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void form() {
	}

	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@Valid NewsConf newsConf, BindingResult result, Model model,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest, 
	        @RequestParam(required = false, value = "action") String action) {
		if (result.hasErrors()) {
			return null;
		}
		Map<String, Object> listRes = listConfigVerification.verify(newsConf.getListConf());
		String testurl = newsConf.getTestUrl();
		Map<String, Object> detailRes = newsDetailConfigVerification.verify(testurl, false,
				newsConf.getDetailConf());

		FieldError error = null;
		if (listRes.get("error") instanceof FieldError) {
			error = (FieldError) listRes.get("error");
		}
		if (error != null) {
			result.addError(error);
		}

		if (result.hasErrors()) {
			return null;
		}

		if (!StringUtils.isEmpty(action)) {
			if ("验证".equals(action)) {

			} else {
				ListConf listConf = newsConf.getListConf();
				listConf.setCategory("news");
				NewsDetailConf detailConf = newsConf.getDetailConf();
				try {
	                detailConf.setHost(Utils.getHost(testurl));
                } catch (MalformedURLException e) {
	                e.printStackTrace();
                }
				confService.saveNewsConf(listConf, detailConf);
			}
		}
		model.addAttribute("listRes", listRes);
		model.addAttribute("detailRes", detailRes);
		return "/newsConf";
	}

}
