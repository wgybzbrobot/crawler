package com.zxsoft.crawler.web.dao.website.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.entity.Auth;
import com.zxsoft.crawler.entity.Website;
import com.zxsoft.crawler.web.dao.website.WebsiteDao;

@Repository
public class WebsiteDaoImpl implements WebsiteDao {

        @Autowired
        private HibernateTemplate hibernateTemplate;

        @Transactional
        @Override
        public Page<Website> getWebsites(Website website, int pageNo, int pageSize) {

                if (pageNo <= 0)
                        pageNo = 1;
                if (pageSize < 0)
                        pageSize = 100;

                Map<String, Object> params = new HashMap<String, Object>();

                StringBuffer sb = new StringBuffer(" from Website a where 1=1 ");
                if (website != null) {
                        if (!StringUtils.isEmpty(website.getSite())) {
                                sb.append(" and a.site like :site ");
                                params.put("site", "%" + website.getSite().trim() + "%");
                        }
                        if (!StringUtils.isEmpty(website.getSiteType())) {
                                sb.append(" and a.type =:type");
                                params.put("type", website.getSiteType().getType());
                        }
                        if (!StringUtils.isEmpty(website.getComment())) {
                                sb.append(" and a.comment like :comment");
                                params.put("comment", "%" + website.getComment() + "%");
                        }
                        if (null != website.getProvinceId() && website.getProvinceId() != 0) {
                            sb.append(" and a.provinceId = :provinceId");
                            params.put("provinceId", website.getProvinceId());
                        }
                        sb.append(" and a.region = :region");
                        params.put("region", website.getRegion() == null ? 1 : website.getRegion());
                        
                        sb.append(" and a.status = :status");
                        params.put("status", website.getStatus() == null ? "open" : website.getStatus());
                    
                }
                
                sb.append(" order by a.comment ");

                HibernateCallback<Page<Website>> action = HibernateCallbackUtil.getCallbackWithPage(sb, params, null, pageNo, pageSize);

                Page<Website> page = hibernateTemplate.execute(action);

                return page;
        }

        @Override
        public void addWebsite(Website website) {
                if (StringUtils.isEmpty(website.getId())) {
                        hibernateTemplate.save(website);
                } else {
                        hibernateTemplate.saveOrUpdate(website);
                }
        }

        @Override
        public void addWebsites(List<Website> websites) {
                hibernateTemplate.saveOrUpdateAll(websites);
        }

        @Override
        public Website getWebsite(Integer id) {
                return hibernateTemplate.get(Website.class, id);
        }

        @Override
        @Transactional
        public void deleteWebsite(Website website) {
                hibernateTemplate.delete(website);
        }


        @Override
        public List<Auth> getAuths(String id) {

                return hibernateTemplate.find("from Auth a where a.website.id = ?", id);
        }

        @Override
        public void addAuth(Auth auth) {
                if (StringUtils.isEmpty(auth.getId())) {
                        hibernateTemplate.save(auth);
                } else {
                        hibernateTemplate.saveOrUpdate(auth);
                }
        }

        @Override
        public Auth getAuth(String id) {
                return hibernateTemplate.get(Auth.class, id);
        }

        @Override
        public void deleteAuth(String id) {
                Auth auth = hibernateTemplate.get(Auth.class, id);
                hibernateTemplate.delete(auth);
        }

}
