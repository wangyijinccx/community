package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMemCash;

public interface CommMemCashMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CommMemCash record);

    int insertSelective(CommMemCash record);

    CommMemCash selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommMemCash record);

    int updateByPrimaryKey(CommMemCash record);
}