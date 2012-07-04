package com.jijesoft.core.plugin.plugin_basecontroller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.util.FileBufferedOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jijesoft.apollo.base.mapper.JsonMapper;
import com.jijesoft.boh.core.threadlocal.RunData;
import com.jijesoft.core.plugin.basecontroller.service.utils.ServletConfig;
import com.jijesoft.core.plugin.basecontroller.servicecore.OperateServiceCore;

/**
 * controller servlet
 * 
 * @author nancy.zhou
 * 
 */
public class AppServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4106179048591838979L;

	@Autowired
	private OperateServiceCore operateServiceCore;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {	
    	RunData.setSiteeid(req.getSession().getId());
    	RunData.setTntpid(req.getSession().getId());
    	
		String uri = req.getRequestURI().trim();
		if (uri.startsWith(ServletConfig.URI_SEPAPATOR)) {
			uri = uri.substring(1);
		}
		String[] attributes = uri.split(ServletConfig.URI_SEPAPATOR);
		Map<String, Object> attributesMap = new HashMap<String, Object>();
		Map<String, Object> params = WebUtils
				.getParametersStartingWith(req, "");
		attributesMap.putAll(params);
		if(attributes.length>2){
			attributesMap.put(ServletConfig.REQUEST_PARAMETER_MODULE_NAME,
					attributes[2]);
		}
		if(attributes.length>3){
			attributesMap.put(ServletConfig.REQUEST_PARAMETER_FUNCTION_NAME,
					attributes[3]);	
		}
		if (ServletConfig.AJAX_URI.equals(attributes[1])) {
			this.handleAjax(attributesMap, resp);
		}
		if (ServletConfig.REPORT_URI.equals(attributes[1])) {
			this.handleReport(attributesMap, resp);
		}
		if (ServletConfig.SERVICE_URI.equals(attributes[1])) {
			attributesMap.put("session", req.getSession());
			this.handleService(attributesMap, resp);
		}


	}
	
    /**
     *handle frontend ajax request
     * @param attributesMap
     * @param resp
     * @throws IOException
     */
	private void handleAjax(Map<String, Object> attributesMap,
			HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html;charset=utf-8");
		Object returnData = operateServiceCore.getData(attributesMap);
		JsonMapper jsonMapper = new JsonMapper();
		String result = jsonMapper.toJson(returnData);
		resp.getWriter().write(result);
		resp.getWriter().close();
	}
	
    /**
     * handle report service
     * @param attributesMap
     * @param resp
     * @throws IOException
     */
	private void handleReport(Map<String, Object> attributesMap,
			HttpServletResponse resp) throws IOException {
		String format = (String) attributesMap.get(ServletConfig.REPORT_FORMAT);
		String handleMethod = (String) attributesMap
				.get(ServletConfig.REPORT_HANDLEMETHOD);
		Object returnData = operateServiceCore.getData(attributesMap);

		if (ServletConfig.REPORT_VIEW.equals(handleMethod)) {
			resp.setContentType("text/html;charset=utf-8");
			JsonMapper jsonMapper = new JsonMapper();
			String result = jsonMapper.toJson(returnData);
			resp.getWriter().write((String) returnData);
			resp.getWriter().close();
		} else if (ServletConfig.REPORT_EXPORT.equals(handleMethod)) {
			if (ServletConfig.REPORT_FORMAT_PDF.equals(format)) {
				resp.setContentType("application/pdf");
				resp.setHeader("Content-Disposition",
						"attachment;filename=report.pdf");
			}
			if (ServletConfig.REPORT_FORMAT_XLS.equals(format)) {
				resp.setContentType("application/x-xls");
				resp.setHeader("Content-Disposition",
						"attachment;filename=report.xls");
			}
			FileBufferedOutputStream fbos = (FileBufferedOutputStream) returnData;
			OutputStream ouputStream = resp.getOutputStream();
			fbos.writeData(ouputStream);
			ouputStream.close();
		}
	}
	
	private void handleService(Map<String, Object> attributesMap,
			HttpServletResponse resp) throws IOException{
		resp.setContentType("text/html;charset=utf-8");
		Object returnData = operateServiceCore.getData(attributesMap);
		JsonMapper jsonMapper = new JsonMapper();
		String result = jsonMapper.toJson(returnData);
		resp.getWriter().write(result);
		resp.getWriter().close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
