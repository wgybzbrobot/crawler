package org.thinkingcloud.framework.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NetUtils {

	/** get url query parameters */
	public static Map<String, String> getParameters(URL u) {
		String url = u.toString();
		if (url.lastIndexOf("?") != -1) {
			url = url.split("\\?")[1];
		}

		Map<String, String> map = new HashMap<String, String>();

		String[] strs = url.split("&");
		for (String str : strs) {
			if (StringUtils.isEmpty(str))
				continue;
			map.put(str.split("=")[0], str.split("=")[1]);
		}

		return map;
	}

	public static String createQueryString(Map<String, String> map) {
		if (map == null)
			return "";

		Set<String> set = map.keySet();
		StringBuilder sb = new StringBuilder();
		for (String str : set) {
			sb.append("&" + str + "=" + map.get(str));
		}
		return sb.toString().substring(1);
	}

	/**
	 * 获取域名
	 */
	public static String getHost(URL url) {
		if (url == null) return "";
		return url.getProtocol() + "://" + url.getHost();
	}

	public static boolean isUrl(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		if (str.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
			return true;
		return false;
	}

	/**
	 * Get an absolute URL from a URL attribute that may be relative (i.e. an
	 * <code>&lt;a href></code> or <code>&lt;img src></code>).
	 * <p/>
	 * E.g.: <code>String absUrl = linkEl.absUrl("href");</code>
	 * <p/>
	 * If the attribute value is already absolute (i.e. it starts with a
	 * protocol, like <code>http://</code> or <code>https://</code> etc), and it
	 * successfully parses as a URL, the attribute is returned directly.
	 * Otherwise, it is treated as a URL relative to the 
	 * {@code #baseUri}, and made absolute using that.
	 * <p/>
	 * 
	 * @param baseUri
	 * @param relUrl
	 */
	public static String absUrl(String baseUri, String relUrl) {
		URL base;
		try {
			try {
				base = new URL(baseUri);
			} catch (MalformedURLException e) {
				// the base is unsuitable, but the attribute may be abs on its
				// own, so try that
				URL abs = new URL(relUrl);
				return abs.toExternalForm();
			}
			// workaround: java resolves '//path/file + ?foo' to '//path/?foo',
			// not '//path/file?foo' as desired
			if (relUrl.startsWith("?"))
				relUrl = base.getPath() + relUrl;
			URL abs = new URL(base, relUrl);
			return abs.toExternalForm();
		} catch (MalformedURLException e) {
			return "";
		}
	}
}
