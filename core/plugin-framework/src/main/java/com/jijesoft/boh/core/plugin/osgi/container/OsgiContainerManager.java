package com.jijesoft.boh.core.plugin.osgi.container;

import java.io.File;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentRegistration;


/**
 * Manages the OSGi container and handles any interactions with it
 */
public interface OsgiContainerManager
{
    /**
     * Starts the OSGi container
     *
     * @throws OsgiContainerException If the container cannot be started
     */
    void start() throws OsgiContainerException;

    /**
     * Stops the OSGi container
     *
     * @throws OsgiContainerException If the container cannot be stopped
     */
    void stop() throws OsgiContainerException;

    /**
     * Installs a bundle into a running OSGI container
     * @param file The bundle file to install
     * @return The created bundle
     * @throws OsgiContainerException If the bundle cannot be loaded
     */
    Bundle installBundle(File file) throws OsgiContainerException;

    /**
     * @return If the container is running or not
     */
    boolean isRunning();

    /**
     * Gets a list of installed bundles
     *
     * @return An array of bundles
     */
    Bundle[] getBundles();

    /**
     * Gets a list of service references
     * @return An array of service references
     */
    ServiceReference[] getRegisteredServices();

    /**
     * Gets a list of host component registrations
     *
     * @return A list of host component registrations
     */
    List<HostComponentRegistration> getHostComponentRegistrations();

    /**
     * Gets a service tracker to follow a service registered under a certain interface
     *
     * @param interfaceClassName The interface class as a String
     * @return A service tracker to follow all instances of that interface
     * @throws IllegalStateException If the OSGi container is not running
     */
    ServiceTracker getServiceTracker(String interfaceClassName);
}
