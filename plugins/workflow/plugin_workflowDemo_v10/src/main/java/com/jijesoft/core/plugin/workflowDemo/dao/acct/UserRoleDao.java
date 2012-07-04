package com.jijesoft.core.plugin.workflowDemo.dao.acct;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class UserRoleDao extends SqlSessionDaoSupport{

	public void insertUserRole(Long userId, Long roleId){
		Map<String, Object> parames = new LinkedHashMap<String, Object>();
		parames.put("userId", userId);
		parames.put("roleId", roleId);
		
		getSqlSession().insert("UserRole.insertUserRole", parames);
	}
	
	public void deleteUserRoleByUserId(Long userId){
		getSqlSession().delete("UserRole.deleteUserRoleByUserId", userId);
	}
	
	public void deleteUserRoleByRoleId(Long roleId){
		getSqlSession().delete("UserRole.deleteUserRoleByRoleId", roleId);
	}
}
