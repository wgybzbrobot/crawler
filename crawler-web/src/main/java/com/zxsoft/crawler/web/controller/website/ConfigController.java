package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.web.service.website.ConfigService;

@Controller
@RequestMapping("/config")
public class ConfigController {

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@Autowired
	private ConfigService configService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(@RequestParam(value = "urlbase64", required = false) String urlbase64,
	        @RequestParam(value = "comment", required = false) String comment, Model model) {
		model.addAttribute("comment", comment);

		Map<String, Object> map = configService
		        .getConfig(new String(Base64.decodeBase64(urlbase64)));
		model.addAttribute("confList", (ConfList) map.get("confList"));
		model.addAttribute("confDetails", (List<ConfDetail>) map.get("confDetails"));

		return "website/config";
	}

}
