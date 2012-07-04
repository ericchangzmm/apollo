package com.jijesoft.core.plugin.workflowDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.basecontroller.service.IOperateService;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.Role;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.User;
import com.jijesoft.core.plugin.workflowDemo.service.acct.UserService;

@Component
public class WorkflowDemoService implements IOperateService {

	@Autowired
	private UserService userService;

	public Object getData(Map<String, Object> map) {

		String act = String.valueOf(map.get("function"));
		if (StringUtils.equals(act, "userList"))
			return userList(map);
		if (StringUtils.equals(act, "userView"))
			return userView(map);
		if (StringUtils.equals(act, "userDelete"))
			return userDelete(map);
		if (StringUtils.equals(act, "userSave"))
			return userSave(map);
		if (StringUtils.equals(act, "roleList"))
			return roleList(map);
		if (StringUtils.equals(act, "roleView"))
			return roleView(map);
		if (StringUtils.equals(act, "roleDelete"))
			return roleDelete(map);
		if (StringUtils.equals(act, "roleSave"))
			return roleSave(map);
		return null;
	}

	private Object userList(Map<String, Object> map) {
		try {
			return userService.getAllUser();
		} catch (Exception e) {
			e.printStackTrace();
			return "userList error";
		}
	}

	private Object userView(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			return userService.getUserById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return "userView error";
		}
	}

	private Object userDelete(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			userService.deleteUser(id);
			return "delete success";
		} catch (Exception e) {
			e.printStackTrace();
			return "delete error";
		}
	}

	private Object userSave(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			String name = String.valueOf(map.get("name"));
			User user = new User();
			if (id > 0)
				user.setId(id);
			user.setName(name);
			String[] roleIds = (String[]) map.get("roleIds");
			List<Role> roles = new ArrayList<Role>();
			for (String roleId : roleIds) {
				roles.add(userService.getRoleById(Long.valueOf(roleId)));
			}
			user.setRoleList(roles);
			userService.saveUser(user);
			return "save success";
		} catch (Exception e) {
			e.printStackTrace();
			return "save error";
		}
	}

	private Object roleList(Map<String, Object> map) {
		try {
			return userService.getAllRole();
		} catch (Exception e) {
			e.printStackTrace();
			return "roleList error";
		}
	}

	private Object roleView(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			return userService.getRoleById(id);
		} catch (Exception e) {
			e.printStackTrace();
			return "userView error";
		}
	}

	private Object roleDelete(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			userService.deleteRole(id);
			return "delete role success";
		} catch (Exception e) {
			e.printStackTrace();
			return "delete role error";
		}
	}

	private Object roleSave(Map<String, Object> map) {
		try {
			Long id = NumberUtils.toLong(String.valueOf(map.get("id")), 0);
			String name = String.valueOf(map.get("name"));
			Role role = new Role();
			if (id > 0)
				role.setId(id);
			role.setName(name);
			userService.saveRole(role);
			return "save success";
		} catch (Exception e) {
			e.printStackTrace();
			return "save error";
		}
	}

}
