package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommUserDay;

public interface CommUserDayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommUserDay record);

    int insertSelective(CommUserDay record);

    CommUserDay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommUserDay record);

    int updateByPrimaryKey(CommUserDay record);
}