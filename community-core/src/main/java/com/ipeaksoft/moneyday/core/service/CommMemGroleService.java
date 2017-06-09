package com.ipeaksoft.moneyday.core.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommMemGrole;
import com.ipeaksoft.moneyday.core.mapper.CommMemGroleMapper;

@Service
public class CommMemGroleService extends BaseService{

	@Autowired
	private CommMemGroleMapper commMemGroleMapper;
	
	public int insertSelective(CommMemGrole record){
		return commMemGroleMapper.insertSelective(record);
	}
	
	public int updateByPrimaryKeySelective(CommMemGrole record){
		return commMemGroleMapper.updateByPrimaryKeySelective(record);
	}
	
	public int updateByRoleId(CommMemGrole record){
		return commMemGroleMapper.updateByRoleId(record);
	}
	
	public CommMemGrole selectByRoleId(String roleId){
		return commMemGroleMapper.selectByRoleId(roleId);
	}
	
	
}
