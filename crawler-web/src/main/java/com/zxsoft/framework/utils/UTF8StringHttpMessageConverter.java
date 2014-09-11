package com.zxsoft.framework.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.FileCopyUtils;

/**
 * Copyright (C), 2000-2010, eKingstar Co., Ltd. 
 * File name: com.supwisdom.framework.utils.UTF8StringHttpMessageConverter.java
 * Description: TODO
 * Modify History（或Change Log）:  
 * 操作类型（创建、修改等）   操作日期       操作者             操作内容简述
 * 创建  				       2012-9-28  HLQ             
 * <p>
 *
 * @author      HLQ
 * @version     1.0
 * @since       1.0
 */
public class UTF8StringHttpMessageConverter extends StringHttpMessageConverter {
	private static final MediaType utf8 = new MediaType("text", "plain",

	Charset.forName("UTF-8"));

	private boolean writeAcceptCharset = true;

	@Override
	protected MediaType getDefaultContentType(String dumy) {

		return utf8;

	}

	protected List<Charset> getAcceptedCharsets() {

		return Arrays.asList(utf8.getCharSet());

	}

	protected void writeInternal(String s, HttpOutputMessage outputMessage)

	throws IOException {

		if (this.writeAcceptCharset) {

			outputMessage.getHeaders().setAcceptCharset(getAcceptedCharsets());

		}

		Charset charset = utf8.getCharSet();

		FileCopyUtils.copy(s, new OutputStreamWriter(outputMessage.getBody(),

		charset));

	}

}
