package com.ipeaksoft.moneyday.api.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.service.ComConfirmService;
import com.ipeaksoft.moneyday.api.service.WeChatService;
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
	@Autowired
	WeChatService weChatService;

	public static final String auth_type = "1";
	public static final String account = "v14903519";
	public static final String password = "anywn123";

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
			// 是否注册
			CommUser model = commUserService.selectBymobile(phoneNumber);
			JSONObject userInfo = new JSONObject();
			if (model != null) {
				result.put("result", 5);
				result.put("msg", "账号已存在");
				return result;
			}

			// 验证邀请码
			CommUser pCommUser = commUserService.selectBymobile(invitationCode);
			if (pCommUser == null) {
				result.put("result", 3);
				result.put("msg", "邀请码不存在");
				return result;
			}

			// 注册微吼 密码为手机号的后六位
			// 验证手机号是否注册过微吼账号 注册过就不用注册了
			String validationUrl = "http://e.vhall.com/api/vhallapi/v2/user/get-user-id";
			Map<String, String> postParams = new HashMap<String, String>();
			postParams.put("third_user_id", phoneNumber);
			postParams.put("auth_type", auth_type);
			postParams.put("account", account);
			postParams.put("password", password);
			String validationCallback = httpService.post(validationUrl,
					postParams);
			JSONObject validationJson = JSONObject
					.parseObject(validationCallback);
			if (null == validationJson) {
				result.put("result", 3);
				result.put("msg", "请求微吼服务器异常");
				return result;
			}
			if (!"200".equals(validationJson.getString("code"))) {
				// 没有注册过
				String pass = passUtil.getPassWord(phoneNumber);
				String url = "http://e.vhall.com/api/vhallapi/v2/user/register";
				Map<String, String> params = new HashMap<String, String>();
				params.put("third_user_id", phoneNumber);
				params.put("pass", pass);
				params.put("auth_type", auth_type);
				params.put("account", account);
				params.put("password", password);
				String callback = httpService.post(url, params);
				JSONObject json = JSONObject.parseObject(callback);
				if (null == json || !"200".equals(json.getString("code"))) {
					result.put("result", 3);
					result.put("msg", "微吼注册失败");
					return result;
				}
			}

			// 注册小妹公会服务器 不需要验证是否注册过了

			// 生成token 注册西瓜妹社区
			// 验证是否注册过西瓜妹社区
			String token = UUID.randomUUID().toString().replace("-", "");
			Integer pid = pCommUser.getId();
			CommUser commUser = new CommUser();
			commUser.setMobile(phoneNumber);
			commUser.setPid(pid);
			commUser.setIndicate(token);
			commUser.setCreateTime(new Date());
			commUser.setUpdateTime(new Date());
			commUser.setStatus("1");
			commUser.setAward(0);
			commUser.setTdaward(0);
			commUser.setTotalaward(0);
			if (commUserService.insertSelective(commUser) < 1) {
				result.put("result", 3);
				result.put("msg", "西瓜妹注册失败");
				return result;
			}
			userInfo = commUserService.userInfo(token);

			result.put("result", 4);
			result.put("liveUrl", "");
			result.put("userInfo", userInfo);
			result.put("msg", "注册成功");
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
	 * 
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
		String mob = confirmService.checkMobile(checkCode,
				SMSType.CONFIRM_AUTHENTICATE_MOBILE);
		if (!StringUtils.isEmpty(mob) && mob.equals(phoneNumber)) {
			CommUser model = commUserService.selectBymobile(phoneNumber);
			if (model == null) {
				result.put("result", 3);
				result.put("msg", "用户不存在");
				return result;
			} else {
				// 共用接口
				JSONObject userInfo = commUserService.userInfo(model
						.getIndicate());
				result.put("result", 4);
				// result.put("token", model.getIndicate());
				result.put("userInfo", userInfo);
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

	/**
	 * 绑定微信
	 * 
	 * @param code
	 * @param token
	 *            推广员令牌
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("bindingWeiXin")
	public Object bindingWeiXin(String code, String token,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		// 判断用户是是否存在
		CommUser model = commUserService.selectByIndicate(token);
		if (model == null) {
			result.put("result", 2);
			result.put("msg", "用户不存在");
			return result;
		}
		JSONObject json = weChatService.getUserInfo(code);
		CommUser commUser = commUserService.toUser(null, json);
		commUser.setId(model.getId());
		if (commUserService.updateByPrimaryKeySelective(commUser) < 1) {
			result.put("result", 2);
			result.put("msg", "保存信息失败");
			return result;
		}
		result.put("result", 1);
		result.put("msg", "绑定成功");
		return result;
	}

	/**
	 * 微信登录
	 * 
	 * @param code
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("weiXinLogin")
	public Object weiXinLogin(String code, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = weChatService.getUserInfo(code);
		String openid = json.getString("openid");
		CommUser commUser = commUserService.selectByOpenid(openid);
		if (commUser == null) {
			result.put("result", 2);
			result.put("msg", "登陆失败");
		} else {
			JSONObject userInfo = commUserService.userInfo(commUser
					.getIndicate());
			result.put("result", 1);
			// result.put("token", commUser.getIndicate());
			result.put("userInfo", userInfo);
			result.put("msg", "登陆成功");
		}
		return result;
	}

	/**
	 * 用户信息 --公共接口
	 * 
	 * @param token
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("userinfo")
	public Object userInfo(String token, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser model = commUserService.selectByIndicate(token);
		if (model == null) {
			result.put("result", 2);
			result.put("msg", "用户不存在");
			return result;
		}
		result = commUserService.userInfo(token);
		result.put("result", 1);
		return result;
	}
}
