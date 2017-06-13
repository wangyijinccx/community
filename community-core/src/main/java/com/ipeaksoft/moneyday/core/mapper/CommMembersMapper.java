package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ipeaksoft.moneyday.core.entity.CommMembers;

public interface CommMembersMapper {
	int deleteByPrimaryKey(Long id);

	int insert(CommMembers record);

	int insertSelective(CommMembers record);

	CommMembers selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(CommMembers record);

	int updateByPrimaryKey(CommMembers record);

	CommMembers selectByUserName(String username);

	List<Map<String, Object>> selectGameMems(@Param("oaAppId")Integer oaAppId,
			@Param("promoterId")Integer promoterId, @Param("ordeStr")String ordeStr, @Param("currentPage")Integer currentPage,
			@Param("pageSize")Integer pageSize);
}