package com.jijesoft.core.plugin.basecontroller.service.utils;



/**
 * @author Nancy.Zhou
 *
 */
public class ServletConfig {
	
	/**
	 * This static variable is for request mapping.
	 * So the request URL can be short.
	 * eg:
	 *     If a bundle(symbolic name: jijesoft.plugin.module) want to receive a request,  
	 *     so the request URL can be: /app/ajax/{module}/{function}
	 * see ActionServletHandler, DefaultServletMapping.
	 */
	public static final String BUNDLE_SYMBOLIC_PREFIX = "jijesoft.plugin.";
	
	/**
	 * 
	 */
	
	public static final String REQUEST_PARAMETER_MODULE_NAME = "module";
	
	public static final String REQUEST_PARAMETER_FUNCTION_NAME = "function";
	
	public static final String REQUEST_PARAMETER_FOLDER_PATH_NAME = "folderpath";

	public static final String REQUEST_PARAMETER_TEMPLATE_NAME = "template";

	public static final String URI_SEPAPATOR="/";
	
	public static final String AJAX_URI="ajax";
	
	public static final String REPORT_URI = "report";
	
	public static final String SERVICE_URI = "service";
	
	public static final String REPORT_FORMAT_PDF = "pdf";
		
	public static final String REPORT_FORMAT_XLS = "xls";
	
	public static final String REPORT_VIEW = "view"; // View

	public static final String REPORT_EXPORT = "export"; // Export
	
	public static final String REPORT_FORMAT = "format";
	
	public static final String REPORT_HANDLEMETHOD = "handleMethod"; // Export

}
