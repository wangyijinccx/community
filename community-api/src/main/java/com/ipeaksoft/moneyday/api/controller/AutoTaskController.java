package com.ipeaksoft.moneyday.api.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.vo.AdLangyi;
import com.ipeaksoft.moneyday.core.entity.TaskFast;
import com.ipeaksoft.moneyday.core.service.LangYiService;
import com.ipeaksoft.moneyday.core.service.TaskAutoService;
import com.ipeaksoft.moneyday.core.service.TaskFastService;
import com.ipeaksoft.moneyday.core.util.Channel;
import com.ipeaksoft.moneyday.core.util.PersistRedisKey;

/**
 * 子渠道获取朗亿的在线任务
 * @author liyazi
 * @date 2015年10月14日
 */
@Controller
public class AutoTaskController extends BaseController {
	@Autowired
	LangYiService langYiService;
	@Autowired
	TaskAutoService 		taskAutoService;
    @Autowired
    TaskFastService         taskFastService;

	@ResponseBody
	@RequestMapping("/langyiold")
	public Object onlineTask(
			@RequestParam(value = "channel", required = false) String channel) {
		Set<String> onlineAdid = langYiService.getOnlineTask();

		//TODO: remove
		//		if( onlineAdid.size() == 0 ){
		//			String[] ids = new String[]{"100726", "100905", "100756", "100442"};
		//			Collections.addAll(onlineAdid, ids);
		//		}
		
		Channel c = Channel.XIGUAMEI;
		if( channel!=null && StringUtils.equals(channel.toLowerCase(), Channel.TAOJINZHE.name().toLowerCase()) ){
			c = Channel.TAOJINZHE;
		}

		Map<String, Map<String, Object>> map = langYiService.getTaskJson(onlineAdid, c);
		return JSONObject.toJSONString(map);
	}
	
