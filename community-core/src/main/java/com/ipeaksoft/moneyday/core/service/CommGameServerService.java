package com.ipeaksoft.moneyday.core.service;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommGameServer;
import com.ipeaksoft.moneyday.core.mapper.CommGameServerMapper;

@Service
public class CommGameServerService extends BaseService{

	@Autowired
	private CommGameServerMapper commGameServerMapper;
	
	public int insertSelective(CommGameServer record){
		return commGameServerMapper.insertSelective(record);
	}
	
	public int updateBySerIdAndAppId(CommGameServer record){
		return commGameServerMapper.updateBySerIdAndAppId(record);
	}
}
