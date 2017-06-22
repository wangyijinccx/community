package com.ipeaksoft.moneyday.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommGameSeparate;
import com.ipeaksoft.moneyday.core.mapper.CommGameSeparateMapper;

@Service
public class CommGameSeparateService extends BaseService {

	@Autowired
	private CommGameSeparateMapper commGameSeparateMapper;

	public int insertSelective(CommGameSeparate record) {
		return commGameSeparateMapper.insertSelective(record);
	}
	
	public CommGameSeparate selectByPromoterIdAndAppid(Integer promoterId,
			Integer appid){
		return commGameSeparateMapper.selectByPromoterIdAndAppid(promoterId,appid);
	}

	public int updateByPromoterIdAndAppid(CommGameSeparate record){
		return commGameSeparateMapper.updateByPromoterIdAndAppid(record);
	}

}
