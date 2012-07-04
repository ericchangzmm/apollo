package com.jijesoft.core.plugin.workflow;

import java.util.Map;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jijesoft.boh.core.workflow.manage.Workflow;
import com.jijesoft.boh.core.workflow.manage.WorkflowManager;
import com.jijesoft.core.plugin.basecontroller.service.IOperateService;
import com.jijesoft.core.plugin.workflow.service.ActivitiService;

@Component
public class WorkflowService implements IOperateService {

	@Autowired
	private ActivitiService activitiService;

	public Object getData(Map<String, Object> map) {

		String act = String.valueOf(map.get("function"));
		if (StringUtils.equals(act, "deploy"))
			return deploy(map);
		if (StringUtils.equals(act, "undeploy"))
			return undeploy(map);
		if (StringUtils.equals(act, "processList"))
			return processList(map);
		if (StringUtils.equals(act, "traceProcess"))
			return traceProcess(map);
		if (StringUtils.equals(act, "runningList"))
			return runningList(map);
		if (StringUtils.equals(act, "finishedList"))
			return finishedList(map);
		if (StringUtils.equals(act, "taskList"))
			return taskList(map);
		if (StringUtils.equals(act, "startWorkflow"))
			return startWorkflow(map);
		if (StringUtils.equals(act, "transact"))
			return transact(map);
		if (StringUtils.equals(act, "claim"))
			return claim(map);
		if (StringUtils.equals(act, "complete"))
			return complete(map);

		return null;
	}

	private Object deploy(Map<String, Object> map) {
		try {
			String workflowKey = String.valueOf(map.get("key"));
			Workflow workflow = WorkflowManager.get(workflowKey);
			activitiService.deploy(workflow.getDiagramInputStream());
			return "deploy success";
		} catch (Exception e) {
			e.printStackTrace();
			return "deploy error";
		}
	}

	private Object undeploy(Map<String, Object> map) {
		try {
			String deploymentId = String.valueOf(map.get("deploymentId"));
			String cascadeStr = String.valueOf(map.get("cascadeStr"));
			boolean cascade = StringUtils.isBlank(cascadeStr)
					|| StringUtils.equals("true", cascadeStr);
			activitiService.deleteDeployment(deploymentId, cascade);
			return "undeploy success";
		} catch (Exception e) {
			e.printStackTrace();
			return "undeploy error";
		}
	}

	private Object processList(Map<String, Object> map) {
		try {
			return activitiService.processList();
		} catch (Exception e) {
			e.printStackTrace();
			return "processList error";
		}
	}

	private Object traceProcess(Map<String, Object> map) {
		try {
			String pid = String.valueOf(map.get("pid"));
			return activitiService.traceProcess(pid);
		} catch (Exception e) {
			e.printStackTrace();
			return "traceProcess error";
		}
	}

	private Object runningList(Map<String, Object> map) {
		try {
			String userId = String.valueOf(map.get("userId"));
			return activitiService.findRunningProcessInstaces(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return "runningList error";
		}
	}

	private Object finishedList(Map<String, Object> map) {
		try {
			String userId = String.valueOf(map.get("userId"));
			return activitiService.findFinishedProcessInstaces(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return "finishedList error";
		}
	}

	private Object taskList(Map<String, Object> map) {
		try {
			String userId = String.valueOf(map.get("userId"));
			return activitiService.findTodoTasks(userId);
		} catch (Exception e) {
			e.printStackTrace();
			return "taskList error";
		}
	}

	private Object startWorkflow(Map<String, Object> map) {
		try {
			String userId = String.valueOf(map.get("userId"));
			String workflowKey = String.valueOf(map.get("key"));

			Workflow workflow = WorkflowManager.get(workflowKey);
			ProcessInstance processInstance = activitiService.startWorkflow(
					userId, workflow, map);

			return "流程已启动，流程ID：" + processInstance.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "startworkflow error";
		}
	}

	private Object transact(Map<String, Object> map) {
		String workflowKey = String.valueOf(map.get("key"));
		String taskId = String.valueOf(map.get("taskId"));
		Workflow workflow = WorkflowManager.get(workflowKey);
		Task task = activitiService.getTaskById(taskId);
		return (String) workflow.onTransactTask(map,
				task.getTaskDefinitionKey());
	}

	private Object claim(Map<String, Object> map) {
		try {
			String userId = String.valueOf(map.get("userId"));
			String workflowKey = String.valueOf(map.get("key"));
			String taskId = String.valueOf(map.get("taskId"));

			Workflow workflow = WorkflowManager.get(workflowKey);
			activitiService.claim(taskId, userId, workflow, map);
			return "claim success";
		} catch (Exception e) {
			e.printStackTrace();
			return "claim error";
		}
	}

	private Object complete(Map<String, Object> map) {
		try {
			String workflowKey = String.valueOf(map.get("key"));
			String taskId = String.valueOf(map.get("taskId"));
			Workflow workflow = WorkflowManager.get(workflowKey);

			activitiService.complete(taskId, workflow, map);
			return "complete sucess";
		} catch (Exception e) {
			e.printStackTrace();
			return "complete error";
		}
	}

}
