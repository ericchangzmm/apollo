package com.jijesoft.core.plugin.stateless;



import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class StatelessRequestWrapper extends HttpServletRequestWrapper {

	private static Logger logger = LoggerFactory
			.getLogger(StatelessRequestWrapper.class);

	private HttpServletRequest originalRequest = null;
	private StatelessSession session = null;
	private ISessionBackend sessionBackend = StatelessConfig.SESSION_BACKEND;

	public StatelessRequestWrapper(HttpServletRequest request) {
		super(request);
		originalRequest = request;
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean create) {

		if (session == null) {
			session = new StatelessSession(originalRequest);

			SessionData sessionData = sessionBackend.get(originalRequest);
			if (sessionData != null) {
				session.init(false);
				session.merge(sessionData);
			} else {
				session.init(true);
			}
		}
		// 如果session不是new 则要判断是否过期
		if (!session.isNew() && session.isExpired()) {
			session.invalidate();
		}
		// 记录session最后访问时间
		session.setLastAccessedTime(System.currentTimeMillis());
		return session;
	}

	public void writeSession(HttpServletResponse response) throws IOException {

		if (session != null) {

			SessionData sessionData = new SessionData();
			sessionData.setId(session.getId());
			sessionData.setContent(session.getContent());
			sessionData.setCreationTime(session.getCreationTime());
			sessionData.setLastAccessedTime(session.getLastAccessedTime());
			sessionData
					.setMaxInactiveInterval(session.getMaxInactiveInterval());

			sessionBackend.save(sessionData, originalRequest, response);
		}
	}
}
