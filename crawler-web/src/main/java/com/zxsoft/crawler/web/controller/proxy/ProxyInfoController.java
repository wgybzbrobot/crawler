package com.zxsoft.crawler.web.controller.proxy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.proxy.Proxy;

@Controller
@RequestMapping("/proxyInfo")
public class ProxyInfoController {


	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, @ModelAttribute("listConf") ListConf listConf,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo) {
		return "/proxy/list";
	}

	/**
	 * display list configuration infomation
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.POST)
	public List<Proxy> list(@ModelAttribute("ajaxRequest") boolean ajaxRequest,
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "15", required = false) Integer pageSize,
	        Proxy param) {
		return null;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

	@ModelAttribute("proxy")
	public Proxy createFormBean() {
		return new Proxy();
	}

	@RequestMapping(value = "addProxy", method = RequestMethod.GET)
	public String addListConf(/*@Valid ListConf listConf, BindingResult result, Model model,*/
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		return "listConf";
	}

	@ResponseBody
	@RequestMapping(value = "addProxy", method = RequestMethod.POST)
	public String addProxy(ListConf listConf, BindingResult result, Model model,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		model.addAttribute("listConf", listConf);
//		if (result.hasErrors()) {
//			return "listConf";
//		}
		return "";
	}

}
