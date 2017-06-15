package com.ipeaksoft.moneyday.api.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.util.MD5Util;
import com.ipeaksoft.moneyday.core.entity.CommGameServer;
import com.ipeaksoft.moneyday.core.service.CommGameServerService;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Controller
@RequestMapping(value = "/server")
public class CommGameServerController extends BaseController {

	@Autowired
	CommGameServerService commGameServerService;

	/**
	 * 游戏区服添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("add")
	public Object add(HttpServletRequest request, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			Map<String,String[]> maps = request.getParameterMap();
			String js= strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_gameadd:{}", json.toString());
			if(!PLAT_ID.equals(json.getString("plat_id"))){
				result.put("code", 401);
				result.put("fun", "/server/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("app_id"))
					+ "&server_id="
					+ URLEncoder.encode(json.getString("server_id"))
					+ "&server_code="
					+ URLEncoder.encode(json.getString("server_code"))
					+ "&server_name="
					+ URLEncoder.encode(json.getString("server_name"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/server/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGameServer commGameServer = new CommGameServer();
			commGameServer.setServerId(json.getInteger("server_id"));
			commGameServer.setStartTime(json.getLong("start_time"));
			commGameServer.setOaAppId(json.getInteger("app_id"));
			commGameServer.setSerCode(json.getString("server_code"));
			commGameServer.setSerName(json.getString("server_name"));
			commGameServer.setSerDesc(json.getString("server_desc"));
			commGameServer.setStatus((byte) 1);
			commGameServer.setIsDelete((byte) 2);
			commGameServer.setCreateTime(json.getLong("creat_time"));
			commGameServer.setUpdateTime(json.getLong("creat_time"));
			commGameServer.setParentId(0);
			if (commGameServerService.insertSelective(commGameServer) < 1) {
				result.put("code", 1000);
				result.put("fun", "/server/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/server/add");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}

	/**
	 * 游戏区服修改
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("update")
	public Object update(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			Map<String,String[]> maps = request.getParameterMap();
			String js= strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_gameadd:{}", json.toString());
			if(!PLAT_ID.equals(json.getString("plat_id"))){
				result.put("code", 401);
				result.put("fun", "/server/update");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			JSONObject jsonUpInfo = json.getJSONObject("serinfo");
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("app_id"))
					+ "&gamename="
					+ URLEncoder.encode(json.getString("gamename"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&serinfo=" + URLEncoder.encode(jsonUpInfo.toString())
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/server/update");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommGameServer commGameServer = new CommGameServer();
			commGameServer.setServerId(jsonUpInfo.getInteger("server_id"));
			if (null != json.getLong("run_time")) {
				commGameServer.setStartTime(jsonUpInfo.getLong("run_time"));
			}
			commGameServer.setOaAppId(json.getInteger("app_id"));
			if (null != json.getLong("server_code")) {
				commGameServer.setSerCode(jsonUpInfo.getString("server_code"));
			}

			if (null != json.getLong("server_name")) {
				commGameServer.setSerCode(jsonUpInfo.getString("server_name"));
			}
			commGameServer.setUpdateTime(jsonUpInfo.getLong("update_time"));
			if (commGameServerService.updateBySerIdAndAppId(commGameServer) < 1) {
				result.put("code", 1000);
				result.put("fun", "/server/update");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

		} catch (Exception e) {
			result.put("code", 1000);
			result.put("fun", "/server/update");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}

}
