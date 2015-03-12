package crawler.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import oracle.jdbc.driver.OracleDriver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.master.utils.OracleDao;
import com.zxsoft.crawler.storage.ListConf;
import com.zxsoft.crawler.storage.Website;

/**
 * 将oracle中id转换到mysqltid
 */
public class SiteIdTransfer {

        private static Logger LOG = LoggerFactory
                                        .getLogger(SiteIdTransfer.class);

        private static JdbcTemplate getMysqlJdbcTemplate() {
                Driver driver = null;
                try {
                        driver = new Driver();
                } catch (SQLException e) {
                        e.printStackTrace();
                }
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("mysql.properties");
                try {
                        prop.load(stream);
                } catch (IOException e1) {
                       LOG.error(e1.getMessage(), e1);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("mysql database address: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                JdbcTemplate mysqlJdbcTemplate = new JdbcTemplate(dataSource);
                return mysqlJdbcTemplate;
        }
        
        private static JdbcTemplate getOracleJdbcTemplate() {
                OracleDriver driver = new OracleDriver();
                Properties prop = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                InputStream stream = loader.getResourceAsStream("oracle.properties");
                try {
                        prop.load(stream);
                } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                }
                String url = prop.getProperty("db.url");
                String username = prop.getProperty("db.username");
                String password = prop.getProperty("db.password");
                LOG.info("oracle database address: " + url);
                SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url, username, password);
                JdbcTemplate oracleJdbcTemplate = new JdbcTemplate(dataSource);
                return oracleJdbcTemplate;
        }
        
        
        public static void importTid(JdbcTemplate mysqlJdbcTemplate, JdbcTemplate oracleJdbcTemplate, String category, int num ) {

                List<Website> list = mysqlJdbcTemplate.query("select a.id, a.site, a.comment, a.tid "
                                                + " from website a, section b where a.id = b.site and b.category = ? ",
                                                new Object[]{category},
                        new RowMapper<Website>() {
                                public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                                        return new Website( rs.getInt("id"),   rs.getString("site"),  rs.getString("comment"), rs.getInt("tid"));
                                }
                });
                
                for (Website website : list) {
                        if (website.getTid() != 0) 
                                continue;
                        
                        List<Map<String, Object>> _list = oracleJdbcTemplate.query("select id, zdmc from fllb_cjlb "
                                                        + " where zdmc=? and zdbs=? and shzt=1 and bz=0 ", 
                                                        new Object[]{website.getComment(), num}, new RowMapper<Map<String, Object>>() {
                                public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("id", rs.getString("id"));
                                        map.put("zdmc", rs.getString("zdmc"));
                                        return map;
                                }
                        });
                        
                        if (CollectionUtils.isEmpty(_list)) {
                                LOG.warn("没有找到记录 comment=" + website.getComment());
                                continue;
                        }
                        
                        Map<String, Object> _map = _list.get(0);
                        mysqlJdbcTemplate.update("update website set tid= ? where id = ? ", new Object[]{_map.get("id"), website.getId()});
                        
                }
        }

        /**
         * mysql有, oracle没有的网站
         * @param mysqlJdbcTemplate
         * @param oracleJdbcTemplate
         */
        public static int mysqlHas(JdbcTemplate mysqlJdbcTemplate, JdbcTemplate oracleJdbcTemplate, String category, int num ) {
                List<Website> list = mysqlJdbcTemplate.query("select a.id, a.site, a.comment, a.tid "
                                                + " from website a, section b where a.id = b.site and b.category = ? and a.tid is null ",
                                                new Object[]{category},
                        new RowMapper<Website>() {
                                public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                                        return new Website( rs.getInt("id"),   rs.getString("site"),  rs.getString("comment"), rs.getInt("tid"));
                                }
                });
                LOG.info("mysql 有但oracle没有的站点:" + category);
                Set<String> set = new HashSet<String>();
                
                for (Website website : list) {
                        if (website.getTid() != 0) 
                                continue;
                        
                        List<Map<String, Object>> _list = oracleJdbcTemplate.query("select id, zdmc from fllb_cjlb "
                                                        + " where zdmc=? and zdbs=? and shzt=0  ", 
                                                        new Object[]{website.getComment(), num}, new RowMapper<Map<String, Object>>() {
                                public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("id", rs.getString("id"));
                                        map.put("zdmc", rs.getString("zdmc"));
                                        return map;
                                }
                        });
                        
                        if (CollectionUtils.isEmpty(_list)) {
                                set.add(website.getComment() + "\t\t\t" + website.getSite());
                        } 
                }
                System.out.println("size:" + set.size());
                for (String string : set) {
                       System.out.println(string);
                }
                return set.size();
        }

        /**
         * oracle 有,但mysql没有
         * @param mysqlJdbcTemplate
         * @param oracleJdbcTemplate
         */
        public static int oracleHas(JdbcTemplate mysqlJdbcTemplate, JdbcTemplate oracleJdbcTemplate, String category, int num ) {
                List<Website> list = oracleJdbcTemplate.query("select id, zdmc, zdzy from fllb_cjlb "
                                                + " where   zdbs=? and shzt=1  ", new Object[]{num},  new RowMapper<Website>() {
                        public Website mapRow(ResultSet rs, int arg1) throws SQLException {
                                Website website = new Website();
                                website.setId(rs.getInt("id"));
                                website.setComment(rs.getString("zdmc"));
                                website.setSite(rs.getString("zdzy"));
                                return website;
                        }
                });
                

                LOG.info("oracle 有但mysql没有的站点" + category);
                Set<String> set = new HashSet<String>();
                
                for (Website website : list) {
                        List<Website> _list = mysqlJdbcTemplate.query("select a.id, a.site, a.comment, a.tid "
                                                        + " from website a, section b where a.id = b.site and b.category = ? and a.comment=?", 
                                                        new Object[]{category, website.getComment()},
                                new RowMapper<Website>() {
                                        public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                return new Website( rs.getInt("id"),   rs.getString("site"),  rs.getString("comment"), rs.getInt("tid"));
                                        }
                        });
                        
                        if (CollectionUtils.isEmpty(_list)) {
                                set.add(website.getComment() + "\t\t\t" + website.getSite());
                        }
                }
                System.out.println("size:" + set.size());
                for (String string : set) {
                       System.out.println(string);
                }
                return  set.size();
        }
        
        
        public static void main(String[] args) {
                JdbcTemplate mysqlJdbcTemplate = getMysqlJdbcTemplate();
                JdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplate();
                
                Map<String, Integer> categories = new HashMap<String, Integer>();
                categories.put("news", 1);
                categories.put("forum", 2);
//                categories.put("news", 3);
                categories.put("blog", 4);
//                categories.put("news", 5);
                categories.put("search", 6);
                
                int sum = 0;
                for (String key : categories.keySet()) {
                        int i = categories.get(key);
//                        sum += oracleHas(mysqlJdbcTemplate, oracleJdbcTemplate,key, i);
                sum += mysqlHas(mysqlJdbcTemplate, oracleJdbcTemplate, key, i);
//                importTid(mysqlJdbcTemplate, oracleJdbcTemplate, key, i);
                        
                }
                System.out.println("sum" + sum);
                
        }

}
