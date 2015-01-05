package com.zxsoft.crawler.web.controller.website;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>规则的导入导出，提供如下接口
 * <ol>
 * <li>导入网站配置</li>
 * <li>导出网站配置</li>
 * <li>导入版块配置</li>
 * <li>导出版块配置</li>
 * </ol>
 * @author xiayun
 *
 */
@Controller
@RequestMapping("/rule")
public class RuleController {

        /**
         * 导入整个网站配置
         * @param json  网站配置
         * @param model
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "importWebsite", method = RequestMethod.POST)
        public Map<String, Object> importWebsite(@RequestParam(value = "json", required = false) String json, Model model) {

                return null;
        }

        /**
         * 导出整个网站<code>ids</code>的规则到目标站点<code>targetUrls</code>
         * @param siteIds        网站ids
         * @param targetUrls    目标站点
         * @param model
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "exportWebsite", method = RequestMethod.POST)
        public Map<String, Object> exportWebsite(@RequestParam(value = "siteIds", required = false) List<String> siteIds,
                                        @RequestParam(value = "targetUrls", required = false) List<String> targetUrls, Model model) {

                return null;
        }

        /**
         * 导入版块
         * @param json
         * @param model
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "importSection", method = RequestMethod.POST)
        public String importSection(@RequestParam(value = "json", required = false) String json, Model model) {

                return "";
        }

        /**
         * 
         * @param sectionIds    版块id
         * @param targetUrls    目标配置站点
         * @param model
         * @return
         */
        @ResponseBody
        @RequestMapping(value = "exportSection", method = RequestMethod.POST)
        public String exportSection(@RequestParam(value = "sectionId", required = false) List<String> sectionIds,
                                        @RequestParam(value = "targetUrls", required = false) List<String> targetUrls, Model model) {

                return "";
        }

}
