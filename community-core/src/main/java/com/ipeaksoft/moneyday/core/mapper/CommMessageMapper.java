package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ipeaksoft.moneyday.core.entity.CommMessage;

public interface CommMessageMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(CommMessage record);

	int insertSelective(CommMessage record);

	CommMessage selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(CommMessage record);

	int updateByPrimaryKey(CommMessage record);

	Map<String, Object> selectOrderInfo(String orderId);
	
	int findUnreadCount(String token);
	
	int updateRead(Integer id);
	
    List<CommMessage> selectMessages(
    		@Param(value = "token") String token,
			@Param(value = "currentPage") Integer start,
			@Param(value = "pageSize") Integer length);
}