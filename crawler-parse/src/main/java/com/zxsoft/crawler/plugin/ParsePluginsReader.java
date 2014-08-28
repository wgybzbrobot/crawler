package com.zxsoft.crawler.plugin;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.zxsoft.crawler.util.CrawlerConfiguration;


public class ParsePluginsReader {

    public static final Logger LOG = LoggerFactory.getLogger(ParsePluginsReader.class);

    /** The property name of the parse-plugins location */
    private static final String PP_FILE_PROP = "parse.plugin.file";

    private String fParsePluginsFile = null;

    public ParsePluginsReader() {
    }

    /**
     * Reads the <code>parse-plugins.xml</code> file
     */
    public Map<String, String> parse(Configuration conf) {

        Map<String, String> pluginMap = new HashMap<String, String>();

        // open up the XML file
        DocumentBuilderFactory factory = null;
        DocumentBuilder parser = null;
        Document document = null;
        InputSource inputSource = null;

        InputStream ppInputStream = null;
        if (fParsePluginsFile != null) {
            URL parsePluginUrl = null;
            try {
                parsePluginUrl = new URL(fParsePluginsFile);
                ppInputStream = parsePluginUrl.openStream();
            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to load parse plugins file from URL " + "[" + fParsePluginsFile
                            + "]. Reason is [" + e + "]");
                }
                return pluginMap;
            }
        } else {
        	String name = conf.get(PP_FILE_PROP);
            ppInputStream = conf.getConfResourceAsInputStream(name);
        }

        inputSource = new InputSource(ppInputStream);

        try {
            factory = DocumentBuilderFactory.newInstance();
            parser = factory.newDocumentBuilder();
            document = parser.parse(inputSource);
        } catch (Exception e) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Unable to parse [" + fParsePluginsFile + "]." + "Reason is [" + e + "]");
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

        Map<String, String> pulgins = reader.parse(CrawlerConfiguration.create());

        for (String str : pulgins.keySet()) {
            LOG.info(pulgins.get(str));
        }

    }

    public String getfParsePluginsFile() {
        return fParsePluginsFile;
    }

    public void setfParsePluginsFile(String fParsePluginsFile) {
        this.fParsePluginsFile = fParsePluginsFile;
    }
    
}
