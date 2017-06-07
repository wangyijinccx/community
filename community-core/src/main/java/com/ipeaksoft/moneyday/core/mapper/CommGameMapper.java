package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommGame;

public interface CommGameMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommGame record);

    int insertSelective(CommGame record);

    CommGame selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommGame record);

    int updateByPrimaryKey(CommGame record);
    
    int updateByGameid(CommGame record);
}