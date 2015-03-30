package com.zxsoft.crawler.protocol.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import com.zxisl.commons.utils.StringUtils;

/**
 * MD5生成器
 * @author xiayun
 *
 */
public class Md5Signatrue {
	
	public static String generateMd5(String... args) {

		if (StringUtils.isEmpty(args)) {
			throw new IllegalArgumentException("生产MD5时参数为空.");
		}
		
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			if (StringUtils.isEmpty(arg))
				continue;
			sb.append(arg.trim());
		}

		byte[] result = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = md.digest(sb.toString().getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return new String(Hex.encodeHexString(result)).toUpperCase();
	}

}
