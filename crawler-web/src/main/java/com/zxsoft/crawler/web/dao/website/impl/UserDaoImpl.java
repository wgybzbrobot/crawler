package com.zxsoft.crawler.web.dao.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.entity.Account;
import com.zxsoft.crawler.web.dao.website.UserDao;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Override
	public Account getAccount(String username, String password) {
		
		String hql = "from Account a where a.username = ? and a.password = ?";
		List<Account> list = hibernateTemplate.find(hql, username, password);
		
		if (CollectionUtils.isEmpty(list)) return null;
		
		return list.get(0);
		
	}
}
