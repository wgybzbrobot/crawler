package com.zxsoft.crawler.web.controller.website;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.code.Code;
import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.SiteType;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.DictService;
import com.zxsoft.crawler.web.service.website.WebsiteService;

@Controller
@RequestMapping("/website")
public class WebsiteController {

	@Autowired
	private WebsiteService websiteServiceImpl;
	
	@Autowired
	private DictService dictService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index(@ModelAttribute("website") Website website, Model model) {
		Page<Website> page = websiteServiceImpl.getWebsite(website, 1, 100);
		model.addAttribute("page", page);
		
		if (website != null)
			model.addAttribute("website", website);
		
		List<SiteType> siteTypes = dictService.getSiteTypes();
		model.addAttribute("siteTypes", siteTypes);
		
		return "website/index";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String search(@ModelAttribute("website") Website website, Model model) {
	        return index(website, model);
	}

	/**
	 * 加载更多网站
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/list", method = RequestMethod.GET)
	public List<Website> list(
	        @RequestParam(value = "page", defaultValue = "1", required = false) Integer pageNo,
	        @RequestParam(value = "rows", defaultValue = "50", required = false) Integer pageSize,
	        Website website) {
		Page<Website> page = websiteServiceImpl.getWebsite(website, pageNo, pageSize);
		List<Website> list = page.getRes();
		return list;
	}

	/**
	 * 新增或修改网站
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/add", method = RequestMethod.POST)
	public String save(@RequestParam(value = "site", required = false) String site,
	        @RequestParam(value = "id", required = false) String id,
	        @RequestParam(value = "comment", required = false) String comment,
	        @RequestParam(value = "sitetype", required = false) String type,
	        @RequestParam(value = "region", required = false) String region,
	        @RequestParam(value = "status", required = false) String status,  Model model) {
	        type = "001";
		SiteType siteType = new SiteType(type);
		Website website = new Website(site, siteType, comment);
		website.setId(id);
		website.setRegion(region);
		website.setStatus(status);
	        int code = websiteServiceImpl.save(website);
	        if (code == Code.RECORD_EXIST)
	                return "urlExist";
		return "success";
	}

	/**
	 * 删除网站
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "id", required = false) String id,  Model model) {
	        websiteServiceImpl.deleteWebsite(id);
	        return "success";
	}

	/**
	 * 获取某个网站的详细信息
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/moreinfo/{id}", method = RequestMethod.GET)
	public Map<String, Object> moreinfo(@PathVariable(value = "id") String id, Model model) {
		Website website = websiteServiceImpl.getWebsite(id);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", website.getId());
		map.put("site", website.getSite());
		map.put("comment", website.getComment());
		map.put("region", website.getRegion());
		map.put("status", website.getStatus());
		map.put("sitetype", website.getSiteType().getType());
		return map;
	}

	
	/*
	 * Auth
	 */
	
	
	@RequestMapping(value = "auth/{id}", method = RequestMethod.GET)
	public String auth(@PathVariable(value = "id") String id, Model model) {
		Assert.hasLength(id);
		
		List<Auth> auths = websiteServiceImpl.getAuths(id);
		model.addAttribute("auths", auths);
		return "website/auth";
	}

	@ResponseBody
	@RequestMapping(value = "ajax/auth/add", method = RequestMethod.POST)
	public String addAuth(@ModelAttribute("auth") Auth auth, Model model) {
		Assert.notNull(auth);
		websiteServiceImpl.saveAuth(auth);
		return "success";
	}

	@ResponseBody
	@RequestMapping(value = "auth/info/{id}", method = RequestMethod.GET)
	public Map<String, Object> loadAuthInfo(@PathVariable(value = "id") String id, Model model) {
		Assert.hasLength(id);
		Auth auth = websiteServiceImpl.getAuth(id);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", auth.getId());
		map.put("username", auth.getUsername());
		map.put("password", auth.getPassword());
		map.put("website.id", auth.getWebsite().getId());
		
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "ajax/auth/delete/{id}", method = RequestMethod.GET)
	public String deleteAuth(@PathVariable(value = "id") String id, Model model) {
		Assert.hasLength(id);
		websiteServiceImpl.deleteAuth(id);
		return "success";
	}
	
	
	
}
