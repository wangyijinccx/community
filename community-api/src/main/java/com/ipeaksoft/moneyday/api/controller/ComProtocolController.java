package com.ipeaksoft.moneyday.api.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.service.ComConfirmService;
import com.ipeaksoft.moneyday.core.enums.SMSType;

@Controller
public class ComProtocolController extends BaseController {
	
	@Autowired
	ComConfirmService confirmService;

	@ResponseBody
	@RequestMapping("testMobile")
	public Object test() {
		JSONObject result = new JSONObject();
		return result;
	}

	// 发送验证码
	@ResponseBody
	@RequestMapping(value="mobile")
	public Object mobile(String mobile) {
		JSONObject result = new JSONObject();
		if (StringUtils.isNotEmpty(mobile)) {
			boolean sms_result = confirmService.sendCaptcha(mobile,
					SMSType.CONFIRM_AUTHENTICATE_MOBILE);
			if (sms_result) {
				result.put("result", 1);
				result.put("msg", "发送成功");
			} else {
				result.put("result", 2);
				result.put("msg", "发送失败");
			}
		} else {
			result.put("result", 2);
			result.put("msg", "发送失败");
		}
		logger.info("request url:{}, result:{}", request.getRequestURI(), result);
		return result;
	}

	/**
	 * 验证
	 * 
	 * @param mobile
	 * @param code
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("mobileValidation")
	public Object validate(String mobile, String code,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String mob = confirmService.checkMobile(code, SMSType.CONFIRM_AUTHENTICATE_MOBILE);
		if (!StringUtils.isEmpty(mob) && mob.equals(mobile)) {
			result.put("result", 1);
			result.put("msg", "验证码正确");
		} else {
			result.put("result", 2);
			result.put("msg", "验证码错误");
		}
		logger.info("request url:{}, result:{}", request.getRequestURI(), result);
		return result;
	}
}
