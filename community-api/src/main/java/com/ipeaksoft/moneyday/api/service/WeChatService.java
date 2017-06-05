package com.ipeaksoft.moneyday.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.service.HttpService;


@Service
public class WeChatService extends BaseService{

	@Autowired
	HttpService httpService;
	
	//西瓜妹App的appid和appsecret
	public static final String APPID = "wx37e922c6c3f99a9a";
	public static final String APPSECRET = "d0f60aeba1d1501ac60da4683d747a48";
	
	private String getBaseInfo(String code) {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
		url = String.format(url, APPID, APPSECRET, code);
		String result = httpService.get(url);
		return result;
	}

	public String getOpenid(String code) {
		String result = getBaseInfo(code);
		if (StringUtils.isNotBlank(result)) {
			JSONObject json = JSONObject.parseObject(result);
			if (json.containsKey("openid")) {
				return json.getString("openid");
			}
		}
		return null;
	}

	public JSONObject getUserInfo(String code) {
		String result = getBaseInfo(code);
		if (StringUtils.isNotBlank(result)) {
			JSONObject json = JSONObject.parseObject(result);
			if (json.containsKey("access_token")) {
				String token = json.getString("access_token");
				String openid = json.getString("openid");
				String url = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
				url = String.format(url, token, openid);
				result = httpService.get(url);
				return JSONObject.parseObject(result);
			}
		}
		return null;
	}

}
