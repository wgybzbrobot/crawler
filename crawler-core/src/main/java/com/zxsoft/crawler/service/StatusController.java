package com.zxsoft.crawler.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/status")
public class StatusController {

	@RequestMapping(method=RequestMethod.GET)
	public  String status() {
		System.out.println("heloooo");
		return "hello spring boot";
	}
}
