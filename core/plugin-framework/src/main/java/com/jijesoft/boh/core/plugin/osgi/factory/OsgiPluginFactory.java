package com.jijesoft.boh.core.plugin.osgi.factory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

import com.jijesoft.boh.core.plugin.JarPluginArtifact;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.ChainModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.event.PluginEventManager;
import com.jijesoft.boh.core.plugin.factories.PluginFactory;
import com.jijesoft.boh.core.plugin.impl.UnloadablePlugin;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerManager;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiPersistentCache;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.DefaultPluginTransformer;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformer;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.model.SystemExports;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParser;
import com.jijesoft.boh.core.plugin.parsers.DescriptorParserFactory;

/**
 * Plugin loader that starts an OSGi container and loads plugins into it, wrapped as OSGi bundles.
 */
public class OsgiPluginFactory implements PluginFactory
{
    private static final Log log = LogFactory.getLog(OsgiPluginFactory.class);

    private final OsgiContainerManager osgi;
    private final String pluginDescriptorFileName;
    private final DescriptorParserFactory descriptorParserFactory;
    private final PluginEventManager pluginEventManager;
    private final Set<String> applicationKeys;
    private final OsgiPersistentCache persistentCache;
//    private final ModuleDescriptorFactory transformedDescriptorFactory = new DefaultModuleDescriptorFactory(new DefaultHostContainer())
//    {{
//            addModuleDescriptor("component", ComponentModuleDescriptor.class);
//            addModuleDescriptor("component-import", ComponentImportModuleDescriptor.class);
//            addModuleDescriptor("module-type", ModuleTypeModuleDescriptor.class);
//        }};

    private volatile PluginTransformer pluginTransformer;

    private ServiceTracker moduleDescriptorFactoryTracker;

    public OsgiPluginFactory(String pluginDescriptorFileName, String applicationKey, OsgiPersistentCache persistentCache, OsgiContainerManager osgi, PluginEventManager pluginEventManager)
    {
        this(pluginDescriptorFileName, new HashSet<String>(Arrays.asList(applicationKey)), persistentCache, osgi, pluginEventManager);
    }

    public OsgiPluginFactory(String pluginDescriptorFileName, Set<String> applicationKeys, OsgiPersistentCache persistentCache, OsgiContainerManager osgi, PluginEventManager pluginEventManager)
    {
        Validate.notNull(pluginDescriptorFileName, "Plugin descriptor is required");
        Validate.notNull(osgi, "The OSGi container is required");
        Validate.notNull(applicationKeys, "The application keys are required");
        Validate.notNull(persistentCache, "The osgi persistent cache is required");
        Validate.notNull(persistentCache, "The plugin event manager is required");

        this.osgi = osgi;
        this.pluginDescriptorFileName = pluginDescriptorFileName;
        this.descriptorParserFactory = new OsgiPluginXmlDescriptorParserFactory();
        this.pluginEventManager = pluginEventManager;
        this.applicationKeys = applicationKeys;
        this.persistentCache = persistentCache;
    }

    private PluginTransformer getPluginTransformer()
    {
        if (pluginTransformer == null)
        {
            String exportString = (String) osgi.getBundles()[0].getHeaders()
                    .get(Constants.EXPORT_PACKAGE);
            SystemExports exports = new SystemExports(exportString);
            pluginTransformer = new DefaultPluginTransformer(persistentCache, exports, applicationKeys, pluginDescriptorFileName);
        }
        return pluginTransformer;
    }

    public String canCreate(PluginArtifact pluginArtifact) throws PluginParseException
    {
        Validate.notNull(pluginArtifact, "The plugin artifact is required");

        String pluginKey = null;
        InputStream descriptorStream = null;
        try
        {
            descriptorStream = pluginArtifact.getResourceAsStream(pluginDescriptorFileName);

            if (descriptorStream != null)
            {
                final DescriptorParser descriptorParser = descriptorParserFactory.getInstance(descriptorStream, applicationKeys.toArray(new String[applicationKeys.size()]));
//                if (descriptorParser.getPluginsVersion() == 2)
//                {
                    pluginKey = descriptorParser.getKey();
//                }
            }
        }
        finally
        {
            IOUtils.closeQuietly(descriptorStream);
        }
        return pluginKey;
    }


