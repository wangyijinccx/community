package com.ipeaksoft.moneyday.api.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.api.service.CallbackService;
import com.ipeaksoft.moneyday.api.vo.AdLangyi;
import com.ipeaksoft.moneyday.core.dto.FastClick;
import com.ipeaksoft.moneyday.core.entity.IdfaApp;
import com.ipeaksoft.moneyday.core.entity.TaskFast;
import com.ipeaksoft.moneyday.core.entity.UserTaskFastActive;
import com.ipeaksoft.moneyday.core.entity.UserTaskFastClick;
import com.ipeaksoft.moneyday.core.service.AdIdfaService;
import com.ipeaksoft.moneyday.core.service.FastActiveService;
import com.ipeaksoft.moneyday.core.service.FastClickService;
import com.ipeaksoft.moneyday.core.service.IdfaAppService;
import com.ipeaksoft.moneyday.core.service.RedisClient;
import com.ipeaksoft.moneyday.core.service.TaskFastService;
import com.ipeaksoft.moneyday.core.service.UserFastService;
import com.ipeaksoft.moneyday.core.service.UserService;
import com.ipeaksoft.moneyday.core.util.Constant;
import com.ipeaksoft.moneyday.core.util.FastActive;
import com.ipeaksoft.moneyday.core.util.HttpRequest;
import com.ipeaksoft.moneyday.core.util.IPUtils;
import com.ipeaksoft.moneyday.core.util.MD5Util;
import com.ipeaksoft.moneyday.core.util.RedisKeyUtil;

@Controller
public class TaskBridgeController extends BaseController {

    @Autowired
    RedisClient             redisClient;
    @Autowired
    UserService             userService;
    @Autowired
    FastActiveService       fastActiveService;
    @Autowired
    FastClickService        fastClickService;
    @Autowired
    UserFastService         userFastService;
    @Autowired
    TaskFastService         taskFastService;
//    @Autowired
//    TaskFastCallbackService callbackService;
    @Autowired
    AdIdfaService  adIdfaService;
    @Autowired
	IdfaAppService idfaAppService;

    @Autowired
    CallbackService callbackService;

