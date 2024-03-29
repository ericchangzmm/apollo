package com.jijesoft.boh.core.plugin.osgi.container.felix;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

import com.jijesoft.boh.core.plugin.event.PluginEventListener;
import com.jijesoft.boh.core.plugin.event.PluginEventManager;
import com.jijesoft.boh.core.plugin.event.events.PluginFrameworkShutdownEvent;
import com.jijesoft.boh.core.plugin.event.events.PluginFrameworkStartingEvent;
import com.jijesoft.boh.core.plugin.event.events.PluginFrameworkWarmRestartingEvent;
import com.jijesoft.boh.core.plugin.event.events.PluginUpgradedEvent;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerException;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerManager;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiPersistentCache;
import com.jijesoft.boh.core.plugin.osgi.container.PackageScannerConfiguration;
import com.jijesoft.boh.core.plugin.osgi.container.impl.DefaultOsgiPersistentCache;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentProvider;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.impl.DefaultComponentRegistrar;
import com.jijesoft.boh.core.plugin.osgi.util.OsgiHeaderUtil;
import com.jijesoft.boh.core.plugin.util.ClassLoaderUtils;
import com.jijesoft.boh.core.plugin.util.FileUtils;

/**
 * Felix implementation of the OSGi container manager
 */
public class FelixOsgiContainerManager implements OsgiContainerManager
{
   // public static final String OSGI_FRAMEWORK_BUNDLES_ZIP = "osgi-framework-bundles.zip";

    private static final Log log = LogFactory.getLog(FelixOsgiContainerManager.class);
    private static final String OSGI_BOOTDELEGATION = "org.osgi.framework.bootdelegation";
    private static final String JIJESOFT_PREFIX = "jijesoft.";

    private final OsgiPersistentCache persistentCache;
//    private final URL frameworkBundlesUrl;
    private final PackageScannerConfiguration packageScannerConfig;
    private final HostComponentProvider hostComponentProvider;
    private final Set<ServiceTracker> trackers;
    private final ExportsBuilder exportsBuilder;
    private final ThreadFactory threadFactory = new ThreadFactory()
    {
        public Thread newThread(final Runnable r)
        {
            final Thread thread = new Thread(r, "Felix:Startup");
            thread.setDaemon(true);
            return thread;
        }
    };

    private BundleRegistration registration = null;
    private Felix felix = null;
    private boolean felixRunning = false;
    private boolean disableMultipleBundleVersions = true;
    private Logger felixLogger;

    /**
     * Constructs the container manager
     * @param frameworkBundlesZip The location of the zip file containing framework bundles
     * @param persistentCache The persistent cache to use for the framework and framework bundles
     * @param packageScannerConfig The configuration for package scanning
     * @param provider The host component provider.  May be null.
     * @param eventManager The plugin event manager to register for init and shutdown events
     *
     * @throws com.jijesoft.boh.core.plugin.osgi.container.OsgiContainerException If the host version isn't supplied and the
     *  cache directory cannot be cleaned.
     */
    public FelixOsgiContainerManager(final OsgiPersistentCache persistentCache,
                                     final PackageScannerConfiguration packageScannerConfig,
                                     final HostComponentProvider provider, final PluginEventManager eventManager)
                                     throws OsgiContainerException
    {
       // Validate.notNull(frameworkBundlesZip, "The framework bundles zip is required");
        Validate.notNull(persistentCache, "The framework bundles directory must not be null");
        Validate.notNull(packageScannerConfig, "The package scanner configuration must not be null");
        Validate.notNull(eventManager, "The plugin event manager is required");

//        frameworkBundlesUrl = frameworkBundlesZip;
        this.packageScannerConfig = packageScannerConfig;
        this.persistentCache = persistentCache;
        hostComponentProvider = provider;
        trackers = Collections.synchronizedSet(new HashSet<ServiceTracker>());
        eventManager.register(this);
        felixLogger = new FelixLoggerBridge(log);
        exportsBuilder = new ExportsBuilder();
    }

    public void setFelixLogger(final Logger logger)
    {
        felixLogger = logger;
    }

    public void setDisableMultipleBundleVersions(final boolean val)
    {
        disableMultipleBundleVersions = val;
    }

    @PluginEventListener
    public void onStart(final PluginFrameworkStartingEvent event)
    {
        start();
    }

    @PluginEventListener
    public void onShutdown(final PluginFrameworkShutdownEvent event)
    {
        stop();
    }

    @PluginEventListener
    public void onPluginUpgrade(PluginUpgradedEvent event)
    {
        registration.refreshPackages();
    }

    @PluginEventListener
    public void onPluginFrameworkWarmRestarting(PluginFrameworkWarmRestartingEvent event)
    {
        registration.loadHostComponents(collectHostComponents(hostComponentProvider));
    }

