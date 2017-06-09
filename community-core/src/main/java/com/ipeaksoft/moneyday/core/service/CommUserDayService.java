package com.ipeaksoft.moneyday.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.entity.CommUserDay;
import com.ipeaksoft.moneyday.core.mapper.CommUserDayMapper;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Service
public class CommUserDayService extends BaseService{

	@Autowired
	private CommUserDayMapper commUserDayMapper;
	@Autowired
	private CommUserService  commUserService;

	public int insertSelective(CommUserDay record) {
		return commUserDayMapper.insertSelective(record);
	}
	
	
    public void  statistical(String agentname,double  real_amount){
    	String xgAccount = strUtil.getAgentName(agentname);
    	CommUser commUser = commUserService.selectBymobile(xgAccount);
    	//用户做任务前，肯定绑定了师傅和主播了
    	Integer masterId = commUser.getPid();
    	Integer hostId= commUser.getCommid();
    	
    }
}
