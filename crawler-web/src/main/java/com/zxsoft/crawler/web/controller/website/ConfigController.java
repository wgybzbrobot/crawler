package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.CollectionUtils;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.SectionService;
import com.zxsoft.crawler.web.service.website.WebsiteService;
import com.zxsoft.crawler.web.service.website.impl.WebsiteServiceImpl;
import com.zxsoft.crawler.web.verification.DetailConfigVerification;
import com.zxsoft.crawler.web.verification.ListConfigVerification;

@Controller
@RequestMapping("/config")
public class ConfigController {

	@Autowired
	private ConfigService configService;
	@Autowired
	private SectionService sectionService;

	/**
	 * 跳转配置页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(@RequestParam(value = "sectionId", required = false) String sectionId,
	        Model model) {
		Section section = sectionService.getSection(sectionId);
		model.addAttribute("section", section);
		
		Map<String, Object> map = configService.getConfig(sectionId);
		model.addAttribute("confList", (ConfList) map.get("confList"));
		model.addAttribute("confDetails", (List<ConfDetail>) map.get("confDetails"));

		return "website/config";
	}

	/**********************************************************************************
	 * 列表页配置
	 * **********************************************************************************/
	private ListConfigVerification listConfigVerification = new ListConfigVerification();

	/**
	 * 验证列表页配置是否正确
	 */
	@ResponseBody
	@RequestMapping(value = "testListConf", method = RequestMethod.POST)
	public Map<String, Object> testListConf(ConfList listConf) {
		Map<String, Object> listRes = listConfigVerification.verify(listConf);
		return listRes;
	}

	/**
	 * 保存列表页配置
	 */
	@ResponseBody
	@RequestMapping(value = "addListConf", method = RequestMethod.POST)
	public Map<String, Object> saveListConf(ConfList listConf) {
		Map<String, Object> listRes = listConfigVerification.verify(listConf);
		if (((Map<String, String>) listRes.get("errors")).size() == 0) { // save
			configService.add(listConf);
			listRes.put("msg", "success");
		} else {
			listRes.put("msg", "failure");
		}
		return listRes;
	}

	/**
	 * 判断listConf是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "listConfExist", method = RequestMethod.GET)
	public boolean listConfExist(@RequestParam(value = "url", required = false) String url,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		// boolean exist = websiteService.listConfExist(url);
		return true;
	}

	/**********************************************************************************
	 * 详细页配置
	 * **********************************************************************************/
	private DetailConfigVerification detailConfigVerification = new DetailConfigVerification();

	/**
	 * 验证详细页配置
	 */
	@ResponseBody
	@RequestMapping(value = "testDetailConf", method = RequestMethod.POST)
	public Map<String, Object> testDetailConf(ConfDetail detailConf, String testUrl) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf, testUrl);
		return map;
	}

	/**
	 * 保存详细页配置
	 */
	@ResponseBody
	@RequestMapping(value = "saveDetailConf", method = RequestMethod.POST)
	public Map<String, Object> saveDetailConf(ConfDetail detailConf, String testUrl, String oldHost) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf, testUrl);

		/*if (CollectionUtils.isEmpty(errors)) { 
			configService.add(detailConf, oldHost);
			map.put("msg", "success");
		} else {
			map.put("msg", "failure");
		}*/
		configService.add(detailConf, oldHost);
		map.put("msg", "success");
		
		return map;
	}

	/**
	 * 检查详细页HOST是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "detailConfExist", method = RequestMethod.GET)
	public boolean detailConfExist(
	        @RequestParam(value = "listUrl", required = false) String listUrl,
	        @RequestParam(value = "host", required = false) String host,
	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		Assert.hasLength(listUrl);
		Assert.hasLength(host);
		// boolean exist = websiteService.detailConfExist(listUrl, host);
		return false;
	}
}
