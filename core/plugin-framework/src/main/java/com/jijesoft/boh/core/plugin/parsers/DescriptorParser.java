package com.jijesoft.boh.core.plugin.parsers;

import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginInformation;
import com.jijesoft.boh.core.plugin.PluginParseException;

/**
 * Interface for parsing a plugin descriptor file, e.g. jijesoft-plugin.xml.
 *
 * @see XmlDescriptorParser
 * @see DescriptorParserFactory
 */
public interface DescriptorParser
{
    /**
     * Sets the configuration on the plugin argument to match the configuration specified in the
     * plugin descriptor (typically an XML file).
     *
     * @param moduleDescriptorFactory a factory for instantiating the required plugin modules
     * @param plugin the plugin whose configuration will be modified
     * @return the original plugin with the configuration changed and the module descriptors added
     * @throws PluginParseException if there was an error getting information about the plugin
     */
    Plugin configurePlugin(ModuleDescriptorFactory moduleDescriptorFactory, Plugin plugin) throws PluginParseException;

    /**
     * @return the key of the plugin specified in the descriptor
     */
    String getKey();

    /**
     * @return The version of the plugin system expected by this plugin.  If unknown, it is assumed to be 1.
     */
    int getPluginsVersion();

    PluginInformation getPluginInformation();
}
