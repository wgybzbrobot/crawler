package com.zxsoft.crawler.web.dao.website;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.Account;

@Repository
public interface UserDao {

	Account getAccount(String username, String password);

	
}
