package com.jijesoft.boh.core.plugin.loaders;

import java.util.Collection;

import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginException;
import com.jijesoft.boh.core.plugin.PluginParseException;

/**
 * Handles loading and unloading plugin artifacts from a location
 */
public interface PluginLoader
{
    Collection<Plugin> loadAllPlugins(ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException;

    /**
     * @return a collection of discovered plugins which have now been loaded by this PluginLoader
     */
    Collection<Plugin> addFoundPlugins(ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException;

    /**
     * @return true if this PluginLoader tracks whether or not plugins are added to it.
     */
    boolean supportsAddition();

    /**
     * @return true if this PluginLoader tracks whether or not plugins are removed from it.
     */
    boolean supportsRemoval();

    /**
     * Remove a specific plugin
     */
    void removePlugin(Plugin plugin) throws PluginException;
}
