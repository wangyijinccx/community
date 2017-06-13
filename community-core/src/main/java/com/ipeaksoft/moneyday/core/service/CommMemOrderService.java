package com.ipeaksoft.moneyday.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommMemOrder;
import com.ipeaksoft.moneyday.core.mapper.CommMemOrderMapper;

@Service
public class CommMemOrderService extends BaseService {

	@Autowired
	private CommMemOrderMapper commMemOrderMapper;

	public int insertSelective(CommMemOrder record) {
		return commMemOrderMapper.insertSelective(record);
	}

	public int updateByPrimaryKeySelective(CommMemOrder record) {
		return commMemOrderMapper.updateByPrimaryKeySelective(record);
	}

	public List<CommMemOrder> selectByOrderId(String orderId) {
		return commMemOrderMapper.selectByOrderId(orderId);
	}

	public int updateByOrderId(CommMemOrder record) {
		return commMemOrderMapper.updateByOrderId(record);
	}

	public List<Map<String, Object>> getOrders(Long id) {
		return commMemOrderMapper.getOrders(id);
	}
}
