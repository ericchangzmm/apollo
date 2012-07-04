package com.jijesoft.boh.core.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jijesoft.boh.core.etljob.descriptor.EtlJobModuleDescriptor;
import com.jijesoft.boh.core.frontend.descriptor.FrontendModuleDescriptor;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.AnnotationDrivenDescriptor;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.ComponentImportModuleDescriptor;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.ComponentModuleDescriptor;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.ComponentScanDescriptor;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.ModuleTypeModuleDescriptor;
import com.jijesoft.boh.core.plugin.osgi.factory.descriptor.PropertiesLoaderDescriptor;
import com.jijesoft.boh.core.plugin.servlet.descriptors.ServletContextListenerModuleDescriptor;
import com.jijesoft.boh.core.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.jijesoft.boh.core.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.jijesoft.boh.core.plugin.util.ClassLoaderUtils;
import com.jijesoft.boh.core.plugin.util.concurrent.CopyOnWriteMap;
import com.jijesoft.boh.core.report.descriptor.ReportModuleDescriptor;
import com.jijesoft.boh.core.workflow.descriptor.WorkflowModuleDescriptor;

/**
 * Default implementation of a descriptor factory that allows filtering of descriptor keys
 */
public class DefaultModuleDescriptorFactory implements ModuleDescriptorFactory
{
    private static Log log = LogFactory.getLog(DefaultModuleDescriptorFactory.class);

    private final Map<String, Class<? extends ModuleDescriptor>> moduleDescriptorClasses = CopyOnWriteMap.newHashMap();
    private final List<String> permittedModuleKeys = new ArrayList<String>();
    private final HostContainer hostContainer;

    /**
     * Instantiates a descriptor factory that uses the host container to create descriptors
     *
     * @param hostContainer The host container implementation for descriptor creation
     */
    public DefaultModuleDescriptorFactory(final HostContainer hostContainer)
    {
        this.hostContainer = hostContainer;
        addModuleDescriptor("listener", ServletContextListenerModuleDescriptor.class);
        addModuleDescriptor("properties-loader", PropertiesLoaderDescriptor.class);
        addModuleDescriptor("annotation-driven", AnnotationDrivenDescriptor.class);
        addModuleDescriptor("component-scan", ComponentScanDescriptor.class);
        addModuleDescriptor("frontend-module", FrontendModuleDescriptor.class);
        addModuleDescriptor("workflow", WorkflowModuleDescriptor.class);
        addModuleDescriptor("job", EtlJobModuleDescriptor.class);
        addModuleDescriptor("report", ReportModuleDescriptor.class);
        addModuleDescriptor("servlet", ServletModuleDescriptor.class);
		addModuleDescriptor("component", ComponentModuleDescriptor.class);
		addModuleDescriptor("component-import",	ComponentImportModuleDescriptor.class);
		addModuleDescriptor("module-type", ModuleTypeModuleDescriptor.class);
		addModuleDescriptor("servlet-filter", ServletFilterModuleDescriptor.class);
    }

    public Class<? extends ModuleDescriptor> getModuleDescriptorClass(final String type)
    {
        return moduleDescriptorClasses.get(type);
    }

    public ModuleDescriptor<?> getModuleDescriptor(final String type) throws PluginParseException, IllegalAccessException, InstantiationException, ClassNotFoundException
    {
        if (shouldSkipModuleOfType(type))
        {
            return null;
        }

        final Class<? extends ModuleDescriptor> moduleDescriptorClazz = getModuleDescriptorClass(type);

        if (moduleDescriptorClazz == null)
        {
            throw new PluginParseException("Cannot find ModuleDescriptor class for plugin of type '" + type + "'.");
        }

        return hostContainer.create(moduleDescriptorClazz);
    }

    protected boolean shouldSkipModuleOfType(final String type)
    {
        synchronized (permittedModuleKeys)
        {
            return (permittedModuleKeys != null) && !permittedModuleKeys.isEmpty() && !permittedModuleKeys.contains(type);
        }
    }

    public void setModuleDescriptors(final Map<String, String> moduleDescriptorClassNames)
    {
        for (final Entry<String, String> entry : moduleDescriptorClassNames.entrySet())
        {
            final Class<? extends ModuleDescriptor<?>> descriptorClass = getClassFromEntry(entry);
            if (descriptorClass != null)
            {
                addModuleDescriptor(entry.getKey(), descriptorClass);
            }
        }
    }

    private <D extends ModuleDescriptor<?>> Class<D> getClassFromEntry(final Map.Entry<String, String> entry)
    {
        if (shouldSkipModuleOfType(entry.getKey()))
        {
            return null;
        }

        try
        {
            final Class<D> descriptorClass = ClassLoaderUtils.<D> loadClass(entry.getValue(), getClass());

            if (!ModuleDescriptor.class.isAssignableFrom(descriptorClass))
            {
                log.error("Configured plugin module descriptor class " + entry.getValue() + " does not inherit from ModuleDescriptor");
                return null;
            }
            return descriptorClass;
        }
        catch (final ClassNotFoundException e)
        {
            log.error("Unable to add configured plugin module descriptor " + entry.getKey() + ". Class not found: " + entry.getValue());
            return null;
        }
    }

    public boolean hasModuleDescriptor(final String type)
    {
        return moduleDescriptorClasses.containsKey(type);
    }

    public void addModuleDescriptor(final String type, final Class<? extends ModuleDescriptor> moduleDescriptorClass)
    {
        moduleDescriptorClasses.put(type, moduleDescriptorClass);
    }

    public void removeModuleDescriptorForType(final String type)
    {
        moduleDescriptorClasses.remove(type);
    }

    protected Map<String, Class<? extends ModuleDescriptor>> getDescriptorClassesMap()
    {
        return Collections.unmodifiableMap(moduleDescriptorClasses);
    }

    /**
     * Sets the list of module keys that will be loaded. If this list is empty, then the factory will
     * permit all recognised module types to load. This allows you to run the plugin system in a 'restricted mode'
     *
     * @param permittedModuleKeys List of (String) keys
     */
    public void setPermittedModuleKeys(List<String> permittedModuleKeys)
    {
        if (permittedModuleKeys == null)
        {
            permittedModuleKeys = Collections.emptyList();
        }

        synchronized (this.permittedModuleKeys)
        {
            // synced
            this.permittedModuleKeys.clear();
            this.permittedModuleKeys.addAll(permittedModuleKeys);
        }
    }
}
