package com.zxsoft.crawler.web.service.crawler.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Reptile;
import com.zxsoft.crawler.web.dao.website.ReptileDao;
import com.zxsoft.crawler.web.service.crawler.ReptileService;

@Service
public class ReptileServiceImple implements ReptileService {

    @Autowired
    private ReptileDao reptileDao;
    
    @Override
    public List<Reptile> getReptiles() {
        
        return reptileDao.getReptiles();
    }

    @Override
    public void add(Reptile reptile) {
        reptileDao.add(reptile);
    }

    @Override
    public Reptile getReptile(int reptileId) {
        return reptileDao.getReptile(reptileId);
    }

    
}
