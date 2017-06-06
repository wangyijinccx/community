package com.ipeaksoft.moneyday.core.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ipeaksoft.moneyday.core.entity.CommPlayback;

public interface CommPlaybackMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CommPlayback record);

    int insertSelective(CommPlayback record);

    CommPlayback selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CommPlayback record);

    int updateByPrimaryKey(CommPlayback record);
    
    List<Map<String, Object>> selectPlaybacks(
    		@Param(value = "webinarId") Integer webinarId,
			@Param(value = "currentPage") Integer start,
			@Param(value = "pageSize") Integer length);
}