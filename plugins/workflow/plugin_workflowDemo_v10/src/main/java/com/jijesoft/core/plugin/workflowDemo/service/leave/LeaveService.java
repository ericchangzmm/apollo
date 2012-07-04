package com.jijesoft.core.plugin.workflowDemo.service.leave;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jijesoft.apollo.base.beanvalidator.BeanValidators;
import com.jijesoft.apollo.base.dto.Variable;
import com.jijesoft.boh.core.workflow.workflowable.Workflowable;
import com.jijesoft.core.plugin.workflowDemo.dao.leave.LeaveDao;
import com.jijesoft.core.plugin.workflowDemo.entity.leave.Leave;

@Component
@Transactional(readOnly = true)
public class LeaveService implements Workflowable {

	@Autowired
	private LeaveDao leaveDao;

	@Autowired
	private Validator validator;

	@Transactional(readOnly = false)
	public Map<String, Object> beforeStartWorkflow(Map<String, Object> reqMap) {
		String userId = String.valueOf(reqMap.get("userId"));
		Leave leave = new Leave();
		leave.setUserId(userId);
		leave.setLeaveType(String.valueOf(reqMap.get("leaveType")));
		leave.setReason(String.valueOf(reqMap.get("reason")));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			leave.setStartTime(format.parse(String.valueOf(reqMap
					.get("startTime"))));
			leave.setEndTime(format.parse(String.valueOf(reqMap.get("endTime"))));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Long leaveId = saveLeave(leave);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("leaveId", leaveId);
		return variables;
	}

	@Transactional(readOnly = false)
	public void afterStartWorkflow(String processInstanceId,
			Map<String, Object> parameters) {
		Long leaveId = (Long) parameters.get("leaveId");
		Leave leave = new Leave();
		leave.setId(leaveId);
		leave.setProcessInstanceId(processInstanceId);

		leaveDao.update(leave);
	}

	public void onClaimTask(Map<String, Object> reqMap, String taskDefinitionKey) {

	}

	@Transactional(readOnly = false)
	public Map<String, Object> onCompleteTask(Map<String, Object> reqMap,
			String taskDefinitionKey, Map<String, Object> parameters) {
		Map<String, Object> result = new HashMap<String, Object>();
		if ("deptLeaderAudit".equals(taskDefinitionKey)
				|| "hrAudit".equals(taskDefinitionKey)) {
			Variable variable = new Variable();
			variable.setKeys(String.valueOf(reqMap.get("keys")));
			variable.setValues(String.valueOf(reqMap.get("values")));
			variable.setTypes(String.valueOf(reqMap.get("types")));
			result = variable.toMap();
		}
		if ("modifyApply".equals(taskDefinitionKey)) {
			boolean reApply = Boolean.getBoolean(String.valueOf(reqMap
					.get("reApply")));
			if (reApply) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Long leaveId = (Long) parameters.get("leaveId");
				Leave leave = new Leave();
				leave.setId(leaveId);
				leave.setLeaveType(String.valueOf(reqMap.get("leaveType")));
				leave.setReason(String.valueOf(reqMap.get("reason")));
				String startTimeStr = String.valueOf(reqMap.get("startTime"));
				String endTimeStr = String.valueOf(reqMap.get("endTime"));
				if (StringUtils.isNotBlank(startTimeStr))
					try {
						leave.setStartTime(format.parse(startTimeStr));
					} catch (ParseException e) {
						throw new RuntimeException("startTime error");
					}
				if (StringUtils.isNotBlank(endTimeStr))
					try {
						leave.setEndTime(format.parse(endTimeStr));
					} catch (ParseException e) {
						throw new RuntimeException("endTimeStr error");
					}
				saveLeave(leave);
			}
			result.put("reApply", reApply);
		}
		if ("reportBack".equals(taskDefinitionKey)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date realityStartTime;
			try {
				realityStartTime = format.parse(String.valueOf(reqMap
						.get("realityStartTime")));
			} catch (ParseException e) {
				throw new RuntimeException("realityStartTime error");
			}
			Date realityEndTime;
			try {
				realityEndTime = format.parse(String.valueOf(reqMap
						.get("realityEndTime")));
			} catch (ParseException e) {
				throw new RuntimeException("realityEndTime error");
			}
			Long leaveId = (Long) parameters.get("leaveId");
			Leave leave = new Leave();
			leave.setId(leaveId);
			leave.setRealityStartTime(realityStartTime);
			leave.setRealityEndTime(realityEndTime);
			leaveDao.update(leave);
		}
		return result;
	}

	public Object onTransactTask(Map<String, Object> reqMap,
			String taskDefinitionKey) {
		return null;
	}

	@Transactional(readOnly = false)
	public Long saveLeave(Leave entity) {
		Validate.notNull(entity);
		BeanValidators.validateWithException(validator, entity);

		return leaveDao.insert(entity);
	}

}
