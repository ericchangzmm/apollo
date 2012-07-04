package com.jijesoft.boh.core.plugin.osgi.factory.transform;

import java.io.File;
import java.util.List;

import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentRegistration;

/**
 * Transforms a plugin jar into a proper OSGi bundle
 */
public interface PluginTransformer
{
    /**
     * Transforms a plugin artifact into a proper OSGi bundle
     *
     * @param pluginArtifact The plugin artifact
     * @param regs The list of registered host components
     * @return The transformed OSGi bundle
     * @throws PluginTransformationException If anything goes wrong
     */
    File transform(PluginArtifact pluginArtifact, List<HostComponentRegistration> regs) throws PluginTransformationException;
}
