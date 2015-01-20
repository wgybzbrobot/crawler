//package com.zxsoft.crawler.protocol.utils;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.HttpException;
//import org.apache.commons.httpclient.HttpMethod;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.apache.tika.io.IOUtils;
//import org.jsoup.nodes.Document;
//import org.junit.Test;
//
//import com.zxisl.commons.utils.Assert;
//import com.zxsoft.crawler.protocol.ProtocolOutput;
//import com.zxsoft.crawler.protocol.util.new EncodingDetector();
//import com.zxsoft.crawler.protocols.http.HttpFetcher;
//import com.zxsoft.crawler.storage.WebPage;
//
//public class Testnew EncodingDetector() {
//
//        @Test
//        public void testGetEncode1() throws HttpException, IOException {
//                HttpClient client = new HttpClient();
//                List<String> urls = new ArrayList<String>();
//                urls.add("http://www.qq.com");
//                urls.add("http://bbs.ahwang.cn/forum-156-1.html");
//                urls.add("http://www.sina.com.cn/");
//
//                for (String url : urls) {
//                        HttpMethod get = new GetMethod(url);
//                        new EncodingDetector() detector = new new EncodingDetector()();
//                        client.executeMethod(get);
//                        InputStream is = get.getResponseBodyAsStream();
//                        String encoding = new EncodingDetector().detect(IOUtils.toByteArray(is));
//                        // detector.detect(is);
//                        System.out.println(url + " Charset: " + encoding + get.getResponseBodyAsString());
//                        get.releaseConnection();
//                }
//
//        }
//
//        static HttpFetcher httpFetcher = new HttpFetcher();
//
//        @Test
//        public void testGetEncode2() throws HttpException, IOException {
//                String url = "http://www.tcren.cc/thread-930872-1-1.html";
//                HttpClient client = new HttpClient();
//                HttpMethod get = new GetMethod(url);
//                client.executeMethod(get);
//                InputStream is = get.getResponseBodyAsStream();
//                 String encoding =
//                 new EncodingDetector().detect(IOUtils.toByteArray(is));
//                 System.out.println(url + " Charset: " + encoding);
//                get.releaseConnection();
//        }
//
//        @Test
//        public void testGetEncode() {
//                String url = "http://news.luaninfo.com/lanews/2014/12/30/170444583463.html";
//                // String url = "http://www.wj0556.com/news/local/3408502.html";
//
//                WebPage page = new WebPage(url, false);
//                ProtocolOutput output = httpFetcher.fetch(page);
//                Document document = output.getDocument();
//                // System.out.println(document.html());
//        }
//        
//        @Test
//        public void testDetectByUniversalchardet() throws HttpException, IOException {
//                List<String> urls = new ArrayList<String>();
//                urls.add("http://www.qq.com");
//                urls.add("http://bbs.ahwang.cn/forum-156-1.html");
//                urls.add("http://www.sina.com.cn/");
//                urls.add("http://ent.sina.com.cn/v/m/2014-12-31/doc-icczmvun4553928.shtml");
//                urls.add("http://video.sina.com.cn/p/ent/s/h/2014-12-31/102164453773.html");
//                urls.add("http://news.luaninfo.com/lanews/2014/12/30/170444583463.html");
//                urls.add("http://www.wj0556.com/news/local/3408502.html");
//                urls.add("http://0556wjw.com/forum-39-1.html"                              );
//                urls.add("http://0556wjw.com/forum.php?mod=forumdisplay&fid=2"             );
//                urls.add("http://bbs.0550.com/f-767-1.html"                                );
//                urls.add("http://bbs.0554cc.cn/forum-50-1.html"                            );
//                urls.add("http://bbs.0558.com/forum-75-1.html"                             );
//                urls.add("http://bbs.0559qm.com/forum.php?mod=forumdisplay&fid=2"          );
//                urls.add("http://bbs.0566cn.net/forum-2-1.html"                            );
//                urls.add("http://bbs.168hs.com/forum-93-1.html"                            );
//                urls.add("http://bbs.233000.com/forum-125-1.html"                          );
////                urls.add("http://bbs.365jia.cn/forum-2543-1.html"                          );
//                urls.add("http://bbs.51minsheng.com/forum.php?mod=forumdisplay&fid=41"     );
//                urls.add("http://bbs.6wang.cc/forum-44-1.html"                             );
//                
//               urls.add( "http://bbs.ahsz.gov.cn/forum.php?mod=forumdisplay&fid=76"                               )                    ;
//               urls.add( "http://bbs.ahtc.cc/forum-42-1.html"                                                     )                    ;
//               urls.add( "http://bbs.ahwang.cn/forum-156-1.html"                                                  )                    ;
//               urls.add( "http://bbs.ahwang.cn/forum-159-1.html"                                                  )                    ;
//               urls.add( "http://bbs.ahyx.cc/forum-37-1.html"                                                     )                    ;
//               urls.add( "http://bbs.ahyx.cc/forum-59-1.html"                                                     )                    ;
//               urls.add( "http://bbs.ahyx.gov.cn/forum-59-1.html"                                                 )                    ;
//               urls.add( "http://bbs.ahyx.net/forum-121-1.html"                                                   )                    ;
//               urls.add( "http://bbs.anhui.cc/forum-111-1.html"                                                   )                    ;
//               urls.add( "http://bbs.anhui.cc/forum-79-1.html"                                                    )                    ;
//               urls.add( "http://bbs.anhuinews.com/forum-510-1.html"                                              )                    ;
//               urls.add( "http://bbs.aqnews.com.cn/forumdisplay.php?fid=46"                                       )                    ;
//               urls.add( "http://bbs.aqw.cc/forum-43-1.html"                                                      )                    ;
//               urls.add( "http://bbs.aqzyzx.com/forum.php?mod=forumdisplay&fid=8"                                 )                    ;
//               urls.add( "http://bbs.chizhouren.com/forum-9-1.html"                                               )                    ;
//               urls.add( "http://bbs.chuzhou.cn/forum-2-1.html"                                                   )                    ;
//               urls.add( "http://bbs.cnwuhu.com/forum-69-1.html"                                                  )                    ;
//               urls.add( "http://bbs.dang3.com/forum-32-1.html"                                                   )                    ;
////               urls.add( "http://bbs.efunan.com/forum.php?mod=forumdisplay&fid=39"                                )                    ;
//               urls.add( "http://bbs.fybxw.com/forum-108-1.html"                                                  )                    ;
//               urls.add( "http://bbs.gd163.com.cn/ShowForum.asp?ForumID=2"                                        )                    ;
//               urls.add( "http://bbs.hb163.cn/forum-79-1.html"                                                    )                    ;
//               urls.add( "http://bbs.hefei.cc/forum-1107-1.html"                                                  )                    ;
//               urls.add( "http://bbs.hefei.cc/forum-61-1.html"                                                    )                    ;
//               urls.add( "http://bbs.hichuzhou.com/forum-88-1.html"                                               )                    ;
//               urls.add( "http://bbs.huoshan.cc/forum-25-1.html"                                                  )                    ;
//               urls.add( "http://bbs.itongcheng.cc/forum-89-1.html"                                               )                    ;
//               urls.add( "http://bbs.laianba.com/forum-84-1.html"                                                 )                    ;
//               urls.add( "http://bbs.lqqncy.com/forum.php?mod=forumdisplay&fid=2"                                 )                    ;
//               urls.add( "http://bbs.luaninfo.com/forum-74-1.html"                                                )                    ;
//               urls.add( "http://bbs.luanren.com/forum-5-1.html"                                                  )                    ;
//               urls.add( "http://bbs.mczx.cn/forum-40-1.html"                                                     )                    ;
//               urls.add( "http://bbs.mczx.cn/forum.php?mod=forumdisplay&fid=15"                                   )                    ;
//               urls.add( "http://bbs.newsxc.com/forum-56-1.html"                                                  )                    ;
//               urls.add( "http://bbs.qsxw.gov.cn/forum-86-1.html"                                                 )                    ;
//               urls.add( "http://bbs.taihexian.com/forum-57-1.html"                                               )                    ;
//               urls.add( "http://bbs.thx.gov.cn/forum.php?mod=forumdisplay&fid=161"                               )                    ;
//               urls.add( "http://bbs.tianya.cn/list-free-1.shtml"                                                 )                    ;
//               urls.add( "http://bbs.tongling.cn/forum-2-1.html"                                                  )                    ;
//               urls.add( "http://bbs.wehefei.com/forum-57-1.html"                                                 )                    ;
//               urls.add( "http://bbs.wuhunews.cn/forum-253-1.html"                                                )                    ;
//
//                HttpClient client = new HttpClient();
//                for (String url : urls) {
//                        HttpMethod get = new GetMethod(url);
//                        client.executeMethod(get);
//                        InputStream is = get.getResponseBodyAsStream();
//                        byte[] bytes = IOUtils.toByteArray(is);
//                        
//                        String encode = new EncodingDetector().detectByUniversalchardet(bytes);
//                        System.out.println(url + " charset: " + encode);
//                        get.releaseConnection();
//                }
//        }
//}
