package com.jijesoft.boh.core.plugin.osgi.factory.transform.stage;

import org.dom4j.Document;
import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformContext;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.model.ComponentImport;
import com.jijesoft.boh.core.plugin.util.PluginUtils;

/**
 * Transforms component imports into a Spring XML file
 *
 */
public class ComponentImportSpringStage implements TransformStage
{
    /** Path of generated Spring XML file */
    private static final String SPRING_XML = "META-INF/spring/jijesoft-plugins-component-imports.xml";

    public void execute(TransformContext context) throws PluginTransformationException
    {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML))
        {
            Document springDoc = SpringHelper.createSpringDocument();
            Element root = springDoc.getRootElement();

            for (ComponentImport comp: context.getComponentImports().values())
            {
                if (!PluginUtils.doesModuleElementApplyToApplication(comp.getSource(), context.getApplicationKeys()))
                {
                    continue;
                }
                Element osgiReference = root.addElement("osgi:reference");
                osgiReference.addAttribute("id", comp.getKey());

                if (comp.getFilter() != null)
                {
                    osgiReference.addAttribute("filter", comp.getFilter());
                }

                Element interfaces = osgiReference.addElement("osgi:interfaces");
                for (String infName : comp.getInterfaces())
                {
                    context.getExtraImports().add(infName.substring(0, infName.lastIndexOf('.')));
                    Element e = interfaces.addElement("beans:value");
                    e.setText(infName);
                }
            }
            if (root.elements().size() > 0)
            {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(springDoc));
            }
        }
    }
}