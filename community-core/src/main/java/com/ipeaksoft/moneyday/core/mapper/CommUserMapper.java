package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import com.ipeaksoft.moneyday.core.entity.CommUser;

public interface CommUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommUser record);

    int insertSelective(CommUser record);

    CommUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommUser record);

    int updateByPrimaryKey(CommUser record);
    
    CommUser selectBymobile(String mobile);
    
    CommUser selectByIndicate(String indicate);
    
    CommUser selectByOpenid(String openid);
    
    Map<String, Object> selectByIndicateSelective(String indicate);
    
    Map<String, Object> selectByIndicateSelective2(String indicate);
    
    int updateByIndicate(CommUser record);
    
    List<CommUser> selectByPid(Integer pid);
}