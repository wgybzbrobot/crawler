package com.zxsoft.crawler.master;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.zxsoft.crawler.common.WorkerConf;

/**
 * worker节点的状态
 */
public class WorkerResource extends ServerResource {

    /**
     * worker定时向master发送tick，表示worker的存在
     * @param workerConf
     * @return
     * @throws Exception
     */
    @Post("json")
    public void workerTick(WorkerConf workerConf) throws Exception {
        //  FIXME  : 探测worker是否存在, 防止客户端发送请求， 而非worker工作节点
        
        MasterApp.slaveMgr.workerTick(workerConf);
    }
    
    /**
     * 获取可用的worker
     * @return
     * @throws Exception
     */
    @Get("json")
    public List<WorkerConf> workers() throws Exception {
        List<WorkerConf> workers = MasterApp.slaveMgr.getWorkers();
        return workers;
    }

}
