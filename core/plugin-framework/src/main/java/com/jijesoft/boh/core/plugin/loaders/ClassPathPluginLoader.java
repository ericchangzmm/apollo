package com.jijesoft.boh.core.plugin.loaders;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginAccessor;
import com.jijesoft.boh.core.plugin.PluginException;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.util.ClassLoaderUtils;

/**
 * Loads plugins from the classpath
 */
public class ClassPathPluginLoader implements PluginLoader
{
    private static Log log = LogFactory.getLog(ClassPathPluginLoader.class);

    private final String fileNameToLoad;
    private List<Plugin> plugins;

    public ClassPathPluginLoader()
    {
        this(PluginAccessor.Descriptor.FILENAME);
    }

    public ClassPathPluginLoader(final String fileNameToLoad)
    {
        this.fileNameToLoad = fileNameToLoad;
    }

    private void loadClassPathPlugins(final ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        URL url = null;
        final Enumeration<URL> pluginDescriptorFiles;
        plugins = new ArrayList<Plugin>();

        try
        {
            pluginDescriptorFiles = ClassLoaderUtils.getResources(fileNameToLoad, this.getClass());
        }
        catch (final IOException e)
        {
            log.error("Could not load classpath plugins: " + e, e);
            return;
        }

        while (pluginDescriptorFiles.hasMoreElements())
        {
            url = pluginDescriptorFiles.nextElement();
            plugins.addAll(new SinglePluginLoader(url).loadAllPlugins(moduleDescriptorFactory));
        }
    }

    public Collection<Plugin> loadAllPlugins(final ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        if (plugins == null)
        {
            loadClassPathPlugins(moduleDescriptorFactory);
        }

        return plugins;
    }

    public boolean supportsRemoval()
    {
        return false;
    }

    public boolean supportsAddition()
    {
        return false;
    }

    public Collection<Plugin> addFoundPlugins(final ModuleDescriptorFactory moduleDescriptorFactory)
    {
        throw new UnsupportedOperationException("This PluginLoader does not support addition.");
    }

    public void removePlugin(final Plugin plugin) throws PluginException
    {
        throw new PluginException("This PluginLoader does not support removal.");
    }
}
