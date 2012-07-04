package com.jijesoft.core.plugin.workflow.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.jijesoft.core.plugin.workflow.service.export.ActivitiServiceSupport;

public class ActivitiInfo implements Serializable {

	private static final long serialVersionUID = -8858925550925552903L;

	/** process instance applyer */
	private User applyer = null;

	/** process definition */
	private String processDefinitionName = null;
	private String processDefinitionVersion = null;

	/** process instance */
	private String processInstanceId = null;
	private Boolean processInstanceSuspended = null;

	/** historic process instance */
	private String historicProcessInstanceStartTime = null;
	private String historicProcessInstanceEndTime = null;

	/** current task */
	private String taskId = null;
	private String taskName = null;
	private String taskCreateTime = null;
	private String taskAssignee = null;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public ActivitiInfo() {
	}

	public ActivitiInfo(ActivitiServiceSupport activitiService,
			ProcessDefinition processDefinition) {
		this.processDefinitionName = processDefinition.getName();
		this.processDefinitionVersion = String.valueOf(processDefinition
				.getVersion());
	}

	public ActivitiInfo(ActivitiServiceSupport activitiService, Task task) {
		this.taskId = task.getId();
		this.taskName = task.getName();
		this.taskCreateTime = dateFormat.format(task.getCreateTime());
		this.taskAssignee = task.getAssignee();

		ProcessInstance processInstance = activitiService.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		this.processInstanceId = processInstance.getId();
		this.processInstanceSuspended = processInstance.isSuspended();

		ProcessDefinition processDefinition = activitiService
				.getRepositoryService().createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId())
				.singleResult();
		this.processDefinitionName = processDefinition.getName();
		this.processDefinitionVersion = String.valueOf(processDefinition
				.getVersion());

		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(processInstance.getBusinessKey()).singleResult();
	}

	public ActivitiInfo(ActivitiServiceSupport activitiService,
			ProcessInstance processInstance) {
		this.processInstanceId = processInstance.getId();
		this.processInstanceSuspended = processInstance.isSuspended();
		ProcessDefinition processDefinition = activitiService
				.getRepositoryService().createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId())
				.singleResult();
		this.processDefinitionName = processDefinition.getName();
		this.processDefinitionVersion = String.valueOf(processDefinition
				.getVersion());
		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(processInstance.getBusinessKey()).singleResult();
		// set current task
		List<Task> tasks = activitiService.getTaskService().createTaskQuery()
				.processInstanceId(processInstance.getId())
				.orderByTaskCreateTime().desc().listPage(0, 1);
		Task task = tasks.get(0);
		this.taskId = task.getId();
		this.taskName = task.getName();
		this.taskCreateTime = dateFormat.format(task.getCreateTime());
		this.taskAssignee = task.getAssignee();
	}

	public ActivitiInfo(ActivitiServiceSupport activitiService,
			HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstanceEndTime = dateFormat
				.format(historicProcessInstance.getEndTime());
		this.historicProcessInstanceStartTime = dateFormat
				.format(historicProcessInstance.getStartTime());
		ProcessDefinition processDefinition = activitiService
				.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(
						historicProcessInstance.getProcessDefinitionId())
				.singleResult();
		this.processDefinitionName = processDefinition.getName();
		this.processDefinitionVersion = String.valueOf(processDefinition
				.getVersion());
		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(historicProcessInstance.getBusinessKey())
				.singleResult();
	}

	public User getApplyer() {
		return applyer;
	}

	public void setApplyer(User applyer) {
		this.applyer = applyer;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}

	public void setProcessDefinitionVersion(String processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Boolean getProcessInstanceSuspended() {
		return processInstanceSuspended;
	}

	public void setProcessInstanceSuspended(Boolean processInstanceSuspended) {
		this.processInstanceSuspended = processInstanceSuspended;
	}

	public String getHistoricProcessInstanceStartTime() {
		return historicProcessInstanceStartTime;
	}

	public void setHistoricProcessInstanceStartTime(
			String historicProcessInstanceStartTime) {
		this.historicProcessInstanceStartTime = historicProcessInstanceStartTime;
	}

	public String getHistoricProcessInstanceEndTime() {
		return historicProcessInstanceEndTime;
	}

	public void setHistoricProcessInstanceEndTime(
			String historicProcessInstanceEndTime) {
		this.historicProcessInstanceEndTime = historicProcessInstanceEndTime;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskCreateTime() {
		return taskCreateTime;
	}

	public void setTaskCreateTime(String taskCreateTime) {
		this.taskCreateTime = taskCreateTime;
	}

	public String getTaskAssignee() {
		return taskAssignee;
	}

	public void setTaskAssignee(String taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

}
