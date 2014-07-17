package com.zxsoft.crawler.protocols.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.zxsoft.crawler.protocol.ProtocolOutput;

@Component
@Scope("prototype")
public class HttpFetcher {

	@Autowired
	private HttpBase htmlUnit;

	@Autowired
	private HttpBase httpClient;

	public ProtocolOutput fetch(String url, boolean ajax) {
		if (!ajax) {
			return httpClient.getProtocolOutput(url);
		} else {
			return htmlUnit.getProtocolOutput(url);
		}
	}

	public ProtocolOutput fetch(String url) {
		return httpClient.getProtocolOutput(url);
	}
}
