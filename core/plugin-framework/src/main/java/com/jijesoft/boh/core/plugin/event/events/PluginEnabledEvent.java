package com.jijesoft.boh.core.plugin.event.events;

import com.jijesoft.boh.core.plugin.Plugin;

/**
 * Event fired when a plugin is enabled, installed or updated.
 */
public class PluginEnabledEvent
{
    private final Plugin plugin;

    public PluginEnabledEvent(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public Plugin getPlugin()
    {
        return plugin;
    }
}
