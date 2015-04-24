package com.zxsoft.crawler.web.dao.website;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.zxsoft.crawler.entity.Reptile;

@Repository
public interface ReptileDao {

	List<Reptile> getReptiles();

    void add(Reptile reptile);

    Reptile getReptile(Integer reptileId);
	
}
