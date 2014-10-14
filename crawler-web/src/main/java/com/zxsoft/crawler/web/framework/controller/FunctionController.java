//package com.zxsoft.crawler.web.framework.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.thinkingcloud.framework.util.StringUtils;
//
//@Controller
//@RequestMapping("/")
//public class FunctionController {
//
//	@RequestMapping(value="/{funId}", method = RequestMethod.GET)
//	public String index(@PathVariable(value = "funId") String funcId, Model model) {
//		
//		model.addAttribute("func", "website");
//		
//		return "default";
//	}
//	
//	@RequestMapping(value = "func/{funId}", method = RequestMethod.GET)
//	public String func(@PathVariable(value = "funId") String funcId,
//	        Model model) {
//
//		if (StringUtils.isEmpty(funcId)) {
//			funcId = "website";
//		}
//		
//		model.addAttribute("funcId", funcId);
//		
//		return "default";
//	}
//}
