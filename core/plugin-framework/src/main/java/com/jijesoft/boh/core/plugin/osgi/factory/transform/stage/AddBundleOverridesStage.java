package com.jijesoft.boh.core.plugin.osgi.factory.transform.stage;

import java.util.List;

import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformContext;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformStage;

/**
 * Adds bundle instruction overrides from the plugin descriptor to be later used in the manifest generation process.
 *
 */
public class AddBundleOverridesStage implements TransformStage
{
    public void execute(TransformContext context) throws PluginTransformationException
    {
        Element pluginInfo = context.getDescriptorDocument().getRootElement().element("plugin-info");
        if (pluginInfo != null)
        {
            Element instructionRoot = pluginInfo.element("bundle-instructions");
            if (instructionRoot != null)
            {
                List<Element> instructionsElement = instructionRoot.elements();
                for (Element instructionElement : instructionsElement)
                {
                    String name = instructionElement.getName();
                    String value = instructionElement.getTextTrim();
                    context.getBndInstructions().put(name, value);
                }
            }
        }
    }
}
