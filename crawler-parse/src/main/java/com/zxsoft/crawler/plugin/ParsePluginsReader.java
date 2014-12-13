package com.zxsoft.crawler.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.io.ResourceReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 读取解析插件
 */
public class ParsePluginsReader {

    public static final Logger LOG = LoggerFactory.getLogger(ParsePluginsReader.class);

    private static final String ParsePluginsFile = "parse-plugins.xml";

    /**
     * Reads the <code>parse-plugins.xml</code> file
     */
    public Map<String, String> parse() {
        Map<String, String> pluginMap = new HashMap<String, String>();
        // open up the XML file
        DocumentBuilderFactory factory = null;
        DocumentBuilder parser = null;
        Document document = null;
        InputSource inputSource = null;
        InputStream ppInputStream = null;
        try {
            ResourceReader rr = new ResourceReader(ParsePluginsFile);
            ppInputStream = rr.getResourceAsInputStream();
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.error("Unable to load parse plugins file from URL " + "[" + ParsePluginsFile
                        + "]. Reason is [" + e + "]");
            }
            return pluginMap;
        }

        inputSource = new InputSource(ppInputStream);

        try {
            factory = DocumentBuilderFactory.newInstance();
            parser = factory.newDocumentBuilder();
            document = parser.parse(inputSource);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unable to parse [" + ParsePluginsFile + "]." + "Reason is [" + e + "]");
            }
            return null;
        }

        Element parsePlugins = document.getDocumentElement();

        NodeList plugins = parsePlugins.getElementsByTagName("plugin");

        for (int i = 0; i < plugins.getLength(); i++) {
            Element plugin = (Element) plugins.item(i);
            Element type = (Element) plugin.getElementsByTagName("type").item(0);
            Element clazz = (Element) plugin.getElementsByTagName("class").item(0);
            pluginMap.put(type.getTextContent(), clazz.getTextContent());
        }
        return pluginMap;
    }

    public static void main(String[] args) throws Exception {
        ParsePluginsReader reader = new ParsePluginsReader();
        Map<String, String> pulgins = reader.parse();
        for (String str : pulgins.keySet()) {
            LOG.info(pulgins.get(str));
        }
    }
}
