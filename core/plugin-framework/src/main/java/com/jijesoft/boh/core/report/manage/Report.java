package com.jijesoft.boh.core.report.manage;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.jijesoft.boh.core.report.descriptor.ReportModuleDescriptor;
import com.jijesoft.boh.core.report.factory.DataSourceFactory;

public class Report {

	private String key; // Report ID

	private String type; // Report type(System or dynamic)

	private DataSourceFactory ds;
	//private Collection<Object> data; // Report data list

	private Map<String, Object> parameters; // Report parameters

	private InputStream templateFile; // Template file(jasper or jrxml)

	private String exportFile; // Output file(pdf,excel..)

	private ReportModuleDescriptor descriptor;
	
	public Report(ReportModuleDescriptor descriptor){
		this.descriptor =descriptor;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Collection<Object> getData() {
		return ds.getDataSource(getParameters());
	}

	
	public void setDs(DataSourceFactory ds) {
		this.ds = ds;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}


	public InputStream getTemplateFile() {
		return descriptor.getPlugin().getResourceAsStream(descriptor.getTemplateFile());
	}

//	public void setTemplateFile(InputStream templateFile) {
//		this.templateFile = templateFile;
//	}

	public String getExportFile() {
		return exportFile;
	}

	public void setExportFile(String exportFile) {
		this.exportFile = exportFile;
	}

}
