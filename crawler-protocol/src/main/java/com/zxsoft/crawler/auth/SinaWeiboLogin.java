package com.zxsoft.crawler.auth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import org.apache.commons.httpclient.NameValuePair;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.StringUtils;

import sun.misc.BASE64Encoder;

import com.google.gson.Gson;
import com.zxsoft.crawler.net.protocols.ProtocolException;
import com.zxsoft.crawler.net.protocols.Response;
import com.zxsoft.crawler.protocols.http.HttpBase;
import com.zxsoft.crawler.protocols.http.httpclient.HttpClient;
import com.zxsoft.crawler.util.EncodingDetector;

public class SinaWeiboLogin implements Login {

	private static Logger LOG = LoggerFactory.getLogger(SinaWeiboLogin.class);
	
	public PreloginEntity preloginEntity;
	
	private HttpBase httpClient = new HttpClient();
	
//	@Cacheable(value = { "cookieCache"}, key="#username" )
	public String login(String username, String password) throws Exception {
		
		String loginUrl = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)";
		prelogin(username);

		if (preloginEntity == null)
			throw new LoginException();

		NameValuePair[] data = {
		        new NameValuePair("entry", "weibo"),
		        new NameValuePair("gateway", "1"),
		        new NameValuePair("from", ""),
		        new NameValuePair("savestate", "7"),
		        new NameValuePair("userticket", "1"),
		        new NameValuePair("vsnf", "1"),
		        new NameValuePair("vsnval", ""),
		        new NameValuePair("su", getUsername(username)),
		        new NameValuePair("service", "miniblog"),
		        new NameValuePair("nonce", preloginEntity.nonce),
		        new NameValuePair("pwencode", "rsa2"),
		        new NameValuePair("sp", getPassword(password,
		                preloginEntity.pubkey, preloginEntity.servertime,
		                preloginEntity.nonce)),
		        new NameValuePair("encoding", "UTF-8"),
		        new NameValuePair("prelt", "115"),
		        new NameValuePair("rsakv", "" + preloginEntity.rsakv),
		        new NameValuePair(
		                "url",
		                "http://weibo.com/ajaxlogin.php?framelogin=1&amp;callback=parent.sinaSSOController.feedBackUrlCallBack"),
		        new NameValuePair("returntype", "META") };
		Response response = httpClient.postForResponse(new URL(loginUrl), data);
		String contentType = response.getHeader(Response.CONTENT_TYPE);
		String charset = EncodingDetector.parseCharacterEncoding(contentType, response.content);
		String html = new String(response.content, charset);
//		LOG.info(html);
 		String ajaxUrlRegex = "location\\.replace\\([\" | \'](.*)[\" | \']\\)";
		Pattern pattern = Pattern.compile(ajaxUrlRegex);
		Matcher matcher = pattern.matcher(html);
		String ajaxUrl = "";
		while (matcher.find()) {
			ajaxUrl = matcher.group();
		}
		if (!StringUtils.isEmpty(ajaxUrl)) {
			ajaxUrl = ajaxUrl.substring(18, ajaxUrl.length() - 2);
			Response newResponse = httpClient.getResponse(new URL(ajaxUrl), true);
			contentType = response.getHeader(Response.CONTENT_TYPE);
			charset = EncodingDetector.parseCharacterEncoding(contentType, newResponse.content);
			String content = new String(newResponse.content, charset);
			String regexp = "\\{(.*)\\}";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				content = matcher.group();
			}
			content = JSONValue.parse(content).toString();
			LOG.debug("Login Message: " + content);
			
			if (content.contains("\"result\":false"))
				throw new LoginException();
			return response.getHeader("Cookie");
		} else {
			LOG.error("Cannot find string match " + ajaxUrlRegex);
			throw new LoginException("Cannot find string match " + ajaxUrlRegex);
		}
	}

	public String getUsername(String username) {
		// username = username.replaceAll("@", "");
		return new BASE64Encoder().encode(username.getBytes());
	}

	public String getPassword(String passwd, String pubkey, long servertime,
	        String nonce) throws Exception {
		BigInteger modulus = new BigInteger(pubkey, 16);
		BigInteger publicExponent = new BigInteger("10001", 16);
		RSAPublicKeySpec key = new RSAPublicKeySpec(modulus, publicExponent);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(key);
		String message = servertime + "\t" + nonce + "\n" + passwd;
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytes = cipher.doFinal(message.getBytes());

		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}

		String encryPasswd = sb.toString();
		LOG.debug("Encryption Password: " + encryPasswd);
		return encryPasswd;
	}

	public void prelogin(String username) throws IOException, ProtocolException {
		String name = getUsername(username);
		String preloginUrl = String
		        .format("http://login.sina.com.cn/sso/prelogin.php?entry=sso&callback=sinaSSOController.preloginCallBack&su=%s&rsakt=mod&client=ssologin.js(v1.4.5)",
		                name);
		Response response = httpClient.getResponse(new URL(preloginUrl), true);
		byte[] content = response.content;
		URL u = response.url;
		if (content == null)
			return;
		InputStream in = new ByteArrayInputStream(content);
		String contentType = response.getHeader(Response.CONTENT_TYPE);
		String charset = EncodingDetector.parseCharacterEncoding(contentType, content);

		String body = new String(content, charset);

		String regex = "\\(.*\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(body);
		String json = "";
		while (matcher.find()) {
			json = matcher.group();
		}
		json = json.substring(1);
		json = json.substring(0, json.length() - 1);
		Gson gson = new Gson();
		preloginEntity = gson.fromJson(json, PreloginEntity.class);
	}
	
	class PreloginEntity {
		public int retcode;
		public long servertime;
		public String pcid;
		public String nonce;
		public String pubkey;
		public long rsakv;
		public long exectime;
	}
}
