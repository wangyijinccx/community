package com.ipeaksoft.moneyday.api.controller;

import java.text.SimpleDateFormat;
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
import com.ipeaksoft.moneyday.core.entity.CommPlayback;
import com.ipeaksoft.moneyday.core.service.CommHostService;
import com.ipeaksoft.moneyday.core.service.CommPlaybackService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.service.HttpService;

@Controller
@RequestMapping(value = "/back")
public class CommPlaybackController extends BaseController {

	@Autowired
	CommUserService commUserService;
	@Autowired
	private HttpService httpService;
	@Autowired
	CommHostService commHostService;
	@Autowired
	CommPlaybackService commPlaybackService;
	
	
	/**
	 *  更新回放列表
	 * @param webinarId
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("updateplayback")
	public Object updatePlayback(String  token,
			HttpServletResponse response) {
		// 是否存在回访id为空的情况？
		JSONObject result = new JSONObject();
		Map<String ,Object> map = commHostService.getwebinarId(token);
		if(null == map){
			result.put("result", 2);
			result.put("msg", "更新回放列表失败");
			return result;
		}
		Integer webinarId = (Integer) map.get("webinarId");
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			String url = "http://e.vhall.com/api/vhallapi/v2/record/list";
			Map<String, String> params = new HashMap<String, String>();
			params.put("webinar_id", webinarId + "");
			params.put("pos", "0");
			params.put("limit", "10");
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
				result.put("msg", "更新回放列表失败");
				return result;
			}
			JSONObject data = json.getJSONObject("data");
			if(data.isEmpty()){
				result.put("result", 1);
				result.put("msg", "没有需要更新的");
				return result;
			}
			JSONArray lists = data.getJSONArray("lists");
			for (int i = 0; i < lists.size(); i++) {
				JSONObject item = lists.getJSONObject(i);
				int status = item.getInteger("status");
				if(1 == status){
					CommPlayback commPlayback = new CommPlayback();
				    commPlayback.setId(item.getInteger("id"));
				    commPlayback.setSubject(item.getString("subject"));
				    commPlayback.setPlayCreateTime((sdf.parse(item.getString("create_time"))));
				    commPlayback.setUrl(item.getString("url"));
				    commPlayback.setWebinarId(item.getInteger("webinar_id"));
				    commPlayback.setDuration(item.getInteger("duration"));
				    commPlayback.setWebinarSubject(item.getString("webinar_subject"));
				    commPlayback.setIsDefault(item.getInteger("is_default"));
				    commPlayback.setStatus(status);
				    CommPlayback model = commPlaybackService.selectByPrimaryKey(item.getInteger("id"));
				    if(null == model){
				    	commPlayback.setCreatetime(new Date());
				    	commPlayback.setUpdatetime(new Date());
				    	if(commPlaybackService.insertSelective(commPlayback)<1){
				    		result.put("result", 2);
							result.put("msg", "更新回放列表失败");
							return result;
				    	}
				    }else{
				    	commPlayback.setUpdatetime(new Date());
				    	if(commPlaybackService.updateByPrimaryKeySelective(commPlayback)<1){
				    		result.put("result", 2);
							result.put("msg", "更新回放列表失败");
							return result;
				    	}
				    }
				}
			}
			result.put("result", 1);
			result.put("records", json);
			result.put("msg", "更新回访列表成功");
			return result;
		}catch(Exception e){
			result.put("result", 2);
			result.put("msg", "未知异常");
			return result;
		}
	}
	
	/**
	 * 获取回放列表
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
		//不传webinarId
		JSONObject result = new JSONObject();
		Map<String ,Object> map_webinarId = commHostService.getwebinarId(token);
		if(null == map_webinarId){
			result.put("result", 2);
			result.put("msg", "获取回放列表失败");
			return result;
		}
		Integer webinarId = (Integer) map_webinarId.get("webinarId");
		List<Map<String, Object>> lists = commPlaybackService.selectPlaybacks(webinarId, pos, 30);
		// 获取主播状态
		CommHost commHost =  commHostService.selectByWebinarId(webinarId);
		//主播信息
		Map<String, Object> map = new HashMap<String, Object>();
		if(1 == commHost.getOnlinestatus()){
			map.put("id", commHost.getId());
			map.put("webinarId", webinarId);
			map.put("subject", commHost.getSubject());
			map.put("imgUrl", commHost.getImgUrl());
			map.put("onlinestatus", commHost.getOnlinestatus());
			map.put("nickname", commHost.getNickname());
		}
		result.put("result", 1);
		result.put("records", lists);
		result.put("onlineLive",map);
		result.put("msg", "获取回放列表成功");
		return result;
	}
	
}
