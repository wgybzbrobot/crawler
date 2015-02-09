package com.zxsoft.crawler.parse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.dao.LocationDao;


public class LocationUtils {

        
        public static String getLocation(String ip) {
                if (StringUtils.isEmpty(ip)) return "";
                String sql = "SELECT get_ip查询(?) FROM dual";
                List<String> locations = LocationDao.getJdbcTemplate().query(sql, new Object[]{ip}, new RowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet arg0, int arg1) throws SQLException {
                                return arg0.getString(1);
                        }
                });
                if (!CollectionUtils.isEmpty(locations)) {
                      return locations.get(0);  
                }
                return "";
        }

        public static int getLocationCode(String ip) {
                if (StringUtils.isEmpty(ip)) return 0;
                String sql = "SELECT get_ip归属(:ip) FROM dual";
                List<Integer> locations = LocationDao.getJdbcTemplate().query(sql, new Object[]{ip}, new RowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet arg0, int arg1) throws SQLException {
                                return arg0.getInt(1);
                        }
                });
                if (!CollectionUtils.isEmpty(locations)) {
                      return locations.get(0);  
                }
                return 0;
        }
        
        
}
