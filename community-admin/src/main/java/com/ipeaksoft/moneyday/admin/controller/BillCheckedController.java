package com.ipeaksoft.moneyday.admin.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.admin.util.BillUtil;
import com.ipeaksoft.moneyday.admin.util.CommonUtil;
import com.ipeaksoft.moneyday.admin.util.DateUtil;
import com.ipeaksoft.moneyday.admin.util.DuibaUtil;
import com.ipeaksoft.moneyday.admin.util.security.SpringSecurityUtils;
import com.ipeaksoft.moneyday.core.entity.StatCash;
import com.ipeaksoft.moneyday.core.entity.User;
import com.ipeaksoft.moneyday.core.entity.UserCash;
import com.ipeaksoft.moneyday.core.entity.UserCashApprove;
import com.ipeaksoft.moneyday.core.entity.UserCashOrder;
import com.ipeaksoft.moneyday.core.entity.UserLoginHistory;
import com.ipeaksoft.moneyday.core.exception.UserNotFoundException;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.StatCashService;
import com.ipeaksoft.moneyday.core.service.UserAdsService;
import com.ipeaksoft.moneyday.core.service.UserAwardService;
import com.ipeaksoft.moneyday.core.service.UserCashApproveService;
import com.ipeaksoft.moneyday.core.service.UserCashService;
import com.ipeaksoft.moneyday.core.service.UserFastService;
import com.ipeaksoft.moneyday.core.service.UserLoginHistoryService;
import com.ipeaksoft.moneyday.core.service.UserService;

@Controller
@RequestMapping(value = "/bill/checked")
public class BillCheckedController extends BaseController {

    BillUtil                billUtil = new BillUtil();
    @Autowired
    UserAwardService        userAwardService;
    @Autowired
    UserAdsService          userAdsService;
    @Autowired
    UserFastService         userFastService;
    @Autowired
    AdminUserService        adminUserService;
    @Autowired
    UserCashService         userCashService;
    @Autowired
    StatCashService         statCashService;
    @Autowired
    UserCashApproveService  userCashApproveService;
    @Autowired
    UserLoginHistoryService userLoginHistoryService;
    @Autowired
    UserService             userService;

    @RequestMapping(value = "/list")
    public String checked(HttpServletRequest request) {
        Integer totalmoney = userCashService.getAllMoney();
        totalmoney = (null == totalmoney) ? 0 : totalmoney;
        request.setAttribute("totalmoney", totalmoney / 100);
        return "/bill/checked";
    }

    @RequestMapping(value = "/daylist")
    public String daylist(HttpServletRequest request) {
        String date = request.getParameter("day");
        request.getSession().setAttribute("day", date);
        return "/bill/daychecked";
    }

    @RequestMapping(value = "/checking")
    public String checking(HttpServletRequest request) {
        billUtil.getAwardByReq(request, userFastService, userAdsService);
        return "/bill/checking";
    }

