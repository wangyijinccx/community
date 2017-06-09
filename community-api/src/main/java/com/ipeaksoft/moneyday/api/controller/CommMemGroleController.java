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
import com.ipeaksoft.moneyday.core.entity.CommMemGrole;
import com.ipeaksoft.moneyday.core.entity.CommMembers;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommMemGroleService;
import com.ipeaksoft.moneyday.core.service.CommMembersService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.util.strUtil;


@Controller
@RequestMapping(value = "/user")
public class CommMemGroleController extends BaseController {

	@Autowired
	CommMemGroleService commMemGroleService;
	
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("uproleinfo")
	public Object uproleinfo(HttpServletRequest request, HttpServletResponse response) {
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
			logger.info("comm_useruproleinfo:{}", content);
			json = JSONObject.parseObject(content);
			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("plat_id"))
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
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/user/uproleinfo");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommMemGrole commMemGrole = new CommMemGrole();
			
			if (commMemGroleService.insertSelective(commMemGrole) < 1) {
				result.put("code", 1000);
				result.put("fun", "/user/uproleinfo");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

		} catch (IOException e) {
			result.put("code", 1000);
			result.put("fun", "/user/uproleinfo");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}
}