package com.jijesoft.boh.core.plugin.event.events;

import com.jijesoft.boh.core.plugin.Plugin;

/**
 * Event fired when the plugin has been refreshed with no user interaction
 *
 */
public class PluginRefreshedEvent
{
    private final Plugin plugin;

    public PluginRefreshedEvent(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public Plugin getPlugin()
    {
        return plugin;
    }
}
