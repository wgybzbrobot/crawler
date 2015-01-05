package com.zxsoft.crawler.protocols.http;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.NetUtils;
import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.dao.ConfDao;
import com.zxsoft.crawler.storage.DetailConf;
import com.zxsoft.crawler.storage.ListConf;


/**
 * 网站帐号登录
 */
public class AuthHelper {

	private static Logger LOG = LoggerFactory.getLogger(AuthHelper.class);
	
	/**
	 * <p>判断是否需要登录认证
	 * 
	 * @param url <code>url</code>是版块地址后者版块下列表页的地址，可从数据库中查询，
	 * 若查询不到，说明数据库中没有该url的相关配置
	 */
	public static boolean isAuth(URL url) {
		ConfDao confDao = new ConfDao();
		String host = NetUtils.getHost(url);
		
		// 先查询ConfDetail
		DetailConf detailConf = confDao.getDetailConf(host);
		String urlstr = url.toExternalForm();
		if (detailConf != null) {
			urlstr = detailConf.getListUrl();
		}
		// 再查询ConfList
		ListConf listConf = confDao.getListConf(urlstr);
		
		if (listConf == null) {
			LOG.warn("没有找到ListConf:" + url.toExternalForm());
			return false;
		}
		return listConf.isAuth();
	}
	
	/**
	 * <p>从url中读取读取Cookie，<code>CookieStore</code>中是将网站地址作为key存储的，
	 * 不是版块地址或网站域名。所以首先通过<code>url</code>得到该url所属的网站，
	 * 通过网站地址查询Cookie。
	 * 
	 * @param url <code>url</code>是版块地址后者版块下列表页的地址，可从数据库中查询;
	 * 若查询不到，说明数据库中没有该url的相关配置
	 * @throws CookieNotFoundException 
	 */
	public static String readCookie(URL url) throws CookieNotFoundException {
		ConfDao confDao = new ConfDao();
		String host = NetUtils.getHost(url);
		
		// 先查询ConfDetail
		DetailConf detailConf = confDao.getDetailConf(host);
		String urlstr = url.toExternalForm();
		if (detailConf != null) {
			urlstr = detailConf.getListUrl();
		}
		// 再查询ConfList
		ListConf listConf = confDao.getListConf(urlstr);
		
		if (listConf == null) {
			LOG.warn("没有找到ListConf:" + url.toExternalForm());
			throw new CookieNotFoundException("不能读取Cookie, 因为没有找到" + url.toExternalForm() + "所属网站地址");
		}
		
		
		return "";
	}
	
	
	
	private static WeakHashMap<String, Set<String>> logger = new WeakHashMap<String, Set<String>>();
	
	public synchronized static String get(String host) {
		Set<String> set = logger.get(host);
		String[] arr = {};
		if (CollectionUtils.isEmpty(set)) {
			return "";
		}
		arr = set.toArray(arr);
		int rand = (int)(Math.random() * arr.length);
		return arr[rand];
	}
	
	public synchronized static void put(String host, String cookie) {
		if (CollectionUtils.isEmpty(logger.get(host))) {
			Set<String> set = new HashSet<String>();
			set.add(cookie);
			logger.put(host, set);
		} else {
			logger.get(host).add(cookie);
		}
	}
}
