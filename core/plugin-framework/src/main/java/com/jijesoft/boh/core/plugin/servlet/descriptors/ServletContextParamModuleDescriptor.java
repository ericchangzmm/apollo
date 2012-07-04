package com.jijesoft.boh.core.plugin.servlet.descriptors;

import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;
import com.jijesoft.boh.core.plugin.util.validation.ValidationPattern;

import static com.jijesoft.boh.core.plugin.util.validation.ValidationPattern.createPattern;
import static com.jijesoft.boh.core.plugin.util.validation.ValidationPattern.test;

/**
 * Allows plugin developers to specify init parameters they would like added to the plugin local {@link javax.servlet.ServletContext}.
 *
 */
public class ServletContextParamModuleDescriptor extends AbstractModuleDescriptor<Void>
{
    private String paramName;
    private String paramValue;
    
    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException
    {
        super.init(plugin, element);
        
        paramName = element.elementTextTrim("param-name");
        paramValue = element.elementTextTrim("param-value");
    }

    @Override
    protected void provideValidationRules(ValidationPattern pattern)
    {
        super.provideValidationRules(pattern);
        pattern.
                rule(
                    test("param-name").withError("Parameter name is required"),
                    test("param-value").withError("Parameter value is required"));
    }

    public String getParamName()
    {
        return paramName;
    }

    public String getParamValue()
    {
        return paramValue;
    }

    @Override
    public Void getModule()
    {
        return null;
    }
}
