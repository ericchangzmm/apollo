package com.jijesoft.core.plugin.workflowDemo.entity.acct;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;

import com.jijesoft.core.plugin.entity.IdEntity;

public class User extends IdEntity {

	private static final long serialVersionUID = 4904910747427006203L;

	@NotBlank
	private String name;

	private List<Role> roleList = new ArrayList<Role>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

}
