package com.zxsoft.crawler.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zxsoft.crawler.CrawlerServer;

@Controller
public class MainController {

	@RequestMapping(value="/status", method=RequestMethod.GET)
	@ResponseBody
	public  String status() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
	
	@RequestMapping(value="/start", method=RequestMethod.GET)
	@ResponseBody
	public  String start() {
		if (CrawlerServer.start)
			return "Crawler is started.";
		
		return "hello spring boot";
	}
	
	/**
	 * wait for all jobs quit.
	 * @return
	 */
	@RequestMapping(value="/quit", method=RequestMethod.GET)
	@ResponseBody
	public  String quit() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
	
	/**
	 * force quit.
	 * @return
	 */
	@RequestMapping(value="/stop", method=RequestMethod.GET)
	@ResponseBody
	public  String stop() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
	
	@RequestMapping(value="/restart", method=RequestMethod.GET)
	@ResponseBody
	public  String restart() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
	
	
	
	
	
}
