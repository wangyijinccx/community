package com.ipeaksoft.moneyday.admin.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.admin.util.BillUtil;
import com.ipeaksoft.moneyday.admin.util.DateUtil;
import com.ipeaksoft.moneyday.core.entity.UserCash;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.StatCashService;
import com.ipeaksoft.moneyday.core.service.UserAdsService;
import com.ipeaksoft.moneyday.core.service.UserAwardService;
import com.ipeaksoft.moneyday.core.service.UserCashService;
import com.ipeaksoft.moneyday.core.service.UserFastService;

@Controller
@RequestMapping(value = "/bill/wrong")
public class BillWrongController extends BaseController {

    BillUtil         billUtil = new BillUtil();
    @Autowired
    UserAwardService userAwardService;
    @Autowired
    UserAdsService   userAdsService;
    @Autowired
    UserFastService  userFastService;
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    UserCashService  userCashService;
    @Autowired
    StatCashService  statCashService;

    @RequestMapping(value = "/list")
    public String list(HttpServletRequest request) {
        return "/bill/wronglist";
    }

    @RequestMapping(value = "/setwrong")
    public String setwrong(HttpServletRequest request) {
        billUtil.getAwardByReq(request, userFastService, userAdsService);
        return "/bill/setwrong";
    }

    // 加载单个用户的推迟订单记录
    @ResponseBody
    @RequestMapping(value = "/data_load_setwrong")
    public String load_data_setwrong(HttpServletRequest request) {
        String draw = request.getParameter("draw");
        String mobile = request.getParameter("mobile");
        Long id = Long.valueOf(request.getParameter("id"));
        logger.info("[data_load_checking][id]" + id);

        // 查询用户的快速累计收益、常规累计收益、奖励累计收益
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobile", mobile);

        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        paramMap.put("id", id);
        int total = userCashService.countPageByUser(paramMap);
        List<UserCash> list = userCashService.getPageByUser(paramMap);

        int start = (int) paramMap.get("start");
        paramMap.remove("start");
        paramMap.put("start", start + 1);
        List<UserCash> fromlist = userCashService.getPageByUser(paramMap);

        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            UserCash userCash = list.get(i);
            int istatus = Integer.valueOf(userCash.getStatus());
            if (3 == istatus) {
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
                billUtil.formatUserBillData(jsonObject, userCash, istatus);
                jsonArray.add(jsonObject);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            UserCash userCash = list.get(i);
            int istatus = Integer.valueOf(userCash.getStatus());
            if (3 != istatus) {
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
                billUtil.formatUserBillData(jsonObject, userCash, istatus);
                jsonArray.add(jsonObject);
            }
        }

        int size = list.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        return result.toString();
    }

    // 加载推迟订单记录
    @ResponseBody
    @RequestMapping(value = "/data_load")
    public String load_data(HttpServletRequest request) {
        try {
            String draw = request.getParameter("draw");//搜索内容
            Map<String, Object> paramMap = billUtil.getWhereMap(request);
            paramMap.put("status", 3);
            int total = userCashService.countPageByStatus(paramMap);

            List<UserCash> list = userCashService.getPageByStatus(paramMap);
            JSONArray jsonArray = new JSONArray();
            for (UserCash userCash : list) {
                JSONObject jsonObject = new JSONObject();
                String createtime = DateUtil.date2Str("yyyy-MM-dd HH:mm:ss", userCash.getCreateTime());
                jsonObject.put("createtime", createtime);
                String operatetime = DateUtil.date2Str("yyyy-MM-dd HH:mm:ss", userCash.getOperateTime());
                jsonObject.put("operatetime", operatetime);
                jsonObject.put("id", userCash.getUserid());
                jsonObject.put("mobile", userCash.getUserphone());
                String fromto = userCash.getFromto();
                fromto = ("online".equals(fromto)) ? "线上" : "线下";
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
                String date = DateUtil.date2Str("dd", userCash.getCreateTime());
                int day = Integer.valueOf(date);
                double index = 1 - amount / (day * 10);
                BigDecimal b = new BigDecimal(index);
                double value = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                jsonObject.put("exchangeindex", value);
                jsonObject.put("operator", userCash.getOperator());
                jsonArray.add(jsonObject);
            }
            int size = list.size();
            JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
            return result.toString();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
