package com.zxsoft.crawler.web.service.crawler.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.thinkingcloud.framework.web.utils.Page;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.CrawlerException.ErrorCode;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.service.crawler.JobService;

@Service
public class JobServiceImpl extends SimpleCrawlerServiceImpl implements JobService {

    private static Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);

    private static final String URLBASE = "urlbase";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Map<String, Object> addJob(final JobConf jobConf) throws CrawlerException {
//        ClientResource cli = new ClientResource(CRAWLER_MASTER
//                        + MasterPath.JOB_RESOURCE_PATH);
//        try {
//            Representation r = cli.post(jobConf);
//            LOG.info(r.getText());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cli.release();
//        }
        
        Page<JobConf> page = getJobs(jobConf.getUrl(), 0, 10);
        if (page != null && !CollectionUtils.isEmpty(page.getRes())) {
            throw new CrawlerException(ErrorCode.SYSTEM_ERROR, "Job exist");
        }
        
        Random random = new Random(9999999);
        long jobId = System.currentTimeMillis() - random.nextLong();
        jobConf.setJobId(jobId);
        double score = 1.0d / (System.currentTimeMillis() / 60000.0d + jobConf.getFetchinterval() * 1.0d);
        redisTemplate.opsForZSet().add(URLBASE, jobConf.toString(), score);
        return null;
    }

    @Override
    public Page<JobConf> getJobs(String query, int start, int end) {
        if (start < 0) // redis index begin with zero
            start = 1;
        start = start - 1;
        end = end - 1;

        long count = redisTemplate.opsForZSet().zCard(URLBASE);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        if (StringUtils.isEmpty(query)) {
            Set<String> jsons = redisTemplate.opsForZSet().range(URLBASE, start, end);
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
            Set<String> jsons = redisTemplate.opsForZSet().range(URLBASE, i, j);
            if (CollectionUtils.isEmpty(jsons))
                break;
            
            for (String json : jsons) {
                if (json.contains(query)) {
                    try {
                        p++;
                        if (p >= start) {
                            JobConf job = gson.fromJson(json, JobConf.class);
                            res.add(job);
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
    }

    @Override
    public JobConf getJob(int jobId) {
        long i = 0, j = 100;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        long count = redisTemplate.opsForZSet().zCard(URLBASE);
        while (i < count ) {
            Set<String> jsons = redisTemplate.opsForZSet().range(URLBASE, i, j);
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
        return null;
    }

    @Override
    public void updateJob(JobConf job) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        long count = redisTemplate.opsForZSet().zCard(URLBASE);
        long i = 0, j = 100;
        while (i < count ) {
            Set<String> jsons = redisTemplate.opsForZSet().range(URLBASE, i, j);
            if (CollectionUtils.isEmpty(jsons))
                return ;
            for (String json : jsons) {
                JobConf _job = null;
                try {
                    _job = gson.fromJson(json, JobConf.class);
                    if (job.getJobId() == _job.getJobId()) {
                        // FIXME: use transaction
                        redisTemplate.opsForZSet().remove(URLBASE, json);
                        double score = 1.0d / (System.currentTimeMillis() / 60000.0d + job.getFetchinterval() * 1.0d);
                        redisTemplate.opsForZSet().add(URLBASE, job.toString(), score);
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            i=j;
            j=j+100;
        }

    }

    @Override
    public void deleteJob(int jobId) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        long count = redisTemplate.opsForZSet().zCard(URLBASE);
        long i = 0, j = 100;
        while (i < count ) {
            Set<String> jsons = redisTemplate.opsForZSet().range(URLBASE, i, j);
            if (CollectionUtils.isEmpty(jsons))
                return ;
            for (String json : jsons) {
                JobConf job = null;
                try {
                    job = gson.fromJson(json, JobConf.class);
                    if (jobId == job.getJobId()) {
                        redisTemplate.opsForZSet().remove(URLBASE, json);
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            i=j;
            j=j+100;
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
