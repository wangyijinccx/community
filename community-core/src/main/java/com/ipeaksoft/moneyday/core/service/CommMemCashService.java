package com.ipeaksoft.moneyday.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommMemCash;
import com.ipeaksoft.moneyday.core.mapper.CommMemCashMapper;

@Service
public class CommMemCashService extends BaseService {

	@Autowired
	private CommMemCashMapper commMemCashMapper;

	public int insertSelective(CommMemCash record) {
		return commMemCashMapper.insertSelective(record);
	}

	public CommMemCash selectByPrimaryKey(Long id) {
		return commMemCashMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(CommMemCash record) {
		return commMemCashMapper.updateByPrimaryKeySelective(record);
	}

}