    // 审核订单的操作（通过、推迟、异常）
    @ResponseBody
    @RequestMapping(value = "/checkingorder")
    public String checkingorder(HttpServletRequest request) {
        Date now = new Date();
        String status = request.getParameter("status");
        String date = request.getParameter("day");
        try {
            int stat = Integer.valueOf(status);
            Long id = Long.valueOf(request.getParameter("id"));
            UserCash userCash = userCashService.getUserById(id);
            logger.info("status: " + status);
            logger.info("date: " + date);
            logger.info("id: " + id);
            if (1 == stat) { // 审核通过
                String ordernum = userCash.getOrdernum();
                String result = DuibaUtil.check(ordernum, "");
                logger.info("result: " + result);
                JSONObject jsonObject = JSONObject.parseObject(result);
                String isReceived = jsonObject.getString("success");
                if ("false".equals(isReceived)) {
                    return "订单未被接收，请重试。";
                }
                JSONObject order = jsonObject.getJSONObject("details").getJSONObject(ordernum);
                String success = order.getString("success");
                logger.info("success: " + success);
                if ("false".equals(success)) {
                    result = DuibaUtil.checkWechatOrder(ordernum, "");
                    logger.info("wechat result: " + result);
                    JSONObject json = JSONObject.parseObject(result);
                    String isReceivedWechat = json.getString("success");
                    if ("false".equals(isReceivedWechat)) {
                        return "订单未被接收，请重试。";
                    }
                    JSONObject wechatOrder = json.getJSONObject("details").getJSONObject(ordernum);
                    String wechatSuccess = wechatOrder.getString("success");
                    logger.info("wechatSuccess: " + wechatSuccess);
                    String wechatMsg = wechatOrder.getString("message");
                    if ("false".equals(wechatSuccess)) {
                        logger.info("wechatMsg: " + wechatMsg);
                        return wechatMsg;
                    }
                }
            }

            // 只有未经过异常处理或者推迟处理的订单，才会对待审核记录进行-1操作
            Integer cashStatus = Integer.valueOf(userCash.getStatus());
            if (2 != cashStatus && 3 != cashStatus) {
                // 修改统计表中的待审核记录
                StatCash statCash = statCashService.getStatByDay(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                int pendingCount = statCash.getPendingCount();
                pendingCount -= 1;
                statCash.setPendingCount(pendingCount);
                statCashService.updateByPrimaryKey(statCash);
            }

            // 修改订单表中的状态
            UserCash cash = userCashService.getUserById(id);
            cash.setStatus(status);
            cash.setOperateTime(now);
            String operateResult = (1 == stat) ? "处理中" : (2 == stat) ? "推迟审核" : (3 == stat) ? "审核异常" : "其他";
            cash.setOperateResult(operateResult);
            String name = SpringSecurityUtils.getCurrentUserName();
            Integer operator = adminUserService.getUserByName(name).getId();
            cash.setOperator(operator.toString());
            int update = userCashService.updateUser(cash);

            // 保存当前订单的状态
            UserCashApprove userCashApprove = new UserCashApprove();
            userCashApprove.setOrderid(userCash.getOrderid());
            userCashApprove.setOrdernum(userCash.getOrdernum());
            String description = (1 == stat) ? "正在充值" : (2 == stat) ? "滞后" : (3 == stat) ? "客服人员正在处理" : "其他";
            userCashApprove.setDescription(description);
            userCashApprove.setResult(stat + "");
            userCashApprove.setOperator(operator);
            userCashApprove.setApproveTime(now);
            int insert = userCashApproveService.insertSelective(userCashApprove);
            if (1 == update && 1 == insert) {
                return "success";
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    // 加载单个用户的审核记录
    @ResponseBody
    @RequestMapping(value = "/data_load_checking")
    public String load_data_checking(HttpServletRequest request) {
        String draw = request.getParameter("draw");
        String mobile = request.getParameter("mobile");
        Long id = Long.valueOf(request.getParameter("id"));
        logger.info("[data_load_checking][id]" + id);

        // 查询用户的快速累计收益、常规累计收益、奖励累计收益
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", mobile);

        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        paramMap.put("id", id);
        paramMap.put("statusorder", 1);
        int total = userCashService.countPageByUser(paramMap);
        List<UserCash> list = userCashService.getPageByUser(paramMap);

        int start = (int) paramMap.get("start");
        paramMap.remove("start");
        paramMap.put("start", start + 1);
        List<UserCash> fromlist = userCashService.getPageByUser(paramMap);

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            UserCash userCash = list.get(i);
            Date from = (i < fromlist.size()) ? fromlist.get(i).getCreateTime() : null;
            JSONObject jsonObject = new JSONObject();
            if (map.containsKey("to"))
                map.remove("to");
            map.put("to", userCash.getCreateTime());
            map.put("from", from);
            Integer fastaward = userFastService.getAwardByMobile(map);
            Integer adsaward = userAdsService.getAwardByMobile(map);
            Integer award = userAwardService.getAwardByMobile(map);
            System.out.println("fastaward---------->> " + fastaward);
            jsonObject.put("fastaward", (null == fastaward) ? 0 : fastaward);
            jsonObject.put("adsaward", (null == adsaward) ? 0 : adsaward);
            jsonObject.put("award", (null == award) ? 0 : award);
            int istatus = Integer.valueOf(userCash.getStatus());
            billUtil.formatUserBillData(jsonObject, userCash, istatus);
            jsonArray.add(jsonObject);
        }
        int size = list.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        return result.toString();
    }

    // 加载单日内的交易记录
    @ResponseBody
    @RequestMapping(value = "/data_load_day")
    public String load_data_day(HttpServletRequest request) {
        //        Map<String, String[]> map = request.getParameterMap();
        //        for (Entry<String, String[]> entry : map.entrySet()) {
        //            logger.info(entry.getKey() + " ," + entry.getValue()[0]);
        //        }
        //        String order = request.getParameter("order[0][column]");
        JSONObject result = new JSONObject();
        try {
            String draw = request.getParameter("draw");//搜索内容
            String date = request.getParameter("day");
            Map<String, Object> paramMap = billUtil.getWhereMap(request);
            paramMap.put("day", date);
            int total = userCashService.countPageByDay(paramMap);
            List<UserCash> list = userCashService.getPageByday(paramMap);
            int today = Integer.valueOf(date.substring(date.lastIndexOf("-") + 1));
            JSONArray jsonArray = new JSONArray();
            for (UserCash userCash : list) {
                JSONObject jsonObject = new JSONObject();
                String day = DateUtil.date2Str("HH:mm:ss", userCash.getCreateTime());
                jsonObject.put("day", day);
                jsonObject.put("id", userCash.getUserid());
                jsonObject.put("orderid", userCash.getOrderid());
                jsonObject.put("mobile", userCash.getUserphone());
                User user = userService.getUserById(Long.valueOf(userCash.getUserid()));
                UserLoginHistory history = userLoginHistoryService.getByUserid(Integer.valueOf(userCash.getUserid()));
                jsonObject.put("inviteCode", user.getInviteCode());
                jsonObject.put("province", (null == history) ? "" : (null == history.getProvince()) ? "" : history.getProvince());
                jsonObject.put("city", (null == history) ? "" : (null == history.getCity()) ? "" : history.getCity());
                String fromto = userCash.getFromto();
                fromto = ("online".equals(fromto)) ? "线上" : ("offline").equals(fromto) ? "线下" : "app".equals(fromto) ? "微信" : "其他";
                String type = fromto.concat(userCash.getType()).concat("类");
                jsonObject.put("type", type);
                String exchangetype = userCash.getExchangeType();
                exchangetype = ("phonebill".equals(exchangetype)) ? "话费充值" : ("alipay".equals(exchangetype)) ? "支付宝" : "其他类";
                jsonObject.put("exchangetype", exchangetype);
                String phone = userCash.getMobile();
                phone = (null == phone) ? "" : phone;
                jsonObject.put("phone", phone);
                String account = userCash.getAlipayAccount();
                account = (null == account) ? "" : account;
                account = account.replaceAll(":|：", "<br/>");
                jsonObject.put("alipayaccount", account);
                double amount = userCash.getAmount() / 100;
                jsonObject.put("amount", amount);
                double index = 1 - amount / (today * 10);
                BigDecimal b = new BigDecimal(index);
                double value = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                //            logger.info("index: " + value);
                jsonObject.put("exchangeindex", value);
                //            jsonObject.put("ordernum", userCash.getOrdernum());
                int istatus = Integer.valueOf(userCash.getStatus());
                String status = (0 == istatus) ? "待审核" : (1 == istatus) ? "已审核" : (2 == istatus) ? "推迟审核" : (3 == istatus) ? "审核异常" : (4 == istatus) ? "已审核" : (9 == istatus) ? "已审核" : "其他";
                jsonObject.put("status", status);
                jsonArray.add(jsonObject);
            }
            request.setAttribute("day", date);
            int size = list.size();
            result = billUtil.formatResult(draw, total, jsonArray, size);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    // 加载每天的交易统计记录
    @ResponseBody
    @RequestMapping(value = "/data_load")
    public String load_data(HttpServletRequest request) {
        String draw = request.getParameter("draw");//搜索内容
        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        int total = statCashService.countAllByWhere(paramMap);
        Date date = null;
        if (0 == total) {
            // 数据库没有数据，则首先统计一次所有日期的信息，并将昨天以前的所有数据保存到数据库
            date = null;
        } else {
            // 数据库有昨天以前的所有数据，则读取昨天和当天的数据，保存昨天的数据并合并三者来显示
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -2);
            date = calendar.getTime();
        }

        // A.统计线上/线下用户的兑换信息,并封装到Map中，再执行B。具体如下：
        // 1、date为null,则list为所有用户信息，也就是首次加载兑换信息
        // 2、date不为null，则list为date日期之后的兑换信息，也就是只统计昨天、今天的兑换信息
        // 3、统计完list，则按日期封装到map中，每天一条数据
        // 4、最后将map中数据遍历保存到数据库，并做判断：若已存在记录则只更新记录；若不存在记录则新增记录，并更新总记录数total
        // 5、执行完则跳转到B
        List<UserCashOrder> list = userCashService.selectOrderByDay(date);
        Map<Date, StatCash> map = new HashMap<Date, StatCash>();
        for (UserCashOrder userCashOrder : list) {
            Date day = userCashOrder.getDay();
            StatCash statCash = null;
            if (map.containsKey(day)) {
                statCash = map.get(day);
                map.remove(day);
            } else {
                statCash = new StatCash();
            }
            if ("phonebill".equals(userCashOrder.getExchange_type())) {
                if ("online".equals(userCashOrder.getFromto())) {
                    statCash.setRechargeOnlineAmount(userCashOrder.getAmount() / 100);
                    statCash.setRechargeOnlineTimes(userCashOrder.getTimes());
                } else if ("offline".equals(userCashOrder.getFromto())) {
                    statCash.setRechargeOfflineAmount(userCashOrder.getAmount() / 100);
                    statCash.setRechargeOfflineTimes(userCashOrder.getTimes());
                }
            } else if ("alipay".equals(userCashOrder.getExchange_type())) {
                if ("online".equals(userCashOrder.getFromto())) {
                    statCash.setCashOnlineAmount(userCashOrder.getAmount() / 100);
                    statCash.setCashOnlineTimes(userCashOrder.getTimes());
                } else if ("offline".equals(userCashOrder.getFromto())) {
                    statCash.setCashOfflineAmount(userCashOrder.getAmount() / 100);
                    statCash.setCashOfflineTimes(userCashOrder.getTimes());
                }
            }
            map.put(day, statCash);
        }

        for (Entry<Date, StatCash> entry : map.entrySet()) {
            Date day = entry.getKey();
            StatCash cash = entry.getValue();
            int wrong = userCashService.countUserByStatus(day);
            cash.setDay(day);
            cash.setCashAmount(cash.getCashOfflineAmount() + cash.getCashOnlineAmount());
            cash.setRechargeAmount(cash.getRechargeOfflineAmount() + cash.getRechargeOnlineAmount());
            cash.setAmount(cash.getCashAmount() + cash.getRechargeAmount());
            cash.setPendingCount(wrong);
            int flag = statCashService.addUser(cash); // 插入今天、昨天的数据到数据库
            if (1 == flag) {// 插入成功，说明数据库无今天的数据
                logger.info("flag add------>: " + flag);
                if (0 == CommonUtil.compareDay(day, new Date())) {
                    total += 1; // 如果是今天的数据插入成功，则总数加1
                }
            } else { // 如果插入失败，说明已存在今天的记录，则更新今天的记录
                int update = statCashService.updateUser(cash);
                if (1 != update) {
                    update = statCashService.updateUser(cash);
                }
            }
        }
        
        //B.从数据库读取线上/线下用户的兑换信息，此时信息已更新昨天、今天的数据，total值也已更新
        List<StatCash> statcashs = statCashService.getPageByDay(paramMap);
        JSONArray jsonArray = new JSONArray();
        for (StatCash _statCash : statcashs) {
            JSONObject jsonObject = new JSONObject();
            String day = DateUtil.date2Str("yyyy-MM-dd", _statCash.getDay());
            jsonObject.put("day", day);
            jsonObject.put("amount", _statCash.getAmount());
            jsonObject.put("rechargeAmount", _statCash.getRechargeAmount());
            jsonObject.put("rechargeAmountType", _statCash.getRechargeOfflineAmount().toString().concat("/").concat(_statCash.getRechargeOnlineAmount().toString()));
            jsonObject.put("rechargeTimes", _statCash.getRechargeOfflineTimes() + _statCash.getRechargeOnlineTimes() + "");
            jsonObject.put("cashAmount", _statCash.getCashAmount());
            jsonObject.put("cashAmountType", _statCash.getCashOfflineAmount().toString().concat("/").concat(_statCash.getCashOnlineAmount().toString()));
            jsonObject.put("cashTimes", _statCash.getCashOfflineTimes() + _statCash.getCashOnlineTimes() + "");
            jsonObject.put("pendingCount", _statCash.getPendingCount());
            jsonArray.add(jsonObject);
        }
        int size = statcashs.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        logger.info(result.toString());
        return result.toString();
    }
}
