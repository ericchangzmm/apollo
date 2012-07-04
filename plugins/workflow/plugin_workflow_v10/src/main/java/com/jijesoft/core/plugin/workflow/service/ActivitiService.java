package com.jijesoft.core.plugin.workflow.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jijesoft.boh.core.workflow.manage.Workflow;
import com.jijesoft.core.plugin.workflow.dto.ActivitiInfo;
import com.jijesoft.core.plugin.workflow.service.export.ActivitiServiceSupport;

@Component
@Transactional(readOnly = true)
public class ActivitiService extends ActivitiServiceSupport {

	/**
	 * process definition list
	 * 
	 * @return
	 */
	public List<ActivitiInfo> processList() {
		List<ActivitiInfo> result = new ArrayList<ActivitiInfo>();

		List<ProcessDefinition> definitions = repositoryService
				.createProcessDefinitionQuery().list();
		for (ProcessDefinition pd : definitions) {
			result.add(new ActivitiInfo(this, pd));
		}

		return result;
	}

	/**
	 * load xxx.bpmn20.xml or xxx.png file by deployment
	 * 
	 * @param deploymentId
	 * @param resourceName
	 *            <ul>
	 *            <li>xxx.bpmn20.xml</li>
	 *            <li>xxx.png</li>
	 *            </ul>
	 * @return
	 */
	public InputStream loadByDeployment(String deploymentId, String resourceName) {
		Validate.notEmpty(deploymentId);
		Validate.notEmpty(resourceName);

		return repositoryService
				.getResourceAsStream(deploymentId, resourceName);
	}

	/**
	 * load xxx.bpmn20.xml or xxx.png file by processinstance
	 * 
	 * @param processInstanceId
	 * @param resourceType
	 *            <ul>
	 *            <li>xml</li>
	 *            <li>image</li>
	 *            </ul>
	 * @return
	 */
	public InputStream loadByProcessInstance(String processInstanceId,
			String resourceType) {
		Validate.notEmpty(processInstanceId);
		Validate.notEmpty(resourceType);

		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId())
				.singleResult();

		String resourceName = StringUtils.equals("xml", resourceType) ? processDefinition
				.getResourceName() : processDefinition.getDiagramResourceName();
		return loadByDeployment(processDefinition.getDeploymentId(),
				resourceName);
	}

	/**
	 * tasks to do
	 * 
	 * @param userId
	 * @return
	 */
	public List<ActivitiInfo> findTodoTasks(String userId) {
		Validate.notEmpty(userId);

		List<ActivitiInfo> result = new ArrayList<ActivitiInfo>();

		// by current user
		List<Task> todoList = taskService.createTaskQuery()
				.taskAssignee(userId).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		// to be claim by current user
		List<Task> unsignedTasks = taskService.createTaskQuery()
				.taskCandidateUser(userId).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		for (Task task : todoList) {
			result.add(new ActivitiInfo(this, task));
		}
		for (Task task : unsignedTasks) {
			result.add(new ActivitiInfo(this, task));
		}

		return result;
	}

	public Task getTaskById(String taskId) {
		Validate.notEmpty(taskId);
		return taskService.createTaskQuery().taskId(taskId).singleResult();
	}

	/**
	 * running process instances started by #{userId}
	 * 
	 * @param userId
	 * @return
	 */
	public List<ActivitiInfo> findRunningProcessInstaces(String userId) {
		Validate.notEmpty(userId);

		List<ActivitiInfo> result = new ArrayList<ActivitiInfo>();

		List<ProcessInstance> list = runtimeService
				.createProcessInstanceQuery()
				.processInstanceBusinessKey(userId).list();
		for (ProcessInstance processInstance : list) {
			result.add(new ActivitiInfo(this, processInstance));
		}

		return result;
	}

	/**
	 * finished process instances started by #{userId}
	 * 
	 * @param userId
	 * @return
	 */
	public List<ActivitiInfo> findFinishedProcessInstaces(String userId) {
		Validate.notEmpty(userId);

		List<ActivitiInfo> result = new ArrayList<ActivitiInfo>();

		List<HistoricProcessInstance> list = historyService
				.createHistoricProcessInstanceQuery().startedBy(userId)
				.finished().orderByProcessInstanceEndTime().desc().list();
		for (HistoricProcessInstance historicProcessInstance : list) {
			result.add(new ActivitiInfo(this, historicProcessInstance));
		}

		return result;
	}

	/**
	 * current activity image info[x, y, width, height]
	 * 
	 * @param processInstanceId
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> traceProcess(String processInstanceId)
			throws Exception {
		Validate.notEmpty(processInstanceId);

		Map<String, Object> activityImageInfo = new HashMap<String, Object>();

		Execution execution = runtimeService.createExecutionQuery()
				.executionId(processInstanceId).singleResult();

		Object property = PropertyUtils.getProperty(execution, "activityId");
		String activityId = property == null ? "" : property.toString();

		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) (((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance
						.getProcessDefinitionId()));

		// all activities
		List<ActivityImpl> activityList = processDefinition.getActivities();
		ActivityImpl currentActivity = null;
		for (ActivityImpl activityImpl : activityList) {
			// current activity
			if (StringUtils.equals(activityImpl.getId(), activityId)) {
				currentActivity = activityImpl;
				break;
			}
		}

		if (currentActivity != null) {
			activityImageInfo.put("x", currentActivity.getX());
			activityImageInfo.put("y", currentActivity.getY());
			activityImageInfo.put("width", currentActivity.getWidth());
			activityImageInfo.put("height", currentActivity.getHeight());
		}

		return activityImageInfo;
	}

	/**
	 * start new process instance
	 * 
	 * @param userId
	 * @param workflow
	 * @param reqMap
	 * @return
	 */
	@Transactional(readOnly = false)
	public ProcessInstance startWorkflow(String userId, Workflow workflow,
			Map<String, Object> reqMap) {
		Validate.notNull(workflow);
		Validate.notNull(reqMap);
		Validate.notEmpty(userId);

		Map<String, Object> parameters = workflow.beforeStartWorkflow(reqMap);
		ProcessInstance processInstance = super.startWorkflow(
				workflow.getKey(), userId, parameters);
		workflow.afterStartWorkflow(processInstance.getId(), parameters);
		return processInstance;
	}

	/**
	 * claim
	 * 
	 * @param taskId
	 * @param userId
	 * @param workflow
	 * @param reqMap
	 */
	@Transactional(readOnly = false)
	public void claim(String taskId, String userId, Workflow workflow,
			Map<String, Object> reqMap) {
		Validate.notNull(workflow);
		Validate.notNull(reqMap);
		Validate.notEmpty(userId);
		Validate.notEmpty(taskId);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		workflow.onClaimTask(reqMap, task.getTaskDefinitionKey());
		super.claim(taskId, userId);
	}

	/**
	 * complete
	 * 
	 * @param taskId
	 * @param workflow
	 * @param map
	 *            variables in current process execution
	 */
	@Transactional(readOnly = false)
	public void complete(String taskId, Workflow workflow,
			Map<String, Object> map) {
		Validate.notEmpty(taskId);
		Validate.notNull(workflow);
		Validate.notNull(map);

		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		super.complete(taskId, workflow.onCompleteTask(map,
				task.getTaskDefinitionKey(),
				runtimeService.getVariables(task.getExecutionId())));
	}
}
