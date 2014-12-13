package com.zxsoft.crawler.parse;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thinkingcloud.framework.util.CollectionUtils;
import org.thinkingcloud.framework.util.StringUtils;
import com.zxsoft.crawler.plugin.ParsePluginsReader;
import com.zxsoft.crawler.plugin.PluginRuntimeException;

public class ParserFactory {

    private static Logger LOG = LoggerFactory.getLogger(ParserFactory.class);

    private static WeakHashMap<String, Parser> parserCache = new WeakHashMap<String, Parser>();
    private static Set<Extension> extensions = new HashSet<Extension>();
    
    private static final String DEFAULT_PARSER_TYPE = "forum";

    public synchronized Parser getParserByCategory(String category) throws ParserNotFoundException {

        if (StringUtils.isEmpty(category)) {
            category = DEFAULT_PARSER_TYPE;
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
//            parserCache.put(category, parser);
            return parser;
        } catch (PluginRuntimeException e) {
            LOG.warn("Cannot initial " + category + "Parser (cause: " + e.toString());
            throw new ParserNotFoundException("Cannot init parser for type [" + category + "]");
        }
    }

    private void setExtensions() {
        ParsePluginsReader reader = new ParsePluginsReader();
        Map<String, String> plugins = reader.parse();
        for (String type : plugins.keySet()) {
            Extension extension = new Extension(type, plugins.get(type));
            extensions.add(extension);
        }
    }
}
