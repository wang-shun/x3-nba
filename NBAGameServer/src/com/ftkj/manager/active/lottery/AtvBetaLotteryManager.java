package com.ftkj.manager.active.lottery;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.console.GameConsole;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveDataField;
import com.ftkj.db.domain.active.base.DBList;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.prop.PropRandomHit;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 抽奖活动
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@EventRegister({EEventType.登录})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.公测_抽奖狂欢, clazz = AtvBetaLotteryManager.AtvBetaLottery.class, shareClass=AtvBetaLotteryManager.AtvBetaLotteryLimit.class)
public class AtvBetaLotteryManager extends ActiveBaseManager {
	private static final Logger log = LoggerFactory.getLogger(AtvBetaLotteryManager.class);
	private static int MAX_COUNT = 5;
	private static int Lottery_Get_Second = 60 * 60 * 3;
	
	@Override
	public void initConfig() {
		MAX_COUNT = getConfigInt("maxCount", 5);
		Lottery_Get_Second = getConfigInt("addCountSecond", 60 * 60 * 3);
	}
	
	/**
	 * 不能领奖
	 */
	@Override
	public ErrorCode checkGetAwardCustom(long teamId, ActiveBase atvObj, int id) {
		return ErrorCode.Error;
	}
	
	@Subscribe
	public void login(LoginParam param) {
		long teamId = param.teamId;
		if(getStatus() != EActiveStatus.进行中) return;
		AtvBetaLottery atvObj = getTeamData(teamId);
		/**
		 * 离线时间也算
		 */
		addOnlineSecond(atvObj, (int)param.offlineSecond);
		atvObj.save();
		// 可领提示更新
		redPointPush(teamId);
	}
	
	public static class AtvBetaLotteryLimit extends ActiveBase {

		private static final long serialVersionUID = 1L;
		@ActiveDataField(fieldName="iData4", size=6)
		private DBList limit;
		
		public AtvBetaLotteryLimit(ActiveBasePO po) {
			super(po);
		}

		public DBList getLimit() {
			return limit;
		}
		
	}
	
	public static class AtvBetaLottery extends ActiveBase {
		
		private static final long serialVersionUID = 1L;

		public void setLotteryCount(int num) {
			this.setiData1(num);
		}
		
		public int getLotteryCount() {
			return this.getiData1();
		}
		
		public void justAddOnlineSecond(int sec) {
			this.setiData2(this.getiData2() + sec);
		}
		
		public void setOnlineSecond(int sec) {
			this.setiData2(sec);
		}
		
		public int getOnlineSecond() {
			return this.getiData2();
		}
		
		public AtvBetaLottery(ActiveBasePO po) {
			super(po);
		}
	}
	
	/**
	 * 新增在线秒数，并结算抽奖次数
	 * 打开界面调用
	 * 下线调用
	 * @param min
	 */
	private void addOnlineSecond(AtvBetaLottery atvObj, int second) {
		if(atvObj.getLotteryCount() >= MAX_COUNT) {
			return;
		}
		atvObj.setLastTime(DateTime.now());
		atvObj.justAddOnlineSecond(second);
		// 计算是否增加次数
		int addCount = atvObj.getOnlineSecond() / Lottery_Get_Second;
		if(addCount < 1) {
			return;
		}
		int total = atvObj.getLotteryCount() + addCount;
		boolean isMax = total > MAX_COUNT;
		atvObj.setLotteryCount(isMax ? MAX_COUNT : total);
		if(isMax) {
			atvObj.setOnlineSecond(0);
		}else {
			atvObj.justAddOnlineSecond(0 - addCount *  Lottery_Get_Second);
		}
	}
	
	@Override
	public void offline(long teamId) {
		resultTime(teamId);
		super.offline(teamId);
	}
	
	/**
	 * 结算时间
	 * 打开界面，下线调用
	 * @param teamId
	 */
	private void resultTime(long teamId) {
		AtvBetaLottery atvObj = getTeamData(teamId);
		if(atvObj.getLastTime().equals(GameConsole.Min_Timestamp)) {
			addOnlineSecond(atvObj, 0);
		}else {
			int addSecond = (int) ((DateTime.now().getMillis() - atvObj.getLastTime().getMillis()) / 1000);
			addOnlineSecond(atvObj, addSecond);
		}
		atvObj.save();
	}
	
	//-----------------------------------
	/**
	 * val = 抽奖次数
	 * other = 累计在线秒数
	 */
	@Override
	public void showView() {
		long teamId = getTeamId();
		resultTime(teamId);
		super.showView();
	}
	/**
	 * 次数不消耗球券，消耗次数，这里应该可以改成通用？用字段控制
	 */
	@Override
	public <T extends ActiveBase> ErrorCode payLottery(T atvObj, int tp) {
		AtvBetaLottery atvData = (AtvBetaLottery) atvObj;
		if(atvData.getLotteryCount() < 1) {
			return ErrorCode.Error;
		}
		atvData.setLotteryCount(atvData.getLotteryCount() - 1);
		atvData.save();
		return ErrorCode.Success;
	}
	
	@Override
	public PropRandomHit sendLotteryAward(long teamId, int id, Set<Integer> continueSet) {
		Map<Integer, Integer> limitMap = getLimitMap(id);
		if(limitMap == null) {
			return super.sendLotteryAward(teamId, id, null);
		}
		AtvBetaLotteryLimit activeData = getShareData();
		Set<Integer> limitSet = Sets.newHashSet();
		for(Integer key : limitMap.keySet()) {
			if(key >= activeData.getLimit().getSize()) {
				continue;
			}
			if(activeData.getLimit().getValue(key) >= limitMap.get(key)) {
				limitSet.add(key);
			}
		}
		log.debug("抽奖限量控制，跳过奖励={}", limitSet);
		return super.sendLotteryAward(teamId, id, limitSet);
	}
	
	/**
	 * 实现实物限量
	 */
	@Override
	public void lotteryFinish(long teamId, int tp, PropRandomHit hit) {
		Map<Integer, Integer> limitMap = getLimitMap(tp);
		if(!limitMap.containsKey(hit.getIndex())) {
			return;
		}
		// 累计已抽次数
		AtvBetaLotteryLimit activeData = getShareData();
		if(activeData.getLimit().getValue(hit.getIndex()) >= limitMap.get(hit.getIndex())) {
			return;
		}
		activeData.getLimit().setValueAdd(hit.getIndex(), 1);
		activeData.save();
	}
	
	/**
	 * 取限量数据
	 * @param tp
	 * @return
	 */
	private Map<Integer, Integer> getLimitMap(int tp) {
		Map<Integer, Integer> limitMap = Maps.newHashMap();
		SystemActiveCfgBean cfg = getAwardConfigList().get(tp);
		if(cfg == null) {
			return limitMap;
		}
		String[] limitStr = cfg.getConditionMap().get("LimitCfg").split("_");
		if(limitStr == null || limitStr.length < 1) {
			return limitMap;
		}
		for(String s : limitStr) {
			if(s.equals("")) continue;
			String[] sc = s.split(":");
			limitMap.put(Integer.valueOf(sc[0]), Integer.valueOf(sc[1]));
		}
		return limitMap;
	}
	
}
