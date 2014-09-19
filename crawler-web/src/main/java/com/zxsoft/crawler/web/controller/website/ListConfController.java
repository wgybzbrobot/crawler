package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.web.service.website.WebsiteService;
import com.zxsoft.crawler.web.service.website.impl.WebsiteServiceImpl;
import com.zxsoft.crawler.web.verification.ListConfigVerification;

@Controller
@RequestMapping("/websiteInfo")
public class ListConfController {

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, @ModelAttribute("listConf") ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo) {
		return "/website/list";
	}

	/**
	 * get list configuration infomation
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.POST)
	public List<ListConf> list(
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
	        ListConf param) {
		Page<ListConf> page = websiteService.getListConfs(pageNo, pageSize, param);
		List<ListConf> list = page.getRes();
		return list;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("listConf")
	public ListConf createFormBean() {
		return new ListConf();
	}

	/**
	 * 返回addListConf界面
	 */
	@RequestMapping(value = "addListConf", method = RequestMethod.GET)
	public String addListConf() {
		return "/website/listConf";
	}

	private ListConfigVerification listConfigVerification = new ListConfigVerification();

	/**
	 * 验证列表页配置是否正确
	 */
	@ResponseBody
	@RequestMapping(value = "testListConf", method = RequestMethod.POST)
	public Map<String, Object> testListConf(ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		Map<String, Object> listRes = listConfigVerification.verify(listConf);
		return listRes;
	}

	private WebsiteService websiteService = new WebsiteServiceImpl();

	/**
	 * 保存列表页配置
	 */
	@ResponseBody
	@RequestMapping(value = "addListConf", method = RequestMethod.POST)
	public Map<String, Object> saveListConf(ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		Map<String, Object> listRes = listConfigVerification.verify(listConf);

		if (((Map<String, String>) listRes.get("errors")).size() == 0) { // save
			websiteService.add(listConf);
		}

		return listRes;
	}

	/**
	 * 判断listConf是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "listConfExist", method = RequestMethod.GET)
	public boolean listConfExist(@RequestParam(value="url", required=false) String url, @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		boolean exist = websiteService.listConfExist(url);
		return exist;
	}

}
