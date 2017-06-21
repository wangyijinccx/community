package com.ipeaksoft.moneyday.api.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.CommHost;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommHostService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.service.HttpService;
import com.ipeaksoft.moneyday.core.service.RedisClient;

@Controller
@RequestMapping(value = "/newhost")
public class CommHostNewController extends BaseController {

	@Autowired
	CommUserService commUserService;
	@Autowired
	private HttpService httpService;
	@Autowired
	CommHostService commHostService;
	@Autowired
	RedisClient redis;

	/**
	 * 获取主播列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getlist")
	public Object getlist() {
		JSONObject result = new JSONObject();
		List<Map<String, Object>> listsOnline = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsUonline = new ArrayList<Map<String, Object>>();
		List<CommHost> commHosts = commHostService.selectAll();
		for (CommHost commHost : commHosts) {
			String url = "http://e.vhall.com/api/vhallapi/v2/webinar/list";
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", commHost.getUserId() + "");
			params.put("type", "1");
			params.put("pos", "0");
			params.put("limit", "100");
			params.put("status", "1");
			params.put("auth_type", auth_type);
			params.put("account", account);
			params.put("password", password);
			String callback = httpService.post(url, params);
			JSONObject json = JSONObject.parseObject(callback);
			if (null == json) {
				result.put("result", 2);
				result.put("msg", "获取主播列表失败");
				return result;
			}
			Map<String, Object> map = new HashMap<String, Object>();
	    	map.put("headimg", commHost.getHeadimg());
	    	map.put("hostid", commHost.getId());
	    	map.put("nickname", commHost.getNickname());
		    if(10019 == json.getInteger("code")){
		    	map.put("onlinestatus", 0);
		    	listsUonline.add(map);
		    }else if(200 == json.getInteger("code")){
		    	map.put("onlinestatus", 1);
		    	listsOnline.add(map);
		    }
		}
		for (Map<String, Object> map : listsUonline) {
			listsOnline.add(map);
		}
		result.put("result", 1);
		result.put("host", listsOnline);
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
		commUser.setUpdateTime(new Date());
		if (commUserService.updateByIndicate(commUser) < 1) {
			result.put("result", 2);
			result.put("msg", "绑定失败");
			return result;
		}
		result.put("result", 1);
		result.put("msg", "绑定成功");
		return result;
	}

	/**
	 * 获取回访列表
	 * 
	 * @param webinarId
	 * @param token
	 * @param pos
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getrecords")
	public Object getRecords(String token, Integer pos,
			HttpServletResponse response) {
		CommUser commUser = commUserService.selectByIndicate(token);
		CommHost commhost = commHostService.selectByPrimaryKey(commUser
				.getCommid());
		Integer userId = commhost.getUserId();
		JSONObject result = new JSONObject();
		String url = "http://e.vhall.com/api/vhallapi/v2/record/list";
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId + "");
		params.put("pos", pos*30 + "");
		params.put("limit", "30");
		params.put("time_seq", "1");
		params.put("auth_type", auth_type);
		params.put("account", account);
		params.put("password", password);
		String callback = httpService.post(url, params);
		JSONObject json = JSONObject.parseObject(callback);
		if (null == json
				|| (!"200".equals(json.getString("code")) && !"10019"
						.equals(json.getString("code")))) {
			result.put("result", 2);
			result.put("msg", "获取回放列表失败");
			return result;
		}
		//获取主播徒弟数
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map<String, Object>> lists = commHostService.getStudentsNew(userId);
		if(!"10019".equals(json.getString("code"))){
			JSONObject Jdata = json.getJSONObject("data");
			JSONArray array = Jdata.getJSONArray("lists");
			for (int i = 0 ; i < array.size() ; i++){
				JSONObject item = array.getJSONObject(i);
				Integer id = item.getInteger("id");
				Integer num = id%9;
				//回访封面图
				item.put("backgroud", "http://101.201.253.194/imags/f"+num+".png");
			}
		}

		// 获取主播状态
		String statusUrl = "http://e.vhall.com/api/vhallapi/v2/webinar/list";
		Map<String, String> statusparams = new HashMap<String, String>();
		statusparams.put("user_id", commhost.getUserId() + "");
		statusparams.put("type", "1");
		statusparams.put("pos", "0");
		statusparams.put("limit", "100");
		statusparams.put("status", "1");
		statusparams.put("auth_type", auth_type);
		statusparams.put("account", account);
		statusparams.put("password", password);
		String statuscallback = httpService.post(statusUrl, statusparams);
		JSONObject statusjson = JSONObject.parseObject(statuscallback);
		if (null == statusjson) {
			result.put("result", 2);
			result.put("msg", "获取主播列表失败");
			return result;
		}
		 if(10019 == json.getInteger("code")){
			 result.put("members",0);
	    }else if(200 == json.getInteger("code")){
	    	//一个子账号 只能有一个活动在直播
	    	JSONObject jo =  statusjson.getJSONObject("data");
	    	JSONArray ja = jo.getJSONArray("lists");
	    	for(int i = 0 ;i <ja.size();i++){
	    		JSONObject item = ja.getJSONObject(i);
	    		Integer webinarId = item.getInteger("webinar_id");
	    		// 主播在线人数
		 		JSONObject memberInfo = (JSONObject) getMembers(webinarId);
		 		if (null == memberInfo || !"1".equals(memberInfo.getString("result"))) {
		 			result.put("result", 2);
		 			result.put("msg", "获取主播在线人数徒弟数失败");
		 			return result;
		 		}
		 		
		 		//活动信息
		 		/*
		 		map.put("id", commhost.getId());
				map.put("webinarId", webinarId);
				map.put("subject", item.getString("subject"));
				map.put("imgUrl",item.getString("webinar_id"));
				map.put("onlinestatus", 1);*/
		 		result.put("members", memberInfo.getInteger("members"));
	    	}
	    }
		result.put("result", 1);
		result.put("students", lists.size());
		result.put("headimg", commhost.getHeadimg());
		result.put("coverimg", commhost.getCoverimg());
		result.put("nickname", commhost.getNickname());
		result.put("id", commhost.getId());
		result.put("records", json);
		result.put("onlineLive", map);
		result.put("msg", "获取回放列表成功");
		return result;
	}

	@ResponseBody
	@RequestMapping("getmembers")
	public Object getMembers(Integer webinar_id) {
		JSONObject result = new JSONObject();
		String key = webinar_id + "";
		Integer cnt = redis.getInteger(key);
		logger.info("commwebinar_id:{},cnt:{}", webinar_id, cnt);
		if (null == cnt) {
			String url = "http://e.vhall.com/api/vhallapi/v2/webinar/current-online-number";
			Map<String, String> params = new HashMap<String, String>();
			params.put("webinar_id", webinar_id + "");
			params.put("auth_type", auth_type);
			params.put("account", account);
			params.put("password", password);
			String callback = httpService.post(url, params);
			JSONObject json = JSONObject.parseObject(callback);
			if (null == json || (!"200".equals(json.getString("code")))) {
				result.put("result", 2);
				result.put("msg", "获取当前在线人数失败");
				return result;
			}
			redis.setInteger(key, json.getInteger("data"));
			redis.expire(key,40);
			result.put("result", 1);
			result.put("members", json.getInteger("data"));
			result.put("msg", "获取当前在线人数成功");
			return result;
		} else {
			result.put("result", 1);
			result.put("members", cnt);
			result.put("msg", "获取当前在线人数成功");
			return result;
		}
	}
}
