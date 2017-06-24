package com.ipeaksoft.moneyday.api.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.ipeaksoft.moneyday.core.entity.CommMemCash;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.entity.CommUserDay;
import com.ipeaksoft.moneyday.core.sdk.duiba.Constant;
import com.ipeaksoft.moneyday.core.service.CommMemCashService;
import com.ipeaksoft.moneyday.core.service.CommUserDayService;
import com.ipeaksoft.moneyday.core.service.CommUserService;

@Controller
@RequestMapping(value = "/drawal")
public class CommDrawalController extends BaseController {

	@Autowired
	CommUserService commUserService;
	@Autowired
	CommUserDayService commUserDayService;
	@Autowired
	CommMemCashService commMemCashService;

	// Alpha
	public static final String app_id = "";// 付宝分配给开发者的应用ID
	public static final String method = "alipay.fund.trans.toaccount.transfe";
	public static final String charset = "UTF-8";

	public static final String privatKey = "";
	public static final String publicKey = "";

	/**
	 * 支付宝提现
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("alipayDrawal")
	public Object alipayDrawal(HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String token = request.getParameter("token");// 用户
		int balance = Integer.parseInt(request.getParameter("balance"));// 提现金额
		String payee_account = request.getParameter("account");// 支付宝账户
		String payer_show_name = "";// 付款姓名
		String realName = request.getParameter("name");
		String remark = "";// 备注

		CommUser commUser = commUserService.selectByIndicate(token);
		CommUserDay commUserDay = commUserDayService.selectCurrentInfo(token);
		if (null == commUser) {
			result.put("result", 5);
			result.put("msg", "用户不存在");
		}
		if (balance != 10 && balance != 50 && balance != 100 && balance != 500) {
			result.put("result", 3);
			result.put("msg", "无效的提现金额");
			return result.toString();
		}
		if (withdrawalNumber - commUserDay.getTodaywithdrawalscount() < 1) {
			result.put("result", 4);
			result.put("msg", "超出提现次数");
			return result.toString();
		}
		Double award = commUser.getAward();
		BigDecimal totalAward = new BigDecimal(award);
		BigDecimal bAward = new BigDecimal(balance);
		Double shAward = totalAward.subtract(bAward).doubleValue();
		if (commUser.getAward() < balance) {
			result.put("result", 6);
			result.put("msg", "余额不足");
			return result.toString();
		}

		// 需要幂等处理 打印日志 异常处理
		try {

			String out_biz_no = UUID.randomUUID().toString();
			// 保存数据
			CommMemCash userCash = new CommMemCash();
			userCash.setOrderid(out_biz_no); // 插入掌通订单号
			userCash.setDescription("支付宝提现：" + balance + "元"); // 插入当前订单的详细描述
			userCash.setOpenid("");
			userCash.setAmount(balance); // 插入当前订单的提现金额（分为单位）
			userCash.setTotalcredits(commUser.getAward()); // 插入当前用户余额
			userCash.setOperator(Constant.SYSTEM); // 插入处理人，系统处理
			userCash.setStatus(Byte.valueOf(Constant.ALIPAY_DO)); // 插入订单状态:成功
			userCash.setType((byte) 0);// 支付宝
			userCash.setCreateTime(new Date()); // 插入当前日期
			userCash.setRealName(realName);
			userCash.setAlipayAccount(payee_account);
			int cashId = commMemCashService.insertSelective(userCash);
			if (cashId <= 0) {
				result.put("result", 7);
				result.put("msg", "生成订单失败");
				return result;
			}

			AlipayClient alipayClient = new DefaultAlipayClient(
					"https://openapi.alipay.com/gateway.do", app_id, privatKey,
					"json", charset, publicKey, "RSA2");
			AlipayFundTransToaccountTransferRequest AlipayRequest = new AlipayFundTransToaccountTransferRequest();
			AlipayRequest.setBizContent("{" + "\"out_biz_no\":\"" + out_biz_no
					+ "\"," + "\"payee_type\":\"ALIPAY_LOGONID\","
					+ "\"payee_account\":\"" + payee_account + "\","
					+ "\"amount\":\"" + balance + "\","
					+ "\"payer_show_name\":\"" + payer_show_name + "\","
					+ "\"payee_real_name\":\"" + realName + "\","
					+ "\"remark\":\"" + remark + "\"" + "  }");
			AlipayFundTransToaccountTransferResponse AlipayResponse = alipayClient
					.execute(AlipayRequest);
			if (AlipayResponse.isSuccess()) {
				CommMemCash userCash_success = new CommMemCash();
				userCash_success.setId(cashId);
				userCash_success.setStatus(Byte
						.valueOf(Constant.ALIPAY_SUCCESS)); // 插入订单状态:成功
				// 修改状态
				commMemCashService
						.updateByPrimaryKeySelective(userCash_success);
				// 修改积分
				CommUser model = new CommUser();
				model.setId(commUser.getId());
				model.setAward(shAward);
				commUserService.updateByPrimaryKeySelective(model);
				result.put("result", 1);
				result.put("msg", "转账成功");
				return result;
			} else {
				CommMemCash userCash_fail = new CommMemCash();
				userCash_fail.setId(cashId);
				userCash_fail.setStatus(Byte.valueOf(Constant.ALIPAY_FAIL)); // 插入订单状态:失败
				// 修改状态
				commMemCashService.updateByPrimaryKeySelective(userCash_fail);
				result.put("result", 2);
				result.put("msg", "转账失败");
				return result;
			}
		} catch (Exception e) {
			result.put("result", 6);
			result.put("msg", "未知异常");
			return result;
		}
	}
}
