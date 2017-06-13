package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import com.ipeaksoft.moneyday.core.entity.CommMembers;

public interface CommMembersMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CommMembers record);

	int insertSelective(CommMembers record);

	CommMembers selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(CommMembers record);

	int updateByPrimaryKey(CommMembers record);

	CommMembers selectByUserName(String username);

	List<Map<String, Object>> selectGameMems(Integer oaAppId,
			Integer promoterId, String ordeStr, Integer currentPage,
			Integer pageSize);
}