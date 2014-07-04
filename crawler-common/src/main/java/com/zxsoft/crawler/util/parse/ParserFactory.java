package com.zxsoft.crawler.util.parse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


public class ParserFactory {

    private static Logger LOG = LoggerFactory.getLogger(ParserFactory.class);

    private static WeakHashMap<String, Parser> parserCache = new WeakHashMap<String, Parser>();
    private static Set<Extension> extensions = new HashSet<Extension>();

    private Configuration conf;
    
    private static final String DEFAULT_PARSER_TYPE = "forum";

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    public Parser getParserByCategory(String category) throws ParserNotFoundException {
        // return new ForumParser();

        if (StringUtils.isEmpty(category)) {
            category = conf.get("parser.type.defualt", DEFAULT_PARSER_TYPE);
        }
        
        if (parserCache.get(category) != null) {
            return parserCache.get(category);
        }

        if (CollectionUtils.isEmpty(extensions)) { // [type, class]
            setExtensions();
        }

        Extension extension = null;
        for (Extension ext : extensions) {
            if (category.equals(ext.getType())) {
                extension = ext;
                break;
            }
        }

        if (extension == null) {
            throw new ParserNotFoundException("Cannot find " + category + " parser.");
        }

        try {
            Parser parser = (Parser) extension.getInstance();
            parserCache.put(category, parser);
            return parser;
        } catch (PluginRuntimeException e) {
            LOG.warn("Cannot initial " + category + "Parser (cause: " + e.toString());
            throw new ParserNotFoundException("Cannot init parser for type [" + category + "]");
        }
    }

    private void setExtensions() {
        ParsePluginsReader reader = new ParsePluginsReader();
        Map<String, String> plugins = reader.parse(conf);
        for (String type : plugins.keySet()) {
            Extension extension = new Extension(type, plugins.get(type));
            extensions.add(extension);
        }
    }
}
