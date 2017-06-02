package com.ipeaksoft.moneyday.api.controller;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.util.AppRank;
import com.ipeaksoft.moneyday.core.util.AppStoreRankUtil;

/**
 * 全局分配appstore里的app给各子系统
 * @author huabing.feng
 *
 */
@Controller
public class GlobalController extends BaseController {
	@Autowired
	AppStoreRankUtil appStoreRankUtil;

	Set<String> keys = new HashSet<String>();
	@PostConstruct
	public void init(){
		keys.add("test");
		keys.add("studio");
		keys.add("xiguamei");
	}
	
	@ResponseBody
	@RequestMapping("/appstore/assign")
	public Object assign(String appkey) {
		JSONObject result = new JSONObject();
		if (StringUtils.isBlank(appkey) || !keys.contains(appkey)){
			result.put("msg", "error");
			return result;
		}
		AppRank app = appStoreRankUtil.assign();
		return JSONObject.toJSONString(app);
	}

}