    /**
     * 获取快速任务列表
     * @return
     */
    @ResponseBody
    @RequestMapping("getChannelAds")
    public Object getChannelAds(String channelName) {
        JSONObject result = new JSONObject();
        try {
            List<TaskFast> channelList = taskFastService.listChannelTask(channelName);
            result.put("code", 0);
            result.put("message", "success");
            result.put("size", String.valueOf(channelList.size()));
            result.put("list", toChannelListNoAppid(channelList));
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 1);
            result.put("message", "程序异常");
        }
        return result;
    }

    /***
     * 万象小吧快速任务的激活
     * @param fastActive
     * @return
     */
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.POST, value = "notifyFinishTask")
//    public Object notifyFinishTask(WanXiangVo vo) {
//        JSONObject result = new JSONObject();
//        int success = 0;
//        String msg = "";
//        String idfa = vo.getIdfa();
//        Long taskId = Long.valueOf(vo.getAdId());
//        try {
//            // 获取MD5加密的参数
//            String params = taskId.toString().concat(vo.getMac()).concat(vo.getIdfa()).concat(vo.getDeviceId()).concat(WanXiangVo.KEY);
//            String sign = MD5Util.md5(params).toUpperCase();
//            if (!sign.equals(vo.getSign())) {
//                result.put("success", success);
//                result.put("msg", "签名错误");
//                logger.info("request url:{}, result:{}", request.getRequestURI(), result);
//                return result;
//            }
//            // 查询快速任务是否存在
//            TaskFast taskFast = taskFastService.findById(taskId);
//            if (null == taskFast) {
//                result.put("success", success);
//                result.put("msg", "任务不存在");
//                logger.info("request url:{}, result:{}", request.getRequestURI(), result);
//                return result;
//            }
//            // 查询任务激活记录
//            UserTaskFastActive taskFastActive = fastActiveService.findByIdfa(idfa, taskId, Constant.CLIENTTYPE_OTHER);
//            if (null == taskFastActive) {
//                // 记录快速任务激活记录
//                UserTaskFastActive active = toActiveObject(vo, taskFast);
//                active = fastActiveService.create(active);
//                // 记录快速任务明细表
//                if ("1".equals(active.getStatus())) {
//                    UserTaskFast fast = toUserTaskFast(taskFast, active);
//                    fast = userFastService.create(fast);
//                    success = 1;
//                    msg = "success";
//                }
//            } else {
//                success = 1;
//                msg = "已激活";
//            }
//        } catch (Exception e) {
//            logger.error("ERROR:", e);
//            msg = "程序异常";
//        }
//        result.put("success", success);
//        result.put("msg", msg);
//        logger.info("request url:{}, result:{}", request.getRequestURI(), result);
//        return result;
//    }

    @ResponseBody
    @RequestMapping("click")
    public Object click(FastClick fastClick, HttpServletResponse response) {
        JSONObject result = new JSONObject();
        try {
            Long taskId = Long.valueOf(fastClick.getTaskId());
            TaskFast fast = taskFastService.findById(taskId);
            String idfa = fastClick.getIdfa();
            String sign = null;
            if("CHUJIN".equals(fast.getTaskSource())){
            	String[] osVersion={"9.0.2","10.1","9.3.1","10.0"};
        		int i = (int) Math.floor(Math.random()*4);
        		fastClick.setOSVersion(osVersion[i]);
            }
            if("SHANDAI".equals(fast.getTaskSource())){
            	String duplicate = callbackService.queryForShanDai(fast, idfa, fastClick);
            	if("NO".equals(duplicate)){
            		result.put("code", 203);
	         		result.put("message", "idfa重复");
	         		return result;
            	}else{
            		sign = duplicate;
            	}
            }else{
            	 boolean duplicate = callbackService.query(fast, idfa, fastClick);
	        	 if (duplicate){
	         		result.put("code", 203);
	         		result.put("message", "idfa重复");
	         		return result;
	             }
            }
            //callbackService.sendXYAd(fast,"click",fastClick);
            // 对不同渠道进行异步分发点击请求
            boolean clickResult = false;
            switch (fast.getTaskSource()){
        	case "DIANRU":
        		clickResult = callbackService.clickSync(fast, idfa, fastClick,sign);
        		break;
        	default:
        		callbackService.clickAsync(fast, idfa, fastClick,sign);
        		clickResult = true;
            }
            if (!clickResult){
        		result.put("code", 204);
        		result.put("message", "任务已下线");
        		return result;
            }

            fastClick.setAppID(fast.getAppid());
            // 查询数据库点击记录
            UserTaskFastClick utFastClick = fastClickService.findChannelByTaskId(fastClick.getIdfa(), taskId);
            if (null != utFastClick) {
                UserTaskFastClick utfClick = new UserTaskFastClick();
                utfClick.setId(utFastClick.getId());
                utfClick.setPoint(fastClick.getPoints());   
                utfClick.setCreateTime(new Date());
                fastClickService.updateBySelective(utfClick);
            } else {
                fastClick.setClientIP((null==fastClick.getClientIP()) ? getIP() : fastClick.getClientIP());
                fastClick.setAppName(fast.getTaskname());
                fastClick.setPoints(fast.getAward().toString());    
                // 记录快速任务点击记录
                utFastClick = fastClick.convertModel();
                fastClickService.create(utFastClick);   
            }
            // 存入redis
            String key = RedisKeyUtil.getKey(fastClick);
            redisClient.setObject(key, fastClick);
            redisClient.expire(key, RedisKeyUtil.TIMEOUT_CLICK);// 设置超时时间
            // 返回
            result.put("code", 0);
            result.put("message", "success");
            return result;
        } catch (Exception e) {
            logger.error("ERROR:", e);
            result.put("code", 1);
            result.put("message", "未知异常");
        }
        return result;
    }
    
    /**
     * 钱咖采量记录
     * @param request
     * @param response
     * @return
     */
	@ResponseBody
	@RequestMapping("qkclick")
	public Object qkClick(HttpServletRequest request,HttpServletResponse response) {
		JSONObject result = new JSONObject();
		try {
			String appid = request.getParameter("appid");
			String idfa = request.getParameter("idfa");
			String ip = request.getParameter("ip");
			if (null == appid || "".equals(appid) || null == idfa || "".equals(idfa)) {
				result.put("code", 202);
				result.put("message", "参数缺少");
				return result;
			}
			// 添加用户点击记录
			UserTaskFastClick utfClick = new UserTaskFastClick();
			utfClick.setAppid(appid);
			utfClick.setIdfa(idfa);
			utfClick.setClientip((null == ip) ? getIP() : ip);
			utfClick.setCreateTime(new Date());
			fastClickService.create(utfClick);
			// 返回
			result.put("code", 0);
			result.put("message", "success");
			return result;
		} catch (Exception e) {
			logger.error("ERROR:", e);
			result.put("code", 1);
			result.put("message", "未知异常");
		}
		return result;
	}
    
    /**
     * @description 第三方激活快速任务的回调方法
     * @author: sxy
     * 2015年5月26日 下午5:37:11
     * @param fastActive
     * @return 
     */
    @ResponseBody
    @RequestMapping(value={"speedTaskActive","active"})
    public Object speedTaskActive(FastActive fastActive) {
    	if (IPUtils.isInnerIP(fastActive.getClientIP())){
            fastActive.setClientIP(getIP());
    	}
        JSONObject result = new JSONObject();
        String code = "1002";
        String message = "";
        try {
            String appid = fastActive.getAppID();
            String idfa = fastActive.getIdfa();
            
            // 异步分发消息到工作室接口
            // 已经作为子渠道回调过， 所以取消此步骤
//            callbackService.callStudio(idfa, appid);
            // 异步分发消息到渠道商激活接口或者上报到广告主
            boolean ifSuccess = callActive(fastActive);
            // ifSuccess  true 成功  false 失败
            if(!ifSuccess){
        	   result.put("code", "1");
               result.put("message", "激活上报失败");
               return result;
            }
            UserTaskFastClick click = fastClickService.getOneByIdfa(idfa, appid);
            if (null != click) {
                // 查询任务激活记录
                UserTaskFastActive taskFastActive = fastActiveService.findByIdfa(idfa, click.getTaskId(), Constant.CLIENTTYPE_OTHER);
                if (null == taskFastActive) {
                    // 记录快速任务激活记录
                    UserTaskFastActive active = toActiveObject(fastActive, click);
                    active = fastActiveService.create(active);

                    // 记录快速任务明细表
                    if ("1".equals(active.getStatus())) {
                        code = "1000";
                        message = "success";
                    }
                } else {
                    code = "1000";
                    message = "已激活";
                }
                syncIdfaApp(idfa, appid);
            } else {
                code = "1005";
                message = "任务未开始，不能提交审核";
            }
        } catch (Exception e) {
            logger.error("ERROR:", e);
            code = "1001";
            message = "未知异常";
        }

        result.put("code", code);
        result.put("message", message);
        return result;
    }

    /**
     * 以渠道的激活为准时渠道上报接口
     * @param fastActive
     * @return
     */
    @ResponseBody
    @RequestMapping("activeUpload")
    public Object activeUpload(String idfa, String taskId,String clientIP,String osVersion) {
        TaskFast fast = null;
        try{
            fast = taskFastService.findById(Long.parseLong(taskId));
        }
        catch(Exception e){
        }
        
        if (fast == null){
    		JSONObject result = new JSONObject();
    		result.put("code", "1002");
            result.put("message", "参数错误");
    		return result;
        }
    	FastActive fastActive = new FastActive();
    	fastActive.setIdfa(idfa);
    	fastActive.setAppID(fast.getAppid());
    	fastActive.setTaskSource(fast.getTaskSource());
    	fastActive.setClientIP(clientIP);
    	fastActive.setOSVersion(osVersion);
    	
    	return speedTaskActive(fastActive);
    }
    
    private List<AdLangyi> toChannelListNoAppid(List<TaskFast> channelList) {
        List<AdLangyi> result = new ArrayList<AdLangyi>();
        for (TaskFast fast : channelList) {
            AdLangyi ad = new AdLangyi();
            ad.setAdid(fast.getId().toString());
            ad.setTask_source(fast.getTaskSource());
            ad.setAd(fast.getTaskname());
            ad.setApp_store_url(fast.getDownloadUrl());
            ad.setPrice(fast.getAward().toString());
           // ad.setItunesid(Long.valueOf(fast.getAppid()));
            result.add(ad);
        }
        return result;
    }

