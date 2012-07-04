package com.jijesoft.core.plugin.utils;



import java.security.SignatureException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class CookieUtils {

	private CookieUtils() {
	}

	public static Cookie createCookie(String name, String content)
			throws SignatureException {
		return createCookie(name, content, null, null, null, false, null);
	}

	public static Cookie createCookie(String name, String content,
			String domain, String path, Integer maxAge)
			throws SignatureException {
		return createCookie(name, content, domain, path, maxAge, false, null);
	}

	public static Cookie createCookie(String name, String content,
			String domain, String path, Integer maxAge, boolean sign, String key)
			throws SignatureException {

		String cContent = content;
		if (sign && content != null) {
			cContent = HmacUtils.signRFC2104HMAC(cContent, key, "|");
		}
		Cookie c = new Cookie(name, cContent);
		if (domain != null) {
			c.setDomain(domain);
		}

		if (path != null) {
			c.setPath(path);
		}

		if (StringUtils.isEmpty(content)) {
			c.setMaxAge(0);
		} else if (maxAge != null) {
			c.setMaxAge(maxAge.intValue());
		}

		return c;
	}

	public static Cookie getCookie(HttpServletRequest request, String name)
			throws SignatureException {
		return getCookie(request, name, false, null);
	}

	public static Cookie getCookie(HttpServletRequest request, String name,
			boolean sign, String key) throws SignatureException {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = cookies.length - 1; i >= 0; i--) {
				if (name.equals(cookies[i].getName())) {
					if (sign && cookies[i].getValue() != null) {
						if (HmacUtils.verifyRFC2104HMAC(cookies[i].getValue(),
								key, "|") != null) {
							return cookies[i];
						}
					} else {
						return cookies[i];
					}

				}
			}
		}
		return null;
	}

	public static String removeCookieSignature(String cookieValue) {
		if (cookieValue != null && cookieValue.contains("|")) {
			return cookieValue.split("\\|")[0];
		}

		return null;
	}
}
