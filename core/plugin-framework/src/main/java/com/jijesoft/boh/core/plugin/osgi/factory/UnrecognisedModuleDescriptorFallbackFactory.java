package com.jijesoft.boh.core.plugin.osgi.factory;

import org.apache.log4j.Logger;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptor;

/**
 * Module descriptor factory for deferred modules.  Turns every request for a module descriptor into a deferred
 * module so be sure that this factory is last in a list of factories.
 *
 * @see {@link UnrecognisedModuleDescriptor}
 */
class UnrecognisedModuleDescriptorFallbackFactory implements ModuleDescriptorFactory
{
    private static final Logger log = Logger.getLogger(UnrecognisedModuleDescriptorFallbackFactory.class);
    public static final String DESCRIPTOR_TEXT = "Support for this module is not currently installed.";

    public UnrecognisedModuleDescriptor getModuleDescriptor(final String type) throws PluginParseException, IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        log.info("Unknown module descriptor of type " + type + " registered as a unrecognised descriptor.");
        final UnrecognisedModuleDescriptor descriptor = new UnrecognisedModuleDescriptor();
        descriptor.setErrorText(DESCRIPTOR_TEXT);
        return descriptor;
    }

    public boolean hasModuleDescriptor(final String type)
    {
        return true;
    }

    public Class<? extends ModuleDescriptor<?>> getModuleDescriptorClass(final String type)
    {
        return UnrecognisedModuleDescriptor.class;
    }
}
