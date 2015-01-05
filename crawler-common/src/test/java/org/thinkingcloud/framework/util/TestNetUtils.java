package org.thinkingcloud.framework.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.NetUtils;

public class TestNetUtils {

	Logger LOG = LoggerFactory.getLogger(TestNetUtils.class);
	
	@Test
	public void testAbsUrl() {
		String baseUri = "http://www.sogou.com/web?query=%E5%90%B8%E6%AF%92";
		String relUrl = "?query=%E5%90%B8%E6%AF%92&repp=1&page=2";
		relUrl = "query=%E5%90%B8%E6%AF%92&repp=1&page=2";
		relUrl = "javascript:void(0)";
		relUrl = "#page=2";
		
		String url = NetUtils.absUrl(baseUri, relUrl);
		LOG.debug(url);
	}
}