	@ResponseBody
	@RequestMapping("/langyi")
	public Object taskLangyi(
			@RequestParam(value = "channel", required = false) String channel) {
		Set<String> onlineAdid = langYiService.getOnlineTask();

		Channel c = Channel.XIGUAMEI;
		if( channel!=null && StringUtils.equals(channel.toLowerCase(), Channel.TAOJINZHE.name().toLowerCase()) ){
			c = Channel.TAOJINZHE;
		}

		Set<Long> taskIds = new HashSet<Long>();
		for (String adid: onlineAdid){
			long taskid = langYiService.getTaskIdByLangYiAdid(adid, c);
			if (taskid > 0){
				taskIds.add(taskid);
			}
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        JSONObject result = new JSONObject();
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            result.put("code", 0);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, new HashSet<String>()));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 1);
            result.put("message", "error");
        }
        return result;
	}
	
	@ResponseBody
	@RequestMapping("/batmobi")
	public Object batmobi(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.BatMobiOnlineTaskStudio.name());
		}
		else if (chann == Channel.XIGUAMEI){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.BatMobiOnlineTaskXiguamei.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);

		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}
		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.BatMobiTaskMapStudio;
		}
		else if (chann == Channel.XIGUAMEI){
			key = PersistRedisKey.BatMobiTaskMapXiguamei;
		}
		Set<Long> taskIds = new HashSet<Long>();
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.BatMobiPriceMapStudio;
    		}
    		else if (chann == Channel.XIGUAMEI){
    			priceKey = PersistRedisKey.BatMobiPriceMapXiguamei;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.BatMobiOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
	@ResponseBody
	@RequestMapping("/mobvista")
	public Object mobvista(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.MobvistaOnlineTaskStudio.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);
		
		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}

		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.MobvistaTaskMapStudio;
		}
		
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		
		Set<Long> taskIds = new HashSet<Long>();
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.MobvistaPriceMapStudio;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
            
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.MobvistaOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
	@ResponseBody
	@RequestMapping("/yeahmobi")
	public Object yeahmobi(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.YeahMobiOnlineTaskStudio.name());
		}
		else if (chann == Channel.XIGUAMEI){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.YeahMobiOnlineTaskXiguamei.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);
		
		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}

		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.YeahMobiTaskMapStudio;
		}
		else if (chann == Channel.XIGUAMEI){
			key = PersistRedisKey.YeahMobiTaskMapXiguamei;
		}
		Set<Long> taskIds = new HashSet<Long>();
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.YeahMobiPriceMapStudio;
    		}
    		else if (chann == Channel.XIGUAMEI){
    			priceKey = PersistRedisKey.YeahMobiPriceMapXiguamei;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.YeahMobiOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
	@ResponseBody
	@RequestMapping("/avazu")
	public Object avazu(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.AvazuOnlineTaskStudio.name());
		}
		else if (chann == Channel.XIGUAMEI){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.AvazuOnlineTaskXiguamei.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);

		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}

		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.AvazuTaskMapStudio;
		}
		else if (chann == Channel.XIGUAMEI){
			key = PersistRedisKey.AvazuTaskMapXiguamei;
		}
		Set<Long> taskIds = new HashSet<Long>();
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.AvazuPriceMapStudio;
    		}
    		else if (chann == Channel.XIGUAMEI){
    			priceKey = PersistRedisKey.AvazuPriceMapXiguamei;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.AvazuOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
	
	@ResponseBody
	@RequestMapping("/appcoach")
	public Object Appcoach(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		
		//adid
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.AppcoachOnlineTaskStudio.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);

		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}

		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.AppcoachTaskMapStudio;
		}
		//adid-> taskId
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		
		//taskIds
		Set<Long> taskIds = new HashSet<Long>();
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.AppcoachPriceMapStudio;
    		}
            
    		//adid-> price
    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
            
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.AppcoachOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
	@ResponseBody
	@RequestMapping("/wanpu")
	public Object wanPu(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.XIGUAMEI){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.WanPuOnlineTaskStudio.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);
		
		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}

		PersistRedisKey key = null;
		if (chann == Channel.XIGUAMEI){
			key = PersistRedisKey.WanPuTaskMapXiguamei;
		}
		
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		
		Set<Long> taskIds = new HashSet<Long>();
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.XIGUAMEI){
    			priceKey = PersistRedisKey.WanPuPriceMapXiguamei;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
            
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.WanPuOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}
	
    private List<AdLangyi> toChannelList(List<TaskFast> channelList, Set<String> prizeAdids) {
        List<AdLangyi> result = new ArrayList<AdLangyi>();
        for (TaskFast fast : channelList) {
            AdLangyi ad = new AdLangyi();
            ad.setAdid(fast.getId().toString());
            ad.setTask_source(fast.getTaskSource());
            ad.setAd(fast.getTaskname());
            ad.setApp_store_url(fast.getDownloadUrl());
            ad.setIcon(fast.getImg());
            ad.setPrice(fast.getIncome());
            ad.setDescription(fast.getDescription());
            ad.setItunesid(Long.valueOf(fast.getAppid()));
            if (prizeAdids != null && prizeAdids.contains(fast.getAdId())){
            	ad.setPrize(1);
            }
            ad.setDailyCap(fast.getTotal());
            ad.setKeyWord(fast.getKeyWord());
            result.add(ad);
        }
        return result;
    }
    
    
    @ResponseBody
	@RequestMapping("/dianru")
	public Object dianRu(@RequestParam(value = "channel", required = false) String channel) {
        JSONObject result = new JSONObject();

        Channel chann = Channel.valueOf(channel);
		if (chann == null){
            result.put("code", 0);
            result.put("message", "error");
            return result;
		}
		Set<String> onlineAdid = null;
		if (chann == Channel.STUDIO){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.DianRuOnlineTaskStudio.name());
		}
		else if (chann == Channel.XIGUAMEI){
			onlineAdid = taskAutoService.getOnlineTask(PersistRedisKey.DianRuOnlineTaskXiguamei.name());
		}
		logger.debug("onlineAdid: "+onlineAdid);

		if (onlineAdid == null || onlineAdid.size()==0){
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", "0");
            return result;
		}
		PersistRedisKey key = null;
		if (chann == Channel.STUDIO){
			key = PersistRedisKey.DianRuTaskMapStudio;
		}
		else if (chann == Channel.XIGUAMEI){
			key = PersistRedisKey.DianRuTaskMapXiguamei;
		}
		Set<Long> taskIds = new HashSet<Long>();
		Map<String, String> map = taskAutoService.getTaskIdMap(key);
		for (String adid: onlineAdid){
			taskIds.add(NumberUtils.toLong(map.get(adid)));
		}
		logger.debug("onlineTaskIds: "+taskIds);
		
        try {
            List<TaskFast> channelList = taskFastService.listByIds(taskIds);
            PersistRedisKey priceKey = null;
    		if (chann == Channel.STUDIO){
    			priceKey = PersistRedisKey.DianRuPriceMapStudio;
    		}
    		else if (chann == Channel.XIGUAMEI){
    			priceKey = PersistRedisKey.DianRuPriceMapXiguamei;
    		}

    		Map<String, String> priceMap = taskAutoService.getTaskPrice(priceKey);
            if (priceMap != null){
                channelList.forEach(t->{
                	if (priceMap.containsKey(t.getAdId())){
                    	t.setIncome(priceMap.get(t.getAdId()));
                	}
                });
            }
    		Set<String> prizeAdids = taskAutoService.getOnlineTask(PersistRedisKey.DianRuOnlineTaskXiguamei.name());
            result.put("code", 1);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelList(channelList, prizeAdids));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 0);
            result.put("message", "error");
        }
        return result;
	}


}
