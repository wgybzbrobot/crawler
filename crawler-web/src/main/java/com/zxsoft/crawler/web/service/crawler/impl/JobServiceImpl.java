package com.zxsoft.crawler.web.service.crawler.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.redis.JedisPoolFactory;
import com.zxsoft.crawler.web.service.crawler.JobService;
import com.zxsoft.crawler.web.service.crawler.ReptileService;

@Service
public class JobServiceImpl  implements JobService {

    private static Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    private static final String URLBASE = "urlbase";

//    @Autowired
//    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private ReptileService reptileService;

    private JedisPool getJedisPool(int reptileId) {
        Reptile reptile = reptileService.getReptile(reptileId);
        String rhp = reptile.getRedis();
        String[] hp = rhp.split(":"); 
        
        String host=hp[0], passwd = "";
        int port = 6379;
        if (hp[1].contains(";")) {
            String[] _str = hp[1].split(";");
            port = Integer.valueOf(_str[0]);
            passwd = _str[1];
        } else {
            port = Integer.valueOf(hp[1]);
        }
        
        JedisPool pool = JedisPoolFactory.getInstance(host, port, passwd);
        return pool;
    }
    
    @Override
    public Map<String, Object> addJob(Integer reptileId,final JobConf jobConf) throws CrawlerException {
        
//        Reptile reptile = reptileService.getReptile(reptileId);
//        String rhp = reptile.getRedis();
//        String[] hp = rhp.split(":"); 
//        
//        String host=hp[0], passwd = "";
//        int port = 6379;
//        if (hp[1].contains(";")) {
//            String[] _str = hp[1].split(";");
//            port = Integer.valueOf(_str[0]);
//            passwd = _str[1];
//        } else {
//            port = Integer.valueOf(hp[1]);
//        }
//        
//        JedisPool pool = JedisPoolFactory.getInstance(host, port, passwd);
        JedisPool pool = getJedisPool(reptileId);
        
        Jedis jedis = pool.getResource();

        try {
            Page<JobConf> page = getJobs(reptileId,jobConf.getUrl(), 0, 10);
            if (page != null && !CollectionUtils.isEmpty(page.getRes())) {
                throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "Job exist");
            }
            // 1434342855662
            Random random = new Random();
            long jobId = System.currentTimeMillis() - random.nextInt(9999999);
            jobConf.setJobId(jobId);
            double score = 1.0d / (1.0d + jobConf.getFetchinterval() * 1.0d);
            jedis.zadd(URLBASE,score, jobConf.toString());
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }
    
