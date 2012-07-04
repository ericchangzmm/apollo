package com.jijesoft.core.plugin.stateless;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.jijesoft.apollo.base.mapper.JsonMapper;
import com.jijesoft.apollo.base.spring.SpringContextHolder;
import com.jijesoft.core.plugin.memcached.ISpyMemcachedClient;
import com.jijesoft.core.plugin.memcached.MemcachedObjectType;

public class MemcacheBackend extends CookieBackendSupport {

	private static final String REQ_ATTR_SESSION_ID = "stateless.memcache.id";

	private ISpyMemcachedClient spyMemcachedClient = SpringContextHolder
			.getBean("spyMemcachedClient");

	private JsonMapper jsonMapper = JsonMapper.buildNonNullMapper();

	public MemcacheBackend() {
		// setCookieName("mid");
	}

	@Override
	public SessionData get(HttpServletRequest request) {

		byte[] data = getCookieData(request, null);
		if (data != null) {
			String id = new String(data);
			request.setAttribute(REQ_ATTR_SESSION_ID, id);

			String json = spyMemcachedClient.get(MemcachedObjectType.SESSION
					.getPrefix() + id);
			if (StringUtils.isNotBlank(json))
				return jsonMapper.fromJson(json, SessionData.class);
		}
		return null;
	}

	@Override
	public void save(SessionData sessionData, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (sessionData != null) {
			spyMemcachedClient.set(MemcachedObjectType.SESSION.getPrefix()
					+ sessionData.getId(),
					MemcachedObjectType.SESSION.getExpiredTime(),
					jsonMapper.toJson(sessionData));

			Object reqSessionId = request.getAttribute(REQ_ATTR_SESSION_ID);
			if (reqSessionId == null
					|| !StringUtils.equals((String) reqSessionId,
							sessionData.getId())) {
				setCookieData(request, response, sessionData.getId().getBytes());
			}
		} else {
			setCookieData(request, response, null);
		}
	}

}
