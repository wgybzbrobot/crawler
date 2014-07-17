package com.zxsoft.carson.plugin.parse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zxsoft.crawler.protocols.http.HttpFetcher;

public class TieBaParserTest {

	HttpFetcher httpFetcher = new HttpFetcher();
	
	@Test
	public void testGetSubReply1() {
		
		
		String uno = "http://tieba.baidu.com/p/comment?tid=3073183090&pid=51350571182&pn=1&t=1401331331927";
		Document document = httpFetcher.fetch(uno).getDocument();
		
		Elements elements = document.select("li.lzl_single_post.j_lzl_s_p");
		
		
		if (CollectionUtils.isEmpty(elements)) {
			System.out.println("1: no sub reply");
		} else {
			System.out.println(elements.html());
		}
		
		
	}
	
	@Test
	public void testGetSubReply2() {
		String uyes = "http://tieba.baidu.com/p/comment?tid=2715141907&pid=41926303511&pn=1&t=1401331331927";
//		String uyes = "http://tieba.baidu.com/p/2715141907";
		Document document = httpFetcher.fetch(uyes).getDocument();
		Elements elements = document.select("li.lzl_single_post.j_lzl_s_p");
		if (CollectionUtils.isEmpty(elements)) {
			System.out.println("2: no sub reply");
		} else {
			System.out.println("2: has sub reply");
		}
	}
	
	@Test
	public void testGetTieBa() {
		String json = "{\"author\": {\"user_id\":342636106,\"user_name\":\"\u9065\u5fc6\u5bd2\u661f\"," +
				  "\"name_u\":\"%D2%A3%D2%E4%BA%AE%D0%C7\",\"user_sex\":2," + 
				  "\"portrait\":\"4a36d2a3d2e4baaed0c76c14\",\"is_like\":1," +
				  "\"level_id\":12,\"level_name\":\"\u5fd8\u6211\u5883\u754c\"," + 
				  "\"cur_score\":6256,\"bawu\":0,\"props\":null}," + 
				  "\"content\":{\"post_id\":41926318166," + 
				  "\"is_anonym\":false,\"open_id\":\"tieba\"," + 
				  "\"open_type\":\"\"," + 
				  "\"date\":\"2013-11-20 13:13\"," + 
				  "\"vote_crypt\":\"\",\"post_no\":4,\"type\":\"0\"," + 
				  "\"comment_num\":16,\"ptype\":\"0\"," + 
				  "\"is_saveface\":false," + 
				  "\"props\":null," + 
				  "\"post_index\":3}}";
		String json2 = "{'post_id':41926318166, 'is_anonym':false,'open_id':'tieba'," 
				  + "'open_type':'', 'date':'2013-11-20 13:13', 'vote_crypt':'','post_no':4,'type':'0',"
				  + "'comment_num':16,'ptype':'0',"
				  + "'is_saveface':false,"
				  + "'props':null,"
				  + "'post_index':3}";
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
		
		JsonParser jsonParser = new JsonParser();
		JsonObject content = jsonParser.parse(json)
		    .getAsJsonObject().getAsJsonObject("content");
		
		String pid = content.get("post_id").getAsString();
		String date = content.get("date").getAsString();
		System.out.println(pid + "\t" + date);
		
		/*TieBa tieBa = gson.fromJson(json2, TieBa.class);
		System.out.println(tieBa.getPost_id() + "\t" + tieBa.getDate());*/
		
	}
	
	public static void main(String[] args) {
	    System.out.println(System.currentTimeMillis());
    }
}
