package com.jijesoft.boh.core.workflow.manage;

import java.io.InputStream;
import java.util.Map;

import com.jijesoft.boh.core.workflow.descriptor.WorkflowModuleDescriptor;
import com.jijesoft.boh.core.workflow.workflowable.Workflowable;

public class Workflow {

	private String key;

	private String name;

	private Workflowable workflowable;

	private String diagramFilePath;

	private WorkflowModuleDescriptor descriptor;

	public Workflow(WorkflowModuleDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public Map<String, Object> beforeStartWorkflow(Map<String, Object> reqMap) {
		return workflowable.beforeStartWorkflow(reqMap);
	}

	public void afterStartWorkflow(String processInstanceId,
			Map<String, Object> parameters) {
		workflowable.afterStartWorkflow(processInstanceId, parameters);
	}

	public Object onTransactTask(Map<String, Object> reqMap,
			String taskDefinitionKey) {
		return workflowable.onTransactTask(reqMap, taskDefinitionKey);
	}

	public void onClaimTask(Map<String, Object> reqMap, String taskDefinitionKey) {
		workflowable.onClaimTask(reqMap, taskDefinitionKey);
	}

	public Map<String, Object> onCompleteTask(Map<String, Object> reqMap,
			String taskDefinitionKey, Map<String, Object> parameters) {
		return workflowable.onCompleteTask(reqMap, taskDefinitionKey,
				parameters);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Workflowable getWorkflowable() {
		return workflowable;
	}

	public void setWorkflowable(Workflowable workflowable) {
		this.workflowable = workflowable;
	}

	public InputStream getDiagramInputStream() {
		return descriptor.getPlugin().getResourceAsStream(diagramFilePath);
	}

	public String getDiagramFilePath() {
		return diagramFilePath;
	}

	public void setDiagramFilePath(String diagramFilePath) {
		this.diagramFilePath = diagramFilePath;
	}

}
