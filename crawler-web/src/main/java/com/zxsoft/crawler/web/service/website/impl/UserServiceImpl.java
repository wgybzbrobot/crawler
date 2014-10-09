package com.zxsoft.crawler.web.service.website.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Account;
import com.zxsoft.crawler.web.dao.website.UserDao;
import com.zxsoft.crawler.web.service.website.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Override
	public Account getAccount(String username, String password) {
		
		return userDao.getAccount(username, password);
	}

}
