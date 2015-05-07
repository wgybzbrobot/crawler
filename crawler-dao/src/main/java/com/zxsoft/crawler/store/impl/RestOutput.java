package com.zxsoft.crawler.store.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.zxisl.commons.utils.Assert;
import com.zxisl.commons.utils.CollectionUtils;
import com.zxisl.commons.utils.StringUtils;
import com.zxsoft.crawler.storage.RecordInfo;
import com.zxsoft.crawler.store.Output;
import com.zxsoft.crawler.store.OutputException;

public class RestOutput implements Output {
    private static Logger LOG = LoggerFactory.getLogger(RestOutput.class);
    private static String urls;
    private static String _url;
    private static final Output mysqlOutput = new MysqlOutput();
    private static boolean writeToMysqlIfFail = false;

    static {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("restoutput.properties");
        try {
            prop.load(stream);
            urls = prop.getProperty("data.output.address");
            if (StringUtils.isEmpty(urls)) {
                throw new NullPointerException("data.output.address not set");
            }
            LOG.info("data.output.address: " + urls);
            String _writeToMysqlIfFail = prop.getProperty("writeToMysqlIfFail");
            if ("yes".equals(_writeToMysqlIfFail)) {
                writeToMysqlIfFail = true;
            }
            try {
                _url = prop.getProperty("data.output.lucene");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            LOG.error("Cannot find restoutput.properties file, cannot write data to solr service.");
        } catch (IOException e1) {
            LOG.error("Load restoutput.properties failed, 将导致无法写数据到solr服务.");
        }
    }

    /**
     * 写一条
     */
    public int write(RecordInfo info) {
        Assert.notNull(info);

        if (!StringUtils.isEmpty(_url)) {
            writeToLuceneService(info);
        }

        List<RecordInfo> recordInfos = new LinkedList<RecordInfo>();
        recordInfos.add(info);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("num", 1);
        map.put("records", recordInfos);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(map, Map.class);
        Client client = Client.create();
        ClientResponse response = null;
        try {
            String[] urlArr = urls.split(",");
            for (String u : urlArr) {
                if (!StringUtils.hasLength(u))
                    continue;
                WebResource webResource = client.resource(u.trim());
                response = webResource.type("application/json").post(ClientResponse.class,
                                json);
                String msg = response.getEntity(String.class);
                OutputReturn ret = gson.fromJson(msg, OutputReturn.class);
                if (ret.errorCode != 0) {
                    LOG.error("Output to " + u + " failed: " + ret.errorMessage);
                    if (writeToMysqlIfFail) {
                        mysqlOutput.write(recordInfos);
                    }
                } 
            }
            
        } catch (ClientHandlerException e) {
            LOG.error("Write solr failed: " + info.toString());
            if (writeToMysqlIfFail) {
                LOG.error(e.getMessage() + ", will write data to Mysql", e);
                mysqlOutput.write(info);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            // throw new
            // OutputException("Solr service url address is null or not valid.",
            // e);
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.destroy();
            }
        }
        return 1;
    }

    final class OutputReturn {
        int errorCode = -1;
        String errorMessage;
    }

    public int write(List<RecordInfo> recordInfos) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        if (CollectionUtils.isEmpty(recordInfos))
            return 0;
        int realSize = recordInfos.size(), size = recordInfos.size();
        int successCount = 0;
        Client client = Client.create();
        client.setConnectTimeout(20000);
        client.setReadTimeout(20000);
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("num", size);
            map.put("records", recordInfos);
            String json = gson.toJson(map, Map.class);
            
            String[] urlArr = urls.split(",");
            for (String u : urlArr) {
                if (!StringUtils.hasLength(u))
                    continue;
                WebResource webResource = client.resource(u);
                ClientResponse response = null;
                try {
                    response = webResource.type("application/json").post(
                                    ClientResponse.class, json);
                    String msg = response.getEntity(String.class);
                    LOG.debug(msg + ", status code:" + response.getStatus());
                    OutputReturn ret = gson.fromJson(msg, OutputReturn.class);
                    if (ret.errorCode != 0) {
                        LOG.error("Output to " + u + " failed: " + ret.errorMessage);
                        if (writeToMysqlIfFail) {
                            mysqlOutput.write(recordInfos);
                        }
                    } else {
                        successCount += size;
                        LOG.debug("success write to solr:" + json);
                    }
                } catch (ClientHandlerException e) {
                    for (RecordInfo _info : recordInfos) {
                        LOG.error("Write solr failed: " + _info.toString());
                    }
                    if (writeToMysqlIfFail) {
                        LOG.error(e.getMessage() + ", will write data to mysql");
                        mysqlOutput.write(recordInfos);
                    } else {
                        throw new OutputException(e.getMessage());
                    }
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
            }
        } catch (Exception e) {
            // throw new OutputException(e.getMessage());
        } finally {
            client.destroy();
        }

        if (successCount < realSize) {
            LOG.warn("Total record count is " + realSize
                            + ", but write to rest service record count is "
                            + successCount);
        }

        if (!StringUtils.isEmpty(_url)) {
            for (RecordInfo recordInfo : recordInfos) {
                writeToLuceneService(recordInfo);
            }
        }

        return realSize;
    }

    /**
     * 写到lucene服务
     * 
     * @param recordInfo
     */
    private void writeToLuceneService(RecordInfo recordInfo) {
        Client client = Client.create();
        ClientResponse response = null;
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            String json = gson.toJson(recordInfo, RecordInfo.class);
            WebResource webResource = client.resource(_url);
            response = webResource.type("application/json").post(ClientResponse.class,
                            json);
            String msg = response.getEntity(String.class);
            LOG.debug(msg);
        } catch (Exception e) {
            LOG.error("Write to lucene " + _url + " failed: " + e.getMessage());
        } finally {
            if (response != null) {
                response.close();
            }
            if (client != null) {
                client.destroy();
            }
        }
    }
}
