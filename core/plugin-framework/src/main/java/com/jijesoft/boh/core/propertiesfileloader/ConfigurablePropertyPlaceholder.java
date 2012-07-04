package com.jijesoft.boh.core.propertiesfileloader;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.jijesoft.boh.startup.Env;

public class ConfigurablePropertyPlaceholder extends PropertyPlaceholderConfigurer{
	
	private String propertyFileName;


	public String getPropertyFileName() {
		return propertyFileName;
	}


	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}


	@Override
	protected void loadProperties(Properties props) throws IOException {
	      Resource location = null;
	        if(StringUtils.isNotEmpty(propertyFileName)){
	        	String configFilepath=Env.getEnvInstance().getConfigFilePath();
	    		if(!propertyFileName.startsWith("/")){
	    			configFilepath=configFilepath+"/";
	    		}

	            location = new FileSystemResource(configFilepath+propertyFileName);
	        }

	        setLocation(location);
	        super.loadProperties(props);   

	}

}
