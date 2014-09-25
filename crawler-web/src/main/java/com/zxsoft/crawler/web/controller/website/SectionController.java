package com.zxsoft.crawler.web.controller.website;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.SectionService;
import com.zxsoft.crawler.web.service.website.WebsiteService;

@Controller
@RequestMapping("/section")
public class SectionController {

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@Autowired
	private SectionService sectionService;
	
	@Autowired
	private WebsiteService websiteService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(
	        @RequestParam(value = "websiteId", required = false) String websiteId,
	        Model model) {
		Website website = websiteService.getWebsite(websiteId);
		model.addAttribute("website", website);

		Section section = new Section();
		section.setWebsite(website);
		Page<Section> page = sectionService.getSections(section, 1, Page.DEFAULT_PAGE_SIZE);
		model.addAttribute("page", page);
		
		return "section";
	}

	/**
	 * 添加或修改版块
	 */
	@ResponseBody
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String addOrUpdate(Section section, Model model) {
		/*String urlbase64 = Base64.encodeBase64String(section.getUrl().getBytes());
		section.setUrlbase64(urlbase64);*/
		sectionService.saveOrUpdate(section);
		return "success";
	}

}
