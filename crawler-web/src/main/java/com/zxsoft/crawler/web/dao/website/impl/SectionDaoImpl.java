package com.zxsoft.crawler.web.dao.website.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;
import org.thinkingcloud.framework.web.utils.Page;

import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.entity.Section;
import com.zxsoft.crawler.web.dao.website.SectionDao;

@Repository
public class SectionDaoImpl implements SectionDao {

        @Autowired
        private HibernateTemplate hibernateTemplate;

        @Override
        public Section getSection(Integer id) {
                return hibernateTemplate.get(Section.class, id);
        }

        @Override
        public Page<Section> getSections(Section section, int pageNo, int pageSize) {
                if (pageNo <= 0)
                        pageNo = 1;

                Map<String, Object> params = new HashMap<String, Object>();

                StringBuffer sb = new StringBuffer(" from Section a where 1=1 ");
                if (section != null) {
                        if (!StringUtils.isEmpty(section.getUrl())) {
                                sb.append(" and a.url like :url ");
                                params.put("url", "%" + section.getUrl().trim() + "%");
                        }
                        if (!StringUtils.isEmpty(section.getComment())) {
                                sb.append(" and a.comment like :comment");
                                params.put("comment", "%" + section.getComment() + "%");
                        }
                        if (section.getCategory() != null) {
                                if ( !StringUtils.isEmpty(section.getCategory().getId())) {
                                        sb.append(" and a.category.id =:cateId");
                                        params.put("cateId", section.getCategory().getId());
                                }
                        }
                        if (section.getWebsite() != null ) {
                                sb.append(" and a.website.id =:id");
                                params.put("id", section.getWebsite().getId());
                        }
                        if (section.getAccount() !=null ) {
                                if (!StringUtils.isEmpty(section.getAccount().getUsername())) {
                                        sb.append(" and a.account.username =:username");
                                        params.put("username", section.getAccount().getUsername());
                                }
                        }
                }

                HibernateCallback<Page<Section>> action = HibernateCallbackUtil.getCallbackWithPage(sb, params, null, pageNo, pageSize);

                Page<Section> page = hibernateTemplate.execute(action);

                return page;
        }

        @Override
        public void saveOrUpdate(Section section) {
                if (StringUtils.isEmpty(section.getId())) {
                        hibernateTemplate.save(section);
                } else {
                        hibernateTemplate.saveOrUpdate(section);
                }
        }

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Override
        public void delete(Integer id) {
                Section section = hibernateTemplate.get(Section.class, id);

                final String url = section.getUrl();
                ConfList confList = hibernateTemplate.get(ConfList.class, url);
                if (confList != null)
                        hibernateTemplate.delete(confList);

                jdbcTemplate.update("delete from conf_detail  where listurl = ?", new Object[] { url });

                hibernateTemplate.delete(section);
        }

        @Override
        public void delete(Section section) {
                final String url = section.getUrl();
                ConfList confList = hibernateTemplate.get(ConfList.class, url);
                if (confList != null)
                        hibernateTemplate.delete(confList);

                jdbcTemplate.update("delete from conf_detail  where listurl = ?", new Object[] { url });

                hibernateTemplate.delete(section);

        }

}
