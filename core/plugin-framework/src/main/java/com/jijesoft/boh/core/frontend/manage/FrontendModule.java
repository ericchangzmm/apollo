package com.jijesoft.boh.core.frontend.manage;

import com.jijesoft.boh.core.frontend.descriptor.FrontendModuleDescriptor;

public class FrontendModule {
	
	private String key;
	private String path;
	private FrontendModuleDescriptor descriptor;
	
	public FrontendModule(FrontendModuleDescriptor descriptor){
		this.descriptor=descriptor;
	}

	public String getKey() {
		return descriptor.getKey();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPath() {
		return descriptor.getFrontendFilePath();
	}

	public void setPath(String path) {
		this.path = path;
	}

}
