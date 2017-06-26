package com.ipeaksoft.moneyday.api.schedule;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.service.BaseService;

@Service
@Lazy(true)
public class ScheduleService extends BaseService {
	
	
    @PostConstruct
    public void init() {
    	logger.debug("init .........");
    }
	
    @Scheduled(cron = "0 */1 * * * ?")
    public void doCacheMinute(){
      
    }
    

}
