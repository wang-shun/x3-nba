package com.ftkj.manager.active.longtime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.PayParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.server.GameSource;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 累计消费天数
 * 累计消费金额
 * 长期活动
 * @author Jay
 * @time:2018年2月1日 下午5:32:26
 */
@EventRegister({EEventType.消费})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.消费统计, clazz=AtvPayFKStatisticsManager.AtvPayFKStatistics.class)
public class AtvPayFKStatisticsManager extends ActiveBaseManager {

	/**
	 * 查询当天前15天消费记录常量
	 */
	private static final int MAX_BEFORE_DAY = 15;
	private Map<Long, Map<String, AtvPayFKStatistics>> teamMap;
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		teamMap = Maps.newConcurrentMap();
	}
	
	/**
	 * 活动数据类
	 */
	public static class AtvPayFKStatistics extends ActiveBase {
		
		private static final long serialVersionUID = 1L;

		public AtvPayFKStatistics(ActiveBasePO po) {
			super(po);
		}
		
		/**
		 * 消费统计逻辑都在这里：包括次数，最大金额等
		 * 注意，调set方法才行
		 * @param moneyFK
		 */
		public void payMoney(int moneyFK) {
			this.setCount(this.getCount()+1);
			this.setSumTotal(this.getSumTotal() + moneyFK);
			if(moneyFK > this.getMax()) {
				this.setMax(moneyFK);
			}
		}

		/**
		 * 累计消费笔数
		 */
		public int getCount() {
			return this.getiData1();
		}
		public void setCount(int count) {
			this.setiData1(count);
		}
		/**
		 * 累计消费金额
		 */
		public int getSumTotal() {
			return this.getiData2();
		}
		public void setSumTotal(int sumTotal) {
			this.setiData2(sumTotal);
		}
		/**
		 * 当天一次性消费最大金额
		 */
		public int getMax() {
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
	public Map<String, AtvPayFKStatistics> getTeamDayDataMap(long teamId) {
		Map<String, AtvPayFKStatistics> listMap = teamMap.get(teamId);
		if(listMap == null) {
			// 15天前的消费记录
			List<ActiveBasePO> poList = queryActiveDataListBeforeDay(teamId, MAX_BEFORE_DAY);
			if(poList == null) {
				poList = Lists.newArrayList();
			}
			// 转成map，按时间
			listMap = poList.stream().map(s-> new AtvPayFKStatistics(s))
				.collect(Collectors.toConcurrentMap(AtvPayFKStatistics::getCreateDay, s-> s));
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
	public AtvPayFKStatistics getTeamDataByCreateTime(long teamId, DateTime dateTime) {
		// 是否存在当天的记录，如果不存在，则创建，DB表要调整可以多条
		String createDay = DateTimeUtil.getString(dateTime);
		Map<String, AtvPayFKStatistics> listMap = getTeamDayDataMap(teamId);
		AtvPayFKStatistics atvObj = listMap.get(createDay);
		if(atvObj == null) {
			atvObj = new AtvPayFKStatistics(new ActiveBasePO(this.getId(), GameSource.shardId, teamId, teamManager.getTeamName(teamId)));
			listMap.put(createDay, atvObj);
		}
		return atvObj;
	}
	
	/**
	 * 消费回调
	 * @param param
	 */
	@Subscribe
	public void payMoneyFK(PayParam param) {
		long teamId = param.teamId;
		// 用消费订单的时间来处理
		AtvPayFKStatistics atvObj = getTeamDataByCreateTime(teamId, param.time);
		atvObj.payMoney(param.fk);
		atvObj.save();
	}
	
	// 提供查询接口
	/**
	 * 取某活动时间段范围内的累计消费金额
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getPayTotalBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvPayFKStatistics> map = getTeamDayDataMap(teamId);
		int sum = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getSumTotal()).sum();
		return sum;
	}
	
	/**
	 * 消费笔数
	 * @param teamId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getPayCountBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvPayFKStatistics> map = getTeamDayDataMap(teamId);
		int sum = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getCount()).sum();
		return sum;
	}
	
	/**
	 * 一次性消费最大球券数
	 * @param teamId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getPayMaxBetweenDay(long teamId, DateTime startTime, DateTime endTime) {
		Map<String, AtvPayFKStatistics> map = getTeamDayDataMap(teamId);
		int max = map.values().stream()
				.filter(s-> s.getCreateTime().isAfter(startTime) && s.getCreateTime().isBefore(endTime))
				.mapToInt(s-> s.getMax()).max().orElse(0);
		return max;
	}
	
	
}
