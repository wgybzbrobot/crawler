package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.web.service.WebsiteService;
import com.zxsoft.crawler.web.service.impl.WebsiteServiceImpl;
import com.zxsoft.crawler.web.verification.DetailConfigVerification;
import com.zxsoft.framework.utils.Page;

@Controller
@RequestMapping("/websiteInfo")
public class WebsiteDetailConfController {
	
	private WebsiteService websiteService = new WebsiteServiceImpl();

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	/**
	 * get detail page configuration infomation
	 */
	@ResponseBody
	@RequestMapping(value = "detail", method = RequestMethod.POST)
	public List<DetailConf> detail(
			@RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
			@RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
			DetailConf param) {
		Page<DetailConf> page = websiteService.getDetailConfs(pageNo, pageSize, param);
		List<DetailConf> list = page.getRes();
		return list;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("detailConf")
	public DetailConf createFormBean() {
		return new DetailConf();
	}

	/**
	 * 返回addDetailConf界面
	 */
	@RequestMapping(value = "addDetailConf", method = RequestMethod.GET)
	public String addDetailConf() {
		return "/website/detailConf";
	}
	
	private DetailConfigVerification detailConfigVerification = new DetailConfigVerification();
	
	@ResponseBody
	@RequestMapping(value = "testDetailConf", method = RequestMethod.POST)
	public Map<String, Object> testDetailConf(DetailConf detailConf) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf);
		return map;
	}

	WebsiteService WebsiteService = new WebsiteServiceImpl();
	
	@ResponseBody
	@RequestMapping(value = "saveDetailConf", method = RequestMethod.POST)
	public Map<String, Object> saveDetailConf(DetailConf detailConf) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf);
		List<FieldError> errors = (List<FieldError>) map.get("errors");
		
		if (CollectionUtils.isEmpty(errors)) { // save
			websiteService.add(detailConf);
			map.put("msg", "success");
		} else {
			map.put("msg", "failure");
		}
		
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "detailConfExist", method = RequestMethod.GET)
	public boolean detailConfExist(@RequestParam(value="listUrl", required=false) String listUrl, @RequestParam(value="host", required=false) String host, @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		Assert.hasLength(listUrl);
		Assert.hasLength(host);
		boolean exist = websiteService.detailConfExist(listUrl, host);
		return exist;
	}

}
