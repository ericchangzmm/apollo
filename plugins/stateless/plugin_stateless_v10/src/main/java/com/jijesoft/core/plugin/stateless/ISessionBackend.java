package com.jijesoft.core.plugin.stateless;



import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ISessionBackend {

	SessionData get(HttpServletRequest request);

	void save(SessionData sessionData, HttpServletRequest request,
			HttpServletResponse response) throws IOException;
}
