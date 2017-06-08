package com.ipeaksoft.moneyday.core.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.mapper.CommGameServerMapper;

@Service
public class CommGameServerService extends BaseService{

	@Autowired
	private CommGameServerMapper commGameServerMapper;
	
	
}
