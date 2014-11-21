package com.zxsoft.crawler.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.Assert;
import org.thinkingcloud.framework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Utils {

	private static Logger LOG = LoggerFactory.getLogger(Utils.class);

	public static int getPositiveNumber(int i, int j) {
		if (j < 1)
			j = 1;
		if (i < 1)
			return j;
		return i;
	}

	/**
	 * 获取域名
	 */
	public static String getHost(String url) throws MalformedURLException {
		if (!url.startsWith("http")) {
			url = "http://" + url;
		}
		URL host = new URL(url);
		return host.getProtocol() + "://" + host.getHost();
	}

	/**
	 * 格式化日期
	 * @throws ParseException 
	 */
	public static Date formatDate(String text) throws ParseException {
		if (!StringUtils.hasLength(text)) {
			LOG.info("Input text for formate date is null.");
			return null;
		}
		
		text = text.replaceAll("\\s+", " ");
		text = text.replaceAll("\u00a0", " "); // 去除&nbsp;表示的空格
		
		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(text);
		scanner.useDelimiter("[^\\p{Alnum}\\:-]");
		while (true) {
			if (scanner.hasNextInt()) {
				int i = scanner.nextInt();
				sb.append(" " + i + " ");
			} else if (scanner.hasNext()) {
				String str = scanner.next();
				sb.append(" " + str + " ");
			} else
				break;
		}
		scanner.close();
		String nt = sb.toString();
		
		SimpleDateFormat sdf = null;
		try {
			 if (text.trim().matches(".*\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{2}:\\d{2}")) { // 2014-4-16 21:08
				text = nt;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return sdf.parse(text);
			} else if (text.trim().matches(".*\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2}")) { // 2014-4-16 21:08:48
				text = nt;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.parse(text);
			} else if (text.trim().matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2}")) { // 2014年05月22日 09:08
				sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
				return sdf.parse(text);
			} else if (text.trim().matches("\\d{4}年\\d{1,2}月\\d{1,2}日\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}")) { // 2014年10月29日 20:46:46
				sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
				return sdf.parse(text);
			} else if (text.trim().matches(".*\\d{4}年\\d{1,2}月\\d{1,2}日.*")) { // 博讯北京时间2014年11月01日 转载	
				Scanner scan = new Scanner(text);
				scan.useDelimiter("[^d{4}年\\d{1,2}月\\d{1,2}日]");
				StringBuilder _sb = new StringBuilder();
				while (true) {
					if (scan.hasNextInt()) {
						int i = scan.nextInt();
						_sb.append(" " + i + " ");
					} else if (scan.hasNext()) {
						String str = scan.next();
						_sb.append(" " + str + " ");
					} else
						break;
				}
				scan.close();
				text = _sb.toString();
				sdf = new SimpleDateFormat("yyyy年MM月dd日");
				return sdf.parse(text);
			} else if (text.trim().matches(".*\\d{1,2}\\s*天前")) { // 发表于 3 天前
				int num = Utils.extractNum(text);
				if (num != -1) {
					return new Date(System.currentTimeMillis() - num * 24 * 60 * 60 * 1000);
				}
			} else if (text.trim().matches(".*\\d{1,2}\\s*小时前")) { // 发表于 3 小时前 ;
				int num = Utils.extractNum(text);
				if (num != -1) {
					return new Date(System.currentTimeMillis() - num * 60 * 60 * 1000);
				}
			} else if (text.trim().matches(".*\\d{1,2}\\s*分钟前")) { // 发表于 3 分钟前
				int num = Utils.extractNum(text);
				if (num != -1) {
					return new Date(System.currentTimeMillis() - num * 60 * 1000);
				}
			} else if (text.trim().matches(".*\\d{1,2}\\s*秒前")) { // 发表于 3 秒前
				int num = Utils.extractNum(text);
				if (num != -1) {
					return new Date(System.currentTimeMillis() - num * 1000);
				}
			} else if (text.trim().matches(".*前天\\s*\\d{1,2}:\\d{1,2}")) { // 发表于 前天08:27
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -2);
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cal.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				return cal.getTime();
			} else if (text.trim().matches(".*今天\\s*\\d{1,2}:\\d{1,2}")) { // 发表于 今天08:27
				Calendar cal = Calendar.getInstance();
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cal.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				return cal.getTime();
			} else if (text.trim().matches(".*今天\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}")) { // 今天 13:40:01
				Calendar cal = Calendar.getInstance();
				String[] n = nt.split(":");
				if (n != null && n.length == 3) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cal.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
					cal.set(Calendar.SECOND, Integer.valueOf(n[2].trim()));
				}
				return cal.getTime();
			} else if (text.trim().matches(".*昨天\\s*\\d{1,2}:\\d{1,2}")) { // 发表于 昨天08:27
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cal.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				return cal.getTime();
			} else if (text.trim().matches(".*半小时前")) {
				return new Date(System.currentTimeMillis() - 30 * 60 * 1000);
			}
		}	catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * only for Baidu Tieba
	 * @throws ParseException 
	 */
	public static Date extractDate(String json) throws ParseException {
		JsonParser jsonParser = new JsonParser();
		JsonObject content = jsonParser.parse(json).getAsJsonObject().getAsJsonObject("content");
		return formatDate(content.get("date").getAsString());
	}

	public static int extractNum(String str) {
		int result = -1;
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			try {
				result = Integer.valueOf(matcher.group(0));
			} catch (NumberFormatException e) {
				LOG.info("NumberFormatException: cannot extract number from " + str);
			}
		}
		return result;
	}

	public static boolean isNum(String str) {
		if (!StringUtils.isEmpty(str)) {
			if (str.matches("\\d+"))
				return true;
		}
		return false;
	}

	public static boolean isUrl(String str) {
		if (StringUtils.isEmpty(str))
			return false;
		if (str.matches("^(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"))
			return true;
		return false;
	}

	public static List<String> extractUrls(Element element) {
		if (element == null) return null;
		
		List<String> list = new LinkedList<String>();
		Elements anchors = element.getElementsByTag("a");
		for (Element ele : anchors) {
	        if (isUrl(ele.absUrl("href")))
	        	list.add(ele.absUrl("href"));
        }
		return list;
	}
}
