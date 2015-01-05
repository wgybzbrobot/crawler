package com.zxsoft.crawler.slave.utils;

import org.junit.Test;

public class TestOracleDao {

        @Test
        public void test() {
                OracleDao dao = new OracleDao();
                int num = dao.updateTaskExecuteStatus();
                System.out.println(num);
        }
}
