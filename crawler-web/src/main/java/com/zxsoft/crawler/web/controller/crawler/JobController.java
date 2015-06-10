package com.zxsoft.crawler.web.controller.crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxsoft.crawler.api.JobType;
import com.zxsoft.crawler.common.CrawlerException;
import com.zxsoft.crawler.common.DetailRule;
import com.zxsoft.crawler.common.JobConf;
import com.zxsoft.crawler.common.ListRule;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.crawler.JobService;
import com.zxsoft.crawler.web.service.crawler.ReptileService;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.DictService;
import com.zxsoft.crawler.web.service.website.SectionService;

/**
 * 任务接口
 *
 */
@Controller
@RequestMapping("job")
public class JobController {

    private static Logger LOG = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private JobService jobService;
    @Autowired
    private SectionService sectionService;
    @Autowired
    private DictService dictService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ReptileService reptileService;
    
    /**
     * 查询任务
     * 
     * @param query
     * @param start
     * @param end
     * @param model
     * @return
     */
    @RequestMapping(value = "search/{reptileId}", method = RequestMethod.GET)
    public String jobs(
                    @PathVariable(value = "reptileId") Integer reptileId,
                    @RequestParam(value = "query", required = false) String query,
                    @RequestParam(value = "start", required = false, defaultValue = "1") Integer start,
                    @RequestParam(value = "end", required = false, defaultValue = "10") Integer end,
                    Model model) {
        model.addAttribute("reptileId", reptileId);
        model.addAttribute("query", query);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        Reptile reptile = reptileService.getReptile(reptileId);
        model.addAttribute("reptile", reptile);
        
        Page<JobConf> page = jobService.getJobs(reptileId, query, start, end);
        model.addAttribute("page", page);
        return "/crawler/job";
    }

    /**
     * 删除任务
     * 
     * @param jobId
     * @return
     */
    @RequestMapping(value = "delete/{reptileId}/{jobId}", method = RequestMethod.GET)
    public String deleteJob(@PathVariable(value = "reptileId") Integer reptileId,
                    @PathVariable(value = "jobId") Long jobId) {

        jobService.deleteJob(reptileId, jobId);
        return "redirect:/job/search/" + reptileId;
    }

    /**
     * 暂停任务
     * 
     * @param comment
     * @param start
     * @return
     */
    @RequestMapping(value = "ajax/control/{reptileId}/{jobId}", method = RequestMethod.GET)
    public String haltOrStartJob(@PathVariable(value = "reptileId") Integer reptileId,
                    @PathVariable(value = "jobId") Long jobId) {
        // TODO: 暂停任务
        return "";
    }

    /**
     * 添加网络巡检任务
     * 
     * @param ids
     *            版块id
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @ResponseBody
    @RequestMapping(value = "ajax/addInspectJob/{reptileId}.html", method = RequestMethod.GET)
    public Map<String, Object> addInspectJob(
                    @PathVariable(value = "reptileId") Integer reptileId,
                    @RequestParam(value = "ids", required = false) Set<Integer> ids)
                    throws IllegalArgumentException, IllegalAccessException {

        Map<String, Object> result = new HashMap<String, Object>();

        if (CollectionUtils.isEmpty(ids)) {
            result.put("msg", "请选中要添加的版块");
            return result;
        }
        for (Integer id : ids) {
            try {
                Section section = sectionService.getSection(id);
                ConfList confList = configService.getConfList(section.getUrl());
                List<ConfDetail> confDetails = configService.getConfDetail(confList
                                .getUrl());
                ListRule listRule = new ListRule(confList.getAjax(),
                                confList.getCategory(), confList.getListdom(),
                                confList.getLinedom(), confList.getUrldom(),
                                confList.getDatedom(), confList.getUpdatedom(),
                                confList.getSynopsisdom(), confList.getAuthordom());
                Set<DetailRule> detailRules = new HashSet<DetailRule>();
                for (ConfDetail cd : confDetails) {
                    DetailRule detailRule = new DetailRule(cd.getId().getHost(),
                                    cd.getReplyNum(), cd.getReviewNum(),
                                    cd.getForwardNum(), cd.getSources(),
                                    cd.getFetchOrder(), cd.getAjax(), cd.getMaster(),
                                    cd.getAuthor(), cd.getDate(), cd.getContent(),
                                    cd.getReply(), cd.getReplyAuthor(),
                                    cd.getReplyDate(), cd.getReplyContent(),
                                    cd.getSubReply(), cd.getSubReplyAuthor(),
                                    cd.getSubReplyDate(), cd.getSubReplyContent());
                    detailRule.setEncode(cd.getEncode());
                    detailRules.add(detailRule);
                }

                Website website = section.getWebsite();
                int tid = website.getTid(), source_id = website.getTid();
                JobConf jobConf = null;

                jobConf = jobService.querySourceId(tid);

                String source_name = website.getComment();
                String type = section.getComment();
                int sectionId = section.getId();
                int country_code = website.getRegion() == null ? 0 : website.getRegion();
                int province_code = website.getProvinceId() == null ? 0 : website
                                .getProvinceId();
                int city_code = website.getCityId() == null ? 0 : website.getCityId();

                JobConf jc = new JobConf(JobType.NETWORK_INSPECT, section.getUrl(),
                                source_name, source_id, sectionId, type, listRule,
                                detailRules);
                jc.setCountry_code(country_code);
                jc.setProvince_code(province_code);
                jc.setCity_code(city_code);
                jc.setFetchinterval(confList.getFetchinterval());
                jc.setIdentify_md5("xiayun");
                jobConf.merge(jc);
                jobService.addJob(reptileId, jobConf);
            } catch (CrawlerException e) {

            } catch (NullPointerException e) {
                LOG.error("May be no tid, section id is " + id,e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // TODO 页面没有完成

        return null;
    }

    @ResponseBody
    @RequestMapping(value = "ajax/edit/{reptileId}/{jobId}", method = RequestMethod.GET)
    public JobConf edit(@PathVariable(value = "reptileId") Integer reptileId,
                    @PathVariable(value = "jobId") Long jobId) {
        JobConf job = jobService.getJob(reptileId, jobId);
        return job; 
    }

    @RequestMapping(value = "addJob/{reptileId}", method = RequestMethod.POST)
    public String addJob(@PathVariable(value = "reptileId") Integer reptileId, JobConf job)
                    throws CrawlerException {
        // TODO 添加任务，处理页面
        return "redirect:/job/search";
    }
}
