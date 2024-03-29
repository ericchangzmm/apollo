package com.jijesoft.boh.core.plugin.parsers;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginInformation;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.Resources;
import com.jijesoft.boh.core.plugin.descriptors.UnloadableModuleDescriptor;
import com.jijesoft.boh.core.plugin.descriptors.UnloadableModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptor;
import com.jijesoft.boh.core.plugin.descriptors.UnrecognisedModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.impl.UnloadablePluginFactory;
import com.jijesoft.boh.core.plugin.util.PluginUtils;

/**
 * Provides access to the descriptor information retrieved from an XML InputStream.
 * <p/>
 * Uses the dom4j {@link SAXReader} to parse the XML stream into a document
 * when the parser is constructed.
 *
 * @see XmlDescriptorParserFactory
 */
public class XmlDescriptorParser implements DescriptorParser
{
    private static final Log log = LogFactory.getLog(XmlDescriptorParser.class);

    private final Document document;
    private final Set<String> applicationKeys;

    /**
     * Constructs a parser with an already-constructed document
     * @param source the source document
     * @param applicationKeys the application key to filter modules with, null for all unspecified
     * @throws PluginParseException if there is a problem reading the descriptor from the XML {@link InputStream}.
     */
    public XmlDescriptorParser(final Document source, final String... applicationKeys) throws PluginParseException
    {
        Validate.notNull(source, "XML descriptor source document cannot be null");
        document = source;
        if (applicationKeys == null)
        {
            this.applicationKeys = Collections.emptySet();
        }
        else
        {
            this.applicationKeys = new HashSet<String>(Arrays.asList(applicationKeys));
        }
    }

    /**
     * Constructs a parser with a stream of an XML document for a specific application
     * @param source The descriptor stream
     * @param applicationKeys the application key to filter modules with, null for all unspecified
     * @throws PluginParseException if there is a problem reading the descriptor from the XML {@link InputStream}.
     */
    public XmlDescriptorParser(final InputStream source, final String... applicationKeys) throws PluginParseException
    {
        Validate.notNull(source, "XML descriptor source cannot be null");
        document = createDocument(source);
        if (applicationKeys == null)
        {
            this.applicationKeys = Collections.emptySet();
        }
        else
        {
            this.applicationKeys = new HashSet<String>(Arrays.asList(applicationKeys));
        }
    }

    protected Document createDocument(final InputStream source) throws PluginParseException
    {
        final SAXReader reader = new SAXReader();
        try
        {
            return reader.read(source);
        }
        catch (final DocumentException e)
        {
            throw new PluginParseException("Cannot parse XML plugin descriptor", e);
        }
    }

    protected Document getDocument()
    {
        return document;
    }

    public Plugin configurePlugin(final ModuleDescriptorFactory moduleDescriptorFactory, final Plugin plugin) throws PluginParseException
    {
        final Element pluginElement = getPluginElement();
        plugin.setName(pluginElement.attributeValue("name"));
        plugin.setKey(getKey());
        plugin.setPluginsVersion(getPluginsVersion());
        plugin.setSystemPlugin(isSystemPlugin());

        if (pluginElement.attributeValue("i18n-name-key") != null)
        {
            plugin.setI18nNameKey(pluginElement.attributeValue("i18n-name-key"));
        }

        if (plugin.getKey().indexOf(":") > 0)
        {
            throw new PluginParseException("Plugin keys cannot contain ':'. Key is '" + plugin.getKey() + "'");
        }

        if ("disabled".equalsIgnoreCase(pluginElement.attributeValue("state")))
        {
            plugin.setEnabledByDefault(false);
        }

        for (final Iterator i = pluginElement.elementIterator(); i.hasNext();)
        {
            final Element element = (Element) i.next();

            if ("plugin-info".equalsIgnoreCase(element.getName()))
            {
                plugin.setPluginInformation(createPluginInformation(element));
            }
            else if (!"resource".equalsIgnoreCase(element.getName()))
            {
                final ModuleDescriptor<?> moduleDescriptor = createModuleDescriptor(plugin, element, moduleDescriptorFactory);

                // If we're not loading the module descriptor, null is returned, so we skip it
                if (moduleDescriptor == null)
                {
                    continue;
                }

                if (plugin.getModuleDescriptor(moduleDescriptor.getKey()) != null)
                {
                    throw new PluginParseException("Found duplicate key '" + moduleDescriptor.getKey() + "' within plugin '" + plugin.getKey() + "'");
                }

                plugin.addModuleDescriptor(moduleDescriptor);

                // If we have any unloadable modules, also create an unloadable plugin, which will make it clear that there was a problem
                if (moduleDescriptor instanceof UnloadableModuleDescriptor)
                {
                    log.error("There were errors loading the plugin '" + plugin.getName() + "'. The plugin has been disabled.");
                    return UnloadablePluginFactory.createUnloadablePlugin(plugin);
                }
            }
        }

        plugin.setResources(Resources.fromXml(pluginElement));

        return plugin;
    }

