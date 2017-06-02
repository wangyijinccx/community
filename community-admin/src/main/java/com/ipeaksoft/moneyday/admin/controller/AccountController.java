package com.ipeaksoft.moneyday.admin.controller;

import java.net.URLDecoder;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.admin.util.JsonTransfer;
import com.ipeaksoft.moneyday.admin.util.MD5Util;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.Competitor;
import com.ipeaksoft.moneyday.core.entity.TaskFast;
import com.ipeaksoft.moneyday.core.entity.User;
import com.ipeaksoft.moneyday.core.entity.UserLoginException;
import com.ipeaksoft.moneyday.core.entity.UserTask;
import com.ipeaksoft.moneyday.core.mapper.UserLoginExceptionMapper;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.CompetitorService;
import com.ipeaksoft.moneyday.core.service.UserService;

@Controller
@RequestMapping(value = "/account")
public class AccountController extends BaseController {
	@Autowired
	private UserService UserService;
	@Autowired
	private AdminUserService AdminUserService;
	@Autowired
	private UserLoginExceptionMapper userLoginExceptionService;
	@Autowired
	private CompetitorService competitorService;

	@RequestMapping(value = "/search", method = { RequestMethod.GET })
	public String search(ModelMap map, Principal principal, HttpServletRequest request)
			throws Exception {
		try {
			String key = request.getParameter("key");
			map.put("keyword", key);
			return "/account/search";
		} catch (Exception ex) {
			throw ex;
		}
	}

	@RequestMapping(value = "/exception")
	public String Exception(ModelMap map, Principal principal, HttpServletRequest request) {
		return "/account/exception_account";
	}

