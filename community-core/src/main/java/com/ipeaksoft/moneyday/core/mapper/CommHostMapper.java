package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;

import com.ipeaksoft.moneyday.core.entity.CommHost;

public interface CommHostMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommHost record);

    int insertSelective(CommHost record);

    CommHost selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommHost record);

    int updateByPrimaryKey(CommHost record);
    
    List<CommHost> selectAll();
}