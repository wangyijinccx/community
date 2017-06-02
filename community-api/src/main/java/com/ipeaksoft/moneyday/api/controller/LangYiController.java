package com.ipeaksoft.moneyday.api.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.service.LangYiService;
import com.ipeaksoft.moneyday.core.util.Channel;

/**
 * 子渠道获取朗亿的在线任务
 * @author liyazi
 * @date 2015年10月14日
 */
@Controller
@RequestMapping(value = "/langyi")
public class LangYiController extends BaseController {
	@Autowired
	private LangYiService langYiService;

	@ResponseBody
	@RequestMapping("/online/task")
	public Object onlineTask(
			@RequestParam(value = "channel", required = false) String channel) {
		Set<String> onlineAdid = langYiService.getOnlineTask();

		//TODO: remove
		//		if( onlineAdid.size() == 0 ){
		//			String[] ids = new String[]{"100726", "100905", "100756", "100442"};
		//			Collections.addAll(onlineAdid, ids);
		//		}
		
		Channel c = Channel.XIGUAMEI;
		if( channel!=null && StringUtils.equals(channel.toLowerCase(), Channel.TAOJINZHE.name().toLowerCase()) ){
			c = Channel.TAOJINZHE;
		}

		Map<String, Map<String, Object>> map = langYiService.getTaskJson(onlineAdid, c);
		return JSONObject.toJSONString(map);
	}

}