    public void start() throws OsgiContainerException
    {


        if (isRunning())
        {
            return;
        }

        final DefaultComponentRegistrar registrar = collectHostComponents(hostComponentProvider);
        // Create a case-insensitive configuration property map.
        final StringMap configMap = new StringMap(false);

        // Add the bundle provided service interface package and the core OSGi
        // packages to be exported from the class path via the system bundle.
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, exportsBuilder.determineExports(registrar.getRegistry(), packageScannerConfig,
            persistentCache.getOsgiBundleCache()));

        // Explicitly specify the directory to use for caching bundles.
        configMap.put(BundleCache.CACHE_ROOTDIR_PROP, persistentCache.getOsgiBundleCache().getAbsolutePath());

        configMap.put(FelixConstants.LOG_LEVEL_PROP, String.valueOf(felixLogger.getLogLevel()));
        configMap.put(FelixConstants.LOG_LOGGER_PROP, felixLogger);
        String bootDelegation = getSpecificOsgiSystemProperty(OSGI_BOOTDELEGATION);
        if ((bootDelegation == null) || (bootDelegation.trim().length() == 0))
        {
            bootDelegation = "weblogic,weblogic.*," +
                             "META-INF.services," +
                             "com.yourkit,com.yourkit.*," +
                             "com.jprofiler,com.jprofiler.*," +
                             "org.apache.xerces,org.apache.xerces.*";
        }

        configMap.put(FelixConstants.FRAMEWORK_BOOTDELEGATION, bootDelegation);

        configMap.put(FelixConstants.FRAMEWORK_BUNDLE_PARENT, FelixConstants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK);
        if (log.isDebugEnabled())
        {
            log.debug("Felix configuration: " + configMap);
        }

        validateConfiguration(configMap);

