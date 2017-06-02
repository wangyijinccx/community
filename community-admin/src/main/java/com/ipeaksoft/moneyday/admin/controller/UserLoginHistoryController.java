package com.ipeaksoft.moneyday.admin.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.UserLoginHistory;
import com.ipeaksoft.moneyday.core.service.UserLoginHistoryService;

@Controller
@RequestMapping(value = "/UserLoginHistory")
public class UserLoginHistoryController extends BaseController {

	public final static String USER_SESSION_KEY = "user_session_key";

	@Autowired
	protected UserLoginHistoryService userLoginHistoryService;

	/**
	 * 分页加载登录记录列表
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getUserLoginHistoryList")
	public Object getUserLoginHistoryList(HttpServletRequest request) {
		int start = Integer.parseInt(request.getParameter("start"));// 开始记录数
		int pageSize = Integer.parseInt(request.getParameter("length"));// 每页记录数

		// 返回
		Map<String, String> map = new HashMap<String, String>();
		try {
			Map<String, Object> where = new HashMap<String, Object>();
			where.put("currentPage", start);
			where.put("pageSize", pageSize);
			List<UserLoginHistory> list = userLoginHistoryService.pageUserLoginHistory(where);
			int total = userLoginHistoryService.userLoginHistoryAmount(where);
			JSONObject result = new JSONObject();
			result.put("draw", "");
			result.put("recordsTotal", total);
			result.put("recordsFiltered", total);
			result.put("data", list);
			return result;
		} catch (Exception e) {
			logger.error("ERROR:", e);
			map.put("code", "1002");
			map.put("message", "数据错误");
			return JSONObject.toJSON(map);
		}
	}

}
