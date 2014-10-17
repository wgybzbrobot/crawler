package org.thinkingcloud.framework.web.utils;

import org.apache.commons.logging.impl.SLF4JLocationAwareLog;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

public class Test {

	public static void main(String[] args) {
		ClassLoader loader = Test.class.getClassLoader();
		
		Marker marker ;
		LocationAwareLogger logger;
		SLF4JLocationAwareLog log;
		
		System.out.println(loader.getResource("org.slf4j.Marker.class"));
		System.out.println(loader.getResource("org.slf4j.spi.LocationAwareLogger.class"));
		System.out.println(loader.getResource("org.apache.commons.logging.impl.SLF4JLocationAwareLog.class"));
		 
	}
}
