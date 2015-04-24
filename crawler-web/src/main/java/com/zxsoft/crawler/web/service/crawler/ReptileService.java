package com.zxsoft.crawler.web.service.crawler;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.Reptile;

@Service
public interface ReptileService {

    List<Reptile> getReptiles() ;

    void add(Reptile reptile);

    Reptile getReptile(int reptileId);
    
}
