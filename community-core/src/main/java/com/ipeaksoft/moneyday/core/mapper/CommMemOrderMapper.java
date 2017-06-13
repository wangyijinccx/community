package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import com.ipeaksoft.moneyday.core.entity.CommMemOrder;

public interface CommMemOrderMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(CommMemOrder record);

	int insertSelective(CommMemOrder record);

	CommMemOrder selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(CommMemOrder record);

	int updateByPrimaryKey(CommMemOrder record);

	List<CommMemOrder> selectByOrderId(String orderId);

	int updateByOrderId(CommMemOrder record);

	List<Map<String, Object>> getOrders(Long id);

}