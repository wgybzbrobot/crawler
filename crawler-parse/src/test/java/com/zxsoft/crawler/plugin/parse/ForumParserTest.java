package com.zxsoft.crawler.plugin.parse;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;

import com.zxsoft.crawler.parse.FetchStatus;
import com.zxsoft.crawler.parse.Parser;
import com.zxsoft.crawler.parse.FetchStatus.Status;
import com.zxsoft.crawler.storage.WebPage;

public class ForumParserTest {

	private Logger LOG = LoggerFactory.getLogger(ForumParserTest.class);
	
	@Test
	public void testParseAnhuiNews() throws Exception {
		String baseUrl = "http://bbs.anhuinews.com/thread-1102159-1-2.html";
		WebPage page = new WebPage();
		page.setBaseUrl(baseUrl);
		page.setListUrl("http://bbs.anhuinews.com/forum-316-1.html");
		page.setTitle("我为祖国贴春联”春联征集火热进行中，欢迎网友踊跃参与");
		page.setAjax(false);

		Parser parser = new ForumParser();
		FetchStatus fetchStatus = parser.parse(page);
		Assert.isTrue(fetchStatus.getStatus() == Status.SUCCESS);
		LOG.info("count:" + fetchStatus);
	}

	@Test
	public void testParseTianYa() throws Exception {
		String baseUrl = "http://bbs.tianya.cn/post-free-4114906-1.shtml";
		WebPage page = new WebPage();
		page.setBaseUrl(baseUrl);
		page.setListUrl("http://bbs.tianya.cn/list-50148-1.shtml");
		page.setTitle("满大街都是有才华的穷人");
		page.setAjax(false);
		
		Parser parser = new ForumParser();
		FetchStatus fetchStatus = parser.parse(page);
		Assert.isTrue(fetchStatus.getStatus() == FetchStatus.Status.SUCCESS);
		LOG.info("count:" + fetchStatus);
	}
}
