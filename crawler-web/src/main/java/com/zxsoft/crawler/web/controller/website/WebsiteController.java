package com.zxsoft.crawler.web.controller.website;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.WebsiteService;

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

	/**
	 * 修改或新增
	 */
	@ResponseBody
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public String save(@RequestParam(value = "site", required = false) String site,
	        @RequestParam(value = "comment", required = false) String comment,
	        @RequestParam(value = "sitetype", required = false) String type,
	        @RequestParam(value = "region", required = false) String region,
	        @RequestParam(value = "username", required = false) String username,
	        @RequestParam(value = "password", required = false) String password, Model model) {
		SiteType siteType = new SiteType(type);
		Website website = new Website(site, siteType, comment);
		website.setUsername(username);
		website.setPassword(password);
		website.setRegion(region);
		websiteServiceImpl.save(website);

		return "success";
	}

	/**
	 * 获取某个网站的详细信息
	 */
	@ResponseBody
	@RequestMapping(value = "moreinfo/{sitebase64}", method = RequestMethod.GET)
	public Map<String, Object> moreinfo(@PathVariable(value="sitebase64") String sitebase64, Model model) {
		Assert.hasLength(sitebase64, "参数<网站地址>不能为空");
		String site = new String(Base64.decodeBase64(sitebase64.getBytes()));
		Website website = websiteServiceImpl.getWebsite(site);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("site", website.getSite());
		map.put("comment", website.getComment());
		map.put("region", website.getRegion());
		map.put("sitetype", website.getSiteType().getType());
		map.put("username", website.getUsername());
		map.put("password", website.getPassword());
		return map;
	}

}
