package com.jijesoft.boh.core.workflow.workflowable;

import java.util.Map;

public interface Workflowable {

	/**
	 * do something your own before start new process instance
	 * 
	 * @param reqMap
	 * @return
	 */
	Map<String, Object> beforeStartWorkflow(Map<String, Object> reqMap);

	/**
	 * do something your own after start new process instance
	 * 
	 * @param processInstanceId
	 * @param parameters
	 *            the map beforeStartWorkflow returned
	 */
	void afterStartWorkflow(String processInstanceId,
			Map<String, Object> parameters);

	/**
	 * maybe it should return a web page form
	 * 
	 * @param reqMap
	 *            the map of httpservletrequest parameters
	 * @param taskDefinitionKey
	 *            the task id defined in xxx.bpmn20.xml
	 * @return
	 */
	Object onTransactTask(Map<String, Object> reqMap, String taskDefinitionKey);

	/**
	 * do something your own on claim task
	 * 
	 * @param reqMap
	 *            the map of httpservletrequest parameters
	 * @param taskDefinitionKey
	 *            the task id defined in xxx.bpmn20.xml
	 */
	void onClaimTask(Map<String, Object> reqMap, String taskDefinitionKey);

	/**
	 * do something your own on complete task
	 * 
	 * @param reqMap
	 *            the map of httpservletrequest parameters
	 * @param taskDefinitionKey
	 *            the task id defined in xxx.bpmn20.xml
	 * @param parameters
	 *            variables in current process execution
	 * @return
	 */
	Map<String, Object> onCompleteTask(Map<String, Object> reqMap,
			String taskDefinitionKey, Map<String, Object> parameters);
}
