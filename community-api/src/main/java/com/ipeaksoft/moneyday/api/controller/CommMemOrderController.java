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
import com.ipeaksoft.moneyday.core.entity.CommMemOrder;
import com.ipeaksoft.moneyday.core.service.CommMemGroleService;
import com.ipeaksoft.moneyday.core.service.CommMemOrderService;
import com.ipeaksoft.moneyday.core.service.CommMembersService;
import com.ipeaksoft.moneyday.core.service.CommUserDayService;
import com.ipeaksoft.moneyday.core.service.CommUserService;

@Controller
@RequestMapping(value = "/user")
public class CommMemOrderController extends BaseController {

	@Autowired
	CommMemGroleService commMemGroleService;
	@Autowired
	CommMembersService commMembersService;
	@Autowired
	CommUserService commUserService;
	@Autowired
	CommMemOrderService commMemOrderService;
	@Autowired
	CommUserDayService commUserDayService;

	@SuppressWarnings("deprecation")
	@ResponseBody
	@RequestMapping("pay")
	public Object pay(HttpServletRequest request, HttpServletResponse response) {
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
			logger.info("comm_userpay:{}", content);
			json = JSONObject.parseObject(content);

			String sign = MD5Util.md5("plat_id=" + URLEncoder.encode(PLAT_ID)
					+ "&app_id=" + URLEncoder.encode(json.getString("plat_id"))
					+ "&timestamp="
					+ URLEncoder.encode(json.getString("timestamp"))
					+ "&uersname="
					+ URLEncoder.encode(json.getString("uersname"))
					+ "&order_id="
					+ URLEncoder.encode(json.getString("order_id"))
					+ "&amount=" + URLEncoder.encode(json.getString("amount"))
					+ "&time=" + URLEncoder.encode(json.getString("time"))
					+ "&payway=" + URLEncoder.encode(json.getString("payway"))
					+ "&ip=" + URLEncoder.encode(json.getString("ip"))
					+ "&device_id="
					+ URLEncoder.encode(json.getString("device_id")) + "&from="
					+ URLEncoder.encode(json.getString("from")) + "&userua="
					+ URLEncoder.encode(json.getString("userua"))
					+ "&role_name="
					+ URLEncoder.encode(json.getString("role_name"))
					+ "&server_id="
					+ URLEncoder.encode(json.getString("server_id"))
					+ "&server_name="
					+ URLEncoder.encode(json.getString("server_name"))
					+ "&status=" + URLEncoder.encode(json.getString("status"))
					+ "&PLAT_SECURE_KEY=" + URLEncoder.encode(PLAT_SECURE_KEY));
			String reqSign = json.getString("sign");
			if (!sign.equals(reqSign)) {
				result.put("code", 404);
				result.put("fun", "/user/pay");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}

			// 记录角色变化
			CommMemGrole commMemGrole = commMemGroleService.selectByRoleId(json
					.getString("role_id"));
			if (null == commMemGrole) {
				result.put("code", 414);
				result.put("fun", "/user/pay");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			} else {
				CommMemGrole comm = new CommMemGrole();
				/*
				 * 这些可以不更新 comm.setPlatId(json.getInteger("plat_id"));
				 * comm.setOaAppId(json.getInteger("app_id")); CommMembers mems
				 * = commMembersService.selectByUserName(json
				 * .getString("username")); if (null == mems) {
				 * result.put("code", 406); result.put("fun", "/user/pay");
				 * result.put("time", new Date()); result.put("info", json);
				 * sdklogger.info("ERROR:{}", result.toString()); return result;
				 * } comm.setMemId(mems.getId().intValue()); String agentname =
				 * json.getString("agentname"); CommUser commUser =
				 * commUserService.selectBymobile(strUtil
				 * .getAgentName(agentname)); if (null == commUser) {
				 * result.put("code", 407); result.put("fun", "/user/pay");
				 * result.put("time", new Date()); result.put("info", json);
				 * sdklogger.info("ERROR:{}", result.toString()); return result;
				 * } comm.setPromoterId(commUser.getId());
				 * comm.setIp(json.getInteger("ip"));//充值公网ip ，跟角色表对应
				 * comm.setUpdateTime(json.getLong("time"));//充值时间，跟角色表对应
				 * comm.setDeviceId(json.getString("device_id"));//充值，跟角色表对应
				 * comm.setFrom(json.getByte("from"));//充值，跟角色表对应
				 */
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
				if (commMemGroleService.updateByRoleId(comm) < 1) {
					result.put("code", 1000);
					result.put("fun", "/user/pay");
					result.put("time", new Date());
					result.put("info", json);
					sdklogger.info("ERROR:{}", result.toString());
					return result;
				}
			}

			// 记录充值信息
			CommMemOrder commMemOrder = new CommMemOrder();
			commMemOrder.setPlatId(json.getInteger("plat_id"));
			commMemOrder.setGroleId(commMemGrole.getId());
			commMemOrder.setIp(json.getInteger("ip"));
			commMemOrder.setTime(json.getLong("time"));
			commMemOrder.setDeviceId(json.getString("device_id"));
			commMemOrder.setFrom(json.getByte("from"));
			commMemOrder.setOrderId(json.getString("order_id"));
			commMemOrder.setPayway(json.getString("payway"));
			commMemOrder.setRealAmount(json.getDouble("real_amount"));
			commMemOrder.setAmount(json.getDouble("amount"));
			commMemOrder.setGmCnt(json.getDouble("gm_cnt"));
			commMemOrder.setStatus(json.getByte("status"));
			commMemOrder.setRebateCnt(json.getDouble("rebate_cnt"));
			commMemOrder.setRoleId(json.getString("role_id"));
			if (null != json.getLong("remark")) {
				commMemOrder.setRemark(json.getString("remark"));
			}
			if (commMemOrderService.insertSelective(commMemOrder) < 1) {
				result.put("code", 1000);
				result.put("fun", "/user/pay");
				result.put("time", new Date());
				result.put("info", json);
				sdklogger.info("ERROR:{}", result.toString());
				return result;
			}
            //收益
			commUserDayService.statistical(json.getString("agentname"),
					json.getDouble("real_amount"));

		} catch (IOException e) {
			result.put("code", 1000);
			result.put("fun", "/user/pay");
			result.put("time", new Date());
			result.put("info", json);
			sdklogger.info("ERROR:{}", result.toString());
			return result;
		}
		result.put("code", 200);
		return result;
	}
}
