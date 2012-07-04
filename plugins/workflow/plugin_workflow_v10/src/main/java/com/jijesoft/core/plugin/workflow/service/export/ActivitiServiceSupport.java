package com.jijesoft.core.plugin.workflow.service.export;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * support service for activiti5
 * 
 * @author eric.zhang
 * 
 */
public abstract class ActivitiServiceSupport {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Provides access to all the services that expose the BPM and workflow
	 * operations.
	 */
	protected ProcessEngine processEngine;

	/**
	 * Service providing access to the repository of process definitions and
	 * deployments.
	 */
	protected RepositoryService repositoryService;

	/**
	 * Service which provides access to {@link Deployment}s,
	 * {@link ProcessDefinition}s and {@link ProcessInstance}s.
	 */
	protected RuntimeService runtimeService;

	/**
	 * Access to form data and rendered forms for starting new process instances
	 * and completing tasks.
	 */
	protected FormService formService;

	/**
	 * Service to manage {@link User}s and {@link Group}s.
	 */
	protected IdentityService identityService;

	/**
	 * Service which provides access to {@link Task} and form related
	 * operations.
	 */
	protected TaskService taskService;

	/**
	 * Service exposing information about ongoing and past process instances.
	 * This is different from the runtime information in the sense that this
	 * runtime information only contains the actual runtime state at any given
	 * moment and it is optimized for runtime process execution performance. The
	 * history information is optimized for easy querying and remains permanent
	 * in the persistent storage.
	 */
	protected HistoryService historyService;

	/**
	 * Service for admin and maintenance operations on the process engine.
	 */
	protected ManagementService managementService;

	/**
	 * deploy process, only support zip file
	 * 
	 * @param inputStream
	 * @return
	 */
	@Transactional(readOnly = false)
	public String deploy(InputStream inputStream) {
		ZipInputStream zis = new ZipInputStream(inputStream);
		return repositoryService.createDeployment().addZipInputStream(zis)
				.deploy().getId();
	}

	/**
	 * delete process deployment
	 * 
	 * @param deploymentId
	 * @param cascade
	 */
	@Transactional(readOnly = false)
	public void deleteDeployment(String deploymentId, boolean cascade) {
		Validate.notEmpty(deploymentId);
		repositoryService.deleteDeployment(deploymentId, cascade);
		logger.debug("delete deployment: id=" + deploymentId + "; cascade is"
				+ cascade);
	}

	/**
	 * start new process instance, the businesskey is the applyer id
	 * 
	 * @param workflowKey
	 * @param createrId
	 * @param variables
	 * @return
	 */
	protected ProcessInstance startWorkflow(String workflowKey,
			Serializable createrId, Map<String, Object> variables) {

		String s_createrId = String.valueOf(createrId);

		// Activiti set creater
		identityService.setAuthenticatedUserId(s_createrId);

		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey(workflowKey, s_createrId, variables);

		return processInstance;
	}

	/**
	 * complete task
	 * 
	 * @param taskId
	 * @param variables
	 */
	protected void complete(String taskId, Map<String, Object> variables) {
		taskService.complete(taskId, variables);
		logger.debug("complete task: taskId=" + taskId);
	}

	/**
	 * claim task
	 * 
	 * @param taskId
	 * @param userId
	 */
	protected void claim(String taskId, String userId) {
		taskService.claim(taskId, userId);
		logger.debug("claim task: taskId=" + taskId + "; userId=" + userId);
	}

	@Autowired
	public final void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;

		repositoryService = processEngine.getRepositoryService();
		runtimeService = processEngine.getRuntimeService();
		formService = processEngine.getFormService();
		identityService = processEngine.getIdentityService();
		taskService = processEngine.getTaskService();
		historyService = processEngine.getHistoryService();
		managementService = processEngine.getManagementService();
	}

	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	public RuntimeService getRuntimeService() {
		return runtimeService;
	}

	public FormService getFormService() {
		return formService;
	}

	public IdentityService getIdentityService() {
		return identityService;
	}

	public TaskService getTaskService() {
		return taskService;
	}

	public HistoryService getHistoryService() {
		return historyService;
	}

	public ManagementService getManagementService() {
		return managementService;
	}

}
