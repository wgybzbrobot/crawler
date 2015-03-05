package com.zxsoft.crawler.master;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zxisl.commons.io.ClassPathResource;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.api.Prey;
import com.zxsoft.crawler.master.impl.RAMSlaveManager;
import com.zxsoft.crawler.util.URLFormatter;

/**
 * 主控节点
 */
public class MasterServer {
        private static final Logger LOG = LoggerFactory.getLogger(MasterServer.class);

        private Component component;
        private MasterApp app;
        private int port;
        private boolean running;

        public MasterServer(int port) {
                this.port = port;
                // Create a new Component.
                component = new Component();
                // Add a new HTTP server listening on port 8182.
                component.getServers().add(Protocol.HTTP, port);
                // Attach the application.
                app = new MasterApp();
                component.getDefaultHost().attach("/master", app);
                component.getContext().getParameters().add("maxThreads", "1000");
                MasterApp.server = this;
        }

        public boolean isRunning() {
                return running;
        }

        private static final String URLBASE = "urlbase";
        private static final String REDIS_HOST;
        private static final int REDIS_PORT;
        private final long heartbeat = 3 * 60 * 1000L; // default is 3 min

        static {
                ClassPathResource resource = new ClassPathResource("redis.properties");
                Properties properties = new Properties();
                try {
                        properties.load(resource.getInputStream());
                } catch (IOException e) {
                        e.printStackTrace();
                }
                REDIS_HOST = properties.getProperty("redis.host");
                REDIS_PORT = Integer.valueOf(properties.getProperty("redis.port"));

                if (StringUtils.isEmpty(REDIS_HOST)) {
                        throw new NullPointerException("redis.properties中没有配置<redis.host>");
                }
                if (StringUtils.isEmpty(REDIS_HOST)) {
                        throw new NullPointerException("redis.properties中没有配置<redis.prot>");
                }
        }

        public void start() throws Exception {
                LOG.info("Starting MasterNode on port " + port + "...");
                component.start();
                LOG.info("Started MasterNode on port " + port);
                running = true;
                MasterApp.started = System.currentTimeMillis();

                SlaveManager slaveManager = new RAMSlaveManager();
                slaveManager.list();

                // 监测slave
                new Thread(new Runnable() {
                        public void run() {
                                while (true) {
                                        try {
                                                SlaveManager slaveManager = new RAMSlaveManager();
                                                try {
                                                        slaveManager.list();
                                                } catch (Exception e) {
                                                        e.printStackTrace();
                                                }
                                                LOG.info("SlaveMonitorThread sleep " + heartbeat / 60000 + " minutes");
                                                TimeUnit.MILLISECONDS.sleep(heartbeat);
                                        } catch (InterruptedException e) {
                                                e.printStackTrace();
                                        }
                                }
                        }
                }, "SlaveMonitorThread").start();

                // 任务队列管理
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                                synchronized (this) {
                                        boolean shouldSleep = false;
                                        while (true) {
                                                if (shouldSleep){
                                                        try {
                                                                TimeUnit.SECONDS.sleep(10);
                                                        } catch (InterruptedException e) {
                                                                LOG.error(e.getMessage());
                                                                e.printStackTrace();
                                                        } finally {
                                                                shouldSleep = false;
                                                        }
                                                }
                                        
                                                Jedis jedis = null;
                                                try {
                                                        jedis = new Jedis(REDIS_HOST, REDIS_PORT);
                                                        Set<String> strs = null;
                                                        strs = jedis.zrevrange(URLBASE, 0, 0);

                                                        if (CollectionUtils.isEmpty(strs)) {
                                                                LOG.warn("No records in redis urlbase, sleep 10s then try again.");
                                                                shouldSleep = true;
                                                                continue;
                                                        }
                                                        String json = strs.toArray(new String[0])[0];

                                                        Prey prey = null;
                                                        try {
                                                                prey = gson.fromJson(json, Prey.class);
                                                        } catch (JsonSyntaxException e) {
                                                                LOG.warn(e.getLocalizedMessage() + ", will remove it from urlbase.");
                                                                jedis.zrem(URLBASE, json);
                                                                continue;
                                                        }
                                                        if (prey == null) {
                                                                continue;
                                                        }
                                                        long interval = System.currentTimeMillis() - prey.getPrevFetchTime();
                                                        long realInterval = prey.getFetchinterval() * 60 * 1000L;
                                                        if (interval >= realInterval) {
                                                                long res = jedis.zrem(URLBASE, json);
                                                                if (res != 1L) {
                                                                        LOG.error(json + " is not member of urlbase, cannot remove it. And it will not create job to slaves.");
                                                                }
                                                                // 将上次抓取时间设置为当前时间，供下次抓取使用
                                                                prey.setPrevFetchTime(System.currentTimeMillis());
                                                                prey.setCount(prey.getCount() + 1);
                                                                double score = 1.0d / (System.currentTimeMillis() / 60000.0d + prey.getFetchinterval() * 1.0d);
                                                                jedis.zadd(URLBASE, score, prey.toString());
                                                        } else {
                                                                long wait = realInterval - interval;
                                                                if (wait > 120000L)
                                                                        wait = 120000L;
                                                                LOG.info("Sleep " + wait + " milliseconds");
                                                                try {
                                                                        Thread.sleep(wait);
                                                                } catch (InterruptedException e) {
                                                                        e.printStackTrace();
                                                                }
                                                                continue;
                                                        }
                                                        LOG.info("Distributing Job: " + prey.toString());
                                                        SlaveManager slaveManager = new RAMSlaveManager();
                                                        try {
                                                                slaveManager.create(prey);
                                                        } catch (Exception e) {
                                                                LOG.warn(e.getMessage());
                                                                e.printStackTrace();
                                                        }
                                                } catch (JedisConnectionException e) {
                                                        LOG.error(e.getMessage() + ", cannot connect to redis, sleep 10s then try again.");
                                                        shouldSleep = true;
                                                } finally {
                                                        if (jedis != null) {
                                                                jedis.close();
                                                        }
                                                }
                                        }
                                }
                        }
                }, "TaskSchedulerThread").start();

                if (enableGetNetworkSearchTaskFromDb) {
                        int realInterval = 10;
                        boolean searchTaskExecutable = false;
                        try {
                                Properties prop = new Properties();
                                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                                InputStream stream = loader.getResourceAsStream("oracle.properties");
                                prop.load(stream);
                                realInterval = Integer.valueOf( prop.getProperty("read.seconds.interval", "10"));
                                searchTaskExecutable = true;
                        } catch (Exception e) {
                                LOG.error("从oracle.properties中读取read.seconds.interval失败", e);
                                LOG.warn("将不会从数据库中读取全网搜索任务, 但您可以调用接口执行全网搜索任务.");
                        }
                        if (searchTaskExecutable) {
                                new Thread( new NetworkSearchThread(realInterval), "NetworkSearchJobSchedular").start();
                        }
                }
                
        }

        private static boolean enableGetNetworkSearchTaskFromDb = false;
        public static boolean enableGetNetworkSearchTaskFromDb() {
                return enableGetNetworkSearchTaskFromDb;
        }
        public static void main(String[] args) throws Exception {
                if (args.length == 0) {
                        System.err.println("Usage: CrawlerServer <port> [enableSearchTask]");
                        System.exit(-1);
                }
                if (args.length == 2) {
                        if ("enableSearchTask".equals(args[1])) {
                                enableGetNetworkSearchTaskFromDb = true;
                        }
                }
                int port = Integer.parseInt(args[0]);
                MasterServer server = new MasterServer(port);
                server.start();
        }

}
