package com.jijesoft.boh.core.plugin.osgi.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.jijesoft.boh.core.plugin.osgi.external.ListableModuleDescriptorFactory;

/**
 * Service tracker that tracks {@link com.jijesoft.boh.core.plugin.osgi.external.ListableModuleDescriptorFactory} instances and handles transforming
 * {@link com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptor}} instances into modules if the new factory supports them.  Updates to factories
 * and removal are also handled.
 *
 */
class UnrecognizedModuleDescriptorServiceTrackerCustomizer implements ServiceTrackerCustomizer
{
    private static final Log log = LogFactory.getLog(UnrecognizedModuleDescriptorServiceTrackerCustomizer.class);

    private final Bundle bundle;
    private final OsgiPlugin plugin;

    public UnrecognizedModuleDescriptorServiceTrackerCustomizer(OsgiPlugin plugin)
    {
        Validate.notNull(plugin);
        this.bundle = plugin.getBundle();
        Validate.notNull(bundle);
        this.plugin = plugin;
    }
    /**
     * Turns any {@link com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptor} modules that can be handled by the new factory into real
     * modules
     */
    public Object addingService(final ServiceReference serviceReference)
    {
        final ListableModuleDescriptorFactory factory = (ListableModuleDescriptorFactory) bundle.getBundleContext().getService(serviceReference);
        boolean usedFactory = false;
        for (final UnrecognisedModuleDescriptor unrecognised : getModuleDescriptorsByDescriptorClass(UnrecognisedModuleDescriptor.class))
        {
            final Element source = plugin.getModuleElements().get(unrecognised.getKey());
            if ((source != null) && factory.hasModuleDescriptor(source.getName()))
            {
                usedFactory = true;
                try
                {
                    final ModuleDescriptor<?> descriptor = factory.getModuleDescriptor(source.getName());
                    descriptor.init(unrecognised.getPlugin(), source);
                    plugin.addModuleDescriptor(descriptor);
                    if (log.isInfoEnabled())
                    {
                        log.info("Turned plugin module " + descriptor.getCompleteKey() + " into module " + descriptor);
                    }
                }
                catch (final Exception e)
                {
                    log.error("Unable to transform " + unrecognised.getCompleteKey() + " into actual plugin module using factory " + factory, e);
                    unrecognised.setErrorText(e.getMessage());
                }
            }
        }
        if (usedFactory)
        {
            return factory;
        }
        else
        {
            // The docs seem to indicate returning null is enough to untrack a service, but the source code and tests
            // show otherwise.
            bundle.getBundleContext().ungetService(serviceReference);
            return null;
        }
    }

    /**
     * Updates any local module descriptors that were created from the modified factory
     */
    public void modifiedService(final ServiceReference serviceReference, final Object o)
    {
        removedService(serviceReference, o);
        addingService(serviceReference);
    }

    /**
     * Reverts any current module descriptors that were provided from the factory being removed into {@link
     * UnrecognisedModuleDescriptor} instances.
     */
    public void removedService(final ServiceReference serviceReference, final Object o)
    {
        final ListableModuleDescriptorFactory factory = (ListableModuleDescriptorFactory) o;
        for (final Class<ModuleDescriptor<?>> moduleDescriptorClass : factory.getModuleDescriptorClasses())
        {
            for (final ModuleDescriptor<?> descriptor : getModuleDescriptorsByDescriptorClass(moduleDescriptorClass))
            {
                final UnrecognisedModuleDescriptor unrecognisedModuleDescriptor = new UnrecognisedModuleDescriptor();
                final Element source = plugin.getModuleElements().get(descriptor.getKey());
                if (source != null)
                {
                    unrecognisedModuleDescriptor.init(plugin, source);
                    unrecognisedModuleDescriptor.setErrorText(UnrecognisedModuleDescriptorFallbackFactory.DESCRIPTOR_TEXT);
                    plugin.addModuleDescriptor(unrecognisedModuleDescriptor);
                    if (log.isInfoEnabled())
                    {
                        log.info("Removed plugin module " + unrecognisedModuleDescriptor.getCompleteKey() + " as its factory was uninstalled");
                    }
                }
            }
        }
    }

    /**
     *
     * @param descriptor
     * @param <T>
     * @return
     */
    <T extends ModuleDescriptor<?>> List<T> getModuleDescriptorsByDescriptorClass(final Class<T> descriptor)
    {
        final List<T> result = new ArrayList<T>();

        for (final ModuleDescriptor<?> moduleDescriptor : plugin.getModuleDescriptors())
        {
            if (moduleDescriptor.getClass()
                    .isAssignableFrom(descriptor))
            {
                result.add(descriptor.cast(moduleDescriptor));
            }
        }
        return result;
    }
}