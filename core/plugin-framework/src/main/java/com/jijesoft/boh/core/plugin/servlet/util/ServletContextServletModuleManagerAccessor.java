package com.jijesoft.boh.core.plugin.servlet.util;

import javax.servlet.ServletContext;

import com.jijesoft.boh.core.plugin.servlet.ServletModuleManager;

/**
 * Provides static access to a {@link com.jijesoft.boh.core.plugin.hostcontainer.HostContainer} instance.  Requires initialisation before first use.
 *
 */
public class ServletContextServletModuleManagerAccessor
{
    private static final String SERVLET_MODULE_MANAGER_KEY = ServletContextServletModuleManagerAccessor.class.getPackage()+".servletModuleManager";

    /**
     * Gets the servlet module manager in the servlet context
     *
     * @param servletContext the servlet context to look up the servlet module manager in
     * @return The servlet module manager instance
     * @throws IllegalStateException If it hasn't been initialised yet
     */
    public static ServletModuleManager getServletModuleManager(ServletContext servletContext) throws IllegalStateException
    {
        ServletModuleManager servletModuleManager = (ServletModuleManager) servletContext.getAttribute(SERVLET_MODULE_MANAGER_KEY);
        if (servletModuleManager == null)
        {
            throw new IllegalStateException("The "+ ServletContextServletModuleManagerAccessor.class.getName()+" has not been " +
                "set with the current ServletContext.");
        }
        return servletModuleManager;
    }

    /**
     * Sets the implementation of the servlet module manager
     *
     * @param servletContext the servlet context to set the manager in
     * @param servletModuleManager the implementation to set
     */
    public static void setServletModuleManager(ServletContext servletContext, ServletModuleManager servletModuleManager)
    {
        servletContext.setAttribute(SERVLET_MODULE_MANAGER_KEY, servletModuleManager);
    }

}
