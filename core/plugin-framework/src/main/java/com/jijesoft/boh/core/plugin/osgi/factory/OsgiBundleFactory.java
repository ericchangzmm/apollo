package com.jijesoft.boh.core.plugin.osgi.factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.jijesoft.boh.core.plugin.JarPluginArtifact;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.event.PluginEventManager;
import com.jijesoft.boh.core.plugin.factories.PluginFactory;
import com.jijesoft.boh.core.plugin.impl.UnloadablePlugin;
import com.jijesoft.boh.core.plugin.loaders.classloading.DeploymentUnit;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerException;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerManager;
import com.jijesoft.boh.core.plugin.osgi.util.OsgiHeaderUtil;

/**
 * Plugin deployer that deploys OSGi bundles that don't contain XML descriptor files
 */
public class OsgiBundleFactory implements PluginFactory
{
    private static final Log log = LogFactory.getLog(OsgiBundleFactory.class);

    private final OsgiContainerManager osgi;
    private final PluginEventManager pluginEventManager;

    public OsgiBundleFactory(OsgiContainerManager osgi, PluginEventManager pluginEventManager)
    {
        Validate.notNull(osgi, "The osgi container is required");
        Validate.notNull(pluginEventManager, "The plugin event manager is required");
        this.osgi = osgi;
        this.pluginEventManager = pluginEventManager;
    }

    public String canCreate(PluginArtifact pluginArtifact) throws PluginParseException {
        Validate.notNull(pluginArtifact, "The plugin artifact is required");
        String pluginKey = null;
        InputStream manifestStream = null;

        try
        {
            manifestStream = pluginArtifact.getResourceAsStream("META-INF/MANIFEST.MF");
            if (manifestStream != null)
            {
                Manifest mf;
                try {
                    mf = new Manifest(manifestStream);
                } catch (IOException e) {
                    throw new PluginParseException("Unable to parse manifest", e);
                }
                String symName = mf.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
                if (symName != null)
                {
                    pluginKey = OsgiHeaderUtil.getPluginKey(mf);
                }
            }
            return pluginKey;
        }
        finally
        {
            IOUtils.closeQuietly(manifestStream);
        }
    }

    /**
     * Deploys the plugin artifact
     * @param pluginArtifact the plugin artifact to deploy
     * @param moduleDescriptorFactory The factory for plugin modules
     * @return The instantiated and populated plugin
     * @throws PluginParseException If the descriptor cannot be parsed
     */
    public Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        Validate.notNull(pluginArtifact, "The plugin artifact is required");
        Validate.notNull(moduleDescriptorFactory, "The module descriptor factory is required");

        File file = pluginArtifact.toFile();
        Bundle bundle;
        try
        {
            bundle = osgi.installBundle(file);
        } catch (OsgiContainerException ex)
        {
            return reportUnloadablePlugin(file, ex);
        }
        String key = OsgiHeaderUtil.getPluginKey(bundle);
        return new OsgiBundlePlugin(bundle, key, pluginEventManager);
    }

    private Plugin reportUnloadablePlugin(File file, Exception e)
    {
        log.error("Unable to load plugin: "+file, e);

        UnloadablePlugin plugin = new UnloadablePlugin();
        plugin.setErrorText("Unable to load plugin: "+e.getMessage());
        return plugin;
    }
}