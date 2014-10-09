package com.zxsoft.crawler.web.service.website;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Account;

@Service
public interface UserService {

	Account getAccount(String username, String password);
}
