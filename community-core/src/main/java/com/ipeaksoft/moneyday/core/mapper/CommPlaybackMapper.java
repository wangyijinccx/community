package com.ipeaksoft.moneyday.core.mapper;

import com.ipeaksoft.moneyday.core.entity.CommPlayback;

public interface CommPlaybackMapper {
    int insert(CommPlayback record);

    int insertSelective(CommPlayback record);
}