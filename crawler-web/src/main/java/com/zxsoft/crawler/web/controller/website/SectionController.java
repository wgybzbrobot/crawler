package com.zxsoft.crawler.web.controller.website;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxsoft.crawler.entity.Account;
import com.zxsoft.crawler.entity.Category;
import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfDetailId;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.service.website.ConfigService;
import com.zxsoft.crawler.web.service.website.DictService;
import com.zxsoft.crawler.web.service.website.SectionService;
import com.zxsoft.crawler.web.service.website.WebsiteService;

@Controller
@RequestMapping("/section")
public class SectionController {

        @Autowired
        private SectionService sectionService;
        @Autowired
        private ConfigService configService;
        @Autowired
        private WebsiteService websiteService;

        @Autowired
        private DictService dictService;

        @RequestMapping(method = RequestMethod.GET)
        public String index(@RequestParam(value = "websiteId", required = false) String websiteId, Model model) {
                Website website = websiteService.getWebsite(websiteId);
                model.addAttribute("website", website);

                List<Category> categories = dictService.getCategories();
                model.addAttribute("categories", categories);

                Section section = new Section();
                section.setWebsite(website);
                Page<Section> page = sectionService.getSections(section, 1, Page.DEFAULT_PAGE_SIZE);
                model.addAttribute("page", page);

                return "website/section";
        }

        /**
         * 添加或修改版块
         */
        @ResponseBody
        @RequestMapping(value = "ajax/add", method = RequestMethod.POST)
        public String addOrUpdate(@RequestParam(value = "copy", required = false) String copy, Section section, String oldUrl, Model model,
                                        HttpSession session) {

                Account account = (Account) session.getAttribute("account");
                if (account == null) {
                        return "NoAccess";
                }
                section.setAccount(account);

                if ("true".equals(copy)) { // 是否是拷贝规则的创建
                        Map<String, Object> map = configService.getConfig(section.getId());
                        ConfList confList = (ConfList) map.get("confList");
                        List<ConfDetail> confDetails = (List<ConfDetail>) map.get("confDetails");

                        if (confList == null) { // 母版没有规则，无法拷贝
                                return "NoConfList";
                        }
                        confList.setUrl(section.getUrl());
                        confList.setComment(section.getComment());
                        if (!CollectionUtils.isEmpty(confDetails)) {
                                for (ConfDetail confDetail : confDetails) {
                                        ConfDetailId confDetailId = new ConfDetailId();
                                        confDetailId.setHost(confDetail.getId().getHost());
                                        confDetailId.setListurl(section.getUrl());
                                        confDetail.setId(confDetailId);
                                }
                        }
                        section.setId(null);
                        sectionService.saveOrUpdate(section);
                        configService.add(confList);
                        configService.add(confDetails);
                } else {
                        sectionService.saveOrUpdate(section);
                        if (!StringUtils.isEmpty(section.getId())) { // 修改
                                if (!section.getUrl().equals(oldUrl)) {
                                        configService.updateConfListKey(oldUrl, section.getUrl());
                                        configService.updateConfDetailKey(oldUrl, section.getUrl());
                                }
                        }
                }

                return "success";
        }

        /**
         * 获取某个网站的详细信息
         */
        @ResponseBody
        @RequestMapping(value = "ajax/moreinfo/{id}", method = RequestMethod.GET)
        public Map<String, Object> moreinfo(@PathVariable(value = "id") String id, Model model) {
                Section section = sectionService.getSection(id);

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", section.getId());
                map.put("url", section.getUrl());
                map.put("comment", section.getComment());
                map.put("category.id", section.getCategory().getId());

                return map;
        }

        @ResponseBody
        @RequestMapping(value = "ajax/delete/{id}", method = RequestMethod.GET)
        public String delete(@PathVariable(value = "id") String id, Model model) {
                sectionService.delete(id);
                return "success";
        }
}
