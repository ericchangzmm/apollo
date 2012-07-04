package com.jijesoft.core.plugin.workflow.service.export;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;

import com.jijesoft.core.plugin.entity.IdEntity;
import com.jijesoft.core.plugin.workflow.WorkflowException;

/**
 * 支持将系统自有的用户权限管理数据同步到Activiti Identify用户数据
 * 
 * @author eric.zhang
 * 
 */
public class ActivitiIdentityServiceSupport extends ActivitiServiceSupport {

	/**
	 * 需重写cloneActivitiUser方法
	 * 
	 * @param user
	 * @param groupIds
	 *            推荐用角色的英文名称
	 */
	protected void saveActivitiUser(IdEntity user, List<String> groupIds) {

		String s_userId = String.valueOf(user.getId());

		List<User> activitiUsers = identityService.createUserQuery()
				.userId(s_userId).list();

		if (activitiUsers.size() > 1) {
			throw new WorkflowException("repeat user: id=" + s_userId);
		}

		if (activitiUsers.size() == 0) {
			newActivitiUser(user, groupIds);
		} else {
			updateActivitiUser(user, groupIds, activitiUsers.get(0));
		}

	}

	protected void cloneActivitiUser(IdEntity user, User activitiUser) {
	}

	protected void cloneActivitiGroup(IdEntity role, Group activitiGroup) {
	}

	/**
	 * 需重写cloneActivitiGroup
	 * 
	 * @param groupId
	 *            作为activiti-group的主键 推荐使用role的英文名称
	 * @param role
	 */
	protected void saveActivitiGroup(String groupId, IdEntity role) {

		List<Group> activitiGroups = identityService.createGroupQuery()
				.groupId(groupId).list();

		if (activitiGroups.size() > 1) {
			throw new WorkflowException("repeat group : id=" + groupId);
		}

		Group activitiGroup = null;
		if (activitiGroups.size() == 0) {
			activitiGroup = identityService.newGroup(groupId);
		} else {
			activitiGroup = activitiGroups.get(0);
		}
		cloneActivitiGroup(role, activitiGroup);
		identityService.saveGroup(activitiGroup);
	}

	/**
	 * 用于系统删除用户时同步删除activiti用户
	 * 
	 * @param userId
	 *            用户id
	 */
	protected void delActivitiUser(Serializable userId) {

		identityService.deleteUser(String.valueOf(userId));
	}

	protected void delActivitiGroup(Serializable groupId) {

		identityService.deleteGroup(String.valueOf(groupId));
	}

	private void updateActivitiUser(IdEntity user, List<String> groupIds,
			User activitiUser) {

		String s_userId = String.valueOf(user.getId());

		cloneAndSaveActivitiUser(user, activitiUser);

		List<Group> activitiGroups = identityService.createGroupQuery()
				.groupMember(s_userId).list();
		for (Group group : activitiGroups) {
			identityService.deleteMembership(s_userId, group.getId());
		}

		addMembershipToIdentify(s_userId, groupIds);
	}

	private void newActivitiUser(IdEntity user, List<String> groupIds) {

		String s_userId = String.valueOf(user.getId());

		cloneAndSaveActivitiUser(user, identityService.newUser(s_userId));

		addMembershipToIdentify(s_userId, groupIds);
	}

	private void addMembershipToIdentify(String s_userId, List<String> roleIds) {
		for (Serializable roleId : roleIds) {
			identityService.createMembership(s_userId, String.valueOf(roleId));
		}
	}

	private void cloneAndSaveActivitiUser(IdEntity user, User activitiUser) {
		cloneActivitiUser(user, activitiUser);
		identityService.saveUser(activitiUser);
	}

}
