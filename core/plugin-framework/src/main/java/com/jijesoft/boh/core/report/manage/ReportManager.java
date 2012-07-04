package com.jijesoft.boh.core.report.manage;

import java.util.HashMap;
import java.util.Map;

public class ReportManager {

	private static ReportManager reportManager;

	Map<String, Report> reports = new HashMap<String, Report>();

	private ReportManager() {

	}

	public static ReportManager getReportInstance() {
		if (null == reportManager) {
			reportManager = new ReportManager();
		}
		return reportManager;
	}

	public void put(Report report) {
		reports.put(report.getKey(), report);
	}

	public Report getReportById(String id) {
		return reports.get(id);
	}

	public void remove(String reportId) {
		if (reports.containsKey(reportId)) {
			reports.remove(reportId);
		}
	}
}
