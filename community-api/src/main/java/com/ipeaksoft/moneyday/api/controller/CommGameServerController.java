package com.ipeaksoft.moneyday.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

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
			BufferedReader reader = request.getReader();
			char[] buf = new char[512];
			int len = 0;
			StringBuffer contentBuffer = new StringBuffer();
			while ((len = reader.read(buf)) != -1) {
				contentBuffer.append(buf, 0, len);
			}
			String content = contentBuffer.toString();
			logger.info("comm_gameadd:{}", content);
			json = JSONObject.parseObject(content);
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("plat_id"))
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

		} catch (IOException e) {
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
			BufferedReader reader = request.getReader();
			char[] buf = new char[512];
			int len = 0;
			StringBuffer contentBuffer = new StringBuffer();
			while ((len = reader.read(buf)) != -1) {
				contentBuffer.append(buf, 0, len);
			}
			String content = contentBuffer.toString();
			logger.info("comm_gameadd:{}", content);
			json = JSONObject.parseObject(content);
			JSONObject jsonUpInfo = json.getJSONObject("serinfo");
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("plat_id"))
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

		} catch (IOException e) {
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
