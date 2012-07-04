package com.jijesoft.core.plugin.workflow.dto;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import com.jijesoft.core.plugin.workflow.service.export.ActivitiServiceSupport;

@Deprecated
public class ActivitiInfoOld implements Serializable {

	private static final long serialVersionUID = -8858925550925552903L;

	private User applyer;

	private ProcessDefinition processDefinition;

	private ProcessInstance processInstance;

	private HistoricProcessInstance historicProcessInstance;

	private Task task;

	public ActivitiInfoOld() {
	}

	public ActivitiInfoOld(ActivitiServiceSupport activitiService, Task task) {
		this.task = task;
		this.processInstance = activitiService.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();
		this.processDefinition = activitiService.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(task.getProcessDefinitionId())
				.singleResult();
		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(this.processInstance.getBusinessKey()).singleResult();
		this.historicProcessInstance = null;
	}

	public ActivitiInfoOld(ActivitiServiceSupport activitiService,
			ProcessInstance processInstance) {
		this.processInstance = processInstance;
		this.processDefinition = activitiService.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId())
				.singleResult();
		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(this.processInstance.getBusinessKey()).singleResult();
		
		List<Task> tasks = activitiService.getTaskService().createTaskQuery()
				.processInstanceId(processInstance.getId())
				.orderByTaskCreateTime().desc().listPage(0, 1);
		this.task = tasks.get(0);
		this.historicProcessInstance = null;
	}

	public ActivitiInfoOld(ActivitiServiceSupport activitiService,
			HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
		this.processDefinition = activitiService
				.getRepositoryService()
				.createProcessDefinitionQuery()
				.processDefinitionId(
						this.historicProcessInstance.getProcessDefinitionId())
				.singleResult();
		this.applyer = activitiService.getIdentityService().createUserQuery()
				.userId(historicProcessInstance.getBusinessKey())
				.singleResult();
		this.processInstance = null;
		this.task = null;
	}

	public User getApplyer() {
		return applyer;
	}

	public void setApplyer(User applyer) {
		this.applyer = applyer;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public void setHistoricProcessInstance(
			HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
