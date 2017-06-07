package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommGameServer;

public interface CommGameServerMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommGameServer record);

    int insertSelective(CommGameServer record);

    CommGameServer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommGameServer record);

    int updateByPrimaryKey(CommGameServer record);
}