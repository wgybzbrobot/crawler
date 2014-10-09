package com.zxsoft.crawler.web.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zxsoft.crawler.entity.Account;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String index(Account account, Model model) {
		
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(Account account, Model model, HttpSession session) {
		
		account.setId("swe");
		session.setAttribute("account", account);
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(@RequestParam(value="accountId", required=false) String accountId, HttpSession session) {
		
		session.removeAttribute("account");
		
		return "redirect:/";
	}
}
