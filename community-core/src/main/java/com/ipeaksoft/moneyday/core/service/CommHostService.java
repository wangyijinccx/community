package com.ipeaksoft.moneyday.core.service;


import java.util.List;
import java.util.Map;

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
		return commHostMapper.updateByPrimaryKeySelective(record);
	}
	
	public CommHost selectByPrimaryKey(Integer id){
		return commHostMapper.selectByPrimaryKey(id);
	}
	
	public CommHost selectByWebinarId(Integer webinarId){
		return commHostMapper.selectByWebinarId(webinarId);
	}
	

	public List<Map<String,Object>> getStudents(Integer webinarId){
		return commHostMapper.getStudents(webinarId);
	}

	public Map<String ,Object> getwebinarId(String token){
		return commHostMapper.getwebinarId(token);
	}
	
	public CommHost selectByUserID(Integer userId){
		return commHostMapper.selectByUserID(userId);
	}
	
}
