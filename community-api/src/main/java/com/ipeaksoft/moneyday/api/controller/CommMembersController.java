package com.ipeaksoft.moneyday.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
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
import com.ipeaksoft.moneyday.core.entity.CommMembers;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommMembersService;
import com.ipeaksoft.moneyday.core.service.CommUserDayService;
import com.ipeaksoft.moneyday.core.service.CommUserService;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Controller
@RequestMapping(value = "/user")
public class CommMembersController extends BaseController {

	@Autowired
	CommMembersService commMembersService;
	@Autowired
	CommUserService commUserService;
	@Autowired
	CommUserDayService commUserDayService;

	/**
	 * 用户注册
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("reg")
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
			logger.info("comm_userreg:{}", content);
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
				result.put("fun", "/user/reg");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommMembers commMembers = new CommMembers();
			commMembers.setUsername(json.getString("uersname"));
			commMembers.setPassword(json.getString("password"));
			commMembers.setDeviceId(json.getString("device_id"));
			commMembers.setRegIp(json.getInteger("ip"));
			commMembers.setPlatId(json.getInteger("plat_id"));
			commMembers.setFrom(json.getByte("from"));
			commMembers.setOaAppId(json.getInteger("app_id"));
			String agentname = json.getString("agentname");
			CommUser commUser = commUserService.selectBymobile(strUtil
					.getAgentName(agentname));
			if (null == commUser) {
				result.put("code", 407);
				result.put("fun", "/user/uproleinfo");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			commMembers.setPromoterId(commUser.getId());
			commMembers.setRegTime(json.getLong("time"));
			commMembers.setUpdateTime(json.getLong("time"));
			if (commMembersService.insertSelective(commMembers) < 1) {
				result.put("code", 1000);
				result.put("fun", "/user/reg");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			commUserDayService.registered(commUser);
		} catch (IOException e) {
			result.put("code", 1000);
			result.put("fun", "/user/reg");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}

	/**
	 * 获取玩家详情
	 * 
	 * @param request
	 * @param gameID
	 * @param Token
	 * @param sortType
	 * @param pos
	 * @param pageSize
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getgamemems")
	public Object getGameMems(HttpServletRequest request, Integer gameID,
			String Token, String sortType, Integer pos, Integer pageSize,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser commUser = commUserService.selectByIndicate(Token);
		if (null == commUser) {
			result.put("result", 2);
			result.put("msg", "该推广员不存在");
		}
		List<Map<String, Object>> mems = commMembersService.selectGameMems(
				gameID, commUser.getId(), sortType, pos, pageSize);
		result.put("result", 1);
		result.put("mems", mems);
		result.put("msg", "获取用户信息成功");
		return result;
	}
    
	/**
	 * 添加qq微信
	 * @param request
	 * @param userid
	 * @param qq
	 * @param weixin
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("addcontact")
	public Object addContact(HttpServletRequest request, Integer userid,
			String qq, String weixin, HttpServletResponse response) {
		JSONObject result = new JSONObject();
	    CommMembers commMemBers = new CommMembers();
	    commMemBers.setId((long)userid);
	    commMemBers.setQq(qq);
	    commMemBers.setWeixin(weixin);
	    if(commMembersService.updateByPrimaryKeySelective(commMemBers)<1){
	    	result.put("result", 2);
			result.put("msg", "添加qq微信失败");
	    }
		result.put("result", 1);
		result.put("msg", "添加qq微信成功");
		return result;
	}

}
