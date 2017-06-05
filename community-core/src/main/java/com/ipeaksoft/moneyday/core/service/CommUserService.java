package com.ipeaksoft.moneyday.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.mapper.CommUserMapper;

@Service
public class CommUserService {

	@Autowired
	private CommUserMapper commUserMapper;
	
	
	 public int insertSelective(CommUser record){
		 return commUserMapper.insertSelective(record);
	 }
	 
	 public CommUser selectBymobile(String mobile){
		 return commUserMapper.selectBymobile(mobile);
	 }

	
}
