package com.zxsoft.crawler.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxsoft.crawler.dao.ConfDao;

public class AddInspectJob {

    public static void main(String[] args) {
        String sql = "select c.url from website a, section b, conf_list c where a.id = b.site and b.url = c.url and provinceid = 340000 and a.tid is not null order by c.comment LIMIT 84,533";
        JdbcTemplate jdbcTemplate = new ConfDao().getJdbcTemplate();
        List<String> urls = jdbcTemplate.query(sql, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
        Client client = Client.create();
        ClientResponse response = null;
        WebResource webResource = client.resource("http://localhost:8080/crawler-web/slaves/ajax/addInspectJob");
        for (String url : urls) {
//            response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, "{\"url\":\"" + url + "\"}");
//            String msg = response.getEntity(String.class);
//            System.out.println(msg);
            System.out.println(url);
        }
        client.destroy();
    }
}
