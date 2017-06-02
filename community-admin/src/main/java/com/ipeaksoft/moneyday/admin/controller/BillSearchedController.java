package com.ipeaksoft.moneyday.admin.controller;

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
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.entity.UserCash;
import com.ipeaksoft.moneyday.core.entity.UserCashApprove;
import com.ipeaksoft.moneyday.core.sdk.duiba.Constant;
import com.ipeaksoft.moneyday.core.service.AdminUserService;
import com.ipeaksoft.moneyday.core.service.UserCashApproveService;
import com.ipeaksoft.moneyday.core.service.UserCashService;

@Controller
@RequestMapping(value = "/bill/searched")
public class BillSearchedController extends BaseController {

    BillUtil               billUtil = new BillUtil();

    @Autowired
    AdminUserService       adminUserService;
    @Autowired
    UserCashService        userCashService;
    @Autowired
    UserCashApproveService userCashApproveService;

    @RequestMapping(value = "/list")
    public String checked(HttpServletRequest request) {
        return "/bill/searched";
    }

    @RequestMapping(value = "/one")
    public String daylist(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id"));
        UserCash userCash = userCashService.getUserById(id);
        String name = null;
        System.out.println("operator------>> " + userCash.getOperator());
        if (null == userCash.getOperator() || Constant.SYSTEM.equals(userCash.getOperator())) {
            name = "system";
        } else {
            AdminUser user = adminUserService.getUserById(Integer.valueOf(userCash.getOperator()));
            name = user.getUsername();
        }
        request.setAttribute("userCash", userCash);
        request.setAttribute("name", name);
        return "/bill/searchone";
    }

    // 加载单个订单的审核历史记录
    @ResponseBody
    @RequestMapping(value = "/data_load_one")
    public String load_data_one(HttpServletRequest request) {
        String draw = request.getParameter("draw");
        String ordernum = request.getParameter("ordernum");
        logger.info("[data_load_one][ordernum]" + ordernum);
        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        paramMap.put("ordernum", ordernum);
        int total = userCashApproveService.countPageByOrder(paramMap);
        List<UserCashApprove> list = userCashApproveService.getPageByOrder(paramMap);
        JSONArray jsonArray = new JSONArray();
        for (UserCashApprove userCashApprove : list) {
            JSONObject jsonObject = new JSONObject();
            String day = DateUtil.date2Str("yyyy-MM-dd HH:mm:ss", userCashApprove.getApproveTime());
            jsonObject.put("day", day);
            int istatus = Integer.valueOf(userCashApprove.getResult());
            String status = (0 == istatus) ? "待审核" : (1 == istatus) ? "处理中" : (2 == istatus) ? "推迟审核" : (3 == istatus) ? "审核异常" : (4 == istatus) ? "充值失败" : (9 == istatus) ? "充值成功" : "其他";
            jsonObject.put("status", status);
            String description = (null == userCashApprove.getDescription()) ? "" : userCashApprove.getDescription();
            jsonObject.put("description", description);
            Integer operator = userCashApprove.getOperator();
            String name = null;
            if (0 == operator) {
                name = "system";
            } else {
                AdminUser user = adminUserService.getUserById(operator);
                name = user.getUsername();
            }
            jsonObject.put("operator", name);
            jsonArray.add(jsonObject);
        }
        int size = list.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        return result.toString();
    }

    // 加载所有订单的记录
    @ResponseBody
    @RequestMapping(value = "/data_load")
    public String load_data(HttpServletRequest request) {
        String draw = request.getParameter("draw");//搜索内容
        String type = request.getParameter("type");
        logger.info("type: " + type);
        Map<String, Object> paramMap = billUtil.getWhereMap(request);
        paramMap.put("type", type);
        int total = userCashService.countPageByType(paramMap);
        List<UserCash> list = userCashService.getPageByType(paramMap);
        JSONArray jsonArray = new JSONArray();
        for (UserCash userCash : list) {
            JSONObject jsonObject = new JSONObject();
            String day = DateUtil.date2Str("yyyy-MM-dd HH:mm:ss", userCash.getCreateTime());
            jsonObject.put("day", day);
            jsonObject.put("id", userCash.getId());
            jsonObject.put("uid", userCash.getUserid());
            jsonObject.put("orderid", userCash.getOrderid());
            jsonObject.put("mobile", userCash.getUserphone());
            String exchangetype = userCash.getExchangeType();
            exchangetype = ("phonebill".equals(exchangetype)) ? "话费充值" : ("alipay".equals(exchangetype)) ? "支付宝" : "其他类";
            jsonObject.put("exchangetype", exchangetype);
            String phone = userCash.getMobile();
            phone = (null == phone) ? "" : phone;
            String account = userCash.getAlipayAccount();
            account = (null == account) ? "" : account;
            account = account.replaceAll(":|：", "<br/>");
            if ("phonebill".equals(type))
                account = phone;
            jsonObject.put("account", account);
            jsonObject.put("ordernum", userCash.getOrdernum());
            int istatus = Integer.valueOf(userCash.getStatus());
            String status = (0 == istatus) ? "待审核" : (1 == istatus) ? "处理中" : (2 == istatus) ? "推迟审核" : (3 == istatus) ? "审核异常" : (4 == istatus) ? "充值失败" : (9 == istatus) ? "充值成功" : "其他";
            jsonObject.put("status", status);
            jsonArray.add(jsonObject);
        }
        request.setAttribute("type", type);
        int size = list.size();
        JSONObject result = billUtil.formatResult(draw, total, jsonArray, size);
        return result.toString();
    }
}
