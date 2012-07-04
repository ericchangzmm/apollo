package com.jijesoft.boh.core.plugin.event.events;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;

/**
 * Event fired when a plugin module is disabled, which can also happen when its
 * plugin is disabled or uninstalled.
 */
public class PluginModuleDisabledEvent
{
    private final ModuleDescriptor module;

    public PluginModuleDisabledEvent(ModuleDescriptor module)
    {
        this.module = module;
    }

    public ModuleDescriptor getModule()
    {
        return module;
    }
}