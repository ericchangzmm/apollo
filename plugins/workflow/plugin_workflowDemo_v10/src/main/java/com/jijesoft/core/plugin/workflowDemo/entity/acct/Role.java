package com.jijesoft.core.plugin.workflowDemo.entity.acct;

import org.hibernate.validator.constraints.NotBlank;

import com.jijesoft.core.plugin.entity.IdEntity;

public class Role extends IdEntity{

	private static final long serialVersionUID = -4741554451430644550L;

	@NotBlank
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
