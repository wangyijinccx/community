package com.ipeaksoft.moneyday.admin.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipeaksoft.moneyday.admin.util.MD5Util;
import com.ipeaksoft.moneyday.core.dto.UserDTO;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.Role;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.RoleService;


@Controller
@RequestMapping(value = "/admin/user")
public class AdminUserController {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AdminUserService adminUserService;
	
	@Autowired
	private RoleService roleService;
 
	/**
	 * 添加一个用户，完成后返回用户列表页
	 * @param user
	 * @param userType
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/add_user")
	public String addUser(AdminUser user, int roleId, ModelMap map) {
 
		try {
			//填充user空的属性
			Date now = new Date();
			user.setCreateTime(now);
			user.setModifyTime(now);
			String password = user.getPassword().trim();
			password = MD5Util.md5(password);
			user.setPassword(password);
			user.setIsValid(1);
			//保存user并返回id
			int id = adminUserService.addUser(user);
			//如果添加成功
			if(id > 0) {
				map.put("success", true);
				map.put("msg", "添加用户成功！");
			} else { //如果添加失败
				map.put("success", false);
				map.put("msg", "添加用户失败！请与开发人员联系。");
			}
		} catch (Exception e) {
			logger.info("ERROR:", e);
		}
		//返回用户列表页
		return "user/super_admin_user_manager";
	}
	
	/**
	 * 根据ID获取一个用户并返回用户信息修改页
	 * @param id
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/get_user_by_id")
	public String getUserById(int id, ModelMap map) {
 
		AdminUser adminUser = adminUserService.getUserById(id);
		UserDTO userDTO = new UserDTO();
		BeanUtils.copyProperties(adminUser, userDTO);
		//设置该用户的角色ID到DTO
		Iterator<Role> it = adminUser.getUserRoles().iterator();
		int roleId = 0;
		if (it.hasNext()) {
			roleId = it.next().getId();
		}
		userDTO.setRoleId(roleId);
		map.put("user", userDTO);
		return "user/super_admin_edit_user_dialog";
	}
	
	/**
	 * 更新一个用户的信息，完成后返回用户列表页
	 * @param user
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/update_user")
	public String updateUser(UserDTO userDTO, ModelMap map) {
		
		AdminUser user = adminUserService.getUserById(userDTO.getId());
		String password = userDTO.getPassword().trim();
		//如果密码有修改
		if(password != "") {
			password = MD5Util.md5(password);
			user.setPassword(password);
		}
		//修改角色
		int roleId = userDTO.getRoleId();
		Role role = roleService.getRoleById(roleId);
		user.setUserRoles(new HashSet<Role>());
		user.getUserRoles().add(role);
		user.setUsername(userDTO.getUsername());
		user.setCompanyName(userDTO.getCompanyName());
		user.setLinkman(userDTO.getLinkman());
		user.setPhone(userDTO.getPhone());
		user.setComment(userDTO.getComment());
		int effectRows = adminUserService.updateUser(user);
		//如果修改成功
		if(effectRows > 0) {
			map.put("success", true);
			map.put("msg", "修改用户成功！");
		} else { //如果修改失败
			map.put("success", false);
			map.put("msg", "修改用户失败！请与开发人员联系。");
		}
		//返回用户列表页
		return "user/super_admin_user_manager";
	}
	
	/**
	 * 删除一个用户，完成后返回用户列表页
	 * @param user
	 * @param userType
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/delete_user")
	public String deleteUser(int id, ModelMap map) {
		
		int effectRows = adminUserService.setUserInvalid(id);
		//如果删除成功
		if(effectRows > 0) {
			map.put("success", true);
			map.put("msg", "删除用户成功！");
		} else { //如果删除失败
			map.put("success", false);
			map.put("msg", "删除用户失败！请与开发人员联系。");
		}
		//返回用户列表页
		return "user/super_admin_user_manager";
	}
	
	/**
	 * 展示管理员用户列表
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/list_admin")
	public String listAdmin(ModelMap map) {
		
		List<AdminUser> admins = adminUserService.getValidAdmin();
		map.put("admins", admins);
		return "user/module_admin_list";
	}
	
	/**
	 * 密码重置
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/resetpassword", method = { RequestMethod.POST })
	@SuppressWarnings("null")
	public String Account_RestPassword(HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"重置成功\"}";
		try {
			String id = request.getParameter("id");
			String password = "123456";// request.getParameter("password");
			if (id == null || id.length() <= 0) {
				return "{\"status\":false,\"msg\":\"用户ID不能为空\"}";
			}
			if (password == null || password.length() <= 0) {
				return "{\"status\":false,\"msg\":\"新密码不能为空\"}";
			}
			AdminUser userInfo = adminUserService.getUserById(Integer.parseInt(id));

			if (userInfo == null) {
				return "{\"status\":false,\"msg\":\"该用户不存在\"}";
			}
			userInfo.setPassword(MD5Util.md5(password));
			if (adminUserService.updateUser(userInfo) < 1) {
				result = "{\"status\":false,\"msg\":\"重置失败\"}";
			}

		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"重置异常\"}";
		}
		return result;
	}
	
	/**
	 * 修改密码
	 * @param map
	 * @return
	 */
	@RequestMapping(value = "/change_password")
	public String changePassword(Integer userId, String password, ModelMap map) {
		
		AdminUser user = new AdminUser();
		user.setId(userId);
		user.setPassword(MD5Util.md5(password));
		int effectRows = adminUserService.updateUser(user);
		//如果修改成功
		if(effectRows > 0) {
			map.put("success", true);
			map.put("message", "修改密码成功！");
		} else { //如果修改失败
			map.put("success", false);
			map.put("message", "修改密码失败！请与开发人员联系。");
		}
		return "/common/change_password";
	}

}