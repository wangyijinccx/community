package com.ipeaksoft.moneyday.api.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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
	@Autowired
	CommMembersService commMembersService;
	@Autowired
	CommUserService commUserService;

	@SuppressWarnings({ "deprecation", "unchecked" })
	@ResponseBody
	@RequestMapping("uproleinfo")
	public Object uproleinfo(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		JSONObject json = new JSONObject();
		String str = "";
		try {
			Map<String,String[]> maps = request.getParameterMap();
			String js= strUtil.map2JsonString(maps);
			json = JSONObject.parseObject(js);
			logger.info("comm_useruproleinfo:{}", json.toString());
			if (!PLAT_ID.equals(json.getString("plat_id"))) {
				result.put("code", 401);
				result.put("fun", "/game/update");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			Map<String, Object> map = JSONObject
					.parseObject(js, Map.class);
			for (Entry<String, Object> entry : map.entrySet()) {
				str += entry.getKey() + "="
						+ URLEncoder.encode((String) entry.getValue()) + "&";
			}
			String sign = MD5Util.md5(str + "PLAT_SECURE_KEY="
					+ URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/user/uproleinfo");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			CommMemGrole comm = new CommMemGrole();
			comm.setPlatId(json.getInteger("plat_id"));
			comm.setOaAppId(json.getInteger("app_id"));
			CommMembers mems = commMembersService.selectByUserName(json
					.getString("username"));
			if (null == mems) {
				result.put("code", 406);
				result.put("fun", "/user/uproleinfo");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
			comm.setMemId(mems.getId().intValue());
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
			comm.setPromoterId(commUser.getId());
			comm.setIp(strUtil.ipToLong(json.getString("ip")));
			comm.setUpdateTime(json.getLong("time"));
			comm.setDeviceId(json.getString("device_id"));
			comm.setFrom(json.getByte("from"));
			comm.setRoleId(json.getString("role_id"));
			comm.setRoleLevel(json.getInteger("role_level"));
			comm.setRoleName(json.getString("role_name"));
			comm.setServerId(json.getInteger("server_id"));
			comm.setServerName(json.getString("server_name"));
			if (null != json.getLong("rolelevel_ctime")) {
				comm.setRolelevelCtime(json.getLong("rolelevel_ctime"));
			}
			if (null != json.getLong("rolelevel_mtime")) {
				comm.setRolelevelMtime(json.getLong("rolelevel_mtime"));
			}

			CommMemGrole commMemGrole = commMemGroleService.selectByRoleId(json
					.getString("role_id"));
			if (null == commMemGrole) {
				comm.setCreatTime(json.getLong("time"));
				if (commMemGroleService.insertSelective(comm) < 1) {
					result.put("code", 1000);
					result.put("fun", "/user/uproleinfo");
					result.put("time", new Date());
					result.put("info", json);
					sdklogger.info("ERROR:{}", result.toString());
					return result;
				}
			} else {
				if (commMemGroleService.updateByRoleId(comm) < 1) {
					result.put("code", 1000);
					result.put("fun", "/user/uproleinfo");
					result.put("time", new Date());
					result.put("info", json);
					sdklogger.info("ERROR:{}", result.toString());
					return result;
				}
			}
		} catch (Exception e) {
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
