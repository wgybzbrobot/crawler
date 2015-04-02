package com.zxsoft.crawler.urlbase;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.JobConf;

public class TaskSchedulerThread extends Thread {

    private static Logger LOG = LoggerFactory.getLogger(TaskSchedulerThread.class);
    private static final String URLBASE = "urlbase";

    private final String host;
    private final int port;
    private final String passwd;
    private JobCreator jobCreator;
    
    public TaskSchedulerThread(String host, int port, String passwd, JobCreator jobCreator) {
        setName("TaskSchedulerThread");
        this.host = host;
        this.port = port;
        this.passwd = passwd;
        this.jobCreator = jobCreator;
    }

    @Override
    public void run() {
        JedisPool jedisPool = JedisPoolFactory.getInstance(host, port, passwd);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        synchronized (this) {
            boolean shouldSleep = false;
            while (true) {
                if (shouldSleep) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        LOG.error("InterruptedException " + e.getMessage());
                    } finally {
                        shouldSleep = false;
                    }
                }

                Jedis jedis = null;
                JobConf jobConf = null;

                try {
                    jedis = jedisPool.getResource();
                    
                    Set<String> strs = jedis.zrevrange(URLBASE, 0, 0);
                    
                    if (CollectionUtils.isEmpty(strs)) {
                        LOG.warn("No records in redis urlbase, sleep 10s then try again.");
                        shouldSleep = true;
                        continue;
                    }
                    String json = strs.toArray(new String[0])[0];

                    try {
                        jobConf = gson.fromJson(json, JobConf.class);
                        if (null == jobConf.getListRule()) {
                            jedis.zrem(URLBASE, json);
                            continue;
                        }
                    } catch (JsonSyntaxException e) {
                        LOG.warn(e.getMessage() + ", will remove it from urlbase.");
                        jedis.zrem(URLBASE, json);
                        continue;
                    }

                    long interval = System.currentTimeMillis() - jobConf.getPrevFetchTime();
                    long realInterval = jobConf.getFetchinterval() * 60 * 1000L;
                    if (interval >= realInterval) {
                        long res = jedis.zrem(URLBASE, json);
                        if (res != 1L) {
                            LOG.error(json + " is not member of urlbase, cannot remove it. And it will not create job to workers.");
                            continue;
                        }
                        // 将上次抓取时间设置为当前时间，供下次抓取使用
                        jobConf.setPrevFetchTime(System.currentTimeMillis());
                        jobConf.setCount(jobConf.getCount() + 1);
                        double score = 1.0d / (System.currentTimeMillis() / 60000.0d + jobConf.getFetchinterval() * 1.0d);
                        jedis.zadd(URLBASE, score, jobConf.toString());
                    } else {
                        long wait = realInterval - interval;
                        if (wait > 60000L)
                            wait = 60000L;
                        LOG.info("Sleep " + wait / 1000 + " seconds");
                        try {
                            Thread.sleep(wait);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    LOG.info("Distributing Job: " + jobConf.toString());
                    
                    try {
                        jobCreator.submitToWorker(jobConf);
                    } catch (CrawlerException | InterruptedException | ExecutionException e2) {
                        LOG.error("Exception occur, put it to urlbase, msg:" + e2.getMessage(), e2); 
                        jobConf.setRecurrence(false);
                        double score = 1.0d / (System.currentTimeMillis() / 60000.0d + jobConf.getFetchinterval() * 1.0d);
                        jedis.zadd(URLBASE, score, jobConf.toString());
                    }
                } catch (JedisConnectionException e) {
                    LOG.error("Cannot connect to redis, sleep 10s then try again.", e);
                    shouldSleep = true;
                    continue;
                } finally {
                    jedisPool.returnResource(jedis);
                }
            }
        }
    }

}
