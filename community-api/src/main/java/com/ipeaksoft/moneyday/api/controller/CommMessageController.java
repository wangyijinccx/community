package com.ipeaksoft.moneyday.api.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.CommDevice;
import com.ipeaksoft.moneyday.core.entity.CommMessage;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommDeviceService;
import com.ipeaksoft.moneyday.core.service.CommMessageService;
import com.ipeaksoft.moneyday.core.service.CommUserService;

@Controller
@RequestMapping(value = "/message")
public class CommMessageController extends BaseController {

	@Autowired
	CommMessageService commMessageService;

	/**
	 * 标记为已读
	 * 
	 * @param request
	 * @param id
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("update")
	public Object update(HttpServletRequest request, Integer id,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		if (commMessageService.updateRead(id) < 1) {
			result.put("result", 2);
			result.put("msg", "修改消息失败");
			return result;
		}
		result.put("result", 1);
		result.put("msg", "修改消息成功");
		return result;
	}

	/**
	 * 获取消息列表 ，时间倒叙 分页每页20
	 * 
	 * @param request
	 * @param token
	 * @param pos
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("list")
	public Object list(HttpServletRequest request, String token, Integer pos,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONArray ja = new JSONArray();
		List<CommMessage> lists = commMessageService.selectMessages(token,
				pos * 20, 20);
		for (CommMessage commMessage : lists) {
			String content = commMessage.getContent();
			JSONObject json = JSONObject.parseObject(content);
			json.put("messid", commMessage.getId());
			json.put("isread", commMessage.getIsread());
			ja.add(json);
		}
		result.put("result", 1);
		result.put("messages", ja);
		result.put("msg", "获取消息列表成功");
		return result;
	}

}
