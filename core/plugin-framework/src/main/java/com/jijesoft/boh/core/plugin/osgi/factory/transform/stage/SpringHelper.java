package com.jijesoft.boh.core.plugin.osgi.factory.transform.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformContext;

/**
 * Helper class for dealing with spring files
 *
 */
class SpringHelper
{

    private static final Logger log = Logger.getLogger(SpringHelper.class);

    /**
     * Creates a basic spring document with the usual namespaces
     *
     * @return An empty spring XML configuration file with namespaces
     */
    static Document createSpringDocument()
    {
        final Document springDoc = DocumentHelper.createDocument();
        final Element root = springDoc.addElement("beans");

        root.addNamespace("beans", "http://www.springframework.org/schema/beans");
        root.addNamespace("osgi", "http://www.springframework.org/schema/osgi");
        root.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.addNamespace("context", "http://www.springframework.org/schema/context");
        root.addNamespace("tx", "http://www.springframework.org/schema/tx");
        root.addAttribute(new QName("schemaLocation", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")),
                "http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd\n" +
                        "http://www.springframework.org/schema/osgi http://www.springframework.org/schema/osgi/spring-osgi.xsd\n"+
                		"http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.1.xsd\n"+
                        "http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.1.xsd");
        root.setName("beans:beans");
        root.addAttribute("osgi:default-timeout", "30000");
        return springDoc;
    }

    /**
     * Converts an XML document into a byte array
     * @param doc The document
     * @return A byte array of the contents
     */
    static byte[] documentToBytes(final Document doc)
    {

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final OutputFormat format = OutputFormat.createPrettyPrint();

        try
        {
            final XMLWriter writer = new XMLWriter(bout, format);
            writer.write(doc);
        }
        catch (final IOException e)
        {
            throw new PluginTransformationException("Unable to print generated Spring XML", e);
        }

        return bout.toByteArray();
    }

    /**
     * Determines if the file should be generated, based on whether it already exists in the context or not
     *
     * @param context The transformation context
     * @param path The path of the file
     * @return True if not present, false otherwise
     */
    static boolean shouldGenerateFile(final TransformContext context, final String path)
    {
        if (context.getPluginJarEntry(path) == null)
        {
            log.debug("File "+path+" not present, generating");
            return true;
        }
        else
        {
            log.debug("File "+path+" already exists in jar, skipping generation");
            return false;
        }
    }

    static boolean isSpringUsed(TransformContext context)
    {
        // Check to see if we've needed it so far
        if (context.shouldRequireSpring())
        {
            return true;
        }

        // Check for the explicit context value
        final String header = context.getManifest().getMainAttributes().getValue("Spring-Context");
        if (header != null)
        {
            return true;
        }

        // Check for the spring files, as the default header value looks here
        if (context.getPluginArtifact().doesResourceExist("META-INF/spring/"))
        {
            return true;
        }

        return false;
    }
}
