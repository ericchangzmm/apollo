package com.jijesoft.boh.core.plugin.osgi.external;

import java.util.Set;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;

/**
 * A module descriptor factory that can list its supported module descriptors.
 *
 */
public interface ListableModuleDescriptorFactory extends ModuleDescriptorFactory
{
    Set<Class<ModuleDescriptor<?>>> getModuleDescriptorClasses();
}
