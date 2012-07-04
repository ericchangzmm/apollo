package com.jijesoft.boh.core.plugin.osgi.factory.descriptor;

import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;

/**
 * Module descriptor for OSGi service imports.  Shouldn't be directly used outside providing read-only information.
 *
 */
public class ComponentImportModuleDescriptor extends AbstractModuleDescriptor<Void>
{
    public Void getModule()
    {
        throw new UnsupportedOperationException();
    }

}