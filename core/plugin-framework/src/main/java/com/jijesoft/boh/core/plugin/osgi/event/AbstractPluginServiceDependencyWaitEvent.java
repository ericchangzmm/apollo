package com.jijesoft.boh.core.plugin.osgi.event;

import org.osgi.framework.Filter;

/**
 * Abstract implementation of plugin service dependency waiting events
 *
 */
class AbstractPluginServiceDependencyWaitEvent implements PluginServiceDependencyWaitEvent
{
    protected final Filter filter;
    protected final String beanName;
    protected final String pluginKey;

    protected AbstractPluginServiceDependencyWaitEvent(String pluginKey, String beanName, Filter filter)
    {
        this.pluginKey = pluginKey;
        this.beanName = beanName;
        this.filter = filter;
    }

    public Filter getFilter()
    {
        return filter;
    }

    public String getBeanName()
    {
        return beanName;
    }

    public String getPluginKey()
    {
        return pluginKey;
    }
}
