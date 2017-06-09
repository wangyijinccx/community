package com.ipeaksoft.moneyday.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommUserDay;
import com.ipeaksoft.moneyday.core.mapper.CommUserDayMapper;

@Service
public class CommUserDayService extends BaseService{

	@Autowired
	private CommUserDayMapper commUserDayMapper;

	public int insertSelective(CommUserDay record) {
		return commUserDayMapper.insertSelective(record);
	}
}
