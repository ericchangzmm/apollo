package com.jijesoft.boh.core.plugin.servlet.descriptors;

import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.AutowireCapablePlugin;
import com.jijesoft.boh.core.plugin.StateAware;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;
import com.jijesoft.boh.core.plugin.servlet.ServletModuleManager;

/**
 * A module descriptor that allows plugin developers to define servlets. Developers can define what urls the
 * servlet should be serve by defining one or more &lt;url-pattern&gt; elements.
 */
public class ServletModuleDescriptor extends BaseServletModuleDescriptor<HttpServlet> implements StateAware
{
    private final ServletModuleManager servletModuleManager;
    private final HostContainer hostContainer;

    /**
     * Creates a descriptor that uses a module factory to create instances
     *
     * @param hostContainer The module factory
     */
    public ServletModuleDescriptor(final HostContainer hostContainer, final ServletModuleManager servletModuleManager)
    {
        Validate.notNull(hostContainer);
        Validate.notNull(servletModuleManager);
        this.hostContainer = hostContainer;
        this.servletModuleManager = servletModuleManager;
    }

    @Override
    public void enabled()
    {
        super.enabled();
        servletModuleManager.addServletModule(this);
    }

    @Override
    public void disabled()
    {
        servletModuleManager.removeServletModule(this);
        super.disabled();
    }

    @Override
    public HttpServlet getModule()
    {
        // Give the plugin a go first
        if (plugin instanceof AutowireCapablePlugin)
        {
            return ((AutowireCapablePlugin) plugin).autowire(getModuleClass());
        }
        return hostContainer.create(getModuleClass());
    }

}
