package com.ipeaksoft.moneyday.core.service;

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

}
