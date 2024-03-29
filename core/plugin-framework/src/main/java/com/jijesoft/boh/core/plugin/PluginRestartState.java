package com.jijesoft.boh.core.plugin;

/**
 * The state of the plugin after restart.  This value indicates the action that will be performed for plugins that
 * cannot be installed, upgraded, or removed at runtime due to the {@link com.jijesoft.boh.core.plugin.descriptors.RequiresRestart @RequiresRestart} annotation on a used module
 * descriptor.
 *
 */
public enum PluginRestartState
{
    /**
     * Indicates an installation will be performed
     */
    INSTALL,

    /**
     * Indicates an upgrade will be performed
     */
    UPGRADE,

    /**
     * Indicates the plugin will be removed
     */
    REMOVE,

    /**
     * Indicates no change upon restart
     */
    NONE
}
