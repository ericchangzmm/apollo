package com.jijesoft.boh.core.plugin.event.events;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;

/**
 * Event fired when a plugin module is enabled, which can also happen when its
 * plugin is enabled or installed.
 */
public class PluginModuleEnabledEvent
{
    private final ModuleDescriptor module;

    public PluginModuleEnabledEvent(ModuleDescriptor module)
    {
        this.module = module;
    }

    public ModuleDescriptor getModule()
    {
        return module;
    }
}
