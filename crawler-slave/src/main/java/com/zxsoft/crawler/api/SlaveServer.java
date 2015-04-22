package com.zxsoft.crawler.api;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.IPUtil;
import com.zxsoft.crawler.api.JobStatus.State;
import com.zxsoft.crawler.api.impl.ErrorHandler;
import com.zxsoft.crawler.api.impl.TickThread;
import com.zxsoft.crawler.slave.SlavePath;
import com.zxsoft.crawler.slave.utils.DbService;

public class SlaveServer {
    private static final Logger LOG = LoggerFactory.getLogger(SlaveServer.class);

    private Component component;
    private SlaveApp app;
    private int port;
    private static boolean running;

    private static boolean enableSearch = false;
    private static String oracle_url;
    private static String oracle_username;
    private static String oracle_passwd;

    public static String getOracle_url() {
        return oracle_url;
    }
    public static void setOracle_url(String oracle_url) {
        SlaveServer.oracle_url = oracle_url;
    }
    public static String getOracle_username() {
        return oracle_username;
    }

    public static void setOracle_username(String oracle_username) {
        SlaveServer.oracle_username = oracle_username;
    }

    public static String getOracle_passwd() {
        return oracle_passwd;
    }

    public static void setOracle_passwd(String oracle_passwd) {
        SlaveServer.oracle_passwd = oracle_passwd;
    }

    private String master;

    public static  boolean enableSearch() {
        return enableSearch;
    }

    public SlaveServer(int port) {
        this.port = port;
        // Create a new Component.
        component = new Component();

        // Add a new HTTP server listening on port 8989.
        component.getServers().add(Protocol.HTTP, port);

        // Attach the application.
        app = new SlaveApp();

        component.getDefaultHost().attach("/" + SlavePath.PATH, app);

        component.getContext().getParameters().set("maxThreads", "1000");

        SlaveApp.server = this;
    }

    public static boolean isRunning() {
        return running;
    }

    private static int machineId;
    private static DbService dbService;

    public static  DbService getDbService() {
        return dbService;
    }

    public static int getMachineId() {
        return machineId;
    }

    public void start() throws Exception {

        List<String> ips = IPUtil.getIPv4();
        String hostPort = "";
        if (!CollectionUtils.isEmpty(ips)) {
            String ip = ips.get(0);
            hostPort = ip + ":" + port;
            String[] nums = ip.split("\\.");
            machineId = Integer.valueOf(nums[2] + nums[3]);
            LOG.info("Server Id: " + machineId);
        }

        LOG.info("Starting SlaveNode on port " + port + "...");
        component.start();
        LOG.info("Started SlaveNode on port " + port);
        running = true;
        SlaveApp.started = System.currentTimeMillis();

        if (enableSearch) {
            Assert.hasLength(oracle_url);
            Assert.hasLength(oracle_username);  
            
            dbService = new DbService(oracle_url.trim(), oracle_username.trim(), oracle_passwd.trim());
            dbService.updateExecuteTaskStatus();
        }

        if (master != null && master.length() != 0) {  // 集群模式
            new TickThread(master, machineId, hostPort, 30000L).start();
            // 配置错误处理器
            ErrorHandler.setMaster(master);
        }
    }

    public boolean canStop() throws Exception {
        List<JobStatus> jobs = SlaveApp.jobMgr.list(null, State.RUNNING);
        if (!jobs.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean stop(boolean force) throws Exception {
        if (!running) {
            return true;
        }
        if (!canStop() && !force) {
            LOG.warn("Running jobs - can't stop now.");
            return false;
        }
        LOG.info("Stopping SlaveServer on port " + port + "...");
        component.stop();
        LOG.info("Stopped SlaveServer on port " + port);
        running = false;
        return true;
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("help", false, "Display this help imformation");
        options.addOption("port", true, "Slave Server port");
        options.addOption("master", true, "master host");
        options.addOption("enableSearch", false, "Enable Search Job");
        options.addOption("oracle_url", true, "oracle host for urlbase");
        options.addOption("oracle_username", true, "oracle username ");
        options.addOption("oracle_passwd", true, "oracle password");

        CommandLineParser parser = new GnuParser();

        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("com.zxsoft.crawler.master.MasterServer", options);
                System.exit(0);
            }
            int port = Integer.valueOf(line.getOptionValue("port", "8989"));

            SlaveServer server = new SlaveServer(port);

            server.master = line.getOptionValue("master");
            
            if (line.hasOption("enableSearch")) {
                SlaveServer.enableSearch = true;
                SlaveServer.oracle_url = line.getOptionValue("oracle_url");
                SlaveServer.oracle_username = line.getOptionValue("oracle_username", null);
                SlaveServer.oracle_passwd = line.getOptionValue("oracle_passwd", null);
            }

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
