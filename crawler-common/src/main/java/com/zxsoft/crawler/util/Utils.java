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
	public static Date formatDate(String param) throws ParseException {
		long time = System.currentTimeMillis();
		if (StringUtils.isEmpty(param))
			return null;
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Scanner scanner = new Scanner(param);
		// anything other than alphanumberic characters, : or - is skipped
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
		Date date = null;
//		try {
			if (param.contains("天前")) {
				time = time - Integer.valueOf(nt.trim()) * 24 * 60 * 60 * 1000;
			} else if (param.contains("今天")) {
				Calendar cd = Calendar.getInstance();
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cd.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				String s = sdf.format(cd.getTime());
				date = sdf.parse(s);
				return date;
			} else if (param.contains("昨天")) {
				Calendar cd = Calendar.getInstance();
				cd.add(Calendar.DATE, -1);
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cd.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				String s = sdf.format(cd.getTime());
				date = sdf.parse(s);
				return date;
			} else if (param.contains("前天")) {
				Calendar cd = Calendar.getInstance();
				cd.add(Calendar.DATE, -2);
				String[] n = nt.split(":");
				if (n != null && n.length == 2) {
					cd.set(Calendar.HOUR_OF_DAY, Integer.valueOf(n[0].trim()));
					cd.set(Calendar.MINUTE, Integer.valueOf(n[1].trim()));
				}
				String s = sdf.format(cd.getTime());
				date = sdf.parse(s);
				return date;
			} else if (param.contains("半小时前")) {
				time = time - 30 * 60 * 1000;
			} else if (param.contains("时前")) {
				time = time - Integer.valueOf(nt.trim()) * 60 * 60 * 1000;
			} else if (param.contains("分钟前")) {
				time = time - Integer.valueOf(nt.trim()) * 60 * 1000;
			} else if (param.contains("秒前")) {
				time = time - Integer.valueOf(nt.trim()) * 1000;
			} else if (param.trim().matches("\\d{4}年\\d{2}月\\d{2}日\\s+\\d{2}:\\d{2}")) {
				sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
				return sdf.parse(param);
			} else if (param.trim().matches("\\d{4}年\\d{2}月\\d{2}日\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}")) {
				sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
				return sdf.parse(param);
			} else if (param.trim().matches("\\d{1,2}-\\d{2}\\s+\\d{1,2}:\\d{2}")) {
				// 05-23 13:52
				param = param.replaceAll("\\s+", " ");
				param = Calendar.getInstance().get(Calendar.YEAR) + "-" + param;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return sdf.parse(param);
			} else if (param.trim().matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{2}:\\d{2}")) {
				// 2013-07-27 05:55 2013-7-7 05:55
				param = param.replaceAll("\\s+", " ");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return sdf.parse(param);
			} else if (nt.trim().matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{2}:\\d{2}")) {
				nt = nt.replaceAll("\\s+", " ");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return sdf.parse(nt);
			} else if (nt.trim().matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2}")) {
				nt = nt.replaceAll("\\s+", " ");
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return sdf.parse(nt);
			} else if (param.trim().matches("\\d{1,2}:\\d{1,2}")) { // 12:43
				param = param.replaceAll("\\s+", " ");
				Calendar cal = Calendar.getInstance();
				param = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-"
				        + cal.get(Calendar.DAY_OF_MONTH) + " " + param;
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				return sdf.parse(param);
			} else if (param.trim().matches("\\d{1,2}-\\d{1,2}")) { // 5-26
				Calendar cal = Calendar.getInstance();
				param = cal.get(Calendar.YEAR) + "-" + param;
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				return sdf.parse(param);
			} else {
//				try {
					date = sdf.parse(nt.trim());
//				} catch (ParseException e) {
//					LOG.error(e.getMessage());
//					return null;
//				}
				return date;
			}
			date = new Date(time);
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return null;
//		}
		return date;
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
				LOG.error("NumberFormatException: cannot extract number from " + str);
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
