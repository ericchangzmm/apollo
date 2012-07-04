package com.jijesoft.boh.core.plugin.impl;

import java.io.InputStream;
import java.net.URL;

import com.jijesoft.boh.core.plugin.PluginException;
import com.jijesoft.boh.core.plugin.util.ClassLoaderUtils;

public class StaticPlugin extends AbstractPlugin
{
    /**
     * Static plugins loaded from the classpath can't be uninstalled.
     */
    public boolean isUninstallable()
    {
        return false;
    }

    public <T> Class<T> loadClass(final String clazz, final Class<?> callingClass) throws ClassNotFoundException
    {
        return ClassLoaderUtils.loadClass(clazz, callingClass);
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public URL getResource(final String name)
    {
        return ClassLoaderUtils.getResource(name, getClass());
    }

    public InputStream getResourceAsStream(final String name)
    {
        return ClassLoaderUtils.getResourceAsStream(name, getClass());
    }

    public boolean isDynamicallyLoaded()
    {
        return false;
    }

    public boolean isDeleteable()
    {
        return false;
    }

    @Override
    protected void uninstallInternal()
    {
        throw new PluginException("Static plugins cannot be uninstalled");
    }
}
