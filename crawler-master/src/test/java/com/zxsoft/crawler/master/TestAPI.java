package com.zxsoft.crawler.master;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.ClientResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.common.WorkerConf;

public class TestAPI {

    private static MasterServer server;
    private static String baseUrl = "http://localhost:8380/master/";

    @BeforeClass
    public static void before() throws Exception {
        server = new MasterServer(8380, "localhost", 6379, "");
        server.start();
    }

    @AfterClass
    public static void after() throws Exception {
        if (!server.stop(false)) {
            for (int i = 1; i < 15; i++) {
                System.err.println("Waiting for jobs to complete - " + i + "s");
                try {
                    Thread.sleep(20000);
                } catch (Exception e) {
                }
                ;
                server.stop(false);
                if (!server.isRunning()) {
                    break;
                }
            }
        }
        if (server.isRunning()) {
            System.err.println("Forcibly stopping server...");
            server.stop(true);
        }
    }

    @Test
    public void testWorkerAPI() throws Exception {
        ClientResource cli = new ClientResource(baseUrl + MasterPath.WORKER_RESOURCE_PATH);

        JacksonRepresentation<WorkerConf> jr = null;
        WorkerConf worker = new WorkerConf(321, "192.168.3.21:8989", 0);
        jr = new JacksonRepresentation<WorkerConf>(worker);
        cli.post(jr).getText();

        worker = new WorkerConf(322, "192.168.3.23:8989", 1);
        jr = new JacksonRepresentation<WorkerConf>(worker);
        cli.post(jr).getText();

        worker = new WorkerConf(323, "192.168.3.23:8989", 2);
        jr = new JacksonRepresentation<WorkerConf>(worker);
        cli.post(jr).getText();

        String json = cli.get().getText();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

//        List<Map> workers = gson.fromJson(json, .class);
//        
//        for (Map<String, Object> _map: workers) {
//            
//        }
    }
    
    @Test
    public void testJobAPI_CreateJob() throws Exception {
        ClientResource cli = new ClientResource(baseUrl + MasterPath.JOB_RESOURCE_PATH);

        JacksonRepresentation<JobConf> jr = null;
        ListRule listRule = new ListRule();
        Set<DetailRule> detailRules = new HashSet<DetailRule>();
        JobConf jobConf = new JobConf(JobType.NETWORK_INSPECT, "http://test.com", "sourcename test", 123, 321, "type test", listRule, detailRules);
        jobConf.setJobId(1);
        jr = new JacksonRepresentation<JobConf>(jobConf);
        cli.post(jr).getText();

//        jobConf = new JobConf(JobType.NETWORK_SEARCH, url, source_name, source_id, sectionId, type, listRule, detailRules)
//        jr = new JacksonRepresentation<JobConf>(jobConf);
//        cli.post(jr).getText();

    }
    
    /**
     * 查询或删除
     * @throws Exception
     */
    @Test
    public void testJobAPI_Jobs() throws Exception {
        ClientResource cli = new ClientResource(baseUrl + MasterPath.JOB_RESOURCE_PATH + "?operation=get");
        String json = cli.get().getText();
        System.out.println(json);
        
        String url = baseUrl + MasterPath.JOB_RESOURCE_PATH + "?operation=delete&jobId=1";
        ClientResource cli2 = new ClientResource(url);
        System.out.println(cli2.get().getText());
    }
    
    @Test
    public void testJobAPI_update() throws Exception {
        ClientResource cli = new ClientResource(baseUrl + MasterPath.JOB_RESOURCE_PATH);
        
        JacksonRepresentation<JobConf> jr = null;
        JobConf jobConf = new JobConf();
        jr = new JacksonRepresentation<JobConf>(jobConf);
        String json = cli.put(jr).getText();
        System.out.println(json);
    }

}
