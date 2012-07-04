package com.jijesoft.core.plugin.basecontroller.service;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @author Nancy.Zhou
 * 
 */
@Component("operateService")
public class OperateService implements IOperateService {

	public Object getData(Map<String, Object> attributesMap) {
		Map<String, Object> data = new HashMap<String, Object>();
		System.out.println("invoke basecontroller operateService~~~~~~~~~");
		return data;
	}

}
