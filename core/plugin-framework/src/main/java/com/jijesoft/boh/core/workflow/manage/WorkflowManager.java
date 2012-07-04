package com.jijesoft.boh.core.workflow.manage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WorkflowManager {

	private static ConcurrentMap<String, Workflow> workflows = new ConcurrentHashMap<String, Workflow>();

	public static void add(Workflow workflow) {
		workflows.put(workflow.getKey(), workflow);
	}

	public static Workflow get(String workflowKey) {
		return workflows.get(workflowKey);
	}

	public static void remove(String workflowKey) {
		if (workflows.containsKey(workflowKey))
			workflows.remove(workflowKey);
	}
}
