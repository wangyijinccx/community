package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMemGrole;

public interface CommMemGroleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommMemGrole record);

    int insertSelective(CommMemGrole record);

    CommMemGrole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommMemGrole record);

    int updateByPrimaryKey(CommMemGrole record);
}