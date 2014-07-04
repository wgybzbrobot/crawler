package com.zxsoft.crawler.storage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.zxsoft.crawler.util.parse.Extension;
import com.zxsoft.crawler.util.parse.ParsePluginsReader;
import com.zxsoft.crawler.util.parse.ParserNotFoundException;
import com.zxsoft.crawler.util.parse.PluginRuntimeException;


public class SeedConfFactory {

    private static Logger LOG = LoggerFactory.getLogger(SeedConfFactory.class);

    private static WeakHashMap<String, SeedConf> cache = new WeakHashMap<String, SeedConf>();
    private static Set<Extension> seedconfs = new HashSet<Extension>();

    private Configuration conf;
    
    private static final String DEFAULT_CONF_TYPE = "news";

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public SeedConf getSeedConfByType(String type) throws ParserNotFoundException {

        if (StringUtils.isEmpty(type)) {
            type = conf.get("seed.conf.defualt", DEFAULT_CONF_TYPE);
        }
        
        type = type.toLowerCase();
        
        if (cache.get(type) != null) {
            return cache.get(type);
        }

        if (CollectionUtils.isEmpty(seedconfs)) { // [type, class]
            setExtensions();
        }

        Extension extension = null;
        for (Extension ext : seedconfs) {
            if (type.equals(ext.getType())) {
                extension = ext;
                break;
            }
        }

        if (extension == null) {
            throw new ParserNotFoundException("Cannot find " + type + " seed conf.");
        }

        try {
            SeedConf seedconf = (SeedConf) extension.getInstance();
            cache.put(type, seedconf);
            return seedconf;
        } catch (PluginRuntimeException e) {
            LOG.warn("Cannot initial " + type + "Seed (cause: " + e.toString());
            throw new ParserNotFoundException("Cannot init seed conf for type [" + type + "]");
        }
    }

    private void setExtensions() {
        ParsePluginsReader reader = new ParsePluginsReader();
        Map<String, String> plugins = reader.parse(conf);
        for (String type : plugins.keySet()) {
            Extension extension = new Extension(type, plugins.get(type));
            seedconfs.add(extension);
        }
    }
}
