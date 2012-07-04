package com.jijesoft.core.plugin.memcached;

/**
 * define the key prefix string and expiretime of the object sotored in
 * memcached
 * 
 * @author eric.zhang
 * 
 */
public enum MemcachedObjectType {

	SESSION("session:", 60 * 60), USER("user:", 60 * 60 * 1);

	private String prefix;

	private int expiredTime;

	private MemcachedObjectType(String prefix, int expiredTime) {
		this.prefix = prefix;
		this.expiredTime = expiredTime;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getExpiredTime() {
		return expiredTime;
	}

}
