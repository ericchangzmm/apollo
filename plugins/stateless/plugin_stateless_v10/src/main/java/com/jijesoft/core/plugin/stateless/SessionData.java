package com.jijesoft.core.plugin.stateless;



import java.io.Serializable;
import java.util.Map;

/**
 * 存入sessionbackend的session DTO
 * 
 * @author eric.zhang
 * 
 */
public class SessionData implements Serializable {

	private static final long serialVersionUID = 4737358974806886485L;

	private String id;
	private Map<String, Object> content;
	private long creationTime;
	private long lastAccessedTime;
	private int maxInactiveInterval;

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

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
