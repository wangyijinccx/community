package com.ipeaksoft.moneyday.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommMembers;
import com.ipeaksoft.moneyday.core.mapper.CommMembersMapper;

@Service
public class CommMembersService extends BaseService {

	@Autowired
	private CommMembersMapper commMembersMapper;

	public int insertSelective(CommMembers record) {
		return commMembersMapper.insertSelective(record);
	}

	public CommMembers selectByUserName(String username) {
		return commMembersMapper.selectByUserName(username);
	}

	public int updateByPrimaryKeySelective(CommMembers record){
		return commMembersMapper.updateByPrimaryKeySelective(record);
	}
	
	public List<Map<String, Object>> selectGameMems(Integer oaAppId,
			Integer promoterId, String ordeStr, Integer currentPage,
			Integer pageSize) {
		return commMembersMapper.selectGameMems(oaAppId, promoterId, ordeStr,
				currentPage, pageSize);
	}

}
