package com.zxsoft.crawler.web.service.crawler.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import com.google.gson.Gson;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.service.crawler.SlaveService;

//import org.apache.hadoop.conf.Configuration;

public class SlaveServiceImpl extends SimpleCrawlerServiceImpl implements SlaveService {

	public SlaveServiceImpl() {
		super();
	}

	@Override
	public List<Map<String, Object>> slaves() throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		ClientResource cli = new ClientResource(new Context(), CRAWLER_MASTER
		        + MasterPath.SLAVE_RESOURCE_PATH);

		cli.setRetryAttempts(0);
		cli.getContext().getParameters().add("socketTimeout", String.valueOf(1000));

		String json = "";
		Representation r = cli.get();
		try {
			json = r.getText();
			Map<String, Object> map = new Gson().fromJson(json, Map.class);
			result = (List<Map<String, Object>>) map.get("slavestatus");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cli.release();
			((Client)cli.getNext()).stop();
		}

		// method 2
//		HttpClient httpclient = new DefaultHttpClient();
//		HttpGet httpget = new HttpGet(CRAWLER_MASTER + MasterPath.SLAVE_RESOURCE_PATH);
//		HttpResponse response = httpclient.execute(httpget);
//		System.out.println(response.getStatusLine());
//		HttpEntity entity = response.getEntity();
//		if (entity != null) {
//			InputStream instream = entity.getContent();
//			try {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
//				StringBuilder builder = new StringBuilder();
//				String aux = "";
//				while ((aux = reader.readLine()) != null) {
//					builder.append(aux);
//				}
//				String json = builder.toString();
//				Map<String, Object> map = new Gson().fromJson(json, Map.class);
//				result = (List<Map<String, Object>>) map.get("slavestatus");
//			} catch (IOException ex) {
//				throw ex;
//			} catch (RuntimeException ex) {
//				httpget.abort();
//				throw ex;
//			} finally {
//				instream.close();
//				httpclient.getConnectionManager().shutdown();
//			}
//		}

		return result;

	}

}
