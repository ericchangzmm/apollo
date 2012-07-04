package com.jijesoft.core.plugin.basecontroller.servicecore;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.basecontroller.service.IOperateService;
import com.jijesoft.core.plugin.basecontroller.service.utils.ServletConfig;

/**
 * @author Nancy.Zhou
 *
 */
@Component("operateServiceCore")
public class OperateServiceCore {

	/*
	 * eq. puName = jijesoft.plugin.basecontroller filter:
	 * {come.jijesoft.core.plugin.basecontroller.
	 * service.IOperateService}={org.springframework.osgi
	 * .bean.name=operateService, Bundle-SymbolicName=jijesoft.plugin.basecontroller,
	 * Bundle-Version=1.0, service.id=76}
	 */
	public Object getData(Map<String,Object> attributesMap) {
		String symbolicName =(String)attributesMap.get(ServletConfig.REQUEST_PARAMETER_MODULE_NAME);
		if (StringUtils.isEmpty(symbolicName)) {
			symbolicName = "jijesoft.plugin.basecontroller";
		} else {
			symbolicName = ServletConfig.BUNDLE_SYMBOLIC_PREFIX + symbolicName;
		}
		IOperateService operateService = (IOperateService) getServiceReference(symbolicName);
		return operateService.getData(attributesMap);
	}

	private Object getServiceReference(String symbolicName) {
		BundleContext context = FrameworkUtil.getBundle(this.getClass())
				.getBundleContext();
		String filter = "(Bundle-SymbolicName=" + symbolicName + ")";
		ServiceReference[] refs = null;
		try {
			refs = context.getServiceReferences(
					IOperateService.class.getName(), filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return context.getService(refs[0]);
	}
}
