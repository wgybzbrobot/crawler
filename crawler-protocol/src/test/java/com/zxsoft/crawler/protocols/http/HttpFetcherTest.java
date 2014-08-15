package com.zxsoft.crawler.protocols.http;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.zxsoft.crawler.protocol.ProtocolOutput;
import com.zxsoft.test.Main;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class HttpFetcherTest {

	@Autowired
	HttpFetcher httpFetcher;

	@Test
	public void testWeibo() {
		String url = "http://weibo.com/yujv520/home?wvr=5&lf=reg";
		ProtocolOutput output = httpFetcher.fetch(url, true);
		Assert.notNull(output);
		Document document = output.getDocument();
		Assert.notNull(document);
		System.out.println(document.html());
	}

	
}
