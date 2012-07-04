package com.jijesoft.core.plugin.workflowDemo.entity.leave;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.jijesoft.core.plugin.entity.IdEntity;

public class Leave extends IdEntity {

	private static final long serialVersionUID = 6304847372185332886L;

	private String processInstanceId;
	private String userId;
	private String leaveType;
	private String reason;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date realityStartTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date realityEndTime;


	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getRealityStartTime() {
		return realityStartTime;
	}

	public void setRealityStartTime(Date realityStartTime) {
		this.realityStartTime = realityStartTime;
	}

	public Date getRealityEndTime() {
		return realityEndTime;
	}

	public void setRealityEndTime(Date realityEndTime) {
		this.realityEndTime = realityEndTime;
	}

}