//    private UserTaskFastActive toActiveObject(WanXiangVo vo, TaskFast taskFast) {
//        UserTaskFastActive record = new UserTaskFastActive();
//        record.setIdfa(vo.getIdfa());
//        long taskId = 0;
//        try {
//            taskId = Long.parseLong(vo.getAdId());
//        } catch (NumberFormatException e) {
//        }
//        record.setTaskId(taskId);
//        record.setCreateTime(new Date());
//        record.setMobile(vo.getMobile());
//        record.setAppid(taskFast.getAppid());
//        record.setStatus("1");
//        record.setClientType(Constant.CLIENTTYPE_OTHER);
//        return record;
//    }

    private UserTaskFastActive toActiveObject(FastActive active, UserTaskFastClick click) {
        UserTaskFastActive record = new UserTaskFastActive();
        String taskSource = active.getTaskSource();
        record.setIdfa(active.getIdfa());
        record.setTaskId(click.getTaskId());
        record.setIp(active.getClientIP());
        record.setCreateTime(new Date());
        record.setStatus("0");
        if (click != null) {
            record.setClientType(click.getClientType());
            record.setMobile(click.getMobile());
            record.setAppid(click.getAppid());
            record.setStatus("1");
        }
        if (taskSource != null && !"self".equals(taskSource)) {
            record.setClientType(Constant.CLIENTTYPE_OTHER);
        }
        return record;
    }

