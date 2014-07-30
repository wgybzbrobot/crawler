package com.zxsoft.crawler.store.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.CrawlerServer;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CrawlerServer.class)
public class RestOutputTest {

	private String records= "{"
	+"\"num\":2,"
	+"\"records\":["
	+"{"
	+"\"id\":\"\","
	+"\"platform\":0,"
	+"\"mid\":\"\","
	+"\"username\":\"\","
	+"\"nickname\":\"初秋溪月\","
	+"\"original_id\":\"\","
	+"\"original_uid\":\"\","
	+"\"original_name\":\"\","
	+"\"original_title\":\"\","
	+"\"original_url\":\"\","
	+"\"url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\","
	+"\"home_url\":\"\","
	+"\"title\":\"独家揭秘：带你了解大老虎霸道的朋友圈！！！\","
	+"\"type\":\"\","
	+"\"isharmful\":false,"
	+"\"content\":\"　　7月29日，鉴于周据《中国共ecial/zhouyongkang/quanzi.shtml\","
	+"\"comment_count\":0,"
	+"\"read_count\":0,"
	+"\"favorite_count\":0,"
	+"\"attitude_count\":0,"
	+"\"repost_count\":0,"
	+"\"video_url\":\"\","
	+"\"pic_url\":\"http://static.tianyaui.com/img/static/2011/imgloadding.gif\","
	+"\"voice_url\":\"\","
	+"\"timestamp\":1406685426660,"
	+"\"source_id\":0,"
	+"\"lasttime\":0,"
	+"\"server_id\":0,"
	+"\"identify_id\":0,"
	+"\"identify_md5\":\"\","
	+"\"keyword\":\"\","
	+"\"first_time\":0,"
	+"\"update_time\":0,"
	+"\"ip\":\"\","
	+"\"location\":\"\","
	+"\"geo\":\"\","
	+"\"receive_addr\":\"\","
	+"\"append_addr\":\"\","
	+"\"send_addr\":\"\","
	+"\"source_name\":\"\","
	+"\"source_type\":0,"
	+"\"country_code\":0,"
	+"\"location_code\":0,"
	+"\"province_code\":0,"
	+"\"city_code\":0"
	+"},"
	+"{"
	+"\"id\":\"31057140-1688-4a56-95f7-6bbd3b4bb6d5\","
	+"\"platform\":0,"
	+"\"mid\":\"\","
	+"\"username\":\"\","
	+"\"nickname\":\"\","
	+"\"original_uid\":\"\","
	+"\"original_name\":\"\","
	+"\"original_title\":\"\","
	+"\"original_url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\","
	+"\"url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\","
	+"\"home_url\":\"\","
	+"\"title\":\"\","
	+"\"type\":\"\","
	+"\"isharmful\":false,"
	+"\"content\":\"　　7月29日，鉴于周永康涉嫌严重违纪ang/quanzi.shtml\","
	+"\"comment_count\":0,"
	+"\"read_count\":0,"
	+"\"favorite_count\":0,"
	+"\"attitude_count\":0,"
	+"\"repost_count\":0,"
	+"\"video_url\":\"\","
	+"\"pic_url\":\"http://static.tiany/2011/imgloading.gif\","
	+"\"voice_url\":\"\","
	+"\"timestamp\":0,"
	+"\"source_id\":0,"
	+"\"lasttime\":0,"
	+"\"server_id\":0,"
	+"\"identify_id\":0,"
	+"\"identify_md5\":\"\","
	+"\"keyword\":\"\","
	+"\"first_time\":0,"
	+"\"update_time\":0,"
	+"\"ip\":\"\","
	+"\"location\":\"\","
	+"\"geo\":\"\","
	+"\"receive_addr\":\"\","
	+"\"append_addr\":\"\","
	+"\"send_addr\":\"\","
	+"\"source_name\":\"\","
	+"\"source_type\":0,"
	+"\"country_code\":0,"
	+"\"location_code\":0,"
	+"\"province_code\":0,"
	+"\"city_code\":0"
	+"}"
	+"]"
	+"}";
			
