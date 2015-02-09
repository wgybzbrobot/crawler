package com.zxsoft.crawler.parse;

import java.util.Map;

import org.junit.Test;

public class OverseaDaoTest {

        @Test
        public void testGetOverSea() {
                int locationCode = 0;
                String ip = "", location = "";
                OverseaDao overseaDao = new OverseaDao();
                Map <String, Object> _map = overseaDao.getOversea("");
                if (_map != null) {
                        ip = (String)_map.get("ip");
                        location = (String)_map.get("location");
                        locationCode = (Integer)_map.get("code");
                }
        }
}
