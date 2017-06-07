package com.ipeaksoft.moneyday.core.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommGame;
import com.ipeaksoft.moneyday.core.mapper.CommGameMapper;

@Service
public class CommGameService extends BaseService{

	@Autowired
	private CommGameMapper  commGameMapper;
	
	
	 public int insertSelective(CommGame record){
		 return commGameMapper.insertSelective(record);
	 }
	 
	 public int updateByGameid(CommGame record){
		 return  commGameMapper.updateByGameid(record);
	 }
	
	
}
