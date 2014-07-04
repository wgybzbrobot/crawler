package com.zxsoft.crawler.storage;

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


public class SeedReader {

    public static final Logger LOG = LoggerFactory.getLogger(SeedReader.class);

    /** The property name of the parse-plugins location */
    private static final String PP_FILE_PROP = "seed.conf.file";

    private String fParsePluginsFile = null;

    public SeedReader() {
    }

    /**
     * Reads the <code>seed-confs.xml</code> file
     */
    public Map<String, String> parse(Configuration conf) {

        Map<String, String> confMap = new HashMap<String, String>();

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
                return confMap;
            }
        } else {
            ppInputStream = conf.getConfResourceAsInputStream(conf.get(PP_FILE_PROP));
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

        NodeList plugins = parsePlugins.getElementsByTagName("conf");

        for (int i = 0; i < plugins.getLength(); i++) {
            Element plugin = (Element) plugins.item(i);
            Element type = (Element) plugin.getElementsByTagName("type").item(0);
            Element clazz = (Element) plugin.getElementsByTagName("class").item(0);
            confMap.put(type.getTextContent(), clazz.getTextContent());
        }
        return confMap;
    }

    public static void main(String[] args) throws Exception {

        SeedReader reader = new SeedReader();

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
