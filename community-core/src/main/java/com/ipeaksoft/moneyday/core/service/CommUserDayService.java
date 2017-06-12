package com.ipeaksoft.moneyday.core.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipeaksoft.moneyday.core.entity.CommHost;
import com.ipeaksoft.moneyday.core.entity.CommUser;
import com.ipeaksoft.moneyday.core.entity.CommUserDay;
import com.ipeaksoft.moneyday.core.mapper.CommUserDayMapper;
import com.ipeaksoft.moneyday.core.util.strUtil;

@Service
public class CommUserDayService extends BaseService {

	@Autowired
	private CommUserDayMapper commUserDayMapper;
	@Autowired
	private CommUserService commUserService;
	@Autowired
	private CommHostService commHostService;

	public int insertSelective(CommUserDay record) {
		return commUserDayMapper.insertSelective(record);
	}

	public CommUserDay selectCurrentInfo(Integer userid) {
		return commUserDayMapper.selectCurrentInfo(userid);
	}

	public int updateCurrentInfo(CommUserDay record) {
		return commUserDayMapper.updateCurrentInfo(record);
	}

	public Map<String, Object> getConsumptionThisMonth(Integer userid) {
		return commUserDayMapper.getConsumptionThisMonth(userid);
	}

	public void statistical(String agentname, double real_amount) {
		String xgAccount = strUtil.getAgentName(agentname);
		CommUser commUser = commUserService.selectBymobile(xgAccount);
		// 用户做任务前，肯定绑定了师傅和主播了
		Integer promoterId = commUser.getId();
		Integer masterId = commUser.getPid();
		Integer hostId = commUser.getCommid();
		BigDecimal realAmonut = new BigDecimal(real_amount);
		BigDecimal promoterAward = realAmonut.multiply(promoterCommission);
		BigDecimal masterAward = realAmonut.multiply(masterCommission);
		BigDecimal hostAward = realAmonut.multiply(hostCommission);

		// 推广员
		CommUserDay cud = selectCurrentInfo(promoterId);
		CommUserDay commUserDay = new CommUserDay();
		commUserDay.setUserid(promoterId);
		if (null == cud) {
			commUserDay.setTodayrechargenum(1);
			commUserDay.setTodaycommission(promoterAward.doubleValue());
			commUserDay.setTodayrechargetotal(real_amount);
			commUserDayMapper.insertSelective(commUserDay);
		} else {
			commUserDay.setTodayrechargenum(cud.getTodayrechargenum() + 1);
			BigDecimal original = new BigDecimal(cud.getTodaycommission());
			commUserDay.setTodaycommission(original.add(promoterAward)
					.doubleValue());
			commUserDay.setTodayrechargetotal(realAmonut.add(
					new BigDecimal(commUserDay.getTodayrechargetotal()))
					.doubleValue());
			commUserDayMapper.updateCurrentInfo(commUserDay);
		}
		// 累计 aworad 余额 totalaword 总收益
		CommUser model = new CommUser();
		model.setId(promoterId);
		model.setTotalaward(promoterAward.add(
				new BigDecimal(commUser.getTotalaward())).doubleValue());
		model.setAward(promoterAward.add(new BigDecimal(commUser.getAward()))
				.doubleValue());
		commUserService.updateByPrimaryKeySelective(model);

		// 推广员的师傅
		CommUserDay masterCud = selectCurrentInfo(masterId);
		CommUserDay masterCommUserDay = new CommUserDay();
		masterCommUserDay.setUserid(masterId);
		if (null == masterCud) {
			masterCommUserDay.setTodaycommission(masterAward.doubleValue());
			commUserDayMapper.insertSelective(masterCommUserDay);
		} else {
			BigDecimal original = new BigDecimal(masterCud.getTodaycommission());
			masterCommUserDay.setTodaycommission(original.add(masterAward)
					.doubleValue());
			commUserDayMapper.updateCurrentInfo(masterCommUserDay);
		}
		// 累计 aworad 余额 tbaword 徒弟收益 totalaword 总收益
		CommUser masterCu = commUserService.selectByPrimaryKey(masterId);
		CommUser masterModel = new CommUser();
		masterModel.setId(masterId);
		masterModel.setTotalaward(masterAward.add(
				new BigDecimal(masterCu.getTotalaward())).doubleValue());
		masterModel.setAward(masterAward.add(
				new BigDecimal(masterCu.getAward())).doubleValue());
		masterModel.setTdaward(masterAward.add(
				new BigDecimal(masterCu.getTdaward())).doubleValue());
		commUserService.updateByPrimaryKeySelective(masterModel);

		// 主播
		CommUserDay hostCud = selectCurrentInfo(hostId);
		CommUserDay hostCommUserDay = new CommUserDay();
		hostCommUserDay.setUserid(hostId);
		if (null == hostCud) {
			hostCommUserDay.setTodayrechargenum(1);
			hostCommUserDay.setTodaycommission(hostAward.doubleValue());
			hostCommUserDay.setTodayrechargetotal(real_amount);
			commUserDayMapper.insertSelective(hostCommUserDay);
		} else {
			hostCommUserDay
					.setTodayrechargenum(hostCud.getTodayrechargenum() + 1);
			BigDecimal original = new BigDecimal(hostCud.getTodaycommission());
			hostCommUserDay.setTodaycommission(original.add(hostAward)
					.doubleValue());
			hostCommUserDay.setTodayrechargetotal(realAmonut.add(
					new BigDecimal(hostCud.getTodayrechargetotal()))
					.doubleValue());
			commUserDayMapper.updateCurrentInfo(hostCommUserDay);
		}
		// 累计 aworad 余额 totalaword 总收益
		CommHost ch = commHostService.selectByPrimaryKey(hostId);
		CommHost commHost = new CommHost();
		commHost.setId(hostId);
		commHost.setTotalaward(hostAward
				.add(new BigDecimal(ch.getTotalaward())).doubleValue());
		commHost.setAward(hostAward.add(new BigDecimal(ch.getAward()))
				.doubleValue());
		commHostService.updateByPrimaryKeySelective(commHost);
	}

