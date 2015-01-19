package com.zxsoft.crawler.web.controller.crawler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zxisl.commons.io.ClassPathResource;
import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.api.Params;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Prey;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.master.MasterPath;
import com.zxsoft.crawler.web.controller.crawler.JobStatus.JobType;
import com.zxsoft.crawler.web.service.crawler.JobService;
import com.zxsoft.crawler.web.service.crawler.impl.JobServiceImpl;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.DictService;
import com.zxsoft.crawler.web.service.website.SectionService;

/**
 * 任务接口
 * @author xiayun
 *
 */
@Controller
@RequestMapping(MasterPath.SLAVE_RESOURCE_PATH)
public class JobController {

        private static Logger LOG = LoggerFactory.getLogger(JobController.class);
        
        private JobService jobService = new JobServiceImpl();
        @Autowired
        private SectionService sectionService;
        @Autowired
        private DictService dictService;
        @Autowired
        private ConfigService configService;

        private static final String URLBASE = "urlbase";
        private static final String REDIS_HOST;
        private static final int REDIS_PORT;

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

        /**
         * 列出任务种子
         * @param index 个数
         * @param model
         * @return
         */
        @RequestMapping(value = "preys/{index}", method = RequestMethod.GET)
        public String jobs(@PathVariable(value = "index") int index, Model model) {
                List<Prey> list = new LinkedList<Prey>();
                Jedis jedis = new Jedis(REDIS_HOST, 6379);
                long count = 0;
                try {
                        count = jedis.zcard(URLBASE);
                        Set<String> set = jedis.zrevrange(URLBASE, 0, index - 1);
                        if (!CollectionUtils.isEmpty(set)) {
                                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                                for (String str : set) {
                                        Prey prey = gson.fromJson(str, Prey.class);
                                        list.add(prey);
                                }
                        }
                } catch (JedisConnectionException e) {
                        model.addAttribute("code", 5000);
                        model.addAttribute("msg", "Redis没有启动: " + REDIS_HOST + ":" + 6379);
                        e.printStackTrace();
                } finally {
                        model.addAttribute("count", count);
                        model.addAttribute("preys", list);
                        jedis.close();
                }

                List<ConfList> confLists = configService.getInspectConfLists(null);
                model.addAttribute("confLists", confLists);

                List<ConfList> engines = dictService.getSearchEngines();
                model.addAttribute("engines", engines);
                
                model.addAttribute("currentTime", new Date().toLocaleString());

                return "/crawler/preys";
        }
        
        /**
         * 查询任务
         * @param name
         * @return
         */
        @RequestMapping(value = "jobs", method = RequestMethod.POST)
        public String  jobs(@RequestParam(value = "job", required = false) String job, Model model) {
                List<Prey> list = new LinkedList<Prey>();
                model.addAttribute("currentTime", new Date().toLocaleString());
                model.addAttribute("searchJobKey", job);
                if (StringUtils.isEmpty(job)) {
                        return jobs(20, model);
                }
                Jedis jedis = new Jedis(REDIS_HOST, 6379);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                long count = 0, begin = 0, end = 100;
                try {
                        count = jedis.zcard(URLBASE);
                        while (begin < count) {
                                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                                if (CollectionUtils.isEmpty(set)) {
                                        break;
                                }
                                for (String str : set) {
                                        Prey prey = gson.fromJson(str, Prey.class);
                                        if (prey.toString().contains(job)) {
                                                list.add(prey);
                                        }
                                }
                                begin = end;
                                end = end + 100;
                        }
                } catch (JedisConnectionException e) {
                        e.printStackTrace();
                        model.addAttribute("code", 5000);
                        model.addAttribute("msg", "Redis没有启动: " + REDIS_HOST + ":" + 6379);
                } finally {
                        model.addAttribute("count", count);
                        model.addAttribute("preys", list);
                        jedis.close();
                }
                
                return "/crawler/preys";
        }
        
