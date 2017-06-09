package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMemOrder;

public interface CommMemOrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommMemOrder record);

    int insertSelective(CommMemOrder record);

    CommMemOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommMemOrder record);

    int updateByPrimaryKey(CommMemOrder record);
}