package com.jijesoft.boh.core.plugin.predicate;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.PluginAccessor;

/**
 * A {@link ModuleDescriptorPredicate} that matches enabled modules.
 */
public class EnabledModulePredicate<T> implements ModuleDescriptorPredicate<T>
{
    private final PluginAccessor pluginAccessor;

    /**
     * @throws IllegalArgumentException if pluginAccessor is <code>null</code>
     */
    public EnabledModulePredicate(final PluginAccessor pluginAccessor)
    {
        if (pluginAccessor == null)
        {
            throw new IllegalArgumentException("PluginAccessor must not be null when constructing an EnabledModulePredicate!");
        }
        this.pluginAccessor = pluginAccessor;
    }

    public boolean matches(final ModuleDescriptor<? extends T> moduleDescriptor)
    {
        return (moduleDescriptor != null) && pluginAccessor.isPluginModuleEnabled(moduleDescriptor.getCompleteKey());
    }
}