        /**
         * 删除任务
         * @param comment
         * @param start
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/job/delete", method = RequestMethod.POST)
        public String deleteJob(@RequestParam(value = "comment", required = false) String comment,
                                        @RequestParam(value = "start", required = false) long start) {
                if (StringUtils.isEmpty(comment) || start == 0L) {
                        return "{\"code\":-1}";
                }
               String ret = "";
                Jedis jedis = new Jedis(REDIS_HOST, 6379);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                long count = 0, begin = 0, end = 100;
                try {
                        count = jedis.zcard(URLBASE);
                        while (begin < count) {
                                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                                if (CollectionUtils.isEmpty(set)) {
                                        ret = "{\"code\":-1}";
                                        break;
                                }
                                for (String str : set) {
                                        Prey prey = gson.fromJson(str, Prey.class);
                                        String json = prey.toString();
                                        if (json.contains(comment) && json.contains(String.valueOf(start))) {
                                               long retNum = jedis.zrem(URLBASE, json);
                                               if (retNum== 0){
                                                       ret = "{\"code\":-1}";
                                               }
                                                ret = "{\"code\":1}";
                                                break;
                                        }
                                }
                                begin = end;
                                end = end + 100;
                        }
                } catch (JedisConnectionException e) {
                        e.printStackTrace();
                } finally {
                        jedis.close();
                }
                
                return ret;
        }

        /**
         * 暂停任务
         * @param comment
         * @param start
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/job/halt", method = RequestMethod.POST)
        public String haltOrStartJob(@RequestParam(value = "comment", required = false) String comment,
                                        @RequestParam(value = "start", required = false) long start) {
                if (StringUtils.isEmpty(comment) || start == 0L) {
                        return "{\"code\":-1}";
                }
               String ret = "";
                Jedis jedis = new Jedis(REDIS_HOST, 6379);
                
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                long count = 0, begin = 0, end = 100;
                try {
                        count = jedis.zcard(URLBASE);
                        while (begin < count) {
                                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                                if (CollectionUtils.isEmpty(set)) {
                                        ret = "{\"code\":-1}";
                                        break;
                                }
                                for (String str : set) {
                                        Prey prey = gson.fromJson(str, Prey.class);
                                        String json = prey.toString();
                                        if (json.contains(comment) && json.contains(String.valueOf(start))) {
                                                Transaction t = jedis.multi();
                                                Response<Long> res = t.zrem(URLBASE, str);
                                                if (prey.getState() == 0 ) { // 已暂停,点击后开始
                                                        prey.setState(1);
                                                        t.zadd(URLBASE, 0.999d, prey.toString());
                                                } else { // 暂停
                                                        prey.setState(0);
                                                        t.zadd(URLBASE, 0.0d, prey.toString());
                                                }
                                               t.exec();
                                               
                                               ret = "{\"code\":1}";
                                                break;
                                        }
                                }
                                begin = end;
                                end = end + 100;
                        }
                } catch (JedisConnectionException e) {
                        e.printStackTrace();
                } finally {
                        jedis.close();
                }
                
                return ret;
        }
        
        /**
         * 添加搜索任务
         * @param keyword
         * @param engineIds
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/addSearchJob", method = RequestMethod.POST)
        public Map<String, Object> addSearchJob(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "engineId", required = false) List<String> engineIds) {
                Assert.hasLength(keyword);
                Assert.notEmpty(engineIds);
                for (String engineId : engineIds) {
                        Map<String, Object> args = new HashMap<String, Object>();
                        args.put(Params.KEYWORD, keyword);
                        args.put(Params.ENGINE_URL, engineId);
                        jobService.addSearchJob(args);
                }
                return null;
        }

        /**
         * 添加网络巡检任务
         * @param url
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/addInspectJob", method = RequestMethod.POST)
        public Map<String, Object> addInspectJob(@RequestParam(value = "url", required = false) String url) {
                Assert.hasLength(url);
                Map<String, Object> args = new HashMap<String, Object>();
                if (url.endsWith("/")) {
                        url = url.substring(0, url.lastIndexOf("/"));
                }
                ConfList confList = configService.getConfList(url);
                if (confList == null) {
                        args.put("msg", "noconflist");
                        return args;
                }

                Section section = sectionService.getSectionByUrl(url);
                if (section == null)
                        throw new NullPointerException("section is null, but conflist is not null: " + url);
                Website website = section.getWebsite();
                String site = website.getSite();
                Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
                Prey prey = null;
                try {
                        // 判断任务列表中是否已存在该任务
                        boolean exist = jobExist(url);
                        if (exist) {
                                args.put("msg", "jobexist");
                        } else {
                                // 添加任务
                                double score = 1.0d / (System.currentTimeMillis() / 60000 );
                                prey = new Prey(site, url, confList.getComment(), JobType.NETWORK_INSPECT.toString(), confList.getFetchinterval());
                                prey.setStart(System.currentTimeMillis());
                                LOG.info("添加任务:" + prey.toString());
                                jedis.zadd(URLBASE, score, prey.toString());
                        }
                } catch (JedisConnectionException e) {
                        args.put("msg", "jedisconnectionexception");
                        e.printStackTrace();
                } finally {
                        jedis.close();
                }
                return args;
        }
        
        /**
         * 判断url任务是否已在redis队列中
         * @param url
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/jobExist", method = RequestMethod.POST)
        public boolean jobExist(@RequestParam(value = "url", required = false) String url) {
                Assert.hasLength(url);
                if (url.endsWith("/")) {
                        url = url.substring(0, url.lastIndexOf("/"));
                }
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT);
                long count = 0, begin = 0, end = 100;
                try {
                        count = jedis.zcard(URLBASE);
                        while (begin < count) {
                                Set<String> set = jedis.zrevrange(URLBASE, begin, end);
                                if (CollectionUtils.isEmpty(set)) break;
                                for (String str : set) {
                                        Prey _prey = gson.fromJson(str, Prey.class);
//                                        String json = _prey.toString();
                                        String _url = _prey.getUrl();
                                        if (_url.endsWith("/")) {
                                                _url = _url.substring(0, _url.lastIndexOf("/"));
                                        }
                                        if (url.equals(_url)) {
                                                return true;
                                        }
//                                        if (json.contains(String.valueOf(url))) {
//                                                return true;
//                                        }
                                }
                                begin = end;
                                end = end + 100;
                        }
                } catch (JedisConnectionException e) {
                        LOG.error(e.getMessage());
                } finally {
                        jedis.close();
                }
                return false;
        }
        

        /**
         * 查找已配置版块
         * 
         * @param name
         *                版块名称或地址
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "ajax/preys", method = RequestMethod.POST)
        public List<ConfList> preys(@RequestParam(value = "name", required = false) String name) {

                ConfList param1 = new ConfList();
                param1.setUrl(name);
                List<ConfList> confLists = configService.getInspectConfLists(param1);

                ConfList param2 = new ConfList();
                param2.setComment(name);
                List<ConfList> confLists2 = configService.getInspectConfLists(param2);

                confLists.addAll(confLists2);
                return confLists;
        }

}
