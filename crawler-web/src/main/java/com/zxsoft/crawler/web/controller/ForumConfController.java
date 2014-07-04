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

import com.zxsoft.crawler.web.model.ForumConf;
import com.zxsoft.crawler.web.model.ForumDetailConf;
import com.zxsoft.crawler.web.model.ListConf;
import com.zxsoft.crawler.web.service.ConfService;
import com.zxsoft.crawler.web.verification.ForumDetailConfigVerification;
import com.zxsoft.crawler.web.verification.ListConfigVerification;
import com.zxsoft.framework.utils.Utils;

@Controller
@RequestMapping("/forumConf")
@SessionAttributes("forumConf")
public class ForumConfController {

	@Autowired
	private ListConfigVerification listConfigVerification;
	@Autowired
	private ForumDetailConfigVerification forumDetailConfigVerification;
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

	@ModelAttribute("forumConf")
	public ForumConf createFormBean() {
		return new ForumConf();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void form() {
	}

	@RequestMapping(method = RequestMethod.POST)
	public String test(@Valid ForumConf forumConf, BindingResult result, Model model,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		model.addAttribute("conf", forumConf);
		if (result.hasErrors()) {
			return null;
		}
		Map<String, Object> listRes = listConfigVerification.verify(forumConf.getListConf());
		Map<String, Object> detailRes = forumDetailConfigVerification.verify(forumConf.getTestUrl(), forumConf.getListConf().isAjax(),
		        forumConf.getForumDetailConf());

		FieldError error = null;
		if (listRes.get("error") instanceof FieldError) {
			error = (FieldError) listRes.get("error");
		}
		if (error != null) {
			result.addError(error);
		}

		if (result.hasErrors()) {
			model.addAttribute("listRes", listRes);
			model.addAttribute("detailRes", detailRes);
			return null;
		}

		model.addAttribute("listRes", listRes);
		model.addAttribute("detailRes", detailRes);
		return "/forumConf";
	}
	
	@RequestMapping(value="/save", method = RequestMethod.POST)
	public String save(@Valid ForumConf forumConf, BindingResult result, Model model,
			@ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		model.addAttribute("conf", forumConf);
		if (result.hasErrors()) {
			return null;
		}
		Map<String, Object> listRes = listConfigVerification.verify(forumConf.getListConf());
		Map<String, Object> detailRes = forumDetailConfigVerification.verify(forumConf.getTestUrl(), forumConf.getListConf().isAjax(),
				forumConf.getForumDetailConf());
		
		FieldError error = null;
		if (listRes.get("error") instanceof FieldError) {
			error = (FieldError) listRes.get("error");
		}
		if (error != null) {
			result.addError(error);
		}
		
		if (result.hasErrors()) {
			model.addAttribute("listRes", listRes);
			model.addAttribute("detailRes", detailRes);
			return null;
		}
		
		ListConf listConf = forumConf.getListConf();
		listConf.setCategory("forum");
		ForumDetailConf detailConf = forumConf.getForumDetailConf();
		try {
			detailConf.setHost(Utils.getHost(forumConf.getTestUrl()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		confService.saveForumConf(listConf, detailConf);
		model.addAttribute("listRes", listRes);
		model.addAttribute("detailRes", detailRes);
		return "/forumConf";
	}

}
