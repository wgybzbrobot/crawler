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

import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.SectionService;

@Controller
@RequestMapping("/section")
public class SectionController {

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@Autowired
	private SectionService sectionService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(
	        @RequestParam(value = "sitebase64", required = false) String sitebase64,
	        @RequestParam(value = "comment", required = false, defaultValue = "没有名称") String comment,
	        Model model) {
		model.addAttribute("comment", comment);
		model.addAttribute("sitebase64", sitebase64);

		String site = new String(Base64.decodeBase64(sitebase64.getBytes()));
		model.addAttribute("site", site);

		Section section = new Section();
		Website website = new Website();
		website.setSite(site);
		section.setWebsite(website);

		Page<Section> page = sectionService.getSections(section, 1, 20);
		model.addAttribute("page", page);

		return "section";
	}

	@ResponseBody
	@RequestMapping(value="add", method = RequestMethod.POST)
	public String save(
			@RequestParam(value = "site", required = false) String site,
	        @RequestParam(value = "url", required = false) String url,
	        @RequestParam(value = "comment", required = false) String comment,
	        @RequestParam(value = "category", required = false) String category,
	        Model model) {
		
		Assert.hasLength(site);
		Assert.hasLength(url);
		Assert.hasLength(comment);
		Assert.hasLength(category);
		
		Website website = new Website();
		website.setSite(site);
		
		String urlbase64 = Base64.encodeBase64String(url.getBytes());
		Section section = new Section(url, urlbase64);
		section.setWebsite(website);
		section.setComment(comment);
		section.setCategory(category);
		
		sectionService.saveOrUpdate(section);
		
		return "保存成功";
	}

}
