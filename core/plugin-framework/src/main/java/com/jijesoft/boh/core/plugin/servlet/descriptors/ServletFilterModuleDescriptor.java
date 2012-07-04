package com.jijesoft.boh.core.plugin.servlet.descriptors;

import java.util.Comparator;

import javax.servlet.Filter;

import org.apache.commons.lang.Validate;
import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.AutowireCapablePlugin;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.StateAware;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;
import com.jijesoft.boh.core.plugin.servlet.ServletModuleManager;
import com.jijesoft.boh.core.plugin.servlet.filter.FilterLocation;

/**
 * A module descriptor that allows plugin developers to define servlet filters.  Developers can define what urls the 
 * filter should be applied to by defining one or more &lt;url-pattern&gt; elements and they can decide where in the
 * filter stack a plugin filter should go by defining the "location" and "weight" attributes. 
 * <p/>
 * The location attribute can have one of four values:
 * </p>
 * <ul>
 * <li>after-encoding - after the character encoding filter</li>
 * <li>before-login - before the login filter</li>
 * <li>before-decoration - before any global decoration like sitemesh</li>
 * <li>before-dispatch - before any dispatching filters or servlets</li>
 * </ul>
 * <p>
 * The default for the location attribute is "before-dispatch".
 * <p/>
 * The weight attribute can have any integer value.  Filters with lower values of the weight attribute will come before
 * those with higher values within the same location.
 *
 */
public class ServletFilterModuleDescriptor extends BaseServletModuleDescriptor<Filter> implements StateAware
{
    static final String DEFAULT_LOCATION = FilterLocation.BEFORE_DISPATCH.name();
    static final String DEFAULT_WEIGHT = "100";
    
    private FilterLocation location;

    private int weight;
    private final ServletModuleManager servletModuleManager;
    private final HostContainer hostContainer;

    /**
     * Creates a descriptor that uses a module factory to create instances
     *
     * @param hostContainer The module factory
     */
    public ServletFilterModuleDescriptor(HostContainer hostContainer, ServletModuleManager servletModuleManager)
    {
        Validate.notNull(hostContainer);
        Validate.notNull(servletModuleManager);
        this.hostContainer = hostContainer;
        this.servletModuleManager = servletModuleManager;
    }

    public static final Comparator<ServletFilterModuleDescriptor> byWeight = new Comparator<ServletFilterModuleDescriptor>()
    {
        public int compare(ServletFilterModuleDescriptor lhs, ServletFilterModuleDescriptor rhs)
        {
            return Integer.valueOf(lhs.getWeight()).compareTo(rhs.getWeight());
        }
    };

    public void init(Plugin plugin, Element element) throws PluginParseException
    {
        super.init(plugin, element);
        try
        {
            location = FilterLocation.parse(element.attributeValue("location", DEFAULT_LOCATION));
            weight = Integer.valueOf(element.attributeValue("weight", DEFAULT_WEIGHT));
        }
        catch (IllegalArgumentException ex)
        {
            throw new PluginParseException(ex);
        }
    }
    
    public void enabled()
    {
        super.enabled();
        servletModuleManager.addFilterModule(this);
    }

    public void disabled()
    {
       servletModuleManager.removeFilterModule(this);
        super.disabled();
    }

    @Override
    public Filter getModule()
    {
        Filter filter = null;
        // Give the plugin a go first
        if (plugin instanceof AutowireCapablePlugin)
            filter = ((AutowireCapablePlugin)plugin).autowire(getModuleClass());
        else
        {
            filter = hostContainer.create(getModuleClass());
        }
        return filter;
    }

    public FilterLocation getLocation()
    {
        return location;
    }

    public int getWeight()
    {
        return weight;
    }

}
