package com.ipeaksoft.moneyday.api.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping(value = "/drawal")
public class CommDrawalController extends BaseController {
	@ResponseBody
	@RequestMapping("withdrawal")
	public Object withDrawal(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		//判断提现次数   提现金额     余额是否够
		result.put("result", 1);
		result.put("msg", "成功");
		return result;
	}

}
