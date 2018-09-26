package com.ftkj.manager.active.longtime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.constant.ChatPushConst;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPayType;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.team.Team;
import com.ftkj.server.GameSource;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 累计充值天数
 * 累计充值金额
 * 长期活动
 * @author Jay
 * @time:2018年2月1日 下午5:32:26
 */
@EventRegister({EEventType.充值})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.充值统计,  clazz=AtvRechargeStatisticsManager.AtvRechargeStatistics.class)
public class AtvRechargeStatisticsManager extends ActiveBaseManager {

	/**
	 * 查询当天前15天充值记录常量
	 */
	private static final int MAX_BEFORE_DAY = 15;
	private Map<Long, Map<String, AtvRechargeStatistics>> teamMap;
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		teamMap = Maps.newConcurrentMap();
	}
	
	/**
	 * 活动数据类
	 */
	public static class AtvRechargeStatistics extends ActiveBase {
		
		private static final long serialVersionUID = 1L;

		public AtvRechargeStatistics(ActiveBasePO po) {
			super(po);
		}
		
		/**
		 * 充值统计逻辑都在这里：包括次数，最大金额等
		 * 注意，调set方法才行
		 * @param moneyFK
		 */
		public void rechargeMoney(int moneyFK) {
			this.setCount(this.getCount()+1);
			this.setSumTotal(this.getSumTotal() + moneyFK);
			if(moneyFK > this.getRechargeMax()) {
				this.setMax(moneyFK);
			}
		}

		/**
		 * 累计充值笔数
		 */
		public int getCount() {
			return this.getiData1();
		}
		public void setCount(int count) {
			this.setiData1(count);
		}
		/**
		 * 累计充值金额
		 */
		public int getSumTotal() {
			return this.getiData2();
		}
		public void setSumTotal(int sumTotal) {
			this.setiData2(sumTotal);
		}
		/**
		 * 当天一次性充值最大金额
		 */
		public int getRechargeMax() {
			return this.getiData3();
		}
		public void setMax(int max) {
			this.setiData3(max);
		}
		public String getCreateDay() {
			return this.getPo().getCreateDay();
		}
	}
	
	/**
	 * 取玩家的活动数据
	 * @param teamId
	 * @return
	 */
	public Map<String, AtvRechargeStatistics> getTeamDayDataMap(long teamId) {
		Map<String, AtvRechargeStatistics> listMap = teamMap.get(teamId);
		if(listMap == null) {
			// 15天前的充值记录
			List<ActiveBasePO> poList = queryActiveDataListBeforeDay(teamId, MAX_BEFORE_DAY);
			if(poList == null) {
				poList = Lists.newArrayList();
			}
			// 转成map，按时间
			listMap = poList.stream().map(s-> new AtvRechargeStatistics(s))
				.collect(Collectors.toConcurrentMap(AtvRechargeStatistics::getCreateDay, s-> s));
			teamMap.put(teamId, listMap);
		}
		return listMap;
	}
	
	/**
	 * 取某天的记录
	 * @param teamId
	 * @param dateTime
	 * @return
	 */
	public AtvRechargeStatistics getTeamDataByCreateTime(long teamId, DateTime dateTime) {
		// 是否存在当天的记录，如果不存在，则创建，DB表要调整可以多条
		String createDay = DateTimeUtil.getString(dateTime);
		Map<String, AtvRechargeStatistics> listMap = getTeamDayDataMap(teamId);
		AtvRechargeStatistics atvObj = listMap.get(createDay);
		if(atvObj == null) {
			atvObj = new AtvRechargeStatistics(new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
			listMap.put(createDay, atvObj);
		}
		return atvObj;
	}
	
	/**
	 * 充值回调
	 * @param param
	 */
	@Subscribe
	public void addMoneyFK(RechargeParam param) {
		if(param.type != EPayType.充值) return;
		long teamId = param.teamId;
		// 用充值订单的时间来处理
		AtvRechargeStatistics atvObj = getTeamDataByCreateTime(teamId, param.time);
		atvObj.rechargeMoney(param.fk);
		atvObj.save();
		// 统计事件
		EventBusManager.post(EEventType.充值活动统计事件, param);
		
		// 充值广播 
		Team team = teamManager.getTeam(teamId);
		taskManager.updateTask(teamId, ETaskCondition.充值, param.fk, EModuleCode.球队.getName());
		if(param.fk >= ChatPushConst.CHAT_PUSH_PAY_MONEY_6480) {
			chatManager.pushGameTip(EGameTip.充值6480球券, 0, team.getName());
		}		
	}
	
	/**
	 * 取某活动时间段范围内的累计充值金额
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getRechargeTotalBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvRechargeStatistics> map = getTeamDayDataMap(teamId);
		int sum = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getSumTotal()).sum();
		return sum;
	}
	
	/**
	 * 充值笔数
	 * @param teamId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getRechargeCountBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvRechargeStatistics> map = getTeamDayDataMap(teamId);
		int sum = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getCount()).sum();
		return sum;
	}
	
	/**
	 * 期间累计充值天数
	 * @param teamId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getRechargeDayBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvRechargeStatistics> map = getTeamDayDataMap(teamId);
		int day = (int) map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.filter(s-> s.getSumTotal() > 0)
				.count();
		return day;
	}
	
	/**
	 * 一次性充值最大球券数
	 * @param teamId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getRechargeMaxBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvRechargeStatistics> map = getTeamDayDataMap(teamId);
		int max = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getRechargeMax()).max().orElse(0);
		return max;
	}
	
	
}