	public void registered(CommUser commUser) {
		// 玩家 玩家所属推广员+1 推广员所属主播+1 一天一个user只有一条记录
		// 用户做任务前，肯定绑定师傅和主播了
		Integer hostId = commUser.getCommid();
		Integer promotersId = commUser.getId();
		registered_update(promotersId);
		registered_update(hostId);
		/*
		 * // 今日推广员 注册数+1 CommUserDay cud = selectCurrentInfo(promotersId);
		 * CommUserDay commUserDay = new CommUserDay();
		 * commUserDay.setUserid(promotersId); if (null == cud) {
		 * commUserDay.setTodayregisternum(1);
		 * commUserDayMapper.insertSelective(commUserDay); } else {
		 * commUserDay.setTodayregisternum(cud.getTodayregisternum() + 1);
		 * commUserDayMapper.updateCurrentInfo(commUserDay); }
		 * 
		 * // 今日主播 注册数+1 CommUserDay hostcud = selectCurrentInfo(hostId);
		 * CommUserDay hostcommUserDay = new CommUserDay();
		 * hostcommUserDay.setUserid(promotersId); if (null == hostcud) {
		 * hostcommUserDay.setTodayregisternum(1);
		 * commUserDayMapper.insertSelective(hostcommUserDay); } else {
		 * hostcommUserDay .setTodayregisternum(hostcud.getTodayregisternum() +
		 * 1); commUserDayMapper.updateCurrentInfo(hostcommUserDay); }
		 */
	}

	public void registered_update(Integer id) {
		CommUserDay cud = selectCurrentInfo(id);
		CommUserDay commUserDay = new CommUserDay();
		commUserDay.setUserid(id);
		if (null == cud) {
			commUserDay.setTodayregisternum(1);
			commUserDayMapper.insertSelective(commUserDay);
		} else {
			commUserDay.setTodayregisternum(cud.getTodayregisternum() + 1);
			commUserDayMapper.updateCurrentInfo(commUserDay);
		}
	}
}
