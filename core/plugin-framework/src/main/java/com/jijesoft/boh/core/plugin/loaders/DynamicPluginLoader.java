package com.jijesoft.boh.core.plugin.loaders;

import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.PluginParseException;


/**
 * Plugin loader that supports installed plugins at runtime
 */
public interface DynamicPluginLoader extends PluginLoader
{
    /**
     * Determines if this loader can load the jar.
     * @param pluginArtifact The jar to test
     * @return The plugin key, null if it cannot load the jar
     * @throws com.jijesoft.boh.core.plugin.PluginParseException If there are exceptions parsing the plugin configuration
     */
    String canLoad(PluginArtifact pluginArtifact) throws PluginParseException;
}
