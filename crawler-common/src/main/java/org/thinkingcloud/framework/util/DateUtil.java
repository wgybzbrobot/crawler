//package org.thinkingcloud.framework.util;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//
///**
// * This class is mainly to extract date from any text.
// * It read date_regexp.config file.
// */
//public class DateUtil {
//	
//	private static String FILENAME = "date_regexp.config";
//	
//	private static List<String> regExps= new ArrayList<String>();
//	
//	static {
//		Resource resource = new ClassPathResource(FILENAME);
//		try {
//	        InputStream is = resource.getInputStream();
//	        BufferedReader reader = null;
//			reader = new BufferedReader(new InputStreamReader(is));
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				line = line.trim();
//				if (line.length() == 0)
//					continue;
//
//				if (line.startsWith("#"))
//					continue;
//				regExps.add(line);
//			}
//        } catch (IOException e) {
//	        e.printStackTrace();
//        }
//	}
//	
//	public Date extractDate(String text) {
//		
//		for (String regExp : regExps) {
//			if (text.matches(regExp)) {
//				
//			}
//		}
//		
//		return null;
//	}
//
//}