    /**
     * 
     */
    @Override
    public void addOrUpdate(Integer reptileId, JobConf job){
        
        JedisPool pool = getJedisPool(reptileId);
        Jedis jedis = pool.getResource();
        
        try {
            JobConf _job = getJob(reptileId, job.getJobId());
            if (_job != null) {
                jedis.zrem(URLBASE, _job.toString());
            }
           
            Random random = new Random();
            long jobId = System.currentTimeMillis() - random.nextInt(9999999);
            job.setJobId(jobId);
            double score = 1.0d / (1.0d + job.getFetchinterval() * 1.0d);
            jedis.zadd(URLBASE, score, job.toString());
            
            
            LOG.info("更新任务为："+job.getSource_name()+"-->"+job.getType());
 
          
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    @Override
    public Page<JobConf> getJobs(Integer reptileId,String query, int start, int end) {
        if (start < 0) // redis index begin with zero
            start = 1;
        start = start - 1;
        end = end - 1;
        
//        Reptile reptile = reptileService.getReptile(reptileId);
        JedisPool pool = getJedisPool(reptileId);
        Jedis jedis = pool.getResource();
        
        try {
            long count = jedis.zcard(URLBASE);
    
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    
            if (StringUtils.isEmpty(query)) {
                Set<String> jsons = jedis.zrevrange(URLBASE, start, end);
                List<JobConf> res = new ArrayList<JobConf>();
    
                for (String json : jsons) {
                    JobConf job = null;
                    try {
                        job = gson.fromJson(json, JobConf.class);
                    } catch (Exception e) {
                        continue;
                    }
                    res.add(job);
                }
    
                return new Page<JobConf>(count, res);
            }
    
            List<JobConf> res = new ArrayList<JobConf>();
            long i = 0, j = 100, p=0;
            boolean flag = false;
            while (i < count && !flag) {
                Set<String> jsons = jedis.zrange(URLBASE, i, j);
                if (CollectionUtils.isEmpty(jsons))
                    break;
                
                for (String json : jsons) {
                    if (json.contains(query)) {
                        try {
                            p++;
                            if (p >= start) {
                                JobConf job = gson.fromJson(json, JobConf.class);
                                if (job.getUrl().equalsIgnoreCase(query) 
                                                || job.getSource_name().contains(query)
                                                || job.getType().contains(query)) {
                                    res.add(job);
                                }
                            }
                            if (res.size() >= end - start) {
                                flag = true;
                                break;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                        
                    }
                }
    
                i=j;
                j=j+100;
            }
            return new Page<JobConf>(count, res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.returnResource(jedis);
        }

    }

    @Override
    public JobConf getJob(Integer reptileId, long jobId) {
        long i = 0, j = 100;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        JedisPool pool = getJedisPool(reptileId);
        Jedis jedis = pool.getResource();
        
        try {
            long count = jedis.zcard(URLBASE);
            while (i < count ) {
                Set<String> jsons = jedis.zrange(URLBASE, i, j);
                if (CollectionUtils.isEmpty(jsons))
                    return null;
                for (String json : jsons) {
                    JobConf job = null;
                    try {
                        job = gson.fromJson(json, JobConf.class);
                        if (jobId == job.getJobId()) {
                            return job;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                i=j;
                j=j+100;
            }
        }finally {
            pool.returnResource(jedis);
        }
            return null;
    }

    @Override
    public void updateJob(Integer reptileId, JobConf job) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        JedisPool pool = getJedisPool(reptileId);
        Jedis jedis = pool.getResource();
        
        try {
            long count = jedis.zcard(URLBASE);
            long i = 0, j = 100;
            while (i < count ) {
                Set<String> jsons = jedis.zrange(URLBASE, i, j);
                if (CollectionUtils.isEmpty(jsons))
                    return ;
                for (String json : jsons) {
                    JobConf _job = null;
                    try {
                        _job = gson.fromJson(json, JobConf.class);
                        if (job.getJobId() == _job.getJobId()) {
                            // FIXME: use transaction
                            jedis.zrem(URLBASE, json);
                            double score = 1.0d / (System.currentTimeMillis() / 60000.0d + job.getFetchinterval() * 1.0d);
                            jedis.zadd(URLBASE, score,job.toString());
                            return;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                i=j;
                j=j+100;
            }
        } finally {
            pool.returnResource(jedis);
        }

    }

    @Override
    public void deleteJob(Integer reptileId, long jobId) {
        JedisPool pool = getJedisPool(reptileId);
        Jedis jedis = pool.getResource();
        
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        try {
            long count = jedis.zcard(URLBASE);
            long i = 0, j = 100;
            while (i < count ) {
                Set<String> jsons = jedis.zrange(URLBASE, i, j);
                if (CollectionUtils.isEmpty(jsons))
                    return ;
                for (String json : jsons) {
                    JobConf job = null;
                    try {
                        job = gson.fromJson(json, JobConf.class);
                        if (jobId == job.getJobId()) {
                            jedis.zrem(URLBASE, json);
                            return;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                i=j;
                j=j+100;
            }
        } finally {
            pool.returnResource(jedis);
        }
    }

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;
    
    @Override
    public JobConf querySourceId(int tid) throws CrawlerException {
        List<JobConf> list = oracleJdbcTemplate.query(
                        "select sourceid ,zdbs platform from  fllb_cjlb  where id = ? ",
                        new Object[] { tid }, new RowMapper<JobConf>() {
                            @Override
                            public JobConf mapRow(ResultSet rs, int arg1)
                                            throws SQLException {
                                JobConf jobConf = new JobConf();
                                jobConf.setSource_id(rs.getInt("sourceid"));
                                jobConf.setPlatform(rs.getInt("platform"));
                                return jobConf;
                            }
                        });

        if (CollectionUtils.isEmpty(list)) {
            throw new CrawlerException(ErrorCode.CONF_ERROR, "Cannot find sourceid from fllb_cjlb using id: " + tid);
        }
        return list.get(0);
    }

}