//    private UserTaskFast toUserTaskFast(TaskFast fast, UserTaskFastActive active) {
//        UserTaskFast record = new UserTaskFast();
//        record.setMobile((null == active.getMobile()) ? "" : active.getMobile());
//        record.setTaskId(active.getTaskId());
//        record.setAppid(active.getAppid());
//        record.setAward(fast.getAward());
//        record.setCreateTime(new Date());
//        record.setDescription("快速任务 " + fast.getTaskname());
//        record.setClickId((long) 0);
//        record.setActiveId(active.getId());
//        return record;
//    }
    
	@ResponseBody
	@RequestMapping("activeWanPu")
	public Object activeWanPu(HttpServletRequest request) {
		// 接口密钥：b11c55d678671ada25f58198f6a6e872
		// 接口:http://ads.i43.com/api/activeWanPu
		logger.info("wanpu参数: {}" , request.getQueryString());
		JSONObject result = new JSONObject();
		Boolean success = true;
		String message = "";
		try {
			String adv_id = request.getParameter("adv_id");
			String appid = taskFastService.getByAdId(adv_id);
			if(null != appid && !"".equals(appid)){
				String app_id = request.getParameter("app_id");
				String key = request.getParameter("key");
				String udid = request.getParameter("udid");
				String bill = request.getParameter("bill");
				if (bill == null) {
					bill = "";
				}
				String points = request.getParameter("points");
				if (points == null) {
					points = "";
				}
				String status = request.getParameter("status");
				if (status == null) {
					status = "";
				}
				String activate_time = request.getParameter("activate_time");
				String order_id = request.getParameter("order_id");
				String wapskey = request.getParameter("wapskey");
				String callBackKey = "b11c55d678671ada25f58198f6a6e872";
				if (!bill.equals("0") && !points.equals("0") && status.equals("1")) {
					activate_time = URLEncoder.encode(activate_time, "UTF-8").toUpperCase();
					String plaintext = adv_id + app_id + key + udid + bill + points
							+ activate_time + order_id + callBackKey;
					String keys = MD5Util.md5(plaintext);
					if (keys.equalsIgnoreCase(wapskey)) {
						String url = "http://ads.i43.com/api/speedTaskActive"
								+ "?taskSource=WANPU&idfa=" + udid.toUpperCase() + "&appID="
								+appid;
						HttpRequest.sendHttpRequest(url, "GET", "UTF-8");
						success = true;
						message = "成功接收";
					} else {
						success = false;
						message = "无效数据";
					}

				} else {
					success = false;
					message = "无效数据";
				}
			}else{
				success = false;
				message = "无效数据";
			}
		} catch (Exception e) {
			logger.error("ERROR:", e);
			success = false;
			message = "无效数据";
		}
		result.put("message", message);
		result.put("success", success);
		return result;
	}
    
	
	@ResponseBody
	@RequestMapping("activewifi")
	public Object activeWifi(String appkey, String ifa) {
		JSONObject result = new JSONObject();
		if (StringUtils.isBlank(appkey)|| StringUtils.isBlank(ifa)){
			result.put("message", "param error");
			result.put("success", false);
			return result;

		}
		String url = "http://ads.i43.com/api/speedTaskActive"
				+ "?taskSource=WIFIGUANJIA&idfa=" + ifa + "&appID="
				+appkey;
		HttpRequest.sendHttpRequest(url, "GET", "UTF-8");
		result.put("message", "成功接收");
		result.put("success", true);
		return result;
	}
	
	/**
	 * 易推回调
	 * @param appkey
	 * @param ifa
	 * @return
	 */
	@ResponseBody
	@RequestMapping("postback")
	public Object postBack(String appid, String ifa ,String mac) {
		logger.info("YITUI参数: {}" , request.getQueryString());
		JSONObject result = new JSONObject();
		if (StringUtils.isBlank(appid)|| StringUtils.isBlank(ifa)){
			result.put("message", "param error");
			result.put("success", false);
			return result;

		}
		FastActive  fastActive  = new FastActive();
		fastActive.setTaskSource("YITUI");
		fastActive.setIdfa(ifa);
		fastActive.setAppID(appid);
		speedTaskActive(fastActive);
		result.put("message", "成功接收");
		result.put("success", true);
		return result;
	}
	
	@Async
	private void syncIdfaApp(String idfa, String appid){
		try {
			int cnt = idfaAppService.selectCntByAppidAndIdfa(appid, idfa);
			IdfaApp record = new IdfaApp();
			record.setIdfa(idfa);
			record.setAppid(appid);
			record.setCreateTime(new Date());
			record.setNumorder(cnt);
			idfaAppService.insert(record);
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * 激活信息上报广告主或者下送渠道
	 * @param fastActive
	 * @throws UnsupportedEncodingException
	 */
	public boolean callActive(FastActive fastActive) throws UnsupportedEncodingException {
		String key = RedisKeyUtil.getKey(fastActive);
		Object obj = redisClient.getObject(key);
		if (obj != null) {
			FastClick click = (FastClick) obj;
			redisClient.delByKey(key);

			Long taskId = Long.valueOf(click.getTaskId());
			TaskFast fast = taskFastService.findById(taskId);
			
			//上报广告主
			if (fast.isActiveUpload()){
				if("CHANDASHI".equals(fast.getTaskSource())){
					//同步
					return callbackService.active(fast, fastActive);
				}else{
					//异步
					callbackService.activeAsync(fast, fastActive);
				}
				
			}
			else{
				//下送子渠道
				callbackService.toChannel(fast,click);
			}
		}else{
			return false;
		}
		return true;
	}
}
