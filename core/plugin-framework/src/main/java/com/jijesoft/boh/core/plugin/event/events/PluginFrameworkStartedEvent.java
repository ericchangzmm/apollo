package com.jijesoft.boh.core.plugin.event.events;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.PluginAccessor;
import com.jijesoft.boh.core.plugin.PluginController;

/**
 * Event that signifies the plugin framework has been started and initialized
 */
public class PluginFrameworkStartedEvent
{
    private final PluginController pluginController;
    private final PluginAccessor pluginAccessor;

    public PluginFrameworkStartedEvent(PluginController pluginController, PluginAccessor pluginAccessor)
    {
        Validate.notNull(pluginController);
        Validate.notNull(pluginAccessor);
        this.pluginController = pluginController;
        this.pluginAccessor = pluginAccessor;
    }

    public PluginController getPluginController()
    {
        return pluginController;
    }

    public PluginAccessor getPluginAccessor()
    {
        return pluginAccessor;
    }
}
