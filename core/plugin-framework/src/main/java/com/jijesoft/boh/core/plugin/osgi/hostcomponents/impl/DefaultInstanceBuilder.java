package com.jijesoft.boh.core.plugin.osgi.hostcomponents.impl;

import com.jijesoft.boh.core.plugin.osgi.hostcomponents.InstanceBuilder;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.PropertyBuilder;

/**
 * Default instance builder for host components
 */
class DefaultInstanceBuilder implements InstanceBuilder
{
    private Registration registration;

    public DefaultInstanceBuilder(Registration registration)
    {
        this.registration = registration;
    }

    public PropertyBuilder forInstance(Object instance)
    {
        registration.setInstance(instance);
        return new DefaultPropertyBuilder(registration);
    }
}
