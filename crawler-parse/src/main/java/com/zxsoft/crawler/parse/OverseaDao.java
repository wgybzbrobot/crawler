package com.zxsoft.crawler.parse;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.dns.DNSCache;

/**
 * 仅仅用于获取境外网站的ip地址信息
 * @author xiayun
 *
 */
public class OverseaDao extends BaseDao {

        public Map<String, Object> getOversea(String host) {
                List<Map<String, Object>> list =  getJdbcTemplate().query("select * from oversea where host = ?",  new Object[] { host }, new RowMapper<Map<String, Object>>() {
                        @Override
                        public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("ip", rs.getString("ip"));
                                map.put("location", rs.getString("location"));
                                map.put("code", rs.getInt("code"));
                                return map;
                        }
                });
                if (!CollectionUtils.isEmpty(list)) {
                        return list.get(0);
                }
                return null;
        }
        
        
        
        public void insertOversea(String host, String ip, String location, int code) {
                getJdbcTemplate().update("insert into oversea(host, ip, location, code) value(?, ?, ?, ?)", host, ip, location, code);
        }

        public static void main(String[] args) throws UnknownHostException, MalformedURLException {

                String[] urls = { "http://www.aboluowang.com                                     ",
                                "http://www.want-daily.com/portal.php?mod=list&catid=1         ",
                                "http://www.dwnews.com/                                        ",
                                "http://www.epochtimes.com/gb/ncChineseCommunity.htm           ",
                                "http://www.uyghurcongress.org/cn/                             ",
                                "http://www.2muslim.com                                        ",
                                "http://www.renminbao.com/rmb/shishi/index.html                ",
                                "http://news.boxun.com/                                        ",
                                "http://www.scmp.com/                                          ",
                                "http://xzzs.yqteam.cc/                                        ",
                                "http://www.epochweek.com/                                     ",
                                "http://www.chinese.rfi.fr/                                    ",
                                "http://www.rfa.org/mandarin/Xinwen                            ",
                                "http://www.canyu.org                                          ",
                                "http://www.voachinese.com                                     ",
                                "http://news.backchina.com/rank.php                            ",
                                "http://www.cna.com.tw/                                        ",
                                "http://blog.udn.com/                                          ",
                                "http://news.edoors.com/                                       ",
                                "http://cn.nytimes.com/                                        ",
                                "http://zlzg.yqteam.cc/                                        ",
                                "http://lhzb.yqteam.cc/                                        ",
                                "http://www.mingpaonews.com/                                   ",
                                "http://cn.wsj.com/gb/index.asp                                ",
                                "http://azrb.yqteam.cc/site1/news/cn/index.shtml               ",
                                "http://www.takungpao.com/                                     ",
                                "http://www.ntdtv.com                                          ",
                                "http://www.jiji.com/                                          ",
                                "http://www.bbc.co.uk/zhongwen/simp/                           ",
                                "http://www.dw.de/                                             ",
                                "http://www.minghui.org/                                       ",
                                "http://news.creaders.net                                      ",
                                "http://appledaily.tw/                                         ",
                                "http://www.yonhapnews.co.kr                                   " };

                OverseaDao dao = new OverseaDao();
                for (String url : urls) {
                        String ip = "", location = "", host = "";
                        int code = 0;
                        try {
                                URL u = new URL(url);
                                host = u.getHost();
                                ip = DNSCache.getIp(u);
                                location = LocationUtils.getLocation(ip);
                                code = LocationUtils.getLocationCode(ip);
//                                System.out.println(u.getHost() + "\t" + ip + "\t" + location + "\t" + code);
                        } catch (Exception e) {
                                System.out.println(url);
                                continue;
                        }
                        dao.insertOversea(host, ip, location, code);
                }
        }

}
