package com.ipeaksoft.moneyday.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.util.MD5Util;
import com.ipeaksoft.moneyday.core.entity.CommGame;

@Controller
@RequestMapping(value = "/game")
public class CommGameController extends BaseController {

	/**
	 * 添加游戏
	 * 
	 * @param webinarId
	 * @param token
	 * @param pos
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
					+ "&app_id="
					+ URLEncoder.encode(json.getString("plat_id"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&gamename="
					+ URLEncoder.encode(json.getString("gamename")) 
					+ "&classify="
					+ URLEncoder.encode(json.getString("classify"))
					+ "&gameflag="
					+ URLEncoder.encode(json.getString("gameflag"))
					+ "&creat_time="
					+ URLEncoder.encode(json.getString("creat_time"))
					+ "&status=" + URLEncoder.encode(json.getString("status"))
					+ "&pinyin=" + URLEncoder.encode(json.getString("pinyin"))
					+ "&initial="
					+ URLEncoder.encode(json.getString("initial"))
					+ "&version="
					+ URLEncoder.encode(json.getString("version"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if(!sign.equals(reqSign)){
				result.put("code", 404);
				result.put("fun", "/game/add");
				result.put("time", new Date());
				result.put("info", json);
				return result;
			}
			CommGame commGame = new CommGame();
			commGame.setPlatId(json.getInteger("plat_id"));
			commGame.setGameId(json.getInteger("app_id"));
			commGame.setName(json.getString("gamename"));

		} catch (IOException e) {
			result.put("code", 1000);
			result.put("fun", "/game/add");
			result.put("time", new Date());
			result.put("info", json);
			return result;
		}
		return result;
	}
}
