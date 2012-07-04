package com.jijesoft.core.plugin.memcached;

import java.util.Collection;
import java.util.Map;

public interface ISpyMemcachedClient {
	public <T> T get(String key);
	public <T> Map<String, T> getBulk(Collection<String> keys);
	public void set(String key, int expiredTime, Object value);
	public boolean safeSet(String key, int expiredTime, Object value);
	public void delete(String key);
	public boolean safeDelete(String key);
	public void handleException(Exception e, String key);
	
}
