package com.jijesoft.core.plugin.stateless;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatelessFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(StatelessFilter.class);

	// 配置cookie的keys
	/** cookie name default:"bohid" **/
	private static final String PARAM_COOKIE_NAME = "cookieName";
	/** cookie path default:"/" **/
	private static final String PARAM_COOKIE_PATH = "cookiePath";
	/** cookie domain default:null **/
	private static final String PARAM_COOKIE_DOMAIN = "cookieDomain";
	/** cookie maxAge default:24 * 60 * 60 seconds **/
	private static final String PARAM_COOKIE_MAXAGE = "cookieMaxage";
	/** cookie use HMAC-SHA1 sign or not default:true **/
	private static final String PARAM_COOKIE_SIGN = "cookieSign";
	/** cookie HMAC-SHA1 sign key **/
	private static final String PARAM_COOKIE_SIGN_KEY = "cookieSignKey";
	/** session maxAge default:20 * 60 seconds **/
	private static final String PARAM_SESSION_MAXAGE = "sessionMaxage";
	/** hexed cookie aes key **/
	private static final String PARAM_HEXED_COOKIE_AES_KEY = "cookieAesKey";
	/** configure the list of the excluded uri patterns. **/
	private static final String PARAM_EXCLUDE_PATTERN_LIST = "excludePatternList";

	private static final String EXCLUDE_PATTERN_SEPARATOR = ",";
	private List<Pattern> excludePatterns = null;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (isExcluded(httpRequest)) {
			chain.doFilter(request, response);
			return;
		}

		// Wrap request
		HttpServletRequest sessionRequest = new StatelessRequestWrapper(
				httpRequest);

		// Wrap response if necessary
		HttpServletResponse targetResponse = httpResponse;
		BufferedHttpResponseWrapper bufferedResponse = null;
		if (StatelessConfig.isBufferingRequired) {
			bufferedResponse = new BufferedHttpResponseWrapper(httpResponse);
			targetResponse = bufferedResponse;
		}

		chain.doFilter(sessionRequest, targetResponse);

		// Write session
		((StatelessRequestWrapper) sessionRequest).writeSession(httpResponse);

		// Flush buffer if necessary
		if (bufferedResponse != null) {
			if (!bufferedResponse.performSend()) {
				bufferedResponse.flushBuffer();
				response.getOutputStream().write(bufferedResponse.getBuffer());
				response.flushBuffer();
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {

		initCookieSet(filterConfig);

		initSessionSet(filterConfig);

		initSecuritySet(filterConfig);

		initExcludedPattern(filterConfig);
	}

	public void destroy() {

	}

	private void initCookieSet(FilterConfig filterConfig) {
		String conf_cookieName = filterConfig
				.getInitParameter(PARAM_COOKIE_NAME);
		if (StringUtils.isNotEmpty(conf_cookieName))
			StatelessConfig.COOKIE_NAME = conf_cookieName;

		String conf_cookiepath = filterConfig
				.getInitParameter(PARAM_COOKIE_PATH);
		if (StringUtils.isNotEmpty(conf_cookiepath))
			StatelessConfig.COOKIE_PATH = conf_cookiepath;

		String conf_cookiedomain = filterConfig
				.getInitParameter(PARAM_COOKIE_DOMAIN);
		if (StringUtils.isNotEmpty(conf_cookiedomain))
			StatelessConfig.COOKIE_DOMAIN = conf_cookiedomain;

		int conf_cookiemaxage = NumberUtils.toInt(
				filterConfig.getInitParameter(PARAM_COOKIE_MAXAGE), 0);
		if (conf_cookiemaxage != 0)
			StatelessConfig.COOKIE_MAXAGE = conf_cookiemaxage;
	}

	private void initSessionSet(FilterConfig filterConfig) {
		int conf_sessionMaxage = NumberUtils.toInt(
				filterConfig.getInitParameter(PARAM_SESSION_MAXAGE), 0);
		if (conf_sessionMaxage != 0)
			StatelessConfig.SESSION_MAXAGE = conf_sessionMaxage;
	}

	private void initSecuritySet(FilterConfig filterConfig) {
		String conf_cookiesign = filterConfig
				.getInitParameter(PARAM_COOKIE_SIGN);
		if (StringUtils.isNotEmpty(conf_cookiesign)) {
			StatelessConfig.COOKIE_SIGN = Boolean.valueOf(conf_cookiesign);
		}

		String conf_cookiesignkey = filterConfig
				.getInitParameter(PARAM_COOKIE_SIGN_KEY);
		if (StringUtils.isNotEmpty(conf_cookiesignkey))
			StatelessConfig.COOKIE_SIGN_KEY = conf_cookiesignkey;

		String conf_cookieAesKey = filterConfig
				.getInitParameter(PARAM_HEXED_COOKIE_AES_KEY);
		if (StringUtils.isNotEmpty(conf_cookieAesKey))
			StatelessConfig.HEXED_COOKIE_AES_KEY = conf_cookieAesKey;
	}

	private void initExcludedPattern(FilterConfig filterConfig) {
		String excludedPatternList = filterConfig
				.getInitParameter(PARAM_EXCLUDE_PATTERN_LIST);
		if (excludedPatternList != null) {
			String[] splittedExcludedPatternList = excludedPatternList
					.split(EXCLUDE_PATTERN_SEPARATOR);
			List<Pattern> patterns = new ArrayList<Pattern>();
			Pattern pattern = null;
			for (String element : splittedExcludedPatternList) {
				pattern = Pattern.compile(element);
				patterns.add(pattern);
			}
			this.excludePatterns = patterns;
		}
	}

	private boolean isExcluded(HttpServletRequest httpRequest) {
		if (this.excludePatterns == null) {
			return false;
		}

		String uri = httpRequest.getRequestURI();
		logger.debug("Check URI : " + uri);

		try {
			uri = new URI(uri).normalize().toString();

			for (Pattern pattern : this.excludePatterns) {
				if (pattern.matcher(uri).matches()) {
					logger.info("URI excluded : " + uri);
					return true;
				}
			}

		} catch (URISyntaxException e) {
			logger.warn(
					"The following URI has a bad syntax. The request will be processed by the filter. URI : "
							+ uri, e);
		}

		return false;
	}

}
