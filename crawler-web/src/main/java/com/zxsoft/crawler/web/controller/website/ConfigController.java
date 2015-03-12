package com.zxsoft.crawler.web.controller.website;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.SectionService;
import com.zxsoft.crawler.web.verification.DetailConfigVerification;
import com.zxsoft.crawler.web.verification.ListConfigVerification;

@Controller
@RequestMapping("/config")
public class ConfigController {

        private static Logger LOG = LoggerFactory.getLogger(ConfigController.class);
        
	@Autowired
	private ConfigService configService;
	@Autowired
	private SectionService sectionService;

	/**
	 * 跳转配置页面
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(@RequestParam(value = "sectionId", required = false) Integer sectionId,
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
	@RequestMapping(value = "ajax/testListConf", method = RequestMethod.POST, produces="application/json")
	public Map<String, Object> testListConf(ConfList listConf, @RequestParam(value="keyword", required=false) String keyword) {
	        Map<String, Object> listRes = new HashMap<String, Object>();
	        
	        if (StringUtils.isEmpty(listConf.getUrl())) {
	                Map<String, String> errors = new HashMap<String, String>();
                        errors.put("urlerror", "必填");
                        listRes.put("errors", errors);
                        return listRes;
                }
	        
	        Section section = sectionService.getSectionByUrl(listConf.getUrl());
	        if (section == null) {
                       Map<String, String> errors = new HashMap<String, String>();
	                errors.put("section", "section not found");
                        listRes.put("errors", errors);
                        LOG.error("Section not found with url " + listConf.getUrl());
                        return listRes;
	        }
	        boolean autoUrl = section.getAutoUrl();
	        
		listRes = listConfigVerification.verify(listConf, keyword, autoUrl);
		return listRes;
	}

	/**
	 * 保存列表页配置
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/addListConf", method = RequestMethod.POST)
	public Map<String, Object> saveListConf(ConfList listConf) {
		configService.add(listConf);
		Map<String, Object> listRes = new HashMap<String, Object>();
		listRes.put("msg", "success");
		return listRes;
	}

	/**
	 * 判断listConf是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/listConfExist", method = RequestMethod.GET)
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
	@RequestMapping(value = "ajax/testDetailConf", method = RequestMethod.POST)
	public Map<String, Object> testDetailConf(ConfDetail detailConf, String testUrl) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf, testUrl);
		return map;
	}

	/**
	 * 保存详细页配置
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/saveDetailConf", method = RequestMethod.POST)
	public Map<String, Object> saveDetailConf(ConfDetail detailConf, String testUrl, String oldHost) {
		Map<String, Object> map = detailConfigVerification.verify(detailConf, testUrl);
		configService.add(detailConf, oldHost);
		map.put("msg", "success");
		
		return map;
	}

	/**
	 * 检查详细页HOST是否存在
	 */
	@ResponseBody
	@RequestMapping(value = "ajax/detailConfExist", method = RequestMethod.GET)
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
