package com.jijesoft.boh.core.plugin.osgi.factory;

import java.io.InputStream;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParser;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParserFactory;

/**
 * Descriptor parser factory that creates parsers for Osgi plugins.  Must only be used with {@link OsgiPlugin} instances.
 *
 */
public class OsgiPluginXmlDescriptorParserFactory implements DescriptorParserFactory
{
    /**
     * Gets an instance that filters the modules "component", "component-import", "module-type", "bean", and "spring"
     * @param source The descriptor source
     * @return The parser
     * @throws PluginParseException
     */
    public DescriptorParser getInstance(final InputStream source, final String... applicationKeys) throws PluginParseException
    {
        Validate.notNull(source, "The descriptor source must not be null");
        return new OsgiPluginXmlDescriptorParser(source, applicationKeys);
    }
}