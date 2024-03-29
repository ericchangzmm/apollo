package com.jijesoft.boh.core.plugin.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginAccessor;
import com.jijesoft.boh.core.plugin.PluginController;
import com.jijesoft.boh.core.plugin.PluginState;
import com.jijesoft.boh.core.plugin.util.PluginUtils;
import com.jijesoft.boh.core.plugin.util.WaitUntil;

/**
 * Helper class that handles the problem of enabling a set of plugins at once.  This functionality is used for both
 * the initial plugin loading and manual plugin enabling.  The system waits 60 seconds for all dependencies to be
 * resolved, then resets the timer to 5 seconds if only one remains.
 *
 */
class PluginEnabler
{
    private static final Log log = LogFactory.getLog(PluginEnabler.class);
    private static final long LAST_PLUGIN_TIMEOUT = 5 * 1000;

    private final PluginAccessor pluginAccessor;
    private final PluginController pluginController;

    public PluginEnabler(PluginAccessor pluginAccessor, PluginController pluginController)
    {
        this.pluginAccessor = pluginAccessor;
        this.pluginController = pluginController;
    }

    /**
     * Determines, recursively, which disabled plugins this plugin depends upon, and enables all of them at once.
     *
     * @param plugin The requested plugin to enable
     */
    void enableRecursively(Plugin plugin)
    {
        Set<String> dependentKeys = new HashSet<String>();
        scanDependencies(plugin, dependentKeys);

        List<Plugin> pluginsToEnable = new ArrayList<Plugin>();
        for (String key : dependentKeys)
        {
            pluginsToEnable.add(pluginAccessor.getPlugin(key));
        }
        enable(pluginsToEnable);
    }

    /**
     * Enables a collection of plugins at once, waiting for 60 seconds.  If any plugins are still in the enabling state,
     * the plugins are explicitly disabled.
     *
     * @param plugins The plugins to enable
     */
    void enable(Collection<Plugin> plugins)
    {

        final Set<Plugin> pluginsInEnablingState = new HashSet<Plugin>();
        for (final Plugin plugin : plugins)
        {
            try
            {
                plugin.enable();
                if (plugin.getPluginState() == PluginState.ENABLING)
                {
                    pluginsInEnablingState.add(plugin);
                }
            }
            catch (final RuntimeException ex)
            {
                log.error("Unable to enable plugin " + plugin.getKey(), ex);
            }
        }

        if (!pluginsInEnablingState.isEmpty())
        {
            // Now try to enable plugins that weren't enabled before, probably due to dependency ordering issues
            WaitUntil.invoke(new WaitUntil.WaitCondition()
            {
                private long singlePluginTimeout;
                public boolean isFinished()
                {
                    if (singlePluginTimeout > 0 && singlePluginTimeout < System.currentTimeMillis())
                    {
                        return true;
                    }
                    for (final Iterator<Plugin> i = pluginsInEnablingState.iterator(); i.hasNext();)
                    {
                        final Plugin plugin = i.next();
                        if (plugin.getPluginState() != PluginState.ENABLING)
                        {
                            i.remove();
                        }
                    }
                    if (isDevMode() && pluginsInEnablingState.size() == 1 && singlePluginTimeout == 0)
                    {
                        log.info("Only one plugin left not enabled. Resetting the timeout to " +
                                (LAST_PLUGIN_TIMEOUT/1000) + " seconds.");
                        singlePluginTimeout = System.currentTimeMillis() + LAST_PLUGIN_TIMEOUT;
                    }
                    return pluginsInEnablingState.isEmpty();
                }

                public String getWaitMessage()
                {
                    return "Plugins that have yet to be enabled: " + pluginsInEnablingState;
                }

                private boolean isDevMode()
                {
                    return Boolean.getBoolean(PluginUtils.JIJESOFT_DEV_MODE);
                }
            });

            // Disable any plugins that aren't enabled by now
            if (!pluginsInEnablingState.isEmpty())
            {
                final StringBuilder sb = new StringBuilder();
                for (final Plugin plugin : pluginsInEnablingState)
                {
                    sb.append(plugin.getKey()).append(',');
                    pluginController.disablePluginWithoutPersisting(plugin.getKey());
                }
                sb.deleteCharAt(sb.length() - 1);
                log.error("Unable to start the following plugins due to timeout while waiting for plugin to enable: " + sb.toString());
            }
        }
    }

    /**
     * Scans, recursively, to build a set of plugin dependencies for the target plugin
     *
     * @param plugin The plugin to scan
     * @param dependentKeys The set of keys collected so far
     */
    private void scanDependencies(Plugin plugin, Set<String> dependentKeys)
    {
        dependentKeys.add(plugin.getKey());

        // Ensure dependent plugins are enabled first
        for (String dependencyKey : plugin.getRequiredPlugins())
        {
            if (!dependentKeys.contains(dependencyKey) &&
                    (pluginAccessor.getPlugin(dependencyKey) != null) &&
                    !pluginAccessor.isPluginEnabled(dependencyKey))
            {
                scanDependencies(pluginAccessor.getPlugin(dependencyKey), dependentKeys);
            }
        }
    }
}
