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
import com.ipeaksoft.moneyday.core.entity.CommGame;
import com.ipeaksoft.moneyday.core.service.CommGameServerService;

@Controller
@RequestMapping(value = "/server")
public class CommGameServerController extends BaseController {

	@Autowired
	CommGameServerService commGameServerService;

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
					+ "&server_id=" + URLEncoder.encode(json.getString("server_id"))
					+ "&server_code=" + URLEncoder.encode(json.getString("server_code"))
					+ "&server_name=" + URLEncoder.encode(json.getString("server_name"))
					+ "&timestamp=" + URLEncoder.encode(json.getString("timestamp"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/server/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}",result.toString());
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setPlatId(json.getInteger("plat_id"));
			commGame.setGameId(json.getInteger("app_id"));
			commGame.setName(json.getString("gamename"));
			commGame.setGameflag(json.getString("gameflag"));
			commGame.setInitial(json.getString("initial"));
			commGame.setIcon(json.getString("icon"));
			commGame.setStatus(json.getByte("status"));
			commGame.setCreateTime(json.getLong("creat_time"));
			commGame.setUpdateTime(json.getLong("creat_time"));
			commGame.setListorder(0);
			commGame.setTargetCnt(null == json.getInteger("target_cnt") ? 7
					: json.getInteger("target_cnt"));
			commGame.setTargetLevel(null == json.getInteger("target_level") ? 140
					: json.getInteger("target_level"));
			commGame.setParentId(null == json.getInteger("parent_id") ? 0
					: json.getInteger("parent_id"));
			if (true) {
				result.put("code", 1000);
				result.put("fun", "/server/add");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}",result.toString());
				return result;
			}

		} catch (IOException e) {
			result.put("code", 1000);
			result.put("fun", "/server/add");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}",result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}
	

}