	@Test
	public void test() throws Exception {
		String url = "http://192.168.32.11:8900/sentiment/index";
		List<RecordInfo> infos = new LinkedList<RecordInfo>();
		RecordInfo info = new RecordInfo();
		infos.add(info);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("num", infos.size());
		map.put("records", infos);
		Gson gson = new Gson();
		String json = gson.toJson(map, Map.class);
//		String json = "{\"num\":2,\"records\":[{\"id\":\"\",\"platform\":0,\"mid\":\"\",\"username\":\"\",\"nickname\":\"初秋溪月\",\"original_id\":\"\",\"original_uid\":\"\",\"original_name\":\"\",\"original_title\":\"\",\"original_url\":\"\",\"url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\",\"home_url\":\"\",\"title\":\"独家揭秘：带你了解大老虎霸道的朋友圈！！！\",\"type\":\"\",\"isharmful\":false,\"content\":\"　　7月29日，鉴于周永康涉嫌严重违纪，中共中央决定，依据《中国共产党章程》和《中国共产党纪律检查机关案件检查工作条例》的有关规定，由中共中央纪律检查委员会对其立案审查。下面为你独家揭秘大老虎霸道的朋友圈！！！ 　　 　　 　　http://news.ifeng.com/mainland/special/zhouyongkang/quanzi.shtml\",\"comment_count\":0,\"read_count\":0,\"favorite_count\":0,\"attitude_count\":0,\"repost_count\":0,\"video_url\":\"\",\"pic_url\":\"http://static.tianyaui.com/img/static/2011/imgloading.gifhttp://static.tianyaui.com/img/static/2011/imgloading.gif\",\"voice_url\":\"\",\"timestamp\":1406685426660,\"source_id\":0,\"lasttime\":0,\"server_id\":0,\"identify_id\":0,\"identify_md5\":\"\",\"keyword\":\"\",\"first_time\":0,\"update_time\":0,\"ip\":\"\",\"location\":\"\",\"geo\":\"\",\"receive_addr\":\"\",\"append_addr\":\"\",\"send_addr\":\"\",\"source_name\":\"\",\"source_type\":0,\"country_code\":0,\"location_code\":0,\"province_code\":0,\"city_code\":0},{\"id\":\"31057140-1688-4a56-95f7-6bbd3b4bb6d5\",\"platform\":0,\"mid\":\"\",\"username\":\"\",\"nickname\":\"\",\"original_uid\":\"\",\"original_name\":\"\",\"original_title\":\"\",\"original_url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\",\"url\":\"http://bbs.tianya.cn/post-free-4522245-1.shtml\",\"home_url\":\"\",\"title\":\"\",\"type\":\"\",\"isharmful\":false,\"content\":\"　　7月29日，鉴于周永康涉嫌严重违纪，中共中央决定，依据《中国共产党章程》和《中国共产党纪律检查机关案件检查工作条例》的有关规定，由中共中央纪律检查委员会对其立案审查。下面为你独家揭秘大老虎霸道的朋友圈！！！ 　　 　　 　　http://news.ifeng.com/mainland/special/zhouyongkang/quanzi.shtml\",\"comment_count\":0,\"read_count\":0,\"favorite_count\":0,\"attitude_count\":0,\"repost_count\":0,\"video_url\":\"\",\"pic_url\":\"http://static.tianyaui.com/img/static/2011/imgloading.gifhttp://static.tianyaui.com/img/static/2011/imgloading.gif\",\"voice_url\":\"\",\"timestamp\":0,\"source_id\":0,\"lasttime\":0,\"server_id\":0,\"identify_id\":0,\"identify_md5\":\"\",\"keyword\":\"\",\"first_time\":0,\"update_time\":0,\"ip\":\"\",\"location\":\"\",\"geo\":\"\",\"receive_addr\":\"\",\"append_addr\":\"\",\"send_addr\":\"\",\"source_name\":\"\",\"source_type\":0,\"country_code\":0,\"location_code\":0,\"province_code\":0,\"city_code\":0}]}";
		System.out.println(json);
		HttpEntity<String> request = new HttpEntity<String>(json, headers);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(url, request, String.class);
	}
	@Autowired
	private Output output;
	
