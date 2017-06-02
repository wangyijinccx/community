package com.ipeaksoft.moneyday.admin.controller;

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
import com.ipeaksoft.moneyday.core.entity.StatRecharged;
import com.ipeaksoft.moneyday.core.entity.UserCash;
import com.ipeaksoft.moneyday.core.entity.UserCashOrder;
import com.ipeaksoft.moneyday.core.service.StatRechargedService;
import com.ipeaksoft.moneyday.core.service.UserCashService;

@Controller
@RequestMapping(value = "/bill/recharged")
public class BillRechargedController extends BaseController {

    BillUtil             billUtil = new BillUtil();
    @Autowired
    UserCashService      userCashService;
    @Autowired
    StatRechargedService statRechargedService;

    // 跳转到充值订单的列表
    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request) {
        Integer totalalipaybill = userCashService.getAllAlipayMoney();
        Integer totalphonebill = userCashService.getAllPhoneMoney();
        totalalipaybill = (null == totalalipaybill) ? 0 : totalalipaybill;
        totalphonebill = (null == totalphonebill) ? 0 : totalphonebill;
        request.setAttribute("totalalipaybill", totalalipaybill / 100);
        request.setAttribute("totalphonebill", totalphonebill / 100);
        return "/bill/recharged";
    }

    // 跳转到每日充值订单的明细列表
    @RequestMapping(value = "/daylist")
    public String daychecked(HttpServletRequest request) {
        String date = request.getParameter("day");
        request.setAttribute("day", date);
        return "/bill/dayrecharged";
    }

    // 加载每日充值订单的数据
    @ResponseBody
    @RequestMapping(value = "/data_load_day")
    public String load_data_day(HttpServletRequest request) {
        String draw = request.getParameter("draw");//搜索内容
        String date = request.getParameter("day");
        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        paramMap.put("day", date);
        int total = userCashService.countPageByOperate(paramMap);
        List<UserCash> list = userCashService.getPageByOperate(paramMap);

        JSONArray jsonArray = new JSONArray();
        for (UserCash userCash : list) {
            JSONObject jsonObject = new JSONObject();
            String day = DateUtil.date2Str("HH:mm:ss", userCash.getOperateTime());
            jsonObject.put("day", day);
            jsonObject.put("id", userCash.getUserid());
            jsonObject.put("orderid", userCash.getOrderid());
            jsonObject.put("mobile", userCash.getUserphone());
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
            int istatus = Integer.valueOf(userCash.getStatus());
            String status = (0 == istatus) ? "待审核" : (1 == istatus) ? "处理中" : (2 == istatus) ? "推迟审核" : (3 == istatus) ? "审核异常" : (4 == istatus) ? "充值失败" : (9 == istatus) ? "充值成功" : "其他";
            jsonObject.put("status", status);
            String operateResult = (4 == istatus) ? userCash.getOperateResult() : "";
            jsonObject.put("operateResult", operateResult);
            jsonArray.add(jsonObject);
        }
        request.setAttribute("day", date);
        int size = list.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        return result.toString();
    }

    // 加载充值订单的统计记录
    @ResponseBody
    @RequestMapping(value = "/data_load")
    public String load_data(HttpServletRequest request) {
        String draw = request.getParameter("draw");//搜索内容
        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        int total = statRechargedService.countAllByWhere(paramMap);
        Date date = null;
        if (0 == total) {
            // 数据库没有数据，则首先统计一次所有日期的信息，并将昨天以前的所有数据保存到数据库
            date = null;
        } else {
            // 数据库有昨天以前的所有数据，则读取昨天和当天的数据，保存昨天的数据并合并三者来显示
            date = DateUtil.getDateFromNow(Calendar.DAY_OF_YEAR, -2);
        }

        // 统计线上/线下用户的兑换信息,并封装到Map中
        List<UserCashOrder> list = userCashService.selectRechargedByDay(date);
        Map<Date, StatRecharged> map = new HashMap<Date, StatRecharged>();
        for (UserCashOrder userCashOrder : list) {
            Date day = userCashOrder.getDay();
            StatRecharged statRecharged = null;
            if (map.containsKey(day)) {
                statRecharged = map.get(day);
                map.remove(day);
            } else {
                statRecharged = new StatRecharged();
            }
            if ("phonebill".equals(userCashOrder.getExchange_type())) {
                if (9 == userCashOrder.getStatus()) {
                    int rechargeAmountSuccess = statRecharged.getRechargeAmountSuccess();
                    statRecharged.setRechargeAmountSuccess(rechargeAmountSuccess + userCashOrder.getAmount());
                }
                if (4 == userCashOrder.getStatus()) {
                    int rechargedAmountFail = statRecharged.getRechargeAmountFail();
                    statRecharged.setRechargeAmountFail(rechargedAmountFail + userCashOrder.getAmount());
                }

            }
            if ("alipay".equals(userCashOrder.getExchange_type())) {
                if (9 == userCashOrder.getStatus()) {
                    int cashAmountSuccess = statRecharged.getCashAmountSuccess();
                    statRecharged.setCashAmountSuccess(cashAmountSuccess + userCashOrder.getAmount());
                }
                if (4 == userCashOrder.getStatus()) {
                    int cashAmountFail = statRecharged.getCashAmountFail();
                    statRecharged.setCashAmountFail(cashAmountFail + userCashOrder.getAmount());
                }
            }
            if (9 == userCashOrder.getStatus()) {
                int successtimes = statRecharged.getSuccesstimes();
                statRecharged.setSuccesstimes(successtimes + userCashOrder.getTimes());
            }
            if (4 == userCashOrder.getStatus()) {
                int failtimes = statRecharged.getFailtimes();
                statRecharged.setFailtimes(failtimes + userCashOrder.getTimes());
            }
            map.put(day, statRecharged);
        }

        for (Entry<Date, StatRecharged> entry : map.entrySet()) {
            Date day = entry.getKey();
            StatRecharged statRecharged = entry.getValue();
            statRecharged.setDay(day);
            statRecharged.setAmount(statRecharged.getCashAmountFail() + statRecharged.getCashAmountSuccess() + statRecharged.getRechargeAmountFail() + statRecharged.getRechargeAmountSuccess());
            statRecharged.setTimes(statRecharged.getSuccesstimes() + statRecharged.getFailtimes());
            if (0 != CommonUtil.compareDay(day, new Date())) { // 插入今天以前的数据到数据库
                int flag = statRechargedService.addOne(statRecharged);
                if (1 == flag) {
                    logger.info("flag add------>: " + flag);
                }
            } else {
                //对当天数据的处理
                int flag = statRechargedService.addOne(statRecharged);
                if (1 == flag) {
                    total += 1;
                } else {
                    int update = statRechargedService.updateOne(statRecharged);
                    if (1 != update) {
                        update = statRechargedService.updateOne(statRecharged);
                    }
                }
            }
        }
        List<StatRecharged> statRechargeds = statRechargedService.getPageByDay(paramMap);
        int size = statRechargeds.size();
        JSONObject result = billUtil.formatResult(draw, total, statRechargeds, size);
        logger.info(result.toString());
        return result.toString();
    }
}
