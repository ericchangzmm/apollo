package com.jijesoft.boh.core.plugin.osgi.event;

import org.osgi.framework.Filter;

/**
 * Event for when a plugin OSGi service dependency that the system was waiting for has been found
 *
 */
public class PluginServiceDependencyWaitEndedEvent extends AbstractPluginServiceDependencyWaitEvent
{
    private final long elapsedTime;

    public PluginServiceDependencyWaitEndedEvent(String pluginKey, String beanName, Filter filter, long elapsedTime)
    {
        super(pluginKey, beanName, filter);
        this.elapsedTime = elapsedTime;
    }

    public long getElapsedTime()
    {
        return elapsedTime;
    }
}