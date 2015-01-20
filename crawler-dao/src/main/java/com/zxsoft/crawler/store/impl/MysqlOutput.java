package com.zxsoft.crawler.store.impl;

import java.util.Date;
import java.util.List;
import com.zxisl.commons.utils.Assert;
import com.zxsoft.crawler.dao.BaseDao;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

/**
 * 写数据到Mysql
 * @author xiayun
 *
 */
public class MysqlOutput extends BaseDao implements Output {
        
        @Override
        public int write(RecordInfo info) throws OutputException {
                Assert.notNull(info);
                int num = 0;
                try {
                        num = getJdbcTemplate().update("insert into " + TABLE_FAILEDDATA 
                                                + " (JsonType, jsonString, failedNumber, inserttime) values(?,?,?,?)", 
                                                info.getPlatform(), info.toString(),1, new Date());
                }catch (Exception e) {
                        throw new OutputException("Write to Mysql failed, " + e.getMessage());
                }
                return num;
        }

        @Override
        public int write(List<RecordInfo> recordInfos) throws OutputException {
                Assert.notEmpty(recordInfos);
                for (RecordInfo recordInfo : recordInfos) {
                        write(recordInfo);
                }
                return recordInfos.size();
        }
        
        
}
