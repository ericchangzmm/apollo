package com.jijesoft.boh.core.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.jijesoft.boh.core.plugin.event.PluginEventManager;
import com.jijesoft.boh.core.plugin.factories.PluginFactory;
import com.jijesoft.boh.core.plugin.loaders.BundledPluginLoader;
import com.jijesoft.boh.core.plugin.loaders.ClassPathPluginLoader;
import com.jijesoft.boh.core.plugin.loaders.DirectoryPluginLoader;
import com.jijesoft.boh.core.plugin.loaders.PluginLoader;
import com.jijesoft.boh.core.plugin.manager.DefaultPluginManager;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerManager;
import com.jijesoft.boh.core.plugin.osgi.container.felix.FelixOsgiContainerManager;
import com.jijesoft.boh.core.plugin.osgi.factory.OsgiBundleFactory;
import com.jijesoft.boh.core.plugin.osgi.factory.OsgiPluginFactory;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentProvider;
import com.jijesoft.boh.core.plugin.repositories.FilePluginInstaller;

/**
 * Facade interface to the JiJeSoft Plugins framework.  See the package Javadocs for usage information.
 */
public class JijePlugins
{
    private OsgiContainerManager osgiContainerManager;
    private PluginEventManager pluginEventManager;
    private DefaultPluginManager pluginManager;
    private PluginsConfiguration pluginsConfiguration;
    private HotDeployer hotDeployer;

    private static final Logger log = Logger.getLogger(JijePlugins.class);

    /**
     * Suffix for temporary directories which will be removed on shutdown
     */
    public static final String TEMP_DIRECTORY_SUFFIX = ".tmp";

    /**
     * Constructs an instance of the plugin framework with the specified config.  No additional validation is performed
     * on the configuration, so it is recommended you use the {@link PluginsConfigurationBuilder} class to create
     * a configuration instance.
     * @param config The plugins configuration to use
     */
    public JijePlugins(PluginsConfiguration config,PluginEventManager pluginEventManager)
    {
        osgiContainerManager = new FelixOsgiContainerManager(
                config.getOsgiPersistentCache(),
                config.getPackageScannerConfiguration(),
                new CriticalHostComponentProvider(config.getHostComponentProvider(), pluginEventManager),
                pluginEventManager);

        // plugin factories/deployers
        final OsgiPluginFactory osgiPluginDeployer = new OsgiPluginFactory(
                config.getPluginDescriptorFilename(),
                config.getApplicationKey(),
                config.getOsgiPersistentCache(),
                osgiContainerManager,
                pluginEventManager);
        final OsgiBundleFactory osgiBundleDeployer = new OsgiBundleFactory(osgiContainerManager, pluginEventManager);
        final List<PluginFactory> pluginDeployers = new LinkedList<PluginFactory>(Arrays.asList(osgiPluginDeployer, osgiBundleDeployer));
        final List<PluginLoader> pluginLoaders = new ArrayList<PluginLoader>();

        // classpath plugins
        pluginLoaders.add(new ClassPathPluginLoader());

        // bundled plugins
        if (config.getBundledPluginUrl() != null)
        {
            pluginLoaders.add(new BundledPluginLoader(config.getBundledPluginUrl(), config.getBundledPluginCacheDirectory(), pluginDeployers, pluginEventManager));
        }

        // osgi plugins
        pluginLoaders.add(new DirectoryPluginLoader(config.getPluginDirectory(), pluginDeployers, pluginEventManager));

        pluginManager = new DefaultPluginManager(
                config.getPluginStateStore(),
                pluginLoaders,
                config.getModuleDescriptorFactory(),
                pluginEventManager);

        pluginManager.setPluginInstaller(new FilePluginInstaller(config.getPluginDirectory()));

        if (config.getHotDeployPollingPeriod() > 0)
        {
            hotDeployer = new HotDeployer(pluginManager, config.getHotDeployPollingPeriod());
        }
        this.pluginsConfiguration = config;


    }

    /**
     * Starts the plugins framework.  Will return once the plugins have all been loaded and started.  Should only be
     * called once.
     * @throws PluginParseException If there was any problems parsing any of the plugins
     */
    public void start() throws PluginParseException
    {
        pluginManager.init();
        if (hotDeployer != null && !hotDeployer.isRunning())
        {
            hotDeployer.start();
        }
    }

    /**
     * Stops the framework.
     */
    public void stop()
    {
        if (hotDeployer != null && hotDeployer.isRunning())
        {
            hotDeployer.stop();
        }
        pluginManager.shutdown();
    }

    /**
     * @return the underlying OSGi container manager
     */
    public OsgiContainerManager getOsgiContainerManager()
    {
        return osgiContainerManager;
    }

    /**
     * @return the plugin event manager
     */
    public PluginEventManager getPluginEventManager()
    {
        return pluginEventManager;
    }

    /**
     * @return the plugin controller for manipulating plugins
     */
    public PluginController getPluginController()
    {
        return pluginManager;
    }

    /**
     * @return the plugin accessor for accessing plugins
     */
    public PluginAccessor getPluginAccessor()
    {
        return pluginManager;
    }

    private static class CriticalHostComponentProvider implements HostComponentProvider
    {
        private final HostComponentProvider delegate;
        private final PluginEventManager pluginEventManager;

        public CriticalHostComponentProvider(HostComponentProvider delegate, PluginEventManager pluginEventManager)
        {
            this.delegate = delegate;
            this.pluginEventManager = pluginEventManager;
        }

        public void provide(ComponentRegistrar registrar)
        {
            registrar.register(PluginEventManager.class).forInstance(pluginEventManager);
            delegate.provide(registrar);
        }
    }
}