    /**
     * Deploys the plugin artifact
     *
     * @param pluginArtifact          the plugin artifact to deploy
     * @param moduleDescriptorFactory The factory for plugin modules
     * @return The instantiated and populated plugin
     * @throws PluginParseException If the descriptor cannot be parsed
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        Validate.notNull(pluginArtifact, "The plugin deployment unit is required");
        Validate.notNull(moduleDescriptorFactory, "The module descriptor factory is required");

        Plugin plugin = null;
        InputStream pluginDescriptor = null;
        try
        {
            pluginDescriptor = pluginArtifact.getResourceAsStream(pluginDescriptorFileName);
            if (pluginDescriptor == null)
            {
                throw new PluginParseException("No descriptor found in classloader for : " + pluginArtifact);
            }

            ModuleDescriptorFactory combinedFactory = getChainedModuleDescriptorFactory(moduleDescriptorFactory);
            DescriptorParser parser = descriptorParserFactory.getInstance(pluginDescriptor, applicationKeys.toArray(new String[applicationKeys.size()]));

            Plugin osgiPlugin = new OsgiPlugin(parser.getKey(), osgi, createOsgiPluginJar(pluginArtifact), pluginEventManager);

            // Temporarily configure plugin until it can be properly installed
            plugin = parser.configurePlugin(combinedFactory, osgiPlugin);
        }
        catch (PluginTransformationException ex)
        {
            return reportUnloadablePlugin(pluginArtifact.toFile(), ex);
        }
        finally
        {
            IOUtils.closeQuietly(pluginDescriptor);
        }
        return plugin;
    }

    /**
     * Get a chained module descriptor factory that includes any dynamically available descriptor factories
     *
     * @param originalFactory The factory provided by the host application
     * @return The composite factory
     */
    private ModuleDescriptorFactory getChainedModuleDescriptorFactory(ModuleDescriptorFactory originalFactory)
    {
        // we really don't want two of these
        synchronized (this)
        {
            if (moduleDescriptorFactoryTracker == null)
            {
                moduleDescriptorFactoryTracker = osgi.getServiceTracker(ModuleDescriptorFactory.class.getName());
            }
        }

        // Really shouldn't be null, but could be in tests since we can't mock a service tracker :(
        if (moduleDescriptorFactoryTracker != null)
        {
            List<ModuleDescriptorFactory> factories = new ArrayList<ModuleDescriptorFactory>();

//            factories.add(transformedDescriptorFactory);
            factories.add(originalFactory);
            Object[] serviceObjs = moduleDescriptorFactoryTracker.getServices();

            // Add all the dynamic module descriptor factories registered as osgi services
            if (serviceObjs != null)
            {
                for (Object fac : serviceObjs)
                {
                    factories.add((ModuleDescriptorFactory) fac);
                }
            }

            // Catch all unknown descriptors as unrecognised
            factories.add(new UnrecognisedModuleDescriptorFallbackFactory());

            return new ChainModuleDescriptorFactory(factories.toArray(new ModuleDescriptorFactory[factories.size()]));
        }
        else
        {
            return originalFactory;
        }


    }

    private PluginArtifact createOsgiPluginJar(PluginArtifact pluginArtifact)
    {
        File transformedFile = getPluginTransformer().transform(pluginArtifact, osgi.getHostComponentRegistrations());
        return new JarPluginArtifact(transformedFile);
    }

    private Plugin reportUnloadablePlugin(File file, Exception e)
    {
        log.error("Unable to load plugin: " + file, e);

        UnloadablePlugin plugin = new UnloadablePlugin();
        plugin.setErrorText("Unable to load plugin: " + e.getMessage());
        return plugin;
    }
}
