package com.zxsoft.crawler.web.dao.website.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.web.dao.website.ReptileDao;

@Repository
public class RepileDaoImpl implements ReptileDao {

    @Autowired
    private HibernateTemplate hibernateTemplate;
    
    @Override
    public List<Reptile> getReptiles() {

        return hibernateTemplate.find("from Reptile");
    }

    @Override
    public void add(Reptile reptile) {
        hibernateTemplate.saveOrUpdate(reptile);
    }

    @Override
    public Reptile getReptile(Integer id) {
        return hibernateTemplate.get(Reptile.class, id);
    }

}
