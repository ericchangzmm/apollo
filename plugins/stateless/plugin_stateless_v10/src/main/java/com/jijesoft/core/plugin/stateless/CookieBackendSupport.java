package com.jijesoft.core.plugin.stateless;





import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jijesoft.core.plugin.security.Cryptos;
import com.jijesoft.core.plugin.utils.CookieUtils;


/**
 * 基于cookie的sessionBackend的骨架抽象类
 * 
 * @author eric.zhang
 * 
 */
public abstract class CookieBackendSupport implements ISessionBackend {

	private static final Logger logger = LoggerFactory
			.getLogger(CookieBackendSupport.class);

	private String REQ_ATTR_COOKIE_COUNT = "stateless.cookie.mid.count";

	private String cookieName = StatelessConfig.COOKIE_NAME;

	private static byte[] aesKey;

	static {
		try {
			aesKey = Hex.decodeHex(StatelessConfig.HEXED_COOKIE_AES_KEY
					.toCharArray());
		} catch (DecoderException e) {
			e.printStackTrace();
			logger.error("Error aes decodering key "
					+ StatelessConfig.HEXED_COOKIE_AES_KEY, e);
		}
	}

	public abstract SessionData get(HttpServletRequest request);

	public abstract void save(SessionData sessionData,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	protected Cookie createCookie(String name, String content) {
		try {
			return CookieUtils.createCookie(name, content,
					StatelessConfig.COOKIE_DOMAIN, StatelessConfig.COOKIE_PATH,
					StatelessConfig.COOKIE_MAXAGE, StatelessConfig.COOKIE_SIGN,
					StatelessConfig.COOKIE_SIGN_KEY);
		} catch (SignatureException e) {
			logger.error("Error creating cookie", e);
		}
		return null;
	}

	protected Cookie getCookie(HttpServletRequest request, String name) {
		try {
			return CookieUtils.getCookie(request, name,
					StatelessConfig.COOKIE_SIGN,
					StatelessConfig.COOKIE_SIGN_KEY);
		} catch (SignatureException e) {
			logger.error("Error sending cookie", e);
		}
		return null;
	}

	protected byte[] getCookieData(HttpServletRequest request,
			HttpServletResponse response) {
		int i = 0;
		Cookie c = null;
		StringBuilder data = new StringBuilder();

		while ((c = getCookie(request, cookieName + i)) != null) {
			data.append(StatelessConfig.COOKIE_SIGN ? CookieUtils
					.removeCookieSignature(c.getValue()) : c.getValue());
			i++;
		}

		request.setAttribute(REQ_ATTR_COOKIE_COUNT, new Integer(i));

		String dataString = data.toString();
		if (dataString.length() == 0) {
			return null;
		}
		return Cryptos.aesDecrypt(Base64.decodeBase64(dataString), aesKey);
	}

	/**
	 * Set cookie 如果数据过长则会拆分为多个cookie
	 * 
	 * @param request
	 * @param response
	 * @param data
	 */
	protected void setCookieData(HttpServletRequest request,
			HttpServletResponse response, byte[] data) {
		String encoded = "";
		if (data != null) {
			encoded = new String(Base64.encodeBase64(Cryptos.aesEncrypt(data,
					aesKey)));
		}

		ArrayList<String> splittedData = new ArrayList<String>();
		while (encoded.length() > StatelessConfig.COOKIE_MAX_SIZE) {
			splittedData.add(encoded.substring(0,
					StatelessConfig.COOKIE_MAX_SIZE));
			encoded = encoded.substring(StatelessConfig.COOKIE_MAX_SIZE);
		}
		if (encoded.length() > 0) {
			splittedData.add(encoded);
		}

		int i = 0;
		Cookie c = null;
		for (String datapart : splittedData) {
			c = createCookie(cookieName + i, datapart);
			response.addCookie(c);
			i++;
		}

		int previousCount = ((Integer) request
				.getAttribute(REQ_ATTR_COOKIE_COUNT)).intValue();
		while (i < previousCount) {
			c = createCookie(cookieName + i, "");
			response.addCookie(c);
			i++;
		}
	}

	protected void setCookieName(String cookieName) {
		this.cookieName = cookieName;
		REQ_ATTR_COOKIE_COUNT = "stateless.cookie." + cookieName + ".count";
	}
}
