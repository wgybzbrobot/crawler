//package com.zxsoft.crawler.web.controller.crawler;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mvc.extensions.ajax.AjaxUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.context.request.WebRequest;
//
//import com.zxsoft.crawler.web.service.crawler.JobService;
//
//@Controller
//@RequestMapping(JobResource.PATH)
//public class JobController {
//
//	
//	@ModelAttribute
//	public void ajaxAttribute(WebRequest request, Model model) {
//		model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(request));
//	}
//	
//	@Autowired
//	JobService jobService;
//
//	@RequestMapping(method = RequestMethod.GET)
//	public List<Map<String, Object>> jobs(Model model,
//	        @ModelAttribute("ajaxRequest") boolean ajaxRequest) {
//		
//		List<Map<String, Object>> list = jobService.jobs();
//		
//		return list;
//	}
//	
//	public Map<String, Object> job(@RequestParam String cid, @RequestParam String jid) {
//		
//		Map<String, Object> map = jobService.job(cid, jid);
//		
//		return map;
//	}
//}
