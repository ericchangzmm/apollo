package com.jijesoft.boh.core.plugin.osgi.factory.transform.stage;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.XPath;

import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformContext;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformStage;

/**
 * Scans the plugin descriptor for any "class" attribute, and ensures that it will be imported, if appropriate.
 *
 */
public class ScanDescriptorForHostClassesStage implements TransformStage
{
    private static final Log log = LogFactory.getLog(ScanDescriptorForHostClassesStage.class);

    public void execute(TransformContext context) throws PluginTransformationException
    {
        XPath xpath = DocumentHelper.createXPath("//@class");
        List<Attribute> attributes = xpath.selectNodes(context.getDescriptorDocument());
        for (Attribute attr : attributes)
        {
            String className = attr.getValue();
            int dotpos = className.lastIndexOf(".");
            if (dotpos > -1)
            {
                String pkg = className.substring(0, dotpos);
                String pkgPath = pkg.replace('.', '/') + '/';

                // Only add an import if the system exports it and the plugin isn't using the package
                if (context.getSystemExports().isExported(pkg))
                {
                    if (context.getPluginArtifact().doesResourceExist(pkgPath))
                    {
                        log.warn("The plugin '" + context.getPluginArtifact().toString() + "' uses a package '" +
                                pkg + "' that is also exported by the application.  It is highly recommended that the " +
                                "plugin use its own packages.");
                    }
                    else
                    {
                        context.getExtraImports().add(pkg);
                    }
                }
            }
        }
    }
}
