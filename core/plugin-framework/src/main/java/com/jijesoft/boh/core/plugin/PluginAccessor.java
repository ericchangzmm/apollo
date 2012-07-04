package com.jijesoft.boh.core.plugin;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.jijesoft.boh.core.plugin.predicate.ModuleDescriptorPredicate;
import com.jijesoft.boh.core.plugin.predicate.PluginPredicate;

/**
 * Allows access to the current plugin system state
 */
public interface PluginAccessor
{
    /**
     * The plugin descriptor file.
     * 
     */
    public static final class Descriptor
    {
        /**
         * The default filename.
         */
        public static final String FILENAME = "jijesoft-plugin.xml";

        private Descriptor()
        {}
    }

    /**
     * Gets all of the currently installed plugins.
     * @return a collection of installed {@link Plugin}s.
     */
    Collection<Plugin> getPlugins();

    /**
     * Gets all installed plugins that match the given predicate.
     * @param pluginPredicate the {@link PluginPredicate} to match.
     * @return a collection of {@link Plugin}s that match the given predicate.
     */
    Collection<Plugin> getPlugins(final PluginPredicate pluginPredicate);

    /**
     * Get all of the currently enabled plugins.
     * @return a collection of installed and enabled {@link Plugin}s.
     */
    Collection<Plugin> getEnabledPlugins();

    /**
     * Gets all installed modules that match the given predicate.
     * @param moduleDescriptorPredicate the {@link com.jijesoft.boh.core.plugin.predicate.ModuleDescriptorPredicate} to match.
     * @return a collection of modules as per {@link ModuleDescriptor#getModule()} that match the given predicate.
     */
    <M> Collection<M> getModules(final ModuleDescriptorPredicate<M> moduleDescriptorPredicate);

    /**
     * Gets all module descriptors of installed modules that match the given predicate.
     * @param moduleDescriptorPredicate the {@link com.jijesoft.boh.core.plugin.predicate.ModuleDescriptorPredicate} to match.
     * @return a collection of {@link ModuleDescriptor}s that match the given predicate.
     */
    <M> Collection<ModuleDescriptor<M>> getModuleDescriptors(final ModuleDescriptorPredicate<M> moduleDescriptorPredicate);

    /**
     * Retrieve a given plugin (whether enabled or not).
     * @return The enabled plugin, or null if that plugin does not exist.
     */
    Plugin getPlugin(String key);

    /**
     * Retrieve a given plugin if it is enabled.
     * @return The enabled plugin, or null if that plugin does not exist or is disabled.
     */
    Plugin getEnabledPlugin(String pluginKey);

    /**
     * Retrieve any plugin module by complete module key.
     * <p>
     * Note: the module may or may not be disabled.
     */
    ModuleDescriptor<?> getPluginModule(String completeKey);

    /**
     * Retrieve an enabled plugin module by complete module key.
     */
    ModuleDescriptor<?> getEnabledPluginModule(String completeKey);

    /**
     * Whether or not a given plugin is currently enabled.
     */
    boolean isPluginEnabled(String key);

    /**
     * Whether or not a given plugin module is currently enabled.  This also checks
     * if the plugin it is contained within is enabled also
     * @see #isPluginEnabled(String)
     */
    boolean isPluginModuleEnabled(String completeKey);

    /**
     * Retrieve all plugin modules that implement or extend a specific class.
     *
     * @return List of modules that implement or extend the given class.
     */
    <M> List<M> getEnabledModulesByClass(Class<M> moduleClass);


    /**
     * Get all enabled module descriptors that have a specific descriptor class.
     *
     * @param descriptorClazz module descriptor class
     * @return List of {@link ModuleDescriptor}s that implement or extend the given class.
     */
    <D extends ModuleDescriptor<?>> List<D> getEnabledModuleDescriptorsByClass(Class<D> descriptorClazz);


    /**
     * Retrieve a resource from a currently loaded (and active) dynamically loaded plugin. Will return the first resource
     * found, so plugins with overlapping resource names will behave eratically.
     *
     * @param resourcePath the path to the resource to retrieve
     * @return the dynamically loaded resource that matches that path, or null if no such resource is found
     */
    InputStream getDynamicResourceAsStream(String resourcePath);

   
    /**
     * Retrieve the class loader responsible for loading classes and resources from plugins.
     * @return the class loader
     */
    ClassLoader getClassLoader();

    /**
     * @return true if the plugin is a system plugin.
     */
    boolean isSystemPlugin(String key);

    /**
     * Gets the state of the plugin upon restart.  Only useful for plugins that contain module descriptors with the
     * \@RestartRequired annotation, and therefore, cannot be dynamically installed, upgraded, or removed at runtime
     *
     * @param key The plugin key
     * @return The state of the plugin on restart
     */
    PluginRestartState getPluginRestartState(String key);
}
