package com.zxsoft.crawler.web.controller.crawler;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mvc.extensions.ajax.AjaxUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.storage.WebPage.JOB_TYPE;
import com.zxsoft.crawler.web.service.crawler.JobService;
import com.zxsoft.crawler.web.service.crawler.SlaveService;
import com.zxsoft.crawler.web.service.crawler.impl.JobServiceImpl;
import com.zxsoft.crawler.web.service.crawler.impl.SlaveServiceImpl;

@Controller
@RequestMapping(MasterPath.SLAVE_RESOURCE_PATH)
public class SlaveController {

	private static Logger LOG = LoggerFactory.getLogger(SlaveController.class);

	@ModelAttribute
	public void ajaxAttribute(WebRequest request, Model model) {
		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
	}

	@RequestMapping(method = RequestMethod.GET)
	public String index(@ModelAttribute("ajaxRequest") boolean ajaxRequest) {
		return "/crawler/list";
	}

	SlaveService slaveService = new SlaveServiceImpl();

	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public Map<String, Object> slaves(@ModelAttribute("ajaxRequest") boolean ajaxRequest) {

		Map<String, Object> map = new HashMap<String, Object>();

		try {

			map.put("slaves", slaveService.slaves());
			map.put("msg", "success");
			map.put("code", "2000");

		} catch (ConnectException e) {
			LOG.warn(e.getMessage(), e);
			map.put("msg", "无法连接到主控，可能没有启动.");
			map.put("code", "5000");
		} catch (Exception e) {
			LOG.warn(e.getMessage(), e);
			map.put("code", "5000");
			map.put("msg", "无法连接到主控，可能没有启动.");
		}

		return map;
	}

	private JobService jobService = new JobServiceImpl();
	
	@ResponseBody
	@RequestMapping(value = "addSearchJob", method = RequestMethod.POST)
	public Map<String, Object> addSearchJob(@RequestParam(value="keyword", required=false) String keyword,
			@RequestParam(value="engineId", required=false) List<String> engineIds) {
		Assert.hasLength(keyword);
		Assert.notEmpty(engineIds);
		for (String engineId : engineIds) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put(Params.KEYWORD, keyword);
			args.put(Params.ENGINE_ID, engineId);
			jobService.addSearchJob(args);
        }
		return null;
	}

	
	@ResponseBody
	@RequestMapping(value = "addInspectJob", method = RequestMethod.POST)
	public Map<String, Object> addInspectJob(@RequestParam(value="url", required=false) String url) {
	
		Map<String, Object> args = new HashMap<String, Object>();
		args.put(Params.URL, url);
		
		jobService.addInsecptJob(args);
		
		return args;
	}
	
	
	
}
