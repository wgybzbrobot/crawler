package com.zxsoft.crawler.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zxsoft.crawler.Crawler;

@Controller
public class BasicController {

	@RequestMapping(value="status", method=RequestMethod.GET)
	@ResponseBody
	public  boolean status() {
		return Crawler.isCrawlerRunning;
//		return "";
	}
	
	
	/**
	 * wait for all jobs quit.
	 * @return
	 */
	@RequestMapping(value="quit", method=RequestMethod.GET)
	@ResponseBody
	public  String quit() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
	
}
