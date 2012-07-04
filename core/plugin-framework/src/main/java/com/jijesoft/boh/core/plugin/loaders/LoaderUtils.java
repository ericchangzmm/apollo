package com.jijesoft.boh.core.plugin.loaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.Resources;
import com.jijesoft.boh.core.plugin.elements.ResourceDescriptor;

public class LoaderUtils
{
    /**
     * @deprecated use {@link com.jijesoft.boh.core.plugin.Resources#fromXml}
     */
    public static List<ResourceDescriptor> getResourceDescriptors(Element element) throws PluginParseException
    {
        return Resources.fromXml(element).getResourceDescriptors();
    }

    public static Map<String,String> getParams(Element element)
    {
        List<Element> elements = element.elements("param");

        Map<String,String> params = new HashMap<String,String>(elements.size());

        for (Element paramEl : elements)
        {
            String name = paramEl.attributeValue("name");
            String value = paramEl.attributeValue("value");

            if (value == null && paramEl.getTextTrim() != null && !"".equals(paramEl.getTextTrim()))
            {
                value = paramEl.getTextTrim();
            }

            params.put(name, value);
        }

        return params;
    }
}
