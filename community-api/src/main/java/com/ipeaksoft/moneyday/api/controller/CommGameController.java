package com.ipeaksoft.moneyday.api.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.CommHost;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommHostService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.service.HttpService;

@Controller
@RequestMapping(value = "/game")
public class CommGameController extends BaseController {

	@Autowired
	CommUserService commUserService;
	@Autowired
	private HttpService httpService;
	@Autowired
	CommHostService commHostService;

	/**
	 * 添加游戏
	 * 
	 * @param webinarId
	 * @param token
	 * @param pos
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("add")
	public Object add(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		
		
		return result;
	}
}