    private Element getPluginElement()
    {
        return document.getRootElement();
    }

    protected ModuleDescriptor<?> createModuleDescriptor(final Plugin plugin, final Element element, final ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException
    {
        final String name = element.getName();

        // Determine if this module descriptor is applicable for the current application
        if (!PluginUtils.doesModuleElementApplyToApplication(element, applicationKeys))
        {
            log.debug("Ignoring module descriptor for this application: " + element.attributeValue("key"));
            return null;
        }

        ModuleDescriptor<?> moduleDescriptorDescriptor;

        // Try to retrieve the module descriptor
        try
        {
            moduleDescriptorDescriptor = moduleDescriptorFactory.getModuleDescriptor(name);
        }
        // When there's a problem loading a module, return an UnrecognisedModuleDescriptor with error
        catch (final Throwable e)
        {
            final UnrecognisedModuleDescriptor descriptor = UnrecognisedModuleDescriptorFactory.createUnrecognisedModuleDescriptor(plugin, element,
                e, moduleDescriptorFactory);

            log.error("There were problems loading the module '" + name + "' in plugin '" + plugin.getName() + "'. The module has been disabled.");
            log.error(descriptor.getErrorText(), e);

            return descriptor;
        }

        // When the module descriptor has been excluded, null is returned (PLUG-5)
        if (moduleDescriptorDescriptor == null)
        {
            log.info("The module '" + name + "' in plugin '" + plugin.getName() + "' is in the list of excluded module descriptors, so not enabling.");
            return null;
        }

        // Once we have the module descriptor, create it using the given information
        try
        {
            moduleDescriptorDescriptor.init(plugin, element);
        }
        // If it fails, return a dummy module that contains the error
        catch (final Exception e)
        {
            final UnloadableModuleDescriptor descriptor = UnloadableModuleDescriptorFactory.createUnloadableModuleDescriptor(plugin, element, e,
                moduleDescriptorFactory);

            log.error("There were problems loading the module '" + name + "'. The module and its plugin have been disabled.");
            log.error(descriptor.getErrorText(), e);

            return descriptor;
        }

        return moduleDescriptorDescriptor;
    }

    protected PluginInformation createPluginInformation(final Element element)
    {
        final PluginInformation pluginInfo = new PluginInformation();

        if (element.element("description") != null)
        {
            pluginInfo.setDescription(element.element("description").getTextTrim());
            if (element.element("description").attributeValue("key") != null)
            {
                pluginInfo.setDescriptionKey(element.element("description").attributeValue("key"));
            }
        }

        if (element.element("version") != null)
        {
            pluginInfo.setVersion(element.element("version").getTextTrim());
        }

        if (element.element("vendor") != null)
        {
            final Element vendor = element.element("vendor");
            pluginInfo.setVendorName(vendor.attributeValue("name"));
            pluginInfo.setVendorUrl(vendor.attributeValue("url"));
        }

        // initialize any parameters on the plugin xml definition
        for (final Iterator<Element> iterator = element.elements("param").iterator(); iterator.hasNext();)
        {
            final Element param = iterator.next();

            // Retrieve the parameter info => name & text
            if (param.attribute("name") != null)
            {
                pluginInfo.addParameter(param.attribute("name").getData().toString(), param.getText());
            }
        }

        if (element.element("application-version") != null)
        {
            final Element ver = element.element("application-version");
            if (ver.attribute("max") != null)
            {
                pluginInfo.setMaxVersion(Float.parseFloat(ver.attributeValue("max")));
            }
            if (ver.attribute("min") != null)
            {
                pluginInfo.setMinVersion(Float.parseFloat(ver.attributeValue("min")));
            }
        }

        if (element.element("java-version") != null)
        {
            pluginInfo.setMinJavaVersion(Float.valueOf(element.element("java-version").attributeValue("min")));
        }

        return pluginInfo;
    }

    public String getKey()
    {
        return getPluginElement().attributeValue("key");
    }

    public int getPluginsVersion()
    {
        String val = getPluginElement().attributeValue("pluginsVersion");
        if (val == null)
        {
            val = getPluginElement().attributeValue("plugins-version");
        }
        if (val != null)
        {
            try
            {
                return Integer.parseInt(val);
            }
            catch (final NumberFormatException e)
            {
                throw new RuntimeException("Could not parse pluginsVersion: " + e.getMessage(), e);
            }
        }
        else
        {
            return 1;
        }
    }

    public PluginInformation getPluginInformation()
    {
        return createPluginInformation(getDocument().getRootElement().element("plugin-info"));
    }

    public boolean isSystemPlugin()
    {
        return "true".equalsIgnoreCase(getPluginElement().attributeValue("system"));
    }
}
