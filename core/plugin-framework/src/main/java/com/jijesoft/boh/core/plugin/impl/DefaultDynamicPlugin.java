package com.jijesoft.boh.core.plugin.impl;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.JarPluginArtifact;
import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.classloader.PluginClassLoader;
import com.jijesoft.boh.core.plugin.loaders.classloading.DeploymentUnit;

/**
 * A dynamically loaded plugin is loaded through the plugin class loader.
 */
public class DefaultDynamicPlugin extends AbstractPlugin
{
    private final PluginArtifact pluginArtifact;
    private final PluginClassLoader loader;

    public DefaultDynamicPlugin(final DeploymentUnit deploymentUnit, final PluginClassLoader loader)
    {
        this(new JarPluginArtifact(deploymentUnit.getPath()), loader);
    }

    public DefaultDynamicPlugin(final PluginArtifact pluginArtifact, final PluginClassLoader loader)
    {
        Validate.notNull(pluginArtifact, "The plugin artifact cannot be null");
        Validate.notNull(loader, "The plugin class loader cannot be null");
        this.pluginArtifact = pluginArtifact;
        this.loader = loader;
    }

    public <T> Class<T> loadClass(final String clazz, final Class<?> callingClass) throws ClassNotFoundException
    {
        @SuppressWarnings("unchecked")
        final Class<T> result = (Class<T>) loader.loadClass(clazz);
        return result;
    }

    public boolean isUninstallable()
    {
        return true;
    }

    public URL getResource(final String name)
    {
        return loader.getResource(name);
    }

    public InputStream getResourceAsStream(final String name)
    {
        return loader.getResourceAsStream(name);
    }

    public ClassLoader getClassLoader()
    {
        return loader;
    }

    /**
     * This plugin is dynamically loaded, so returns true.
     *
     * @return true
     */
    public boolean isDynamicallyLoaded()
    {
        return true;
    }

    /**
     * @deprecated Since 2.2.0, use {@link #getPluginArtifact()} instead
     */
    public DeploymentUnit getDeploymentUnit()
    {
        return new DeploymentUnit(pluginArtifact.toFile());
    }

    public PluginArtifact getPluginArtifact()
    {
        return pluginArtifact;
    }

    public boolean isDeleteable()
    {
        return true;
    }

    public boolean isBundledPlugin()
    {
        return false;
    }

    @Override
    protected void uninstallInternal()
    {
        loader.close();
    }
}
