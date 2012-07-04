package com.jijesoft.core.plugin.workflowDemo.dao.acct;

import java.util.List;

import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.mybatis.BaseMybatisDao;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.Role;

@Component
public class RoleDao extends BaseMybatisDao<Role> {

	@Override
	protected String getNamespace() {
		return "Role";
	}

	public List<Long> selectRoleIdsByUserId(Long userId) {
		return getSqlSession().selectList(
				getStatement("selectRoleIdsByUserId"), userId);
	}
	
	public List<Role> selectRoleByUserId(Long userId){
		return getSqlSession().selectList(
				getStatement("selectRoleByUserId"), userId);
	}
}
