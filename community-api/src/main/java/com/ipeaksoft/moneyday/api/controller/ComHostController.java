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
@RequestMapping(value = "/host")
public class ComHostController extends BaseController {

	@Autowired
	CommUserService commUserService;
	@Autowired
	private HttpService httpService;
	@Autowired
	CommHostService commHostService;

	/**
	 * 获取主播列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getlist")
	public Object getlist() {
		JSONObject result = new JSONObject();
		// 暂时先这样获取活动信息
		String fields = "subject,introduction,img_url,type";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<CommHost> commHosts = commHostService.selectAll();
		for (CommHost commHost : commHosts) {
			Integer webinar_id = commHost.getWebinarId();
			String url = "http://e.vhall.com/api/vhallapi/v2/webinar/fetch";
			Map<String, String> params = new HashMap<String, String>();
			params.put("webinar_id", webinar_id + "");
			params.put("fields", fields);
			params.put("auth_type", auth_type);
			params.put("account", account);
			params.put("password", password);
			String callback = httpService.post(url, params);
			JSONObject json = JSONObject.parseObject(callback);
			if (null == json || !"200".equals(json.getString("code"))) {
				result.put("result", 2);
				result.put("msg", "获取活动信息失败");
				return result;
			}
			JSONObject data = json.getJSONObject("data");
			commHost.setSubject(data.getString("subject"));
			commHost.setIntroduction(data.getString("introduction"));
			commHost.setImgUrl(data.getString("img_url"));
			commHost.setOnlinestatus(data.getInteger("type") == 1 ? 1 : 0);
			if (commHostService.updateByPrimaryKeySelective(commHost) < 1) {
				result.put("result", 2);
				result.put("msg", "获取活动信息失败");
				return result;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", commHost.getId());
			map.put("webinarId", commHost.getWebinarId());
			map.put("subject", commHost.getSubject());
			map.put("imgUrl", commHost.getImgUrl());
			map.put("onlinestatus", commHost.getOnlinestatus());
			map.put("nickname", commHost.getNickname());
			lists.add(map);
		}
		result.put("result", 1);
		result.put("webinar", lists);
		result.put("msg", "获取主播信息成功");
		return result;
	}

	/**
	 * 绑定主播
	 * 
	 * @param hostid
	 * @param token
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("bindinghost")
	public Object bindingHost(Integer hostid, String token,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser commUser = new CommUser();
		commUser.setIndicate(token);
		commUser.setCommid(hostid);
		if (commUserService.updateByIndicate(commUser) < 1) {
			result.put("result", 2);
			result.put("msg", "绑定失败");
			return result;
		}
		/*
		 * CommHost commHost = commHostService.selectByPrimaryKey(hostid);
		 * Map<String, Object> map = new HashMap<String, Object>();
		 * map.put("id", commHost.getId()); map.put("webinarId",
		 * commHost.getWebinarId()); map.put("subject", commHost.getSubject());
		 * map.put("imgUrl", commHost.getImgUrl()); map.put("onlinestatus",
		 * commHost.getOnlinestatus()); map.put("nickname",
		 * commHost.getNickname());
		 */
		result.put("result", 1);
		// result.put("webinar", map);
		result.put("msg", "绑定成功");
		return result;
	}

	@ResponseBody
	@RequestMapping("getrecords")
	public Object getRecords(Integer webinarId, String token, Integer pos,
			HttpServletResponse response) {
		//不判断主播是否在线
		JSONObject result = new JSONObject();
		String url = "http://e.vhall.com/api/vhallapi/v2/record/list";
		Map<String, String> params = new HashMap<String, String>();
		params.put("webinar_id", webinarId + "");
		params.put("pos", pos + "");
		params.put("limit", "30");
		params.put("time_seq", "1");
		params.put("auth_type", auth_type);
		params.put("account", account);
		params.put("password", password);
		String callback = httpService.post(url, params);
		JSONObject json = JSONObject.parseObject(callback);
		if (null == json
				|| (!"200".equals(json.getString("code")) && !"10019".equals(json
						.getString("code")))) {
			result.put("result", 2);
			result.put("msg", "获取回放列表失败");
			return result;
		}
		result.put("result", 1);
		result.put("records", json);
		result.put("msg", "获取回放列表成功");
		return result;
	}
}
