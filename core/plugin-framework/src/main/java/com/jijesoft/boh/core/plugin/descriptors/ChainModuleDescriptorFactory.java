package com.jijesoft.boh.core.plugin.descriptors;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.PluginParseException;

/**
 * Module descriptor factory that checks multiple factories in sequence.  There is no attempt at caching the results.
 */
public class ChainModuleDescriptorFactory implements ModuleDescriptorFactory
{
    private final ModuleDescriptorFactory[] factories;

    public ChainModuleDescriptorFactory(final ModuleDescriptorFactory... factories)
    {
        this.factories = factories;
    }

    public ModuleDescriptor<?> getModuleDescriptor(final String type) throws PluginParseException, IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        for (final ModuleDescriptorFactory factory : factories)
        {
            if (factory.hasModuleDescriptor(type))
            {
                return factory.getModuleDescriptor(type);
            }
        }
        return null;
    }

    public boolean hasModuleDescriptor(final String type)
    {
        for (final ModuleDescriptorFactory factory : factories)
        {
            if (factory.hasModuleDescriptor(type))
            {
                return true;
            }
        }
        return false;
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(final String type)
    {
        for (final ModuleDescriptorFactory factory : factories)
        {
            final Class<? extends ModuleDescriptor> descriptorClass = factory.getModuleDescriptorClass(type);
            if (descriptorClass != null)
            {
                return descriptorClass;
            }
        }
        return null;
    }
}
