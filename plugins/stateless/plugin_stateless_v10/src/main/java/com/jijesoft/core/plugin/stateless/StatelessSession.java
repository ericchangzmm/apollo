package com.jijesoft.core.plugin.stateless;



import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.NotImplementedException;

@SuppressWarnings("deprecation")
public class StatelessSession implements HttpSession {

	private static final String SECURE_ALGORITHM = "SHA1PRNG";

	private String id;

	private Map<String, Object> content;

	private boolean isNew;

	private long creationTime;

	private long lastAccessedTime = 0;

	// expireTime 如果小于0则不会过期，但是要考虑memcache的expireTime
	private int maxInactiveInterval = StatelessConfig.SESSION_MAXAGE;

	private HttpServletRequest request;

	public StatelessSession(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 初始化session
	 * <ul>
	 * <li>newSession == true 则该session将作为一个全新的session</li>
	 * <li>newSession == false 则该session将作为一个旧的session</li>
	 * </ul>
	 * 
	 * @param newSession
	 */
	public void init(boolean newSession) {
		if (newSession) {
			SecureRandom rand;
			try {
				rand = SecureRandom.getInstance(SECURE_ALGORITHM);
			} catch (NoSuchAlgorithmException e) {
				// never
				throw new RuntimeException(e);
			}
			byte[] data = new byte[32];
			rand.nextBytes(data);
			creationTime = System.currentTimeMillis();
			id = new String(Hex.encodeHex(data)) + creationTime;
		}

		content = new HashMap<String, Object>();
		isNew = newSession;
	}

	/**
	 * merge从sessionbackend中取出的sessiondata
	 * 
	 * @param SessionData
	 */
	public void merge(SessionData data) {
		id = data.getId();
		content.putAll(data.getContent());
		creationTime = data.getCreationTime();
		lastAccessedTime = data.getLastAccessedTime();
	}

	public boolean isExpired() {
		if (maxInactiveInterval >= 0) {
			int timeIdle = (int) ((System.currentTimeMillis() - lastAccessedTime) / 1000L);
			if (timeIdle >= maxInactiveInterval) {
				return true;
			}
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getContent() {
		return content;
	}

	public void setContent(Map<String, Object> content) {
		this.content = content;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}


	public long getCreationTime() {
		return creationTime;
	}


	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	
	public ServletContext getServletContext() {
		return request.getSession().getServletContext();
	}

	
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}


	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}


	@Deprecated
	public HttpSessionContext getSessionContext() {
		throw new NotImplementedException();
	}

	
	public Object getAttribute(String name) {
		return content.get(name);
	}


	@Deprecated
	public Object getValue(String name) {
		return content.get(name);
	}


	@SuppressWarnings("rawtypes")
	public Enumeration getAttributeNames() {
		return IteratorUtils.asEnumeration(content.keySet().iterator());
	}

	
	@Deprecated
	public String[] getValueNames() {
		return (String[]) content.keySet().toArray();
	}


	public void setAttribute(String name, Object value) {
		content.put(name, value);
	}


	@Deprecated
	public void putValue(String name, Object value) {
		content.put(name, value);
	}


	public void removeAttribute(String name) {
		content.remove(name);
	}


	@Deprecated
	public void removeValue(String name) {
		content.remove(name);
	}


	public void invalidate() {
		content = new HashMap<String, Object>();
	}

	
	public boolean isNew() {
		return this.isNew;
	}

}
