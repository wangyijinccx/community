package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommGameSeparate;

public interface CommGameSeparateMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(CommGameSeparate record);

	int insertSelective(CommGameSeparate record);

	CommGameSeparate selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(CommGameSeparate record);

	int updateByPrimaryKey(CommGameSeparate record);

	CommGameSeparate selectByPromoterIdAndAppid(Integer promoterId,
			Integer appid);

	int updateByPromoterIdAndAppid(CommGameSeparate record);
}