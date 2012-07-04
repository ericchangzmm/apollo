package com.jijesoft.core.plugin.workflowDemo.dao.leave;

import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.mybatis.BaseMybatisDao;
import com.jijesoft.core.plugin.workflowDemo.entity.leave.Leave;

@Component
public class LeaveDao extends BaseMybatisDao<Leave>{

	@Override
	protected String getNamespace() {
		return "Leave";
	}

}
