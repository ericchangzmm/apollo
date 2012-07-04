package com.jijesoft.boh.core.plugin.osgi.factory.descriptor;

import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;

/**
 * Module descriptor for Spring components.  Shouldn't be directly used outside providing read-only information.
 *
 */
public class ComponentModuleDescriptor extends AbstractModuleDescriptor<Void>
{

    public Void getModule()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void loadClass(Plugin plugin, String clazz) throws PluginParseException
    {
        // do nothing
    }

    /**
     * @deprecated - BEWARE that this is a temporary method that will not exist for long. Deprecated since 2.3.0
     *
     * @return Module Class Name
     */
    public String getModuleClassName()
    {
        return moduleClassName;
    }
}