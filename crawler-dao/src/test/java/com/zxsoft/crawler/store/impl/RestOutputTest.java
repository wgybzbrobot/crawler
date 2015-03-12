//package com.zxsoft.crawler.store.impl;
//
//import org.junit.Test;
//import org.springframework.util.Assert;
//
//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
//
//public class RestOutputTest {
//
//        @Test
//        public void testJerseyClient() {
//                Client client = Client.create();
//                WebResource webResource = client.resource("http://36.7.150.150:28094/sentiment/index");
//                String json = "{\"num\":1,\"records\":\"[{\"id\":\"3c1d27d4d55e389a677fcd9663201249\",\"platform\":1,\"mid\":\"\","
//                                                + "\"username\":\"\",\"nickname\":\"wanghong\",\"original_id\":\"\",\"original_uid\":\"\",\"original_name\":\"\","
//                                                + "\"original_title\":\"\",\"original_url\":\"\",\"url\":\"http://azrb.yqteam.cc/site1/news/cn/2015/01/27/1666668.shtml\","
//                                                + "\"home_url\":\"\",\"title\":\"宽衣解带裸聊 「激情小妖女」竟是男的\",\"type\":\"中国新闻\",\"isharmful\":false,"
//                                                + "\"content\":\"\",\"comment_count\":0,\"read_count\":0,\"favorite_count\":0,\"attitude_count\":0,\"repost_count\":0,"
//                                                + "\"video_url\":\"\",\"pic_url\":\"\",\"voice_url\":\"\",\"timestamp\":1422339437947,\"source_id\":1230,"
//                                                + "\"lasttime\":1422330460947,\"server_id\":147226,\"identify_id\":0,\"identify_md5\":\"xiayun\",\"keyword\":\"\","
//                                                + "\"first_time\":1422330460947,\"update_time\":0,\"ip\":\"54.178.75.106\",\"location\":\"美国 新泽西州(merck公司)\","
//                                                + "\"geo\":\"\",\"receive_addr\":\"\",\"append_addr\":\"\",\"send_addr\":\"\",\"source_name\":\"\",\"source_type\":2,"
//                                                + "\"country_code\":0,\"location_code\":999999,\"province_code\":0,\"city_code\":0}]\"}";
//                ClientResponse response = webResource.type("application/json").post(ClientResponse.class, json);
//                String msg = response.getEntity(String.class);
//                System.out.println(msg + ", status code:" + response.getStatus());
//                Assert.isTrue(response.getStatus() == 200 || response.getStatus() == 201);
//        }
//
//}
