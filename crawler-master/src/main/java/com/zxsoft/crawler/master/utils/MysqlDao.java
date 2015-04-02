package com.zxsoft.crawler.master.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.mysql.jdbc.Driver;
import com.zxisl.commons.cache.ObjectCache;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;

public class MysqlDao extends BaseDao {
        private static Logger LOG = LoggerFactory.getLogger(MysqlDao.class);
        protected static final String TABLE_CONF_LIST = "conf_list";
        protected static final int TIMEOUT = 360;
        private static final JdbcTemplate mysqlJdbcTemplate;

        static {
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
                mysqlJdbcTemplate = new JdbcTemplate(dataSource);
        }

        public JdbcTemplate getMysqlJdbcTemplate() {
                return mysqlJdbcTemplate;
        }
        
        /**
         * 通过注释取得搜索引擎url
         * @param tid ==> tid
         * 
         * @return
         */
        public String getSearchEngineUrl(int tid) {
                if (StringUtils.isEmpty(tid)) {
                        return null;
                }
                ObjectCache objectCache = ObjectCache.get("ListConf", TIMEOUT);
                String url = "";
                if (objectCache.getObject(String.valueOf(tid)) != null) {
                        LOG.debug("Find search engine url in Cache");
                        url =  (String) objectCache.getObject(String.valueOf(tid));
                } else {
                        // if not found in cache, get ListConf from database
                        LOG.debug("Do not find ListConf in Cache, will get search engine url from database.");
                        LOG.debug("Getting search engine url by tid:" + tid);
                        List<String> list = mysqlJdbcTemplate.query("select a.url from conf_list a, website w, section s where w.id = s.site and s.url = a.url and s.category = 'search'  and w.tid = ?",
                                new Object[] { tid }, new RowMapper<String>() {
                                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                return  rs.getString("url");
                                        }
                                });
                        if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(list.get(0))) {
                                LOG.error("在表<website>没有找到来源tid为<" + tid + ">的记录.");
                                return null;
                        } else {
                                url = list.get(0);
                                LOG.info("找到来源 " + tid + "的网站" + url);
                                objectCache.setObject(String.valueOf(tid), url);
                        }
                }
                return url;
        }

        /**
         * 获取任务的其它参数
         * @param tid
         * @return
         * @throws CrawlerException
         */
        public JobConf getBasicInfos(int tid) throws CrawlerException {
                List<JobConf> list = mysqlJdbcTemplate.query("select a.comment source_name,  a.region, a.tid , a.provinceId, a.cityId, a.areaId, "
                                + "b.id sectionId, b.url, b.comment type from website a, section b"
                                                + "  where a.tid = ?  and a.id=b.site and b.category='search'",
                                                new Object[] { tid }, new RowMapper<JobConf>() {
                                                        public JobConf mapRow(ResultSet rs, int rowNum) throws SQLException {
                                                            JobConf jobConf = new JobConf(JobType.NETWORK_SEARCH, rs.getString("url"), 
                                                                            rs.getString("source_name"), rs.getInt("tid"), rs.getInt("sectionId"), rs.getString("type"), null, null);
                                                            jobConf.setProvince_code(rs.getInt("provinceId"));
                                                            jobConf.setCity_code(rs.getInt("cityId"));
                                                            jobConf.setLocationCode(rs.getInt("areaId"));
                                                            jobConf.setCountry_code(rs.getInt("region"));
                                                            return  jobConf;
                                                        }
                                                });
                if (CollectionUtils.isEmpty(list)) 
                    throw new CrawlerException(ErrorCode.CONF_ERROR, "Cannot find website information using tid: " + tid);
                
                JobConf jobConf = list.get(0);
                
                // get list page rule
                List<ListRule> listRules =  mysqlJdbcTemplate.query("select a.ajax, b.category, a.listdom, a.linedom, "
                                + "a.urldom, a.datedom, a.updatedom, a.synopsisdom, a.authordom from conf_list a, section b "
                                + "  where b.url = a.url  and b.url= ? and b.category='search'",
                                new Object[] { jobConf.getUrl() }, new RowMapper<ListRule>() {
                                        public ListRule mapRow(ResultSet rs, int rowNum) throws SQLException {
                                            ListRule listRule = new ListRule(rs.getBoolean("ajax"), rs.getString("category"), 
                                                            rs.getString("listdom"),rs.getString("linedom"),rs.getString("urldom"),
                                                            rs.getString("datedom"),rs.getString("updatedom"),rs.getString("synopsisdom"),
                                                            rs.getString("authordom"));
                                            return  listRule;
                                        }
                                });
                if (CollectionUtils.isEmpty(listRules))
                    throw new CrawlerException(ErrorCode.CONF_ERROR, "Cannot find list rule by url: " + jobConf.getUrl());
                
                if (listRules.size() > 1)
                    LOG.warn("Find list rule more than one, will use first one in jobcof, url is " + jobConf.getUrl());
                
                // search have no detail page rule, so will not get it.
                
                jobConf.setListRule(listRules.get(0));
                return jobConf;
        }
}
