package com.jijesoft.boh.core.plugin.osgi.factory.transform.stage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.osgi.factory.transform.PluginTransformationException;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformContext;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.TransformStage;
import com.jijesoft.boh.core.plugin.util.PluginUtils;
import com.jijesoft.boh.core.plugin.util.validation.ValidationPattern;

import static com.jijesoft.boh.core.plugin.util.validation.ValidationPattern.createPattern;
import static com.jijesoft.boh.core.plugin.util.validation.ValidationPattern.test;

/**
 * Transforms component tags in the plugin descriptor into the appropriate spring XML configuration file
 *
 */
public class ComponentSpringStage implements TransformStage
{
    /** Path of generated Spring XML file */
    private static final String SPRING_XML = "META-INF/spring/jijesoft-plugins-components.xml";
    
    public void execute(TransformContext context) throws PluginTransformationException
    {
        if (SpringHelper.shouldGenerateFile(context, SPRING_XML))
        {
            Document springDoc = SpringHelper.createSpringDocument();
            Element root = springDoc.getRootElement();
            
            createComponentScan(context,root);
            createPropertiesLoader(context,root);
            List<Element> elements = context.getDescriptorDocument().getRootElement().elements("component");

            ValidationPattern validation = createPattern().
                    rule(
                        test("@key").withError("The key is required"),
                        test("@class").withError("The class is required"),
                        test("not(@public) or interface or @interface").withError("Interfaces must be declared for public components"),
                        test("not(service-properties) or count(service-properties/entry[@key and @value]) > 0")
                                .withError("The service-properties element must contain at least one entry element with key and value attributes"));

            for (Element component : elements)
            {
                if (!PluginUtils.doesModuleElementApplyToApplication(component, context.getApplicationKeys()))
                {
                    continue;
                }
                validation.evaluate(component);

                Element bean = root.addElement("beans:bean");
                bean.addAttribute("id", component.attributeValue("key"));
                bean.addAttribute("alias", component.attributeValue("alias"));
                bean.addAttribute("class", component.attributeValue("class"));
                
            	Element comprops = component.element("compent-properties");
                if (comprops != null)
                {
                    for (Element prop : new ArrayList<Element>(comprops.elements("property")))
                    {
                        Element e = bean.addElement("beans:property");
                        e.addAttribute("name", prop.attributeValue("key"));
                        if(StringUtils.isEmpty(prop.attributeValue("value"))){
                        	 e.addAttribute("ref", prop.attributeValue("ref"));	
                        }else{
                        	 e.addAttribute("value", prop.attributeValue("value"));	
                        }
                       
                    }
                }
                if ("true".equalsIgnoreCase(component.attributeValue("public")))
                {
                    Element osgiService = root.addElement("osgi:service");
                    osgiService.addAttribute("id", component.attributeValue("key") + "_osgiService");
                    osgiService.addAttribute("ref", component.attributeValue("key"));

                    List<String> interfaceNames = new ArrayList<String>();
                    List<Element> compInterfaces = component.elements("interface");
                    for (Element inf : compInterfaces)
                    {
                        interfaceNames.add(inf.getTextTrim());
                    }
                    if (component.attributeValue("interface") != null)
                    {
                        interfaceNames.add(component.attributeValue("interface"));
                    }

                    Element interfaces = osgiService.addElement("osgi:interfaces");
                    for (String name : interfaceNames)
                    {
                        ensureExported(name, context);
                        Element e = interfaces.addElement("beans:value");
                        e.setText(name);
                    }

                    Element svcprops = component.element("service-properties");
                    if (svcprops != null)
                    {
                        Element targetSvcprops = osgiService.addElement("osgi:service-properties");
                        for (Element prop : new ArrayList<Element>(svcprops.elements("entry")))
                        {
                            Element e = targetSvcprops.addElement("beans:entry");
                            e.addAttribute("key", prop.attributeValue("key"));
                            if(StringUtils.isEmpty(prop.attributeValue("value"))){
                            	 e.addAttribute("value-ref", prop.attributeValue("value-ref"));	
                            }else{
                            	 e.addAttribute("value", prop.attributeValue("value"));	
                            }
                           
                        }
                    }
                }
            }

            createAnnotationDriven(context,root);
            if (root.elements().size() > 0)
            {
                context.setShouldRequireSpring(true);
                context.getFileOverrides().put(SPRING_XML, SpringHelper.documentToBytes(springDoc));
            }
        }
    }
    private void createPropertiesLoader(TransformContext context,Element root){
    	Element propertiesLoader=context.getDescriptorDocument().getRootElement().element("properties-loader");
    	if(propertiesLoader!=null){
    		Element propertiesLoaderElement=root.addElement("beans:bean");
    		propertiesLoaderElement.addAttribute("id", propertiesLoader.attributeValue("key"));
    		propertiesLoaderElement.addAttribute("class", "com.jijesoft.boh.core.propertiesfileloader.ConfigurablePropertyPlaceholder");
    		Element propertyElement = propertiesLoaderElement.addElement("beans:property");
    		propertyElement.addAttribute("name", "propertyFileName");
    		String location=propertiesLoader.attributeValue("location");
    		propertyElement.addAttribute("value", location);
    		
    	}
    }
    
    private void createAnnotationDriven(TransformContext context,Element root){
        Element annotationDriven=context.getDescriptorDocument().getRootElement().element("annotation-driven");
        if(annotationDriven!=null){
        	Element annotationDrivenElement=root.addElement("tx:annotation-driven");
        	annotationDrivenElement.addAttribute("transaction-manager", annotationDriven.attributeValue("transaction-manager"));
        	annotationDrivenElement.addAttribute("proxy-target-class", annotationDriven.attributeValue("proxy-target-class"));
        }	
    }
    
    private void createComponentScan(TransformContext context,Element root){
        Element componentScan=context.getDescriptorDocument().getRootElement().element("component-scan");
        if(componentScan!=null){
        	Element componentScanElement=root.addElement("context:component-scan");
        	componentScanElement.addAttribute("base-package", componentScan.attributeValue("base-package"));
        }
    }

    void ensureExported(String className, TransformContext context)
    {
        String pkg = className.substring(0, className.lastIndexOf('.'));
        if (!context.getExtraExports().contains(pkg))
        {
            String fileName = className.replace('.','/') + ".class";
            
            if (context.getPluginArtifact().doesResourceExist(fileName))
            {
                context.getExtraExports().add(pkg);
            }
        }
    }

}
