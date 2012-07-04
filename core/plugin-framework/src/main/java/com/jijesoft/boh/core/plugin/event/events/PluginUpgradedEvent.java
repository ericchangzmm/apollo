package com.jijesoft.boh.core.plugin.event.events;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.Plugin;

/**
 * Event that indicates a plugin has been upgraded at runtime
 *
 */
public class PluginUpgradedEvent
{
    private final Plugin plugin;

    /**
     * Constructs the event
     * @param plugin The plugin that has been upgraded
     */
    public PluginUpgradedEvent(Plugin plugin)
    {
        Validate.notNull(plugin);
        this.plugin = plugin;
    }

    /**
     * @return the plugin that has been upgraded
     */
    public Plugin getPlugin()
    {
        return plugin;
    }
}
