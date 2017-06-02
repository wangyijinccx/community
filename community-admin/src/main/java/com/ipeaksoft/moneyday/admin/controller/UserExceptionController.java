package com.ipeaksoft.moneyday.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipeaksoft.moneyday.admin.util.JsonTransfer;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.BlackUser;
import com.ipeaksoft.moneyday.core.entity.User;
import com.ipeaksoft.moneyday.core.entity.UserLoginException;
import com.ipeaksoft.moneyday.core.entity.UserValidate;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.UserService;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/ExceptionAccount")
public class UserExceptionController extends BaseController {

	public final static String USER_SESSION_KEY = "user_session_key";

	@Autowired
	protected AdminUserService adminUserService;
	@Autowired
	protected UserService userService;

	@ResponseBody
	@RequestMapping(value = "/getUserException")
	public Object getUserException(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String mobile = request.getParameter("mobile");// 用户id
		String id = request.getParameter("id");

		// 返回
		Map<String, String> map = new HashMap<String, String>();
		try {

			List<UserLoginException> list = userService.getExceptionList(mobile);
			String rs = JsonTransfer.getJsonFromList("", list);
			String result = "{\"draw\":" + "\"\"" + ",\"recordsTotal\":" + pageSize
					+ ",\"recordsFiltered\":" + 100 + ",\"data\":" + rs + "}";
			System.out.println("json is:" + JsonTransfer.getJsonFromList("", list));
			// System.out.println("result is:"+result);
			// String result=JsonTransfer.getJsonFromList(sEcho,list);
			return result;
		} catch (Exception e) {
			map.put("code", "1002");
			map.put("message", "数据错误");
			return JSONObject.toJSON(map);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/processException")
	public Object processException(HttpServletRequest request) {
		Long id = Long.parseLong(request.getParameter("id"));
		String mobile = request.getParameter("mobile");
		Map<String, String> map = new HashMap<String, String>();
		try {
			AdminUser operator = getUser();
			userService.ProcessExceptionByUserId(id, mobile, operator);
			map.put("status", "true");
			map.put("msg", "处理成功!");
			return JSONObject.toJSON(map);
		} catch (Exception e) {
			logger.error("ERROR:", e);
			map.put("status", "false");
			map.put("msg", "处理失败!");
			return JSONObject.toJSON(map);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/saveToBlackUser")
	public Object saveToBlackUser(HttpServletRequest request) {
		String mobile = request.getParameter("mobile");
		Map<String, String> map = new HashMap<String, String>();
		try {
			User modelInfo = userService.getUserByMobile(mobile);
			modelInfo.setStatus(5);// 黑名单
			userService.addBlackUser(modelInfo.getId());
			userService.updateByPrimaryKey(modelInfo);// 更新状态
			map.put("status", "true");
			map.put("msg", "处理成功!");
			return JSONObject.toJSON(map);
		} catch (Exception e) {
			logger.error("ERROR:", e);
			map.put("status", "false");
			map.put("msg", "处理失败!");
			return JSONObject.toJSON(map);
		}
	}

	/**
	 * 分页加载黑名单列表
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getBlackUserList")
	public Object getBlackUserList(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 每页记录数

		// 返回
		Map<String, String> map = new HashMap<String, String>();
		try {
			Map<String, Object> where = new HashMap<String, Object>();
			where.put("currentPage", start);
			where.put("pageSize", pageSize);
			List<BlackUser> list = userService.findPageWhere(where);
			int total = userService.findPageWhereCount(where);
			JSONObject result = new JSONObject();
			result.put("draw", "");
			result.put("recordsTotal", total);
			result.put("recordsFiltered", total);
			result.put("data", list);
			return result;
		} catch (Exception e) {
			map.put("code", "1002");
			map.put("message", "数据错误");
			return JSONObject.toJSON(map);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/reNewBlackUser")
	public Object reNewBlackUser(HttpServletRequest request) {
		Long id = Long.parseLong(request.getParameter("id"));// 用户id
		Map<String, String> map = new HashMap<String, String>();
		try {
			userService.renewBlackUser(id);
			map.put("status", "true");
			map.put("msg", "处理成功!");
			return JSONObject.toJSON(map);
		} catch (Exception e) {
			logger.error("ERROR:", e);
			map.put("status", "false");
			map.put("msg", "处理失败!");
			return JSONObject.toJSON(map);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/findUserValidateList")
	public Object findUserValidateList(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 美页记录数
		String startDate = request.getParameter("startDate");// 开始时间
		String endDate = request.getParameter("endDate");// 结束时间
		// 返回
		Map<String, String> map = new HashMap<String, String>();
		try {
			List<UserValidate> list = userService.findUserValidateList(startDate, endDate);
			String rs = JsonTransfer.getJsonFromList("", list);
			String result = "{\"draw\":" + "\"\"" + ",\"recordsTotal\":" + pageSize
					+ ",\"recordsFiltered\":" + 100 + ",\"data\":" + rs + "}";
			System.out.println("json is:" + JsonTransfer.getJsonFromList("", list));
			return result;
		} catch (Exception e) {
			map.put("code", "1002");
			map.put("message", "数据错误");
			return JSONObject.toJSON(map);
		}
	}
}
