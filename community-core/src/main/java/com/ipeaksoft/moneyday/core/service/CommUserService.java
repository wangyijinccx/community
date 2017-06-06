package com.ipeaksoft.moneyday.core.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.mapper.CommUserMapper;
import com.ipeaksoft.moneyday.core.util.passUtil;

@Service
public class CommUserService extends BaseService{

	@Autowired
	private CommUserMapper commUserMapper;

	public int insertSelective(CommUser record) {
		return commUserMapper.insertSelective(record);
	}

	public CommUser selectBymobile(String mobile) {
		return commUserMapper.selectBymobile(mobile);
	}

	public CommUser selectByIndicate(String indicate) {
		return commUserMapper.selectByIndicate(indicate);
	}

	public int updateByPrimaryKeySelective(CommUser record) {
		return commUserMapper.updateByPrimaryKeySelective(record);
	}

	public CommUser selectByOpenid(String openid) {
		return commUserMapper.selectByOpenid(openid);
	}
	
	public Map<String, Object> selectByIndicateSelective(String indicate){
		return commUserMapper.selectByIndicateSelective(indicate);
	}

	public CommUser toUser(CommUser commUser, JSONObject json) {
		if (commUser == null) {
			commUser = new CommUser();
		}
		String openid = json.getString("openid");
		commUser.setOpenid(openid);
		String nickname = json.getString("nickname");
		commUser.setNickname(null == nickname ? "" : nickname);
		Short sex = json.getShort("sex");
		commUser.setSex(null == sex ? 0 : sex);
		String language = json.getString("language");
		commUser.setLanguage(null == language ? "" : language);
		String city = json.getString("city");
		commUser.setCity(null == city ? "" : city);
		String province = json.getString("province");
		commUser.setProvince(null == province ? "" : province);
		String country = json.getString("country");
		commUser.setCountry(null == country ? "" : country);
		String headimgurl = json.getString("headimgurl");
		commUser.setHeadimgurl(null == headimgurl ? "" : headimgurl);
		String privilege = json.getString("privilege");
		commUser.setPrivilege(null == privilege ? "" : privilege);
		String unionid = json.getString("unionid");
		commUser.setUnionid(unionid == null ? "" : unionid);
		return commUser;
	}
	
	public JSONObject userInfo(String indicate){
		JSONObject result = new JSONObject();
		Map<String, Object> commUser = selectByIndicateSelective(indicate);
		//result.put("token", commUser.getIndicate());
		String mobile =  (String) commUser.get("mobile");
		String pass = passUtil.getPassWord(mobile);
		commUser.put("whaccount",mobile);
		commUser.put("whpass",pass);
		commUser.put("xmaccount","xg_"+mobile);
		commUser.put("xmpass",pass);
		result.put("user", commUser);
		result.put("sumIncome", "");
		result.put("todayRegisterNum", "");
		result.put("todayRechargeNum", "");
		result.put("todayCommission", "");
		result.put("consumptionThisMonth", "");
		//result.put("accountBalance", commUser.getAward());
		result.put("todayWithdrawalsCount", "");
		//result.put("openid", commUser.getOpenid());
		return result;
	}

}
