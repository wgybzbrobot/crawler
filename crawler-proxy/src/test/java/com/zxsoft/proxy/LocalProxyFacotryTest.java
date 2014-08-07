package com.zxsoft.proxy;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.zxsoft.test.Main;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class LocalProxyFacotryTest {

	@Autowired
	private ProxyFactory localProxyFactory;
	
	@Test
	public void test() {
		localProxyFactory.getProxies("");

		localProxyFactory.removeCache();
		
		localProxyFactory.getProxies("");
		localProxyFactory.getProxies("");
	}
}
