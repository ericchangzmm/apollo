package com.jijesoft.core.plugin.memcached;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * packaging SpyMemcached Client simply
 * 
 * @author eric.zhang
 * 
 */
@SuppressWarnings("unchecked")
public class SpyMemcachedClient implements DisposableBean, ISpyMemcachedClient {

	private static Logger logger = LoggerFactory
			.getLogger(SpyMemcachedClient.class);

	private MemcachedClient memcachedClient;

	private long shutdownTimeout = 2500;

	private long updateTimeout = 2500;

	/**
	 * get, if throw runtimeException return null
	 */
	public <T> T get(String key) {

		logger.debug("get " + key + " from memcached.");

		try {
			return (T) memcachedClient.get(key);
		} catch (RuntimeException e) {
			handleException(e, key);
			return null;
		}
	}

	/**
	 * GetBulk, if throw runtimeException return null
	 */
	public <T> Map<String, T> getBulk(Collection<String> keys) {
		try {
			return (Map<String, T>) memcachedClient.getBulk(keys);
		} catch (RuntimeException e) {
			handleException(e, StringUtils.join(keys, ","));
			return null;
		}
	}

	/**
	 * asynchronous Set, no matter success or failed
	 */
	public void set(String key, int expiredTime, Object value) {
		memcachedClient.set(key, expiredTime, value);
	}

	/**
	 * asynchronous Set safely. if the time beyond updateTimeout return false
	 * and cancel
	 */
	public boolean safeSet(String key, int expiredTime, Object value) {

		Future<Boolean> future = memcachedClient.set(key, expiredTime, value);
		try {
			return future.get(updateTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.warn(
					"spymemcached client receive an exception in safeSet with key:"
							+ key, e);
			future.cancel(false);
		}
		return false;
	}

	/**
	 * asynchronous Delete, no matter success or failed
	 */
	public void delete(String key) {
		memcachedClient.delete(key);
	}

	/**
	 * asynchronous Delete safely. if the time beyond updateTimeout return false
	 * and cancel
	 */
	public boolean safeDelete(String key) {
		Future<Boolean> future = memcachedClient.delete(key);
		try {
			return future.get(updateTimeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.warn(
					"spymemcached client receive an exception in safeDelete with key:"
							+ key, e);
			future.cancel(false);
		}
		return false;
	}

	public void handleException(Exception e, String key) {
		logger.warn("spymemcached client receive an exception with key:" + key,
				e);
	}

	public void destroy() throws Exception {
		if (memcachedClient != null)
			memcachedClient.shutdown(shutdownTimeout, TimeUnit.MILLISECONDS);
	}

	public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}

	public void setMemcachedClient(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public void setShutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout = shutdownTimeout;
	}

	public void setUpdateTimeout(long updateTimeout) {
		this.updateTimeout = updateTimeout;
	}

}
