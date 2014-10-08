package com.zxsoft.crawler.web.dao.website.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.thinkingcloud.framework.util.StringUtils;
import org.thinkingcloud.framework.web.utils.HibernateCallbackUtil;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfDetailId;
import com.zxsoft.crawler.entity.ConfList;
import com.zxsoft.crawler.web.dao.website.ConfigDao;

@Repository
public class ConfigDaoImpl implements ConfigDao {

	@Autowired
	private HibernateTemplate hibernateTemplate;
	
	@Override
	public ConfList getConfList(String url) {
		return hibernateTemplate.get(ConfList.class, url);
	}

	@Override
	public List<ConfDetail> getConfDetails(String url) {
		StringBuffer sb = new StringBuffer(" from ConfDetail a where 1=1 ");
		
		Map<String, String> params = new HashMap<String, String>();
		
		if (!StringUtils.isEmpty(url)) {
			sb.append(" and a.id.listurl =:url ");
			params.put("url", url);
		}
		
		HibernateCallback<List<ConfDetail>> action = HibernateCallbackUtil.getCallback(sb, params, false, null);
		
		List<ConfDetail> confDetails = hibernateTemplate.executeFind(action);
		
		return confDetails;
	}

	@Override
    public void addListConf(ConfList listConf) {
	    hibernateTemplate.saveOrUpdate(listConf);
    }

	@Override
    public void addDetailConf(ConfDetail detailConf) {
		hibernateTemplate.saveOrUpdate(detailConf);
    }
	
	@Override
	public void addDetailConfs(List<ConfDetail> detailConfs) {
		hibernateTemplate.saveOrUpdateAll(detailConfs);
	}
	
	@Override
	public ConfList getListConf(String url) {
		return hibernateTemplate.get(ConfList.class ,url);
	}

	@Override
	public ConfDetail getDetailConf(String listUrl, String host) {
		ConfDetailId id = new ConfDetailId(listUrl, host);
		return hibernateTemplate.get(ConfDetail.class, id);
	}

	@Override
	public void deleteConfDetail(ConfDetailId id) {
		ConfDetail confDetail = hibernateTemplate.get(ConfDetail.class, id);
		hibernateTemplate.delete(confDetail);
	}
	
	
}