        try
        {
            // Create host activator;
            registration = new BundleRegistration(persistentCache.getFrameworkBundleCache(), registrar);
            final List<BundleActivator> list = new ArrayList<BundleActivator>();
            list.add(registration);
            configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

            // Now create an instance of the framework with
            // our configuration properties and activator.
            felix = new Felix(configMap);

            // Now start Felix instance.  Starting in a different thread to explicity set daemon status
            final Runnable start = new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.currentThread().setContextClassLoader(null);
                        felix.start();
                        felixRunning = true;
                    }
                    catch (final BundleException e)
                    {
                        throw new OsgiContainerException("Unable to start felix", e);
                    }
                }
            };
            final Thread t = threadFactory.newThread(start);
            t.start();

            // Give it 10 seconds
            t.join(10 * 60 * 1000);

        }
        catch (final Exception ex)
        {
            throw new OsgiContainerException("Unable to start OSGi container", ex);
        }
    }

    /**
     *
     * @param configMap The Felix configuration
     * @throws OsgiContainerException If any validation fails
     */
    private void validateConfiguration(StringMap configMap) throws OsgiContainerException
    {
        String systemExports = (String) configMap.get(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA);
        validateCaches(systemExports);
        detectIncorrectOsgiVersion();
        detectXercesOverride(systemExports);
    }

    /**
     * Detect when xerces has no version, most likely due to an installation of Tomcat where an old version of xerces
     * is installed into common/lib/endorsed in order to support Java 1.4.
     *
     * @param systemExports The system exports
     * @throws OsgiContainerException If xerces has no version 
     */
    void detectXercesOverride(String systemExports) throws OsgiContainerException
    {
        int pos = systemExports.indexOf("org.apache.xerces.util");
        if (pos > -1)
        {
            pos += "org.apache.xerces.util".length();

            // only fail if no xerces found and xerces has no version
            if (pos >= systemExports.length() || ';' != systemExports.charAt(pos))
            {
                throw new OsgiContainerException(
                    "Detected an incompatible version of Apache Xerces on the classpath.  If using Tomcat, you may have " +
                    "an old version of Xerces in $TOMCAT_HOME/common/lib/endorsed that will need to be removed.");
            }
        }
    }

    /**
     * Validate caches based on the list of packages exported from the application.  If the list has changed, the cache
     * directories should be cleared.
     *
     * @param systemExports The value of system exports in the header
     */
    private void validateCaches(String systemExports)
    {
        String cacheKey = String.valueOf(systemExports.hashCode());
        persistentCache.validate(cacheKey);

        log.debug("Using Felix bundle cache directory :" + persistentCache.getOsgiBundleCache().getAbsolutePath());
    }

    /**
     * Detects incorrect configuration of WebSphere 6.1 that leaks OSGi 4.0 jars into the application
     */
    private void detectIncorrectOsgiVersion()
    {
        try
        {
            Bundle.class.getMethod("getBundleContext");
        }
        catch (final NoSuchMethodException e)
        {
            throw new OsgiContainerException(
                "Detected older version (4.0 or earlier) of OSGi.  If using WebSphere " + "6.1, please enable application-first (parent-last) classloading and the 'Single classloader for " + "application' WAR classloader policy.");
        }
    }

    public void stop() throws OsgiContainerException
    {
        if (felixRunning)
        {
            for (final ServiceTracker tracker : new HashSet<ServiceTracker>(trackers))
            {
                tracker.close();
            }
            try
            {
                felix.stop();
                felix.waitForStop(5000);
            }
            catch (InterruptedException e)
            {
                log.warn("Interrupting Felix shutdown", e);
            }
            catch (BundleException e)
            {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        felixRunning = false;
        felix = null;
    }

    public Bundle[] getBundles()
    {
        if (isRunning())
        {
            return registration.getBundles();
        }
        else
        {
            throw new IllegalStateException(
                "Cannot retrieve the bundles if the Felix container isn't running. Check earlier in the logs for the possible cause as to why Felix didn't start correctly.");
        }
    }

    public ServiceReference[] getRegisteredServices()
    {
        return felix.getRegisteredServices();
    }

    public ServiceTracker getServiceTracker(final String cls)
    {
        if (!isRunning())
        {
            throw new IllegalStateException("Unable to create a tracker when osgi is not running");
        }

        final ServiceTracker tracker = registration.getServiceTracker(cls, trackers);
        tracker.open();
        trackers.add(tracker);
        return tracker;
    }

    public Bundle installBundle(final File file) throws OsgiContainerException
    {
        try
        {
            return registration.install(file, disableMultipleBundleVersions);
        }
        catch (final BundleException e)
        {
            throw new OsgiContainerException("Unable to install bundle", e);
        }
    }

    DefaultComponentRegistrar collectHostComponents(final HostComponentProvider provider)
    {
        final DefaultComponentRegistrar registrar = new DefaultComponentRegistrar();
        if (provider != null)
        {
            provider.provide(registrar);
        }
        return registrar;
    }

    public boolean isRunning()
    {
        return felixRunning;
    }

    public List<HostComponentRegistration> getHostComponentRegistrations()
    {
        return registration.getHostComponentRegistrations();
    }

    private String getSpecificOsgiSystemProperty(final String originalSystemProperty)
    {
        return System.getProperty(JIJESOFT_PREFIX + originalSystemProperty);
    }

    /**
     * Manages framework-level framework bundles and host components registration, and individual plugin bundle
     * installation and removal.
     */
    static class BundleRegistration implements BundleActivator, BundleListener, FrameworkListener
    {
        private BundleContext bundleContext;
        private DefaultComponentRegistrar registrar;
        private List<ServiceRegistration> hostServicesReferences;
        private List<HostComponentRegistration> hostComponentRegistrations;
        //private final URL frameworkBundlesUrl;
        private final File frameworkBundlesDir;
        private final ClassLoader initializedClassLoader;
        private PackageAdmin packageAdmin;

        public BundleRegistration(final File frameworkBundlesDir, final DefaultComponentRegistrar registrar)
        {
            this.registrar = registrar;
          //  this.frameworkBundlesUrl = frameworkBundlesUrl;
            this.frameworkBundlesDir = frameworkBundlesDir;
            this.initializedClassLoader = Thread.currentThread().getContextClassLoader();
        }

        public void start(final BundleContext context) throws Exception
        {
            bundleContext = context;
            final ServiceReference ref = context.getServiceReference(org.osgi.service.packageadmin.PackageAdmin.class.getName());
            packageAdmin = (PackageAdmin) context.getService(ref);

            context.addBundleListener(this);
            context.addFrameworkListener(this);

            loadHostComponents(registrar);
            extractAndInstallFrameworkBundles();
        }

        public void stop(final BundleContext ctx) throws Exception
        {
            ctx.removeBundleListener(this);
            ctx.removeFrameworkListener(this);
            if (hostServicesReferences != null)
            {
                for (ServiceRegistration ref : hostServicesReferences)
                {
                    ref.unregister();
                }
            }
            bundleContext = null;
            packageAdmin = null;
            hostServicesReferences = null;
            hostComponentRegistrations = null;
            registrar = null;
        }

        public void bundleChanged(final BundleEvent evt)
        {
            switch (evt.getType())
            {
                case BundleEvent.INSTALLED:
                    log.info("Installed bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
                case BundleEvent.RESOLVED:
                    log.info("Resolved bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
                case BundleEvent.UNRESOLVED:
                    log.info("Unresolved bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
                case BundleEvent.STARTED:
                    log.info("Started bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
                case BundleEvent.STOPPED:
                    log.info("Stopped bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
                case BundleEvent.UNINSTALLED:
                    log.info("Uninstalled bundle " + evt.getBundle().getSymbolicName() + " (" + evt.getBundle().getBundleId() + ")");
                    break;
            }
        }

        public Bundle install(final File path, final boolean uninstallOtherVersions) throws BundleException
        {
            boolean bundleUninstalled = false;
            if (uninstallOtherVersions)
            {
                try
                {
                    JarFile jar = null;
                    String pluginKey = null;
                    try
                    {
                        jar = new JarFile(path);
                        pluginKey = OsgiHeaderUtil.getPluginKey(jar.getManifest());
                    }
                    finally
                    {
                        jar.close();
                    }
                    for (final Bundle oldBundle : bundleContext.getBundles())
                    {
                        if (pluginKey.equals(OsgiHeaderUtil.getPluginKey(oldBundle)))
                        {
                            log.info("Uninstalling existing version " + oldBundle.getHeaders().get(Constants.BUNDLE_VERSION));
                            oldBundle.uninstall();
                            bundleUninstalled = true;
                        }
                    }
                }
                catch (final IOException e)
                {
                    throw new BundleException("Invalid bundle format", e);
                }
            }
            final Bundle bundle = bundleContext.installBundle(path.toURI().toString());
            if (bundleUninstalled)
            {
                refreshPackages();
            }
            return bundle;
        }

        public Bundle[] getBundles()
        {
            return bundleContext.getBundles();
        }

        public ServiceTracker getServiceTracker(final String clazz, final Set<ServiceTracker> trackedTrackers)
        {
            return new ServiceTracker(bundleContext, clazz, null)
            {
                @Override
                public void close()
                {
                    trackedTrackers.remove(this);
                }
            };
        }

        public List<HostComponentRegistration> getHostComponentRegistrations()
        {
            return hostComponentRegistrations;
        }

        void loadHostComponents(final DefaultComponentRegistrar registrar)
        {
            // Unregister any existing host components
            if (hostServicesReferences != null)
            {
                for (final ServiceRegistration reg : hostServicesReferences)
                {
                    reg.unregister();
                }
            }

            // Swap the old classloader back in when creating proxies for host components
            ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
            try
            {
                Thread.currentThread().setContextClassLoader(initializedClassLoader);
                // Register host components as OSGi services
                hostServicesReferences = registrar.writeRegistry(bundleContext);
                hostComponentRegistrations = registrar.getRegistry();
            }
            finally
            {
                Thread.currentThread().setContextClassLoader(oldCl);
            }

        }

        private void extractAndInstallFrameworkBundles() throws BundleException
        {
            final List<Bundle> bundles = new ArrayList<Bundle>();
           // FileUtils.conditionallyExtractZipFile(frameworkBundlesUrl, frameworkBundlesDir);
            for (final File bundleFile : frameworkBundlesDir.listFiles(new FilenameFilter()
                {
                    public boolean accept(final File file, final String s)
                    {
                        return s.endsWith(".jar");
                    }
                }))
            {
                bundles.add(install(bundleFile, false));
            }

            for (final Bundle bundle : bundles)
            {
                bundle.start();
            }
        }

        public void refreshPackages()
        {
            final CountDownLatch latch = new CountDownLatch(1);
            FrameworkListener refreshListener = new FrameworkListener()
            {

                public void frameworkEvent(FrameworkEvent event)
                {
                    if (event.getType() == FrameworkEvent.PACKAGES_REFRESHED)
                    {
                        log.info("Packages refreshed");
                        latch.countDown();
                    }
                }
            };

            bundleContext.addFrameworkListener(refreshListener);
            try
            {
                packageAdmin.refreshPackages(null);
                boolean refreshed = false;
                try
                {
                    refreshed = latch.await(10, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    // ignore
                }
                if (!refreshed)
                {
                    log.warn("Timeout exceeded waiting for package refresh");
                }
            }
            finally
            {
                bundleContext.removeFrameworkListener(refreshListener);
            }
        }

        public void frameworkEvent(FrameworkEvent event)
        {
            String bundleBits = "";
            if (event.getBundle() != null)
            {
                bundleBits = " in bundle " + event.getBundle().getSymbolicName();
            }
            switch (event.getType())
            {
                case FrameworkEvent.ERROR:
                    log.error("Framework error" + bundleBits, event.getThrowable());
                    break;
                case FrameworkEvent.WARNING:
                    log.warn("Framework warning" + bundleBits, event.getThrowable());
                    break;
                case FrameworkEvent.INFO:
                    log.info("Framework info" + bundleBits, event.getThrowable());
                    break;
            }
        }
    }

}
