package com.zxsoft.crawler.web.service.website;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zxsoft.crawler.entity.ConfDetail;
import com.zxsoft.crawler.entity.ConfList;

@Service
public interface ConfigService {

    /**
     * 获取配置配置，列表页confList<ConfList>
     * 
     * @param url
     *            版块URL
     */
    ConfList getConfList(String id);

    /**
     * 详细页配置confDetails<List<ConfDetail>>
     * @param listUrl
     * @return
     */
    List<ConfDetail> getConfDetail(String listUrl);

    /**
     * Add website's list-page configuration
     */
    void add(ConfList confList);

    /**
     * add website's detail-page configuration information.
     */
    void add(ConfDetail confDetail, String oldHost);

    void add(List<ConfDetail> confDetails);

    /**
     * 修改ConfList主键
     */
    void updateConfListKey(String oldUrl, String url);

    /**
     * 修改ConfDetail主键
     */
    void updateConfDetailKey(String oldUrl, String url);

    List<ConfList> getInspectConfLists(ConfList confList);
}
