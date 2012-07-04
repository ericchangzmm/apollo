package com.jijesoft.boh.core.plugin.manager;

import static com.jijesoft.boh.core.plugin.manager.PluginPersistentState.Util.buildStateKey;
import static java.util.Collections.unmodifiableMap;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jijesoft.boh.core.plugin.ModuleDescriptor;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginRestartState;

/**
 * Immutable implementation of the {@link PluginPersistentState} interface.
 * <p>
 * The state stored in this object represents only the <i>differences</i> between the desired state
 * and the default state configured in the plugin. So if "getPluginState()" or "getPluginModuleState()" return
 * null, then the manager should assume that the default state applies instead.
 */
public final class DefaultPluginPersistentState implements Serializable, PluginPersistentState
{
    private final Map<String, Boolean> map;

    /**
     * Creates an empty {@link PluginPersistentState}.
     * @deprecated create {@link PluginPersistentState} instances using the 
     * {@link PluginPersistentState.Builder}
     */
    @Deprecated
    public DefaultPluginPersistentState()
    {
        map = Collections.emptyMap();
    }

    /* for use from within this package, the second parameter is ignored */
    DefaultPluginPersistentState(final Map<String, Boolean> map, final boolean ignore)
    {
        this.map = unmodifiableMap(new HashMap<String, Boolean>(map));
    }

    /* (non-Javadoc)
     * @see com.jijesoft.boh.core.plugin.PluginPersistentState#getMap()
     */
    public Map<String, Boolean> getMap()
    {
        return Collections.unmodifiableMap(map);
    }

    /* (non-Javadoc)
     * @see com.jijesoft.boh.core.plugin.PluginPersistentState#isEnabled(com.jijesoft.boh.core.plugin.Plugin)
     */
    public boolean isEnabled(final Plugin plugin)
    {
        final Boolean bool = map.get(plugin.getKey());
        return (bool == null) ? plugin.isEnabledByDefault() : bool.booleanValue();
    }

    /* (non-Javadoc)
     * @see com.jijesoft.boh.core.plugin.PluginPersistentState#isEnabled(com.jijesoft.boh.core.plugin.ModuleDescriptor)
     */
    public boolean isEnabled(final ModuleDescriptor<?> pluginModule)
    {
        if (pluginModule == null)
        {
            return false;
        }

        final Boolean bool = map.get(pluginModule.getCompleteKey());
        return (bool == null) ? pluginModule.isEnabledByDefault() : bool.booleanValue();
    }

    /* (non-Javadoc)
     * @see com.jijesoft.boh.core.plugin.PluginPersistentState#getPluginStateMap(com.jijesoft.boh.core.plugin.Plugin)
     */
    public Map<String, Boolean> getPluginStateMap(final Plugin plugin)
    {
        final Map<String, Boolean> state = new HashMap<String, Boolean>(getMap());
        CollectionUtils.filter(state.keySet(), new StringStartsWith(plugin.getKey()));
        return state;
    }

    public PluginRestartState getPluginRestartState(final String pluginKey)
    {
        for (final PluginRestartState state : PluginRestartState.values())
        {
            if (map.containsKey(buildStateKey(pluginKey, state)))
            {
                return state;
            }
        }
        return PluginRestartState.NONE;
    }

    private static class StringStartsWith implements Predicate
    {
        private final String prefix;

        public StringStartsWith(final String keyPrefix)
        {
            prefix = keyPrefix;
        }

        public boolean evaluate(final Object object)
        {
            final String str = (String) object;
            return str.startsWith(prefix);
        }
    }
}
