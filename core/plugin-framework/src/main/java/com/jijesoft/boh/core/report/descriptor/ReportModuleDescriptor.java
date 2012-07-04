package com.jijesoft.boh.core.report.descriptor;

import java.util.List;

import org.dom4j.Element;

import com.jijesoft.boh.core.plugin.AutowireCapablePlugin;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.StateAware;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;
import com.jijesoft.boh.core.plugin.hostcontainer.HostContainer;
import com.jijesoft.boh.core.report.factory.DataSourceFactory;
import com.jijesoft.boh.core.report.manage.Report;
import com.jijesoft.boh.core.report.manage.ReportManager;

public class ReportModuleDescriptor extends
		AbstractModuleDescriptor<DataSourceFactory> implements StateAware {

	private final HostContainer hostContainer;
	private String templateFile = null;
	private String type;
	public void init(Plugin plugin, Element element)
			throws PluginParseException {
		super.init(plugin, element);
		type = element.attributeValue("type");
		List<Element> properties = element.elements("property");
		for (Element property : properties) {
			if(property.attributeValue("name").equals("templateFile")){
				templateFile = property.attributeValue("value");
			}
		}
	}

	
	@Override
	public void enabled() {
		super.enabled();
		initReport();
		
	}


	private void initReport() {
		Report report = new Report(this);
		report.setType(type);
		report.setDs(getModule());
		report.setKey(this.getKey());
		ReportManager.getReportInstance().put(report);
	}


	@Override
	public void disabled() {
		super.disabled();
		ReportManager.getReportInstance().remove(this.getKey());
	}


	public String getTemplateFile() {
		return templateFile;
	}



	public ReportModuleDescriptor(final HostContainer hostContainer) {
		this.hostContainer = hostContainer;
	}

	@Override
	public DataSourceFactory getModule() {
		if (plugin instanceof AutowireCapablePlugin) {
			return ((AutowireCapablePlugin) plugin).autowire(getModuleClass());
		}
		return hostContainer.create(getModuleClass());
	}

}
