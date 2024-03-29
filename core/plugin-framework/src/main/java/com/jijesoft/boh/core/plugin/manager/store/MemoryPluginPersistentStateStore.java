package com.jijesoft.boh.core.plugin.manager.store;

import com.jijesoft.boh.core.plugin.manager.DefaultPluginPersistentState;
import com.jijesoft.boh.core.plugin.manager.PluginPersistentState;
import com.jijesoft.boh.core.plugin.manager.PluginPersistentStateStore;

/**
 * A basic plugin state store that stores state in memory. Not recommended for production use.
 */
public class MemoryPluginPersistentStateStore implements PluginPersistentStateStore
{
    private volatile PluginPersistentState state = new DefaultPluginPersistentState();

    public void save(final PluginPersistentState state)
    {
        this.state = state;
    }

    public PluginPersistentState load()
    {
        return state;
    }
}
