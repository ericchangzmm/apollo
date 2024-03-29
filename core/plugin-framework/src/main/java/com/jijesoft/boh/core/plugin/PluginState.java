package com.jijesoft.boh.core.plugin;

/**
 * Represents the state of the plugin
 *
 */
public enum PluginState
{
    /**
     * The plugin has been installed into the plugin system
     */
    INSTALLED,

    /**
     * The plugin is in the process of being enabled
     */
    ENABLING,

    /**
     * The plugin has been enabled
     */
    ENABLED,

    /**
     * The plugin has been disabled
     */
    DISABLED,

    /**
     * The plugin has been uninstalled and should be unavailable
     */
    UNINSTALLED
}
