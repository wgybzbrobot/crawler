package com.zxsoft.crawler.web.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zxsoft.crawler.entity.Account;
import com.zxsoft.crawler.web.service.website.UserService;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String index(Account account, Model model) {
		
		return "login";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String index1(Account account, Model model) {
		
		return "register";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(Account acc, Model model, HttpSession session) {
		
		Account account = userService.getAccount(acc.getUsername(), acc.getPassword());
		
		if (account == null) {
			model.addAttribute("msg", "用户名或密码错误");
			return "login";
		}
		
		session.setAttribute("account", account);
		
		return "redirect:/";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(Account account, Model model, HttpSession session) {
		
		userService.newAccount(account);

		session.setAttribute("account", account);
		
		return "redirect:/";
	}
	
	
	
	
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(@RequestParam(value="accountId", required=false) String accountId, HttpSession session) {
		
		session.removeAttribute("account");
		
		return "redirect:/";
	}
}
