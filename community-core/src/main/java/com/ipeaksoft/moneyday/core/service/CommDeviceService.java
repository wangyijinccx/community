package com.ipeaksoft.moneyday.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommDevice;
import com.ipeaksoft.moneyday.core.mapper.CommDeviceMapper;

@Service
public class CommDeviceService extends BaseService {

	@Autowired
	private CommDeviceMapper commDeviceMapper;
	
	
	public int updateByPrimaryKeySelective(CommDevice record){
		return commDeviceMapper.updateByPrimaryKeySelective(record);
	}

    
    public CommDevice selectByUserId(Integer userId){
    	return commDeviceMapper.selectByUserId(userId);
    }
    
    public int insertSelective(CommDevice record){
    	return commDeviceMapper.insertSelective(record);
    }

}