	@Test
	public void testWrite() throws OutputException {
		List<RecordInfo> infos = new LinkedList<RecordInfo>();
		RecordInfo info1 = new RecordInfo("1", "http://1", 1);
		RecordInfo info2 = new RecordInfo("2", "http://1", 1);
		RecordInfo info3 = new RecordInfo("3", "http://1", 1);
		RecordInfo info4 = new RecordInfo("4", "http://1", 1);
		RecordInfo info5 = new RecordInfo("5", "http://1", 1);
		RecordInfo info6 = new RecordInfo("6", "http://1", 1);
		RecordInfo info7 = new RecordInfo("7", "http://1", 1);
		RecordInfo info8 = new RecordInfo("8", "http://1", 1);
		RecordInfo info9 = new RecordInfo("9", "http://1", 1);
		RecordInfo info10 = new RecordInfo("10", "http://1", 1);
		infos.add(info1);
		infos.add(info2);
		infos.add(info3);
		infos.add(info4);
		infos.add(info5);
		infos.add(info6);
		infos.add(info7);
		infos.add(info8);
		infos.add(info9);
		infos.add(info10);
		output.write(infos);
	}
	
	@Test
	public void testJerseyClient() {
		Client client = Client.create();
		WebResource webResource = client.resource("http://192.168.32.11:8900/sentiment/index");
//		String json = "{'num':" + size + ",'records':[" + records + "]}";
		String json = "{\"num\":2,\"records\":[{\"id\":\"123456789\",\"platform\":7,\"mid\":\"123abcdef\",\"username\":\"987654321\","
				+ "\"nickname\":\"wgybzb\",\"original_id\":\"5648333\",\"original_uid\":\"256339988\",\"original_name\":\"owgybzb\","
				+ "\"original_title\":\"原创标题\",\"original_url\":\"http://www.baidu.com\",\"url\":\"http://www.pp.cc\","
				+ "\"home_url\":\"http://www.google.com\",\"title\":\"标题\",\"type\":\"美容美食\",\"isharmful\":true,"
				+ "\"content\":\"这是一条测试数据\",\"comment_count\":100,\"read_count\":200,\"favorite_count\":300,"
				+ "\"attitude_count\":400,\"repost_count\":500,\"video_url\":\"http://www.video.vom\","
				+ "\"pic_url\":\"http://www.pic.com\",\"voice_url\":\"http://www.voice.com\",\"timestamp\":129856473,"
				+ "\"source_id\":153,\"lasttime\":985647213,\"server_id\":5,\"identify_id\":125633,\"identify_md5\":\"identifymd5value\","
				+ "\"keyword\":\"美食娱乐\",\"first_time\":1566423587,\"update_time\":1426879123,\"ip\":\"192.168.1.100\","
				+ "\"location\":\"安徽省合肥市\",\"geo\":\"经度:120.2366554,纬度:50.122599\",\"receive_addr\":\"wanggang@zxils.com\","
				+ "\"append_addr\":\"wanggang@pp.cc\",\"send_addr\":\"wgybzb@sina.cn\",\"source_name\":\"新浪微博\",\"source_type\":5,"
				+ "\"country_code\":1,\"location_code\":2130123,\"province_code\":34,\"city_code\":12},{\"id\":\"987654321\",\"platform\":7,"
				+ "\"mid\":\"456jdjdkff\",\"username\":\"2564788\",\"nickname\":\"wgybzb1\",\"original_id\":\"5648333\",\"original_uid\":\"256339988\","
				+ "\"original_name\":\"owgybzb1\",\"original_title\":\"原创标题\",\"original_url\":\"http://www.baidu.com\",\"url\":\"http://www.pp.cc\",\"home_url\":\"http://www.google.com\",\"title\":\"标题\",\"type\":\"美容美食\",\"isharmful\":false,\"content\":\"这是一条测试数据\",\"comment_count\":100,\"read_count\":200,\"favorite_count\":300,\"attitude_count\":400,\"repost_count\":500,\"video_url\":\"http://www.video.vom\",\"pic_url\":\"http://www.pic.com\",\"voice_url\":\"http://www.voice.com\",\"timestamp\":129856473,\"source_id\":153,\"lasttime\":985647213,\"server_id\":5,\"identify_id\":125633,\"identify_md5\":\"identifymd5value\",\"keyword\":\"美食娱乐\",\"first_time\":1566423587,\"update_time\":1426879123,\"ip\":\"192.168.1.100\",\"location\":\"安徽省合肥市\",\"geo\":\"经度:120.2366554,纬度:50.122599\",\"receive_addr\":\"wanggang@zxils.com\",\"append_addr\":\"wanggang@pp.cc\",\"send_addr\":\"wgybzb@sina.cn\",\"source_name\":\"新浪微博\",\"source_type\":5,\"country_code\":1,\"location_code\":2130123,\"province_code\":34,\"city_code\":12}]}";
		
		ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
		Assert.isTrue(response.getStatus() == 200);
		
	}
	
}
