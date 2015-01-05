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

		return new String(Hex.encodeHexString(result));
	}

	public static void main(String[] args) {
		String md5 = "8ed1d16b21830467335ac1700af045cf";
		String currentUrl = "http://tieba.baidu.com/p/2511412975?pn=33";
		String author = "妖孽半世倾尘";
		String contentString = "吼吼。。。我其实是贞子";
		String img = "http://imgsrc.baidu.com/forum/w%3D580/sign=296f0e44c8ea15ce41eee00186013a25/a2d78b2f070828383564bcd5b999a9014d08f12a.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=239a98e0738b4710ce2ffdc4f3cfc3b2/ca6ac0628535e5dd7e2ac51e77c6a7efcf1b6211.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=00874c29bd3eb13544c7b7b3961fa8cb/24ecb8345982b2b7502f590530adcbef74099be3.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=246b9d0dc2cec3fd8b3ea77de689d4b6/62b985ef76c6a7ef5362c8e7fcfaaf51f1de66d7.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=6d44e6ed8601a18bf0eb1247ae2e0761/f8b6c71b0ef41bd54bfe883550da81cb3bdb3dee.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=66a65902d6ca7bcb7d7bc7278e0b6b3f/4a540b178a82b9015e38a3f2728da9773b12ef86.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=d3f0d9a595eef01f4d1418cdd0ff99e0/ad9c219b033b5bb5ae95403f37d3d539b400bcdd.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=5f57f277c8177f3e1034fc0540cd3bb9/08b814e93901213f36912e1d55e736d12d2e95fd.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=1d7656ca5ab5c9ea62f303ebe53bb622/027e327f9e2f070801d54ac1e824b899ab01f243.jpg http://imgsrc.baidu.com/forum/w%3D580/sign=f7f7e9c05d6034a829e2b889fb1149d9/5091a3003af33a87878260dbc75c10385143b5a8.jpg http://static.tieba.baidu.com/tb/editor/images/face/i_f25.png ";
		System.out.println(generateMd5(currentUrl, author, contentString, img).equals(md5));
	}
}
