package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMessage;

public interface CommMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommMessage record);

    int insertSelective(CommMessage record);

    CommMessage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommMessage record);

    int updateByPrimaryKey(CommMessage record);
}