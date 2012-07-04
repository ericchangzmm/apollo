package com.jijesoft.core.plugin.workflowDemo.service.acct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;

import org.activiti.engine.identity.Group;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jijesoft.apollo.base.beanvalidator.BeanValidators;
import com.jijesoft.apollo.base.utils.ConvertUtil;
import com.jijesoft.core.plugin.entity.IdEntity;
import com.jijesoft.core.plugin.workflow.service.export.ActivitiIdentityServiceSupport;
import com.jijesoft.core.plugin.workflowDemo.dao.acct.RoleDao;
import com.jijesoft.core.plugin.workflowDemo.dao.acct.UserDao;
import com.jijesoft.core.plugin.workflowDemo.dao.acct.UserRoleDao;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.Role;
import com.jijesoft.core.plugin.workflowDemo.entity.acct.User;

@Component
@Transactional(readOnly = true)
public class UserService extends ActivitiIdentityServiceSupport {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private Validator validator;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void saveUser(User user) {

		Validate.notNull(user);
		BeanValidators.validateWithException(validator, user);

		List<Long> roleIds = ConvertUtil.convertElementPropertyToList(
				user.getRoleList(), "id");
		if (user.getId() == null) {
			user.setId(userDao.insert(user));
		} else {
			userDao.update(user);
			userRoleDao.deleteUserRoleByUserId(user.getId());
		}
		for (Long roleId : roleIds) {
			userRoleDao.insertUserRole(user.getId(), roleId);
		}
		List<String> roleNames = ConvertUtil.convertElementPropertyToList(
				user.getRoleList(), "name");
		saveActivitiUser(user, roleNames);
	}

	@Transactional(readOnly = false)
	public void deleteUser(Long id) {

		Validate.notNull(id);

		userDao.delete(id);
		userRoleDao.deleteUserRoleByUserId(id);

		delActivitiUser(id);

	}

	public User selectByUserName(String userName) {

		Validate.notEmpty(userName);
		Map<String, Object> parames = new HashMap<String, Object>();
		parames.put("name", userName);
		List<User> users = (List<User>) userDao.select(parames);
		if (users == null || users.size() == 0)
			return null;
		return users.get(0);
	}

	public List<User> getAllUser() {
		return (List<User>) userDao.select(new HashMap<String, Object>());
	}

	public User getUserById(Long id) {
		Validate.notNull(id);
		User user = userDao.selectById(id);
		user.setRoleList(roleDao.selectRoleByUserId(id));
		return user;
	}

	@Transactional(readOnly = false)
	public void saveRole(Role role) {

		Validate.notNull(role);
		BeanValidators.validateWithException(validator, role);

		if (role.getId() == null) {
			role.setId(roleDao.insert(role));
		} else {
			roleDao.update(role);
		}
		saveActivitiGroup(role.getName(), role);
	}

	public List<Role> getAllRole() {
		return (List<Role>) roleDao.select(new HashMap<String, Object>());
	}

	public Role getRoleById(Long id) {
		Validate.notNull(id);
		return roleDao.selectById(id);
	}

	@Transactional(readOnly = false)
	public void deleteRole(Long id) {

		Validate.notNull(id);

		roleDao.delete(id);
		userRoleDao.deleteUserRoleByRoleId(id);

		delActivitiGroup(id);
	}

	@Override
	protected void cloneActivitiUser(IdEntity user,
			org.activiti.engine.identity.User activitiUser) {
		User theUser = (User) user;
		activitiUser.setEmail("");
		activitiUser.setFirstName(theUser.getName());
		activitiUser.setLastName("");
		activitiUser.setPassword("");
	}

	@Override
	protected void cloneActivitiGroup(IdEntity role, Group activitiGroup) {
		Role theRole = (Role) role;
		activitiGroup.setName(theRole.getName());
		activitiGroup.setType("");
	}

}
