//package com.zxsoft.crawler.web.controller.crawler;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Properties;
//
//import oracle.jdbc.driver.OracleDriver;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.datasource.SimpleDriverDataSource;
//
//import com.zxisl.commons.utils.CollectionUtils;
//import com.zxsoft.crawler.common.CrawlerException;
//import com.zxsoft.crawler.common.JobConf;
//import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
//
//public class OracleDao {
//    private static Logger LOG = LoggerFactory.getLogger(OracleDao.class);
//
//    private static final JdbcTemplate oracleJdbcTemplate;
//    static {
//        OracleDriver driver = new OracleDriver();
//        Properties prop = new Properties();
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        InputStream stream = loader.getResourceAsStream("store.properties");
//        try {
//            prop.load(stream);
//        } catch (IOException e) {
//            LOG.error(e.getMessage(), e);
//        }
//        String url = prop.getProperty("oracle.url");
//        String username = prop.getProperty("oracle.username");
//        String password = prop.getProperty("oracle.password");
//        LOG.info("oracle database address: " + url);
//        SimpleDriverDataSource dataSource = new SimpleDriverDataSource(driver, url,
//                        username, password);
//        oracleJdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//    public JdbcTemplate getOracleJdbcTemplate() {
//        return oracleJdbcTemplate;
//    }
//
//    /**
//     * 查询sourceid, platform
//     * 
//     * @return
//     * @throws CrawlerException 
//     */
//    public JobConf querySourceId(int tid) throws CrawlerException {
//        List<JobConf> list = oracleJdbcTemplate.query(
//                        "select sourceid ,zdbs platform from  fllb_cjlb  where id = ? ",
//                        new Object[] { tid }, new RowMapper<JobConf>() {
//                            @Override
//                            public JobConf mapRow(ResultSet rs, int arg1)
//                                            throws SQLException {
//                                JobConf jobConf = new JobConf();
//                                jobConf.setSource_id(rs.getInt("sourceid"));
//                                jobConf.setPlatform(rs.getInt("platform"));
//                                return jobConf;
//                            }
//                        });
//
//        if (CollectionUtils.isEmpty(list)) {
//            throw new CrawlerException(ErrorCode.CONF_ERROR, "Cannot find sourceid from fllb_cjlb using id: " + tid);
//        }
//        return list.get(0);
//    }
//
//}
