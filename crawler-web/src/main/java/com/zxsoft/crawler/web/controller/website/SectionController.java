package com.zxsoft.crawler.web.controller.website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Account;
import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfDetailId;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.DictService;
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
	private ConfigService configService;
	@Autowired
	private WebsiteService websiteService;

	@Autowired
	private DictService dictService;

	@RequestMapping(method = RequestMethod.GET)
	public String index(@RequestParam(value = "websiteId", required = false) String websiteId,
	        Model model) {
		Website website = websiteService.getWebsite(websiteId);
		model.addAttribute("website", website);

		List<Category> categories = dictService.getCategories();
		model.addAttribute("categories", categories);

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
	public String addOrUpdate(@RequestParam(value = "copy", required = false) String copy,
	        Section section, Model model, HttpSession session) {
		
		/*Account account = (Account) session.getAttribute("account");
		if (account == null) {
			return "NoAccess";
		}
		
		section.setAccount(account);*/
		
		Account account = new Account();
		account.setId("swe");
		section.setAccount(account);
		
		if ("true".equals(copy)) {
			Map<String, Object> map = configService.getConfig(section.getId());
			ConfList confList = (ConfList) map.get("confList");
			List<ConfDetail> confDetails = (List<ConfDetail>) map.get("confDetails");
			
			if (confList == null) {
				return "NoConfList";
			}
			confList.setUrl(section.getUrl());
			if (!CollectionUtils.isEmpty(confDetails)) {
				for (ConfDetail confDetail : confDetails) {
					ConfDetailId confDetailId = new ConfDetailId();
					confDetailId.setHost(confDetail.getId().getHost());
					confDetailId.setListurl(section.getUrl());
	                confDetail.setId(confDetailId);
                }
			}
			section.setId(null);
			sectionService.saveOrUpdate(section);
			configService.add(confList);
			configService.add(confDetails);
		} else {
			sectionService.saveOrUpdate(section);
		}
		
		return "success";
	}

	/**
	 * 获取某个网站的详细信息
	 */
	@ResponseBody
	@RequestMapping(value = "moreinfo/{id}", method = RequestMethod.GET)
	public Map<String, Object> moreinfo(@PathVariable(value = "id") String id, Model model) {
		Section section = sectionService.getSection(id);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", section.getId());
		map.put("url", section.getUrl());
		map.put("comment", section.getComment());
		map.put("category.id", section.getCategory().getId());

		return map;
	}

	@ResponseBody
	@RequestMapping(value = "delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable(value = "id") String id, Model model) {
		sectionService.delete(id);
		return "success";
	}
}
