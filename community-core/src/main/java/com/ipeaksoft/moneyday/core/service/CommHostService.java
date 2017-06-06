package com.ipeaksoft.moneyday.core.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommHost;
import com.ipeaksoft.moneyday.core.mapper.CommHostMapper;

@Service
public class CommHostService extends BaseService{

	@Autowired
	private CommHostMapper commHostMapper;
	
	
	public List<CommHost> selectAll(){
		return commHostMapper.selectAll();
	}
	
	public int updateByPrimaryKeySelective(CommHost record){
		return commHostMapper.updateByPrimaryKey(record);
	}
	
	public CommHost selectByPrimaryKey(Integer id){
		return commHostMapper.selectByPrimaryKey(id);
	}
	
	public CommHost selectByWebinarId(Integer webinarId){
		return commHostMapper.selectByWebinarId(webinarId);
	}
	
}