	@RequestMapping(value = "/exception/detail")
	public ModelAndView Exception_detail(ModelMap map, Principal principal,
			HttpServletRequest request) throws Exception {
		try {
			ModelAndView mv = new ModelAndView();
			String mobile = request.getParameter("mobile");
			String id = request.getParameter("id");
			mv.addObject("id", id);
			if (mobile != null && !mobile.equals("")) {
				mv.addObject("mobile", mobile);
			} else {
				throw new Exception("UserId 不能为空");
			}
			mv.setViewName("/account/exception_account_log");
			return mv;
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 推广员列表
	 * 
	 * @param map
	 * @param principal
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/list")
	public ModelAndView Account_List(ModelMap map, Principal principal, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String pid = request.getParameter("pid");
		if (pid != null && !pid.equals("")) {
			mv.addObject("pid", pid);
			mv.setViewName("/account/list");
		} else {
			AdminUser au = getUser();
			if(3 == au.getLevel()) {
				mv.addObject("pid", au.getId());
				mv.setViewName("/account/member_list");
			}
		}
		return mv;
	}

	/**
	 * 普通会员列表
	 * 
	 * @param map
	 * @param principal
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/memberlist")
	public ModelAndView Account_Member_List(ModelMap map, Principal principal,
			HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		AdminUser userSession = getUser();
		if (userSession != null) {
			mv.addObject("uid", userSession.getId());
		}
		String pid = request.getParameter("pid");
		if (pid != null && !pid.equals("")) {
			mv.addObject("pid", pid);
		}
		mv.setViewName("/account/member_list");
		return mv;
	}

	@RequestMapping(value = "/create", method = { RequestMethod.GET })
	public String Account_Create(ModelMap map, Principal principal, HttpServletRequest request) {
		AdminUser sessionUser = getUser();
		String level = "";
		if (sessionUser.getLevel() == -1) {
			level = "运维人员";
		} else if (sessionUser.getLevel() == 0) {
			level = "一级推广员";
		} else if (sessionUser.getLevel() == 1) {
			level = "二级推广员";
		} else if (sessionUser.getLevel() == 2) {
			level = "三级推广员";
		}
		map.addAttribute("level", level);
		return "/account/create_account";
	}

	@RequestMapping(value = "/UserDetail")
	public ModelAndView UserDetail(TaskFast task, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		try {
			// 加载用户信息
			Long userId = Long.parseLong(request.getParameter("id"));
			User userInfo = UserService.getUserById(userId);
			// 加载用户竞品信息
			List<Competitor> list = competitorService.findByMobile(userInfo.getMobile());
			mv.addObject("competitorList", list);
			mv.addObject("userInfo", userInfo);

			AdminUser sessionUser = getUser();
			mv.addObject("sessionUserId", sessionUser.getId());
			mv.setViewName("/search/search_member_detail");
		} catch (Exception ex) {
			throw ex;
		}
		return mv;
	}

	@RequestMapping(value = "/AccountDetail")
	public ModelAndView AccountDetail(TaskFast task, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		try {
			// 加载用户信息
			int userId = Integer.parseInt(request.getParameter("id"));
			AdminUser userInfo = AdminUserService.getUserById(userId);
			mv.addObject("userInfo", userInfo);

			AdminUser sessionUser = getUser();
			mv.addObject("sessionUserId", sessionUser.getId());
			mv.setViewName("/account/account_detail");
		} catch (Exception ex) {
			throw ex;
		}
		return mv;
	}

	/**
	 * 更新推广员帐号
	 * 
	 * @param userRecord
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/AccountUpdate", method = (RequestMethod.POST))
	public String AccountUpdate(AdminUser userRecord, HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"更新成功\"}";
		try {
			AdminUser userInfo = AdminUserService.getUserById(userRecord.getId());
			if (userInfo == null) {
				result = "{\"status\":false,\"msg\":\"该用户不存在\"}";
			} else {
				AdminUser userSession = getUser();
				
				if (!userSession.getId().equals(userInfo.getPid()) && 4 != userInfo.getLevel()) {
					result = "{\"status\":false,\"msg\":\"您没有权限修改次用户信息\"}";
					return result;
				}
				if (userRecord.getUsername() == null || userRecord.getUsername().equals("")) {
					result = "{\"status\":false,\"msg\":\"用户名不能为空\"}";
					return result;
				}
				if (userRecord.getCompanyName() == null || userRecord.getCompanyName().equals("")) {
					result = "{\"status\":false,\"msg\":\"公司名称不能为空\"}";
					return result;
				}
				if (userRecord.getLinkman() == null || userRecord.getLinkman().equals("")) {
					result = "{\"status\":false,\"msg\":\"联系人不能为空\"}";
					return result;
				}
				if (userRecord.getPhone() == null || userRecord.getPhone().equals("")) {
					result = "{\"status\":false,\"msg\":\"电话不能为空\"}";
					return result;
				}
				AdminUser tempUser =  AdminUserService.getUserByName(userRecord.getUsername());
				if(!userInfo.getUsername().equals(userRecord.getUsername()) && null != tempUser) {
					result = "{\"status\":false,\"msg\":\"用户名已经存在，请换个新的用户名\"}";
					return result;
				}

				userInfo.setUsername(userRecord.getUsername());
				userInfo.setCompanyName(userRecord.getCompanyName());
				userInfo.setLinkman(userRecord.getLinkman());
				userInfo.setPhone(userRecord.getPhone());
				userInfo.setPid(userRecord.getPid());
				userInfo.setComment(userRecord.getComment());

				if (AdminUserService.updateUser(userInfo) < 0) {
					result = "{\"status\":false,\"msg\":\"更新失败\"}";
				}
			}
		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"更新异常\"}";
		}
		return result;
	}

	/**
	 * 加载异常账号数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/data_load_exception_account")
	public String Load_Exception_Account(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String sEcho = request.getParameter("draw");// 搜索内容
		String order = request.getParameter("order[0][column]");
		int limit = start + pageSize;
		int total = 100;
		if (limit >= total - pageSize) {
			limit = total;
		}
		StringBuilder rs = new StringBuilder();
		for (int i = start; i < limit; i++) {
			if (!rs.toString().equals("")) {
				rs.append(",");
			}
			rs.append("{\"pkid\":\"" + i + 1 + "\",\"date\":\"2014-12-19\",\"newuser\":\"" + i + 2
					+ "\",\"visitor\":\"" + i + 3 + "\",\"alluser\":\"" + i + 4
					+ "\",\"visitortouser\":\"" + i + 5 + "\",\"pecent\":\"" + i + 6
					+ "\",\"ptzgj\":\"" + i + 7 + "\",\"ptzgjpecent\":\"" + i + 8
					+ "\",\"loginactive\":\"" + i + 9 + "\",\"downloadactive\":\"" + i + 10 + "\"}");
		}

		String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize
				+ ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
		return result;

	}

	/**
	 * 加载异常账号日志详细
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/data_load_exception_account_log")
	public String Load_Exception_Account_Log(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String sEcho = request.getParameter("draw");// 搜索内容
		String order = request.getParameter("order[0][column]");
		int limit = start + pageSize;
		int total = 100;
		if (limit >= total - pageSize) {
			limit = total;
		}
		StringBuilder rs = new StringBuilder();
		for (int i = start; i < limit; i++) {
			if (!rs.toString().equals("")) {
				rs.append(",");
			}
			rs.append("{\"pkid\":\"" + i + 1 + "\",\"date\":\"2014-12-19\",\"newuser\":\"" + i + 2
					+ "\",\"visitor\":\"" + i + 3 + "\",\"alluser\":\"" + i + 4
					+ "\",\"visitortouser\":\"" + i + 5 + "\",\"pecent\":\"" + i + 6
					+ "\",\"ptzgj\":\"" + i + 7 + "\",\"ptzgjpecent\":\"" + i + 8
					+ "\",\"loginactive\":\"" + i + 9 + "\",\"downloadactive\":\"" + i + 10 + "\"}");
		}

		String result = "{\"draw\":" + sEcho + ",\"recordsTotal\":" + pageSize
				+ ",\"recordsFiltered\":" + total + ",\"data\":[" + rs.toString() + "]}";
		return result;
	}

	/**
	 * 加载推广员用户数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/Load_UserList")
	public Object Load_UserList(HttpServletRequest request) {
		
		// 获取当前登录用户信息
		AdminUser adminSession = getUser();
		if (adminSession == null) {
			return "";
		}

		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String sEcho = request.getParameter("draw");// 搜索内容
		String searchContent = request.getParameter("search[value]");
		String orderColumnIndex = request.getParameter("order[0][column]");
		String orderDir = request.getParameter("order[0][dir]");
		String pid = request.getParameter("pid");// 上一级用户ID
		String ColumnName = null;
		if (orderColumnIndex != null && !orderColumnIndex.equals("") && orderDir != null
				&& !orderDir.equals("")) {
			switch (orderColumnIndex) {
			case "0":
				ColumnName = " create_time " + orderDir;
				break;
			case "1":
				ColumnName = " username " + orderDir;
				break;
			case "2":
				ColumnName = " level " + orderDir;
				break;
			case "3":
				ColumnName = " company_name " + orderDir;
				break;
			case "4":
				ColumnName = " phone " + orderDir;
				break;
			case "5":
				ColumnName = " comment " + orderDir;
				break;
			}
		}
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String area = request.getParameter("area");

		int total = 100;

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("keys", searchContent.equals("") ? null : searchContent);

		where.put("start", start);
		where.put("limit", pageSize);
		// where.put("province", province.equals("") ? null : province);
		// where.put("city", city.equals("") ? null : city);
		// where.put("area", area.equals("") ? null : area);
		where.put("ordeStr", ColumnName);
		if (pid != null && !pid.equals("")) {
			where.put("pid", pid);
		} else {
			where.put("pid", adminSession.getId());
		}
		List<AdminUser> list = AdminUserService.findPageUserByWhere(where);
		if (searchContent != null && !searchContent.equals("")) {
			for (AdminUser item : list) {
				item.setUsername(item.getUsername().replace(searchContent,
						"<font style=\"color:red;\">" + searchContent + "</font>"));
			}
		}
		total = AdminUserService.findPageUserByWhereCount(where);
//		String rs = JsonTransfer.getJsonFromList(searchContent, list);
//		String result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":" + pageSize
//				+ ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
		JSONObject result = new JSONObject();
		result.put("draw", "");
		result.put("recordsTotal", total);
		result.put("recordsFiltered", total);
		result.put("data", list);
		return result;
	}

	/**
	 * 加载推普通用户数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/Load_MemberUserList")
	public Object Load_MemberUserList(HttpServletRequest request) {
		// 获取当前登录用户信息
		AdminUser adminSession = getUser();
		if (adminSession == null) {
			return "";
		}

		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String sEcho = request.getParameter("draw");// 搜索内容
		String searchContent = request.getParameter("search[value]");
		String orderColumnIndex = request.getParameter("order[0][column]");
		String orderDir = request.getParameter("order[0][dir]");
		String pid = request.getParameter("pid");// 上级用户ID
		String ColumnName = null;
		if (orderColumnIndex != null && !orderColumnIndex.equals("") && orderDir != null
				&& !orderDir.equals("")) {
			switch (orderColumnIndex) {
			case "0":
				ColumnName = " create_time " + orderDir;
				break;
			case "1":
				ColumnName = " username " + orderDir;
				break;
			case "2":
				ColumnName = " type " + orderDir;
				break;
			case "3":
				ColumnName = " name " + orderDir;
				break;
			case "4":
				ColumnName = " mobile " + orderDir;
				break;
			case "5":
				ColumnName = " province " + orderDir + " ,city " + orderDir + " ,area " + orderDir;
				break;
			case "6":
				ColumnName = " taskfast " + orderDir;
				break;
			case "7":
				ColumnName = " taskads " + orderDir;
				break;
			case "8":
				ColumnName = " competitorNum " + orderDir;
				break;
			}
		}
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String area = request.getParameter("area");

		int total = 100;

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("keys", searchContent);

		where.put("currentPage", start);
		where.put("pageSize", pageSize);
		where.put("province", province.equals("") ? null : province);
		where.put("city", city.equals("") ? null : city);
		where.put("area", area.equals("") ? null : area);
		if (pid != null && !pid.equals("")) {
			where.put("pid", pid);
		} else {
			where.put("pid", adminSession.getId());
		}
		where.put("ordeStr", ColumnName);
		List<User> list = UserService.findPageByWhere(where);
		if (searchContent != null && !searchContent.equals("")) {
			for (User item : list) {
				item.setUsername(item.getUsername().replace(searchContent,
						"<font style=\"color:red;\">" + searchContent + "</font>"));
			}
		}
		total = UserService.findPageByWhereCount(where);
//		String rs = JsonTransfer.getJsonFromList(searchContent, list);
//		String result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":" + pageSize
//				+ ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
//		logger.info("request url:{}, result:{}", request.getRequestURI(), result);
		JSONObject result = new JSONObject();
		result.put("draw", "");
		result.put("recordsTotal", total);
		result.put("recordsFiltered", total);
		result.put("data", list);
		return result;
	}
	
	/**
	 * 加载vip账号管理员用户数据
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/Load_Weight_Account_Manager_List")
	public String Load_Weight_Account_Manager_List(HttpServletRequest request) {
		
		// 获取当前登录用户信息
		AdminUser adminSession = getUser();
		if (adminSession == null) {
			return "";
		}

		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String searchContent = request.getParameter("search[value]");
		String orderColumnIndex = request.getParameter("order[0][column]");
		String orderDir = request.getParameter("order[0][dir]");
		String ColumnName = null;
		if (orderColumnIndex != null && !orderColumnIndex.equals("") && orderDir != null
				&& !orderDir.equals("")) {
			switch (orderColumnIndex) {
			case "0":
				ColumnName = " create_time " + orderDir;
				break;
			case "1":
				ColumnName = " username " + orderDir;
				break;
			case "2":
				ColumnName = " level " + orderDir;
				break;
			case "3":
				ColumnName = " company_name " + orderDir;
				break;
			case "4":
				ColumnName = " phone " + orderDir;
				break;
			case "5":
				ColumnName = " comment " + orderDir;
				break;
			}
		}

		int total = 100;

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("level", 4);
		where.put("keys", searchContent.equals("") ? null : searchContent);
		where.put("start", start);
		where.put("limit", pageSize);
		where.put("ordeStr", ColumnName);
		List<AdminUser> list = AdminUserService.findPageUserByWhere(where);
		if (searchContent != null && !searchContent.equals("")) {
			for (AdminUser item : list) {
				item.setUsername(item.getUsername().replace(searchContent,
						"<font style=\"color:red;\">" + searchContent + "</font>"));
			}
		}
		total = AdminUserService.findPageUserByWhereCount(where);
		String rs = JsonTransfer.getJsonFromList(searchContent, list);
		String result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":" + pageSize
				+ ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
		return result;

	}


	@ResponseBody
	@RequestMapping(value = "/create", method = { RequestMethod.POST })
	public String Account_Add(AdminUser record, HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"添加成功\"}";
		AdminUser sessionUser = getUser();
		byte level = 0;
		if (sessionUser.getLevel() == -1) {
			level = 0;
		} else if (sessionUser.getLevel() == 0) {
			level = 1;
		} else if (sessionUser.getLevel() == 1) {
			level = 2;
		} else if (sessionUser.getLevel() == 2) {
			level = 3;
		} else {
			return "{\"status\":false,\"msg\":\"您没有权限创建用户\"}";
		}
		record.setLevel(level);
		record.setCreateTime(new Date());// 指定用户创建时间
		record.setModifyTime(new Date());
		record.setPassword(MD5Util.md5(record.getPassword())); // 加密密码
		record.setIsValid(1);
		record.setPid(sessionUser.getId());

		try {
			AdminUser existInfo = AdminUserService.getUserByName(record.getUsername());
			if (existInfo != null) {
				return "{\"status\":false,\"msg\":\"用户名已经存在\"}";
			}
			if (AdminUserService.addUser(record) < 1) {
				result = "{\"status\":false,\"msg\":\"添加失败\"}";
			}
		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"添加失败\"}";
		}
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/create_weight_account_manager", method = { RequestMethod.POST })
	public String Weight_Account_Manager_Add(AdminUser record, HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"添加成功\"}";
		record.setCreateTime(new Date());// 指定用户创建时间
		record.setModifyTime(new Date());
		record.setPassword(MD5Util.md5(record.getPassword())); // 加密密码
		record.setIsValid(1);
		byte level = 4;
		record.setLevel(level);
		record.setPid(-1);

		try {
			AdminUser existInfo = AdminUserService.getUserByName(record.getUsername());
			if (existInfo != null) {
				return "{\"status\":false,\"msg\":\"用户名已经存在\"}";
			}
			if (AdminUserService.addUser(record) < 1) {
				result = "{\"status\":false,\"msg\":\"添加失败\"}";
			}
		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"添加失败\"}";
		}
		return result;
	}

	/**
	 * 更新账户
	 * 
	 * @param record
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public String Account_Update(User record, HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"更新成功\"}";

		try {
			// 根据ID获取数据库中的对象

			User userInfo = UserService.getUserById(record.getId());
			if (userInfo == null) {
				return "{\"status\":false,\"msg\":\"不存在该用户\"}";
			}
//			if (record.getName() == null || record.getName().length() <= 0) {
//				return "{\"status\":false,\"msg\":\"姓名不能为空\"}";
//			}
			if (record.getpUser() == null || record.getpUser().length() <= 0) {
				return "{\"status\":false,\"msg\":\"上级用户不能为空\"}";
			}
			if (record.getMobile() == null || record.getMobile().length() <= 0) {
				return "{\"status\":false,\"msg\":\"电话不能为空\"}";
			}
//			if (record.getAddress() == null || record.getAddress().length() <= 0) {
//				return "{\"status\":false,\"msg\":\"地址不能为空\"}";
//			}
//			if (record.getProvince() == null || record.getProvince().length() <= 0) {
//				return "{\"status\":false,\"msg\":\"省不能为空\"}";
//			}
//			if (record.getCity() == null || record.getCity().length() <= 0) {
//				return "{\"status\":false,\"msg\":\"市不能为空\"}";
//			}
//			if (record.getArea() == null || record.getArea().length() <= 0) {
//				return "{\"status\":false,\"msg\":\"区不能为空\"}";
//			}
			if (!record.getUsername().equals(userInfo.getUsername())) {
				User existUserName = UserService.getUserByName(record.getUsername());
				if (existUserName != null) {
					return "{\"status\":false,\"msg\":\"用户名已存在\"}";
				}
			}
			userInfo.setUsername(record.getUsername());
			userInfo.setName(record.getName());
			userInfo.setpUser(record.getpUser());
			userInfo.setMobile(record.getMobile());
			userInfo.setAddress(record.getAddress());
			userInfo.setProvince(record.getProvince());
			userInfo.setCity(record.getCity());
			userInfo.setArea(record.getArea());
			if (UserService.updateUser(userInfo) < 1) {
				result = "{\"status\":false,\"msg\":\"更新失败\"}";
			}
		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"更新失败\"}";
		}
		return result;
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
			User userInfo = UserService.getUserById(Long.parseLong(id));

			if (userInfo == null) {
				return "{\"status\":false,\"msg\":\"该用户不存在\"}";
			}
			userInfo.setPasswd(MD5Util.md5(password));
			if (UserService.updateUser(userInfo) < 1) {
				result = "{\"status\":false,\"msg\":\"重置失败\"}";
			}

		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"重置异常\"}";
		}
		return result;
	}

	/**
	 * 踢出用户
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/kickout", method = { RequestMethod.POST })
	@SuppressWarnings("null")
	public String kickOut(HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"操作成功\"}";
		try {
			String id = request.getParameter("id");
			if (id == null && id.length() <= 0) {
				return "{\"status\":true,\"msg\":\"用户ID不能为空\"}";
			}
			User userInfo = UserService.getUserById(Long.parseLong(id));
			if (userInfo == null) {
				return "{\"status\":false,\"msg\":\"用户不存在\"}";
			}
			userInfo.setpUser("");
			if (UserService.updateUser(userInfo) <= 0) {
				return "{\"status\":false,\"msg\":\"操作失败\"}";
			}

		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"操作异常\"}";
		}

		return result;
	}

	/**
	 * 重置帐号
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/resetaccount", method = { RequestMethod.POST })
	@SuppressWarnings("null")
	public String resetAccount(HttpServletRequest request) {
		String result = "{\"status\":true,\"msg\":\"重置成功\"}";
		try {
			String id = request.getParameter("id");
			if (id == null && id.length() <= 0) {
				return "{\"status\":true,\"msg\":\"用户ID不能为空\"}";
			}
			User userInfo = UserService.getUserById(Long.parseLong(id));
			if (userInfo == null) {
				return "{\"status\":false,\"msg\":\"用户不存在\"}";
			}
			// 重置动作，待实现---begin

			// 重置动作，待实现---end

		} catch (Exception ex) {
			result = "{\"status\":false,\"msg\":\"重置异常\"}";
		}

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/search", method = { RequestMethod.POST })
	public String searchByKey(ModelMap map, Principal principal, HttpServletRequest request)
			throws Exception {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String sEcho = request.getParameter("draw");// 搜索内容
		String searchContent = request.getParameter("search");
		String order = request.getParameter("order[0][column]");
		searchContent = URLDecoder.decode(request.getParameter("keyword"), "utf-8");

		Map<String, Object> where = new HashMap<String, Object>();
		where.put("keys", searchContent);

		where.put("currentPage", start);
		where.put("pageSize", pageSize);

		List<User> list = UserService.findPageByWhere(where);
		for (User item : list) {
			item.setUsername(item.getUsername().replace(searchContent,
					"<font style=\"color:red;\">" + searchContent + "</font>"));
		}
		
		int total = UserService.findPageByWhereCount(where);
		pageSize = (pageSize>total) ?total : pageSize;
		String rs = JsonTransfer.getJsonFromList(searchContent, list);
		String result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":" + pageSize
				+ ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
		return result;
	}

	/**
	 * 获取异常帐号
	 * 
	 * @param map
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/getExceptionAccountList", method = { RequestMethod.POST })
	public String getExceptionAccountList(ModelMap map, HttpServletRequest request)
			throws Exception {
		try {

			int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
			int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
			String sEcho = request.getParameter("draw");// 搜索内容
			String searchContent = request.getParameter("search");
			String order = request.getParameter("order[0][column]");

			int total = 0;

			Map<String, Object> where = new HashMap<String, Object>();

			where.put("currentPage", start);
			where.put("pageSize", pageSize);
			List<UserLoginException> list = userLoginExceptionService.findPage(where);
			total = userLoginExceptionService.findPageCount();
			String rs = JsonTransfer.getJsonFromList(searchContent, list);
			String result = "{\"draw\":\"" + searchContent + "\",\"recordsTotal\":" + pageSize
					+ ",\"recordsFiltered\":" + total + ",\"data\":" + rs + "}";
			return result;
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 用户名唯一性验证
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/ValidatorUserName", method = { RequestMethod.POST })
	public String ValidatorUserName(HttpServletRequest request) {
		String userName = request.getParameter("username");
		AdminUser modeInfo = adminUserService.getUserByName(userName);
		if (modeInfo != null) {
			return "{\"valid\":false,\"message\":\"用户名已存在\"}";
		}
		return "{\"valid\":true,\"message\":\"用户名填写正确\"}";
	}

	/**
	 * 黑名单
	 * 
	 * @param task
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/backlist")
	public ModelAndView BackList(TaskFast task, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		try {
			// 加载用户信息
			mv.setViewName("/account/blacklist");
		} catch (Exception ex) {
			throw ex;
		}
		return mv;
	}

	/**
	 * 查询用户所完成的任务
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskByUser", method = { RequestMethod.POST })
	public String findTaskByUser(HttpServletRequest request) {
		String result = "";
		String mobile = request.getParameter("mobile");
		if (StringUtils.isNotEmpty(mobile)) {
			int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
			int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("m1", mobile);
			map.put("m2", mobile);
			map.put("currentPage", start);
			map.put("pageSize", pageSize);
			List<UserTask> list = UserService.findTaskByUser(map);
			int total = UserService.findTaskByUserCount(map);
			String rs = JsonTransfer.getJsonFromList("", list);
			result = "{\"draw\":\"\",\"recordsTotal\":" + pageSize + ",\"recordsFiltered\":"
					+ total + ",\"data\":" + rs + "}";
		}
		return result;
	}

}
