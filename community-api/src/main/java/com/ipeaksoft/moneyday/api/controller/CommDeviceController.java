package com.ipeaksoft.moneyday.api.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.CommDevice;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.service.CommDeviceService;
import com.ipeaksoft.moneyday.core.service.CommUserService;

@Controller
@RequestMapping(value = "/device")
public class CommDeviceController extends BaseController {

	@Autowired
	CommDeviceService commDeviceService;
	@Autowired
	CommUserService commUserService;


	@ResponseBody
	@RequestMapping("addforpromate")
	public Object addForPromate(HttpServletRequest request, String token,
			String device, Integer type, HttpServletResponse response) {
		JSONObject result = new JSONObject();
		CommUser commUser = commUserService.selectByIndicate(token);
		if (null == commUser) {
			result.put("result", 2);
			result.put("msg", "推广员不存在");
		}
		CommDevice  model  =  commDeviceService.selectByUserId(token);
		CommDevice commDevice = new CommDevice();
		commDevice.setUserId(token);
		commDevice.setDeviceToken(device);
		commDevice.setType((byte)type.intValue());
		if(null == model){
			commDevice.setCreateTime(new Date());
			commDevice.setUpdateTime(new Date());
			if(commDeviceService.insertSelective(commDevice)<1){
				result.put("result", 3);
				result.put("msg", "添加推广员设备信息失败");
			}
		}else{
			commDevice.setId(model.getId());
			commDevice.setUpdateTime(new Date());
			if(commDeviceService.updateByPrimaryKeySelective(commDevice)<1){
				result.put("result", 3);
				result.put("msg", "更新推广员设备信息失败");
			}
		}
		result.put("result", 1);
		result.put("msg", "添加用户设备信息成功");
		return result;
	}
	
	@ResponseBody
	@RequestMapping("addforhost")
	public Object addForHost(HttpServletRequest request, String token,
			String device, Integer type, HttpServletResponse response) {
		JSONObject result = new JSONObject();
	
		result.put("result", 1);
		result.put("msg", "");
		return result;
	}

}
