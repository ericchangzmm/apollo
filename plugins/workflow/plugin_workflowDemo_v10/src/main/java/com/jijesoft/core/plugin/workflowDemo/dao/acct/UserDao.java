package com.jijesoft.core.plugin.workflowDemo.dao.acct;

import org.springframework.stereotype.Component;

import com.jijesoft.core.plugin.mybatis.BaseMybatisDao;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.User;

@Component
public class UserDao extends BaseMybatisDao<User> {

	@Override
	protected String getNamespace() {
		return "User";
	}

}
