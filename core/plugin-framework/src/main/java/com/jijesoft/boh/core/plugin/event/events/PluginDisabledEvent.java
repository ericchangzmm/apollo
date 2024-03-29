package com.jijesoft.boh.core.plugin.event.events;

import com.jijesoft.boh.core.plugin.Plugin;

/**
 * Event that signifies a plugin has been disabled, uninstalled or updated
 */
public class PluginDisabledEvent
{
    private final Plugin plugin;
    
    public PluginDisabledEvent(Plugin plugin)
    {
        this.plugin = plugin;
    }
    
    public Plugin getPlugin()
    {
        return plugin;
    }
}
