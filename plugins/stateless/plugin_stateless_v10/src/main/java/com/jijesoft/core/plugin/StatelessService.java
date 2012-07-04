package com.jijesoft.core.plugin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.basecontroller.service.IOperateService;

@Component()
public class StatelessService implements IOperateService {

	public Object getData(Map<String, Object> attributesMap) {
		Map<String, Object> data = new HashMap<String, Object>();
		HttpSession session = (HttpSession) attributesMap.get("session");
		session.setAttribute("aaa", "22222222222222222222");
		data.put("id", "这个是首页");
		data.put("name", "candy");
		data.put("pass", "123456");
		return data;
	}

}
