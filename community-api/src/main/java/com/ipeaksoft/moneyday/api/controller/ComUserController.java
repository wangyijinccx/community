package com.ipeaksoft.moneyday.api.controller;

import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.service.ComConfirmService;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.enums.SMSType;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.service.HttpService;
import com.ipeaksoft.moneyday.core.util.passUtil;

@Controller
public class ComUserController extends BaseController {

	@Autowired
	ComConfirmService confirmService;
	@Autowired
	CommUserService commUserService;
	@Autowired
	private HttpService httpService;

	@ResponseBody
	@RequestMapping("testMobile")
	public Object test() {
		JSONObject result = new JSONObject();
		return result;
	}

	/**
	 * 验证码
	 * 
	 * @param phoneNumber
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "mobile")
	public Object mobile(String phoneNumber) {
		JSONObject result = new JSONObject();
		if (StringUtils.isNotEmpty(phoneNumber)) {
			boolean sms_result = confirmService.sendCaptcha(phoneNumber,
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
		logger.info("request url:{}, result:{}", request.getRequestURI(),
				result);
		return result;
	}

	/**
	 * 注册 验证验证码
	 * 
	 * @param phoneNumber
	 *            手机号
	 * @param code
	 *            验证码
	 * @param invitationCode
	 *            邀请码
	 * @return
	 */
	@ResponseBody
	@RequestMapping("registered")
	public Object registered(String phoneNumber, String code,
			String invitationCode, HttpServletResponse response) {
		// 1 验证验证码是否正确 2 保存注册信息
		JSONObject result = new JSONObject();
		String mob = confirmService.checkMobile(code,
				SMSType.CONFIRM_AUTHENTICATE_MOBILE);
		if (!StringUtils.isEmpty(mob) && mob.equals(phoneNumber)) {
			// 验证邀请码
			CommUser pCommUser = commUserService.selectBymobile(invitationCode);
			if (pCommUser == null) {
				result.put("result", 3);
				result.put("msg", "邀请码不存在");
				return result;
			}
			// 注册微吼 密码为手机号的后六位
			// 验证手机号是否注册过微吼账号 注册过就不用注册了
			String validationUrl = "http://e.vhall.com/api/vhallapi/v2/user/get-user-id?third_user_id="
					+ phoneNumber;
			String validationCallback = httpService.get(validationUrl);
			JSONObject validationJson = JSONObject.parseObject(validationCallback);
			if (null == validationJson) {
				result.put("result", 3);
				result.put("msg", "请求微吼服务器异常");
				return result;
			}
			if (!"200".equals(validationJson.getString("code"))) {
				// 没有注册过
				String password = passUtil.getPassWord(phoneNumber);
				String url = "http://e.vhall.com/api/vhallapi/v2/user/register?third_user_id="
						+ phoneNumber + "&pass=" + password;
				String callback = httpService.get(url);
				JSONObject json = JSONObject.parseObject(callback);
				if (null == json || !"200".equals(json.getString("code"))) {
					result.put("result", 3);
					result.put("msg", "微吼注册失败");
					return result;
				}
			}
			// 生成token 注册西瓜妹社区
			// 验证是否注册过西瓜妹社区
			CommUser model = commUserService.selectBymobile(phoneNumber);
			if (model == null) {
				String token = UUID.randomUUID().toString().replace("-", "");
				Integer pid = pCommUser.getId();
				CommUser commUser = new CommUser();
				commUser.setMobile(phoneNumber);
				commUser.setPid(pid);
				commUser.setIndicate(token);
				commUser.setCreateTime(new Date());
				commUser.setUpdateTime(new Date());
				commUser.setStatus("1");
				if (commUserService.insertSelective(commUser) < 1) {
					result.put("result", 3);
					result.put("msg", "西瓜妹注册失败");
					return result;
				}
			}
			// 注册小妹公会服务器 不需要验证是否注册过了
		} else {
			result.put("result", 2);
			result.put("msg", "验证码错误");
		}
		logger.info("request url:{}, result:{}", request.getRequestURI(),
				result);
		return result;
	}
     
	/**
	 * 手机号登录
	 * @param phoneNumber
	 * @param checkCode
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("mobilelogin")
	public Object mobileLogin(String phoneNumber, String checkCode,
			HttpServletResponse response) {
		// 1 验证验证码是否正确 2 保存注册信息
		JSONObject result = new JSONObject();
		String mob = confirmService.checkMobile(checkCode,SMSType.CONFIRM_AUTHENTICATE_MOBILE);
		if (!StringUtils.isEmpty(mob) && mob.equals(phoneNumber)) {
			CommUser model = commUserService.selectBymobile(phoneNumber);
			if(model == null){
				result.put("result", 3);
				result.put("msg", "用户不存在");
				return result;
			}else{
				//共用接口
				result.put("result", 4);
				result.put("token", model.getIndicate());
				result.put("msg", "登陆成功");
				return result;
			}
		} else {
			result.put("result", 2);
			result.put("msg", "验证码错误");
		}
		logger.info("request url:{}, result:{}", request.getRequestURI(),
				result);
		return result;
	}
}
