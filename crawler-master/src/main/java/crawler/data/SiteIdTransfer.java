package crawler.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
        
        public static void main(String[] args) {

                JdbcTemplate mysqlJdbcTemplate = getMysqlJdbcTemplate();
                JdbcTemplate oracleJdbcTemplate = getOracleJdbcTemplate();
                
                List<Website> list = mysqlJdbcTemplate.query("select a.id, a.site, a.comment, a.tid "
                                                + " from website a, section b where a.id = b.site and b.category = 'search';",
                        new RowMapper<Website>() {
                                public Website mapRow(ResultSet rs, int rowNum) throws SQLException {
                                        return new Website( rs.getInt("id"),   rs.getString("site"),  rs.getString("comment"), rs.getInt("tid"));
                                }
                });
                
                for (Website website : list) {
                        if (website.getTid() != 0) 
                                continue;
                        
                        List<Map<String, Object>> _list = oracleJdbcTemplate.query("select id, zdmc from fllb_cjlb "
                                                        + " where zdmc=? and gsfl=1 and shzt=1 and bz=1 ", 
                                                        new Object[]{website.getComment()}, new RowMapper<Map<String, Object>>() {
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

}
