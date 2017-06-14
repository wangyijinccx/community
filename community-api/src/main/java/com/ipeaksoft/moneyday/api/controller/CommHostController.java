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
@RequestMapping(value = "/host")
public class CommHostController extends BaseController {

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
		// 暂时先这样获取活动信息
		String fields = "subject,introduction,img_url,type";
		List<Map<String, Object>> listsOnline = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsUonline = new ArrayList<Map<String, Object>>();
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
			commHost.setUpdatetime(new Date());
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
			map.put("headimg", commHost.getHeadimg());
			map.put("coverimg", commHost.getCoverimg());
			// 主播在线人数徒弟数
			JSONObject memberInfo = (JSONObject) getMembers(webinar_id);
			if (null == memberInfo
					|| !"1".equals(memberInfo.getString("result"))) {
				result.put("result", 2);
				result.put("msg", "获取主播在线人数徒弟数失败");
				return result;
			}
			map.put("members", memberInfo.getInteger("members"));
			map.put("students", memberInfo.getInteger("students"));

			if (1 == commHost.getOnlinestatus()) {
				listsOnline.add(map);
			} else {
				listsUonline.add(map);
			}
		}
		for (Map<String, Object> map : listsUonline) {
			listsOnline.add(map);
		}
		result.put("result", 1);
		result.put("webinar", listsOnline);
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
		updateHosts();
		CommUser commUser = commUserService.selectByIndicate(token);
		CommHost commhost = commHostService.selectByPrimaryKey(commUser
				.getCommid());
		Integer webinarId = commhost.getWebinarId();
		// 不判断主播是否在线
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
				|| (!"200".equals(json.getString("code")) && !"10019"
						.equals(json.getString("code")))) {
			result.put("result", 2);
			result.put("msg", "获取回放列表失败");
			return result;
		}
		
		if(!"10019".equals(json.getString("code"))){
			JSONObject Jdata = json.getJSONObject("data");
			JSONArray array = Jdata.getJSONArray("lists");
			for (int i = 0 ; i < array.size() ; i++){
				JSONObject item = array.getJSONObject(i);
				Integer id = item.getInteger("id");
				Integer num = id%9;
				item.put("backgroud", "http://101.201.253.194/imags/f"+num+".png");
			}
		}

		// 主播在线人数徒弟数
		JSONObject memberInfo = (JSONObject) getMembers(webinarId);
		if (null == memberInfo || !"1".equals(memberInfo.getString("result"))) {
			result.put("result", 2);
			result.put("msg", "获取主播在线人数徒弟数失败");
			return result;
		}

		// 获取主播状态
		CommHost commHost = commHostService.selectByWebinarId(webinarId);
		// 主播信息
		Map<String, Object> map = new HashMap<String, Object>();
		if (1 == commHost.getOnlinestatus()) {
			// map.put("id", commHost.getId());
			map.put("webinarId", webinarId);
			map.put("subject", commHost.getSubject());
			map.put("imgUrl", commHost.getImgUrl());
			map.put("onlinestatus", commHost.getOnlinestatus());
			// map.put("nickname", commHost.getNickname());
			result.put("members", memberInfo.getInteger("members"));
		} else {
			result.put("members", 0);
		}

		result.put("result", 1);
		result.put("students", memberInfo.getInteger("students"));
		result.put("headimg", commHost.getHeadimg());
		result.put("coverimg", commHost.getCoverimg());
		result.put("nickname", commHost.getNickname());
		result.put("id", commHost.getId());
		result.put("records", json);
		result.put("onlineLive", map);
		result.put("msg", "获取回放列表成功");
		return result;
	}

	@ResponseBody
	@RequestMapping("getmembers")
	public Object getMembers(Integer webinar_id) {
		JSONObject result = new JSONObject();
		// 主播徒弟数
		List<Map<String, Object>> lists = commHostService
				.getStudents(webinar_id);
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
			redis.expire(key, 30);
			result.put("result", 1);
			result.put("members", json.getInteger("data"));
			result.put("students", lists.size());
			result.put("msg", "获取当前在线人数成功");
			return result;
		} else {
			result.put("result", 1);
			result.put("members", cnt);
			result.put("students", lists.size());
			result.put("msg", "获取当前在线人数成功");
			return result;
		}
	}

	// 预开发接口

	/**
	 * 更新主播活动
	 * 
	 * @param hostid
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updatehostinfo")
	public Object updateHostInfo(Integer hostid) {
		JSONObject result = new JSONObject();
		String fields = "subject,introduction,img_url,type";
		CommHost commHost = commHostService.selectByPrimaryKey(hostid);
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
			result.put("msg", "更新活动信息失败");
			return result;
		}
		JSONObject data = json.getJSONObject("data");
		commHost.setSubject(data.getString("subject"));
		commHost.setIntroduction(data.getString("introduction"));
		commHost.setImgUrl(data.getString("img_url"));
		commHost.setOnlinestatus(data.getInteger("type") == 1 ? 1 : 0);
		commHost.setUpdatetime(new Date());
		if (commHostService.updateByPrimaryKeySelective(commHost) < 1) {
			result.put("result", 2);
			result.put("msg", "更新活动信息失败");
			return result;
		}
		result.put("result", 1);
		result.put("msg", "更新主播信息成功");
		return result;
	}

	/**
	 * 获取主播列表
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getlist2")
	public Object getlist2() {
		JSONObject result = new JSONObject();
		// 暂时先这样获取活动信息
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		List<CommHost> commHosts = commHostService.selectAll();
		for (CommHost commHost : commHosts) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", commHost.getId());
			map.put("webinarId", commHost.getWebinarId());
			map.put("subject", commHost.getSubject());
			map.put("imgUrl", commHost.getImgUrl());
			map.put("onlinestatus", commHost.getOnlinestatus());
			map.put("nickname", commHost.getNickname());
			
			map.put("headimg", commHost.getHeadimg());
			map.put("coverimg", commHost.getCoverimg());
			// 主播在线人数徒弟数
			JSONObject memberInfo = (JSONObject) getMembers(commHost.getWebinarId());
			if (null == memberInfo
					|| !"1".equals(memberInfo.getString("result"))) {
				result.put("result", 2);
				result.put("msg", "获取主播在线人数徒弟数失败");
				return result;
			}
			map.put("members", memberInfo.getInteger("members"));
			map.put("students", memberInfo.getInteger("students"));
			lists.add(map);
		}
		result.put("result", 1);
		result.put("webinar", lists);
		result.put("msg", "获取主播信息成功");
		return result;
	}

	/**
	 * 更新所有主播
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updatehosts")
	public Object updateHosts() {
		JSONObject result = new JSONObject();
		// 暂时先这样获取活动信息
		String fields = "subject,introduction,img_url,type";
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
				result.put("msg", "获取主播信息失败");
				return result;
			}
			JSONObject data = json.getJSONObject("data");
			commHost.setSubject(data.getString("subject"));
			commHost.setIntroduction(data.getString("introduction"));
			commHost.setImgUrl(data.getString("img_url"));
			commHost.setOnlinestatus(data.getInteger("type") == 1 ? 1 : 0);
			commHost.setUpdatetime(new Date());
			commHostService.updateByPrimaryKeySelective(commHost);
		}
		result.put("result", 1);
		result.put("msg", "更新主播信息成功");
		return result;
	}
}
