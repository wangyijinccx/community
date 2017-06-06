package com.ipeaksoft.moneyday.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommPlayback;
import com.ipeaksoft.moneyday.core.mapper.CommPlaybackMapper;

@Service
public class CommPlaybackService extends BaseService {

	@Autowired
	private CommPlaybackMapper commPlaybackMapper;

	public CommPlayback selectByPrimaryKey(Integer id) {
		return commPlaybackMapper.selectByPrimaryKey(id);
	}

	public int insertSelective(CommPlayback record) {
		return commPlaybackMapper.insert(record);
	}

	public int updateByPrimaryKeySelective(CommPlayback record) {
		return commPlaybackMapper.updateByPrimaryKeySelective(record);
	}

	public List<Map<String, Object>> selectPlaybacks(Integer webinarId,
			Integer start, Integer length) {
		return commPlaybackMapper.selectPlaybacks(webinarId, start, length);

	}

}
