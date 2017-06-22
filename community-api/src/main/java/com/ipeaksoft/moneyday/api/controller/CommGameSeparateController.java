package com.ipeaksoft.moneyday.api.controller;

import java.net.URLEncoder;
import java.util.Date;
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
import com.ipeaksoft.moneyday.api.util.MD5Util;
import com.ipeaksoft.moneyday.core.entity.CommGame;
import com.ipeaksoft.moneyday.core.entity.CommGameSeparate;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommGameSeparateService;
import com.ipeaksoft.moneyday.core.service.CommGameService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.util.HttpRequest;
import com.ipeaksoft.moneyday.core.util.RSAutil;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Controller
@RequestMapping(value = "/separate")
public class CommGameSeparateController extends BaseController {

	@Autowired
	CommUserService commUserService;

	@Autowired
	CommGameService commGameService;

	@Autowired
	CommGameSeparateService commGameSeparateService;

	@ResponseBody
	@RequestMapping("update")
	public Object update(HttpServletRequest request, String token, Integer gameid,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser commUser = commUserService.selectByIndicate(token);
		if (null == commUser) {
			result.put("result", 2);
			result.put("msg", "推广员不存在");
		}
		String agent = "xg_" + commUser.getMobile();
		Integer appid = gameid;
		String encStr = "";
		try {
			String content = agent + appid;
			System.out.println("签名前=" + content);
			String signstr = RSAutil.sign(content);
			encStr = URLEncoder.encode(signstr);
			System.out.println("签名后=" + encStr);
		} catch (Exception e) {
		}
		String xgurl = "http://101.201.253.175/index.php/Register/pack";
		Map<String, Object> postParamsXg = new HashMap<String, Object>();
		postParamsXg.put("agent", agent);
		postParamsXg.put("appid", appid);
		postParamsXg.put("sign", encStr);
		String callback = HttpRequest.postForm(xgurl, postParamsXg);
		JSONObject json = JSONObject.parseObject(callback);
		if (null == json || !"0".equals(json.getString("error"))) {
			result.put("result", 3);
			result.put("msg", "更新游戏分包失败");
			return result;
		}

		CommGameSeparate commGameSeparate = new CommGameSeparate();
		commGameSeparate.setPromoterId(commUser.getId());
		commGameSeparate.setAppId(gameid);
		commGameSeparate.setUrl1(json.getString("url"));

		CommGameSeparate model = commGameSeparateService
				.selectByPromoterIdAndAppid(commUser.getId(), gameid);

		if (null == model) {
			if (commGameSeparateService.insertSelective(commGameSeparate) < 1) {
				result.put("result", 3);
				result.put("msg", "更新游戏分包失败");
				return result;
			}
		} else {
			if (commGameSeparateService
					.updateByPromoterIdAndAppid(commGameSeparate) < 1) {
				result.put("result", 3);
				result.put("msg", "更新游戏分包失败");
				return result;
			}
		}

		result.put("result", 1);
		result.put("msg", "获取游戏包成功");
		return result;
	}

	@ResponseBody
	@RequestMapping("details")
	public Object details(HttpServletRequest request, String token,
			Integer gameid, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser commUser = commUserService.selectByIndicate(token);
		if (null == commUser) {
			result.put("result", 2);
			result.put("msg", "推广员不存在");
		}
		// 游戏信息
		CommGame commGame = commGameService.selectByPrimaryKey(gameid);
		// 分包信息
		CommGameSeparate model = commGameSeparateService
				.selectByPromoterIdAndAppid(commUser.getId(), gameid);
		result.put("beginTime", commGame.getStartTime());
		result.put("endTime", commGame.getEndTime());
		result.put("rule", commGame.getRules());
		result.put("url", model.getUrl1());
		result.put("result", 1);
		result.put("msg", "获取游戏详情成功");
		return result;
	}
}
