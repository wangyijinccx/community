package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommMembers;

public interface CommMembersMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CommMembers record);

    int insertSelective(CommMembers record);

    CommMembers selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommMembers record);

    int updateByPrimaryKey(CommMembers record);
}