package com.jijesoft.boh.core.frontend.descriptor;

import org.dom4j.Element;

import com.jijesoft.boh.core.frontend.manage.FrontendModule;
import com.jijesoft.boh.core.frontend.manage.FrontendModuleManager;
import com.jijesoft.boh.core.plugin.Plugin;
import com.jijesoft.boh.core.plugin.PluginParseException;
import com.jijesoft.boh.core.plugin.descriptors.AbstractModuleDescriptor;

public class FrontendModuleDescriptor  extends AbstractModuleDescriptor<Void> {
	
	private String frontendFilePath;
	
	

	@Override
	public void init(Plugin plugin, Element element)
			throws PluginParseException {
		super.init(plugin, element);
		frontendFilePath = element.attributeValue("path");
	}



	@Override
	public void enabled() {
		super.enabled();
		initFrontendModule();
	}

    private void initFrontendModule(){
    	FrontendModule frontendModule=new FrontendModule(this);
    	FrontendModuleManager.getFrontendModuleManagerInstance().put(frontendModule);
    }

	@Override
	public void disabled() {
		super.disabled();
		FrontendModuleManager.getFrontendModuleManagerInstance().remove(this.getKey());
	}



	public String getFrontendFilePath() {
		return frontendFilePath;
	}

	@Override
	public Void getModule() {
        throw new UnsupportedOperationException();
	}

}
