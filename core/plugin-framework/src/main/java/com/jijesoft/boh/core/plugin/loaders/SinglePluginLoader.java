package com.jijesoft.boh.core.plugin.loaders;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginException;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.impl.StaticPlugin;
import com.jijesoft.boh.core.plugin.impl.UnloadablePlugin;
import com.jijesoft.boh.core.plugin.impl.UnloadablePluginFactory;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParser;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParserFactory;
import com.jijesoft.boh.core.plugin.parsers.XmlDescriptorParserFactory;
import com.jijesoft.boh.core.plugin.util.ClassLoaderUtils;
import static com.jijesoft.boh.core.plugin.util.Assertions.notNull;

/**
 * Loads a single plugin from the descriptor provided, which can either be an InputStream
 * or a resource on the classpath. The classes used by the plugin must already be available
 * on the classpath because this plugin loader does <b>not</b> load any classes.
 *
 * @see PluginLoader
 * @see ClassPathPluginLoader
 */
public class SinglePluginLoader implements PluginLoader
{
    protected Collection<Plugin> plugins;

    /**
     * to load the Stream from the classpath.
     */
    private final String resource;

    /**
     * to load the Stream directly.
     */
    private final URL url;

    private final DescriptorParserFactory descriptorParserFactory = new XmlDescriptorParserFactory();

    private static final Log log = LogFactory.getLog(SinglePluginLoader.class);

    /**
     * @deprecated use URL instead.
     */
    private final AtomicReference<InputStream> inputStreamRef;


    public SinglePluginLoader(final String resource)
    {
        this.resource = notNull("resource", resource);
        url = null;
        inputStreamRef = new AtomicReference<InputStream>(null);
    }

    public SinglePluginLoader(final URL url)
    {
        this.url = notNull("url", url);
        resource = null;
        inputStreamRef = new AtomicReference<InputStream>(null);
    }

    /**
     * @deprecated since 2.2 use the version that passes a URL instead. Not used by the plugins system.
     */
    public SinglePluginLoader(final InputStream is)
    {
        inputStreamRef = new AtomicReference<InputStream>(notNull("inputStream", is));
        resource = null;
        url = null;
    }

    public Collection<Plugin> loadAllPlugins(final ModuleDescriptorFactory moduleDescriptorFactory)
    {
        if (plugins == null)
        {
            Plugin plugin;
            try
            {
                plugin = loadPlugin(moduleDescriptorFactory);
            }
            catch (RuntimeException ex)
            {
                String id = getIdentifier();
                log.error("Error loading plugin or descriptor: " + id, ex);
                plugin = new UnloadablePlugin(id + ": " + ex);
                plugin.setKey(id);
            }
            plugins = Collections.singleton(plugin);
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

    protected Plugin loadPlugin(final ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        final InputStream source = getSource();
        if (source == null)
        {
            throw new PluginParseException("Invalid resource or inputstream specified to load plugins from.");
        }

        Plugin plugin;
        try
        {
            final DescriptorParser parser = descriptorParserFactory.getInstance(source, null);
            plugin = parser.configurePlugin(moduleDescriptorFactory, getNewPlugin());
            if (plugin.getPluginsVersion() == 2)
            {
                UnloadablePlugin unloadablePlugin = UnloadablePluginFactory.createUnloadablePlugin(plugin);
                final StringBuilder errorText = new StringBuilder("OSGi plugins cannot be deployed via the classpath, which is usually WEB-INF/lib.");
                if (resource != null) {
                    errorText.append("\n Resource is: ").append(resource);
                }
                if (url != null) {
                    errorText.append("\n URL is: ").append(url);
                }
                unloadablePlugin.setErrorText(errorText.toString());
                plugin = unloadablePlugin;
            }
        }
        catch (final PluginParseException e)
        {
            throw new PluginParseException("Unable to load plugin resource: " + resource + " - " + e.getMessage(), e);
        }

        return plugin;
    }

    private String getIdentifier()
    {
        if (resource != null) {
            return resource;
        }
        if (url != null) {
            return url.getPath();
        }
        return inputStreamRef.toString();
    }

    protected StaticPlugin getNewPlugin()
    {
        return new StaticPlugin();
    }

    protected InputStream getSource()
    {
        if (resource != null)
        {
            return ClassLoaderUtils.getResourceAsStream(resource, this.getClass());
        }

        if (url != null)
        {
            try
            {
                return url.openConnection().getInputStream();
            }
            catch (IOException e)
            {
                throw new PluginParseException(e);
            }
        }

        final InputStream inputStream = inputStreamRef.getAndSet(null);
        if (inputStream != null)
        {
            return inputStream;
        }
        throw new IllegalStateException("No defined method for getting an input stream.");
    }
}
