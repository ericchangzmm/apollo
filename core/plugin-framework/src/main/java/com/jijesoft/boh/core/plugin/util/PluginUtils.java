package com.jijesoft.boh.core.plugin.util;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.descriptors.RequiresRestart;

/**
 * General plugin utility methods
 *
 */
public class PluginUtils
{
    public static final String JIJESOFT_DEV_MODE = "jijesoft.dev.mode";

    /**
     * Determines if a plugin requires a restart after being installed at runtime.  Looks for the annotation
     * {@link RequiresRestart} on the plugin's module descriptors.
     *
     * @param plugin The plugin that was just installed at runtime, but not yet enabled
     * @return True if a restart is required
     */
    public static boolean doesPluginRequireRestart(final Plugin plugin)
    {
        //PLUG-451: When in dev mode, plugins will should not require a restart.
        if (Boolean.getBoolean(JIJESOFT_DEV_MODE))
        {
            return false;
        }

        for (final ModuleDescriptor<?> descriptor : plugin.getModuleDescriptors())
        {
            if (descriptor.getClass().getAnnotation(RequiresRestart.class) != null)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a module element applies to the current application by matching the 'application' attribute
     * to the set of keys.  If the application is specified, but isn't in the set, we return false
     * @param element The module element
     * @param keys The set of application keys
     * @return True if it should apply, false otherwise
     */
    public static boolean doesModuleElementApplyToApplication(Element element, Set<String> keys)
    {
        Validate.notNull(keys);
        Validate.notNull(element);
        String key = element.attributeValue("application");
        return !(key != null && !keys.contains(key));
    }
}
