package com.jijesoft.boh.core.plugin.factories;

import com.jijesoft.boh.core.plugin.ModuleDescriptorFactory;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.loaders.classloading.DeploymentUnit;



/**
 * Creates the plugin artifact and deploys it into the appropriate plugin management system
 */
public interface PluginFactory
{
    /**
     * Determines if this factory can handle this artifact.
     *
     * @param pluginArtifact The artifact to test
     * @return The plugin key, null if it cannot load the plugin
     * @throws com.jijesoft.boh.core.plugin.PluginParseException If there are exceptions parsing the plugin configuration when
     * the deployer should have been able to deploy the plugin
     */
    String canCreate(PluginArtifact pluginArtifact) throws PluginParseException;

    /**
     * Deploys the plugin artifact by instantiating the plugin and configuring it.  Should only be called if the respective
     * {@link #canCreate(PluginArtifact)} call returned the plugin key
     *
     * @param pluginArtifact the plugin artifact to deploy
     * @param moduleDescriptorFactory the factory for the module descriptors
     * @return the plugin loaded from the plugin artifact, or an UnloadablePlugin instance if loading fails.
     * @throws com.jijesoft.boh.core.plugin.PluginParseException if the plugin could not be parsed
     */
    Plugin create(PluginArtifact pluginArtifact, ModuleDescriptorFactory moduleDescriptorFactory) throws PluginParseException;
}
