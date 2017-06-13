package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import com.ipeaksoft.moneyday.core.entity.CommUserDay;

public interface CommUserDayMapper {
	int deleteByPrimaryKey(Integer id);

	int insert(CommUserDay record);

	int insertSelective(CommUserDay record);

	CommUserDay selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(CommUserDay record);

	int updateByPrimaryKey(CommUserDay record);

	CommUserDay selectCurrentInfo(Integer userid);

	int updateCurrentInfo(CommUserDay record);

	Map<String, Object> getConsumptionThisMonth(Integer userid);

	List<Map<String, Object>> selectByUserId(Integer userid);
}