package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommDevice;

public interface CommDeviceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommDevice record);

    int insertSelective(CommDevice record);

    CommDevice selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommDevice record);

    int updateByPrimaryKey(CommDevice record);
    
    CommDevice selectByUserId(String userId);
}