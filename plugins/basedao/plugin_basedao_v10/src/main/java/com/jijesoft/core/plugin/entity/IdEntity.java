package com.jijesoft.core.plugin.entity;

import java.io.Serializable;

/**
 * base entity defined id unitary
 * 
 * @author eric.zhang
 * 
 */
public abstract class IdEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
