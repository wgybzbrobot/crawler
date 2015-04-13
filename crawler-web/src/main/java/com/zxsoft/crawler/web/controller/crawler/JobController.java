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
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.crawler.JobService;
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

    /**
     * 查询任务
     * 
     * @param query
     * @param start
     * @param end
     * @param model
     * @return
     */
    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String jobs(
                    @RequestParam(value = "query", required = false) String query,
                    @RequestParam(value = "start", required = false, defaultValue = "1") Integer start,
                    @RequestParam(value = "end", required = false, defaultValue = "10") Integer end,
                    Model model) {
        model.addAttribute("query", query);
        model.addAttribute("start", start);
        model.addAttribute("end", end);

        Page<JobConf> page = jobService.getJobs(query, start, end);
        model.addAttribute("page", page);
        return "/crawler/job";
    }

    /**
     * 删除任务
     * 
     * @param jobId
     * @return
     */
    @RequestMapping(value = "delete/{jobId}", method = RequestMethod.GET)
    public String deleteJob(@PathVariable(value = "jobId") Integer jobId) {

        jobService.deleteJob(jobId);
        return "redirect:/job/search";
    }

    /**
     * 暂停任务
     * 
     * @param comment
     * @param start
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "ajax/state", method = RequestMethod.GET)
    public String haltOrStartJob(
                    @RequestParam(value = "jobId", required = false) String jobId) {
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
    @RequestMapping(value = "ajax/addInspectJob", method = RequestMethod.GET)
    public Map<String, Object> addInspectJob(
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
                    detailRules.add(detailRule);
                }

                Website website = section.getWebsite();
                int tid = website.getTid(), source_id = 0;
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
                jc.setIdentify_md5("xiayun3");
                jobConf.merge(jc);
                jobService.addJob(jobConf);
            } catch (CrawlerException e) {

            } catch (NullPointerException e) {
                LOG.error("May be no tid, section id is " + id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
