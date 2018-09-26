package com.ftkj.manager.active.lottery;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.EEmailType;
import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropRandomHit;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.server.RedisKey;
import com.ftkj.util.RandomUtil;

/**
 * 抽奖活动
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@ActiveAnno(redType=ERedType.活动, atv = EAtv.步步高升奖池抽奖, clazz=AtvLotteryManager.AtvLottery.class)
public class AtvLotteryManager extends ActiveBaseManager {
    private static final Logger log = LoggerFactory.getLogger(AtvLotteryManager.class);
	
	/**
	 * 幸运玩家统计
	 */
	private Map<Long, AtvLotteryLucky> luckyMap;
	
	// 抽奖次数，总抽奖球券，幸运大奖的权重（每日，抽的多的会中的次数多）
	@Override
	public void instanceAfter() {
		super.instanceAfter();
		luckyMap = redis.getMapAllKeyValues(getLuckyReidsKey());
	}
	
	/**
	 * 每天数据
	 * @return
	 */
	public String getLuckyReidsKey() {
		return RedisKey.getKey(this.getId(), RedisKey.Lottery_Lucky_Data_Day);
	}
	
	@Override
	public void end() {
		super.end();
		redis.del(getLuckyReidsKey());
	}
	
	/**
	 * 取玩家幸运抽奖数据
	 * @param teamId
	 * @return
	 */
	public AtvLotteryLucky getLotteryLucky(long teamId) {
		if(luckyMap.containsKey(teamId)) return luckyMap.get(teamId);
		AtvLotteryLucky atvObj = new AtvLotteryLucky(teamId);
		saveAtvLotteryLucky(teamId, atvObj);
		return atvObj;
	}
	
	/**
	 * 保存幸运数据
	 * @param teamId
	 * @param atvObj
	 */
	private void saveAtvLotteryLucky(long teamId, AtvLotteryLucky atvObj) {
		luckyMap.put(teamId, atvObj);
		redis.putMapValue(getLuckyReidsKey(), teamId, atvObj);
	}
	
	/**
	 * 活动数据类
	 */
	public static class AtvLottery extends ActiveBase {
		private static final long serialVersionUID = 1L;

		public AtvLottery(ActiveBasePO po) {
			super(po);
		}
		public int getCount() {
			return this.getiData1();
		}
		public int getTotalFk() {
			return this.getiData2();
		}
		/**
		 * 抽奖统计
		 * @param totalFk 抽奖金额
		 */
		public void addLottery(int totalFk) {
			this.setiData1(this.getiData1() + 1);
			this.setiData2(this.getiData2() + totalFk);
		}
	}
	
	/**
	 * 幸运统计数据
	 * @author Jay
	 * @time:2018年2月6日 下午3:58:10
	 */
	public static class AtvLotteryLucky implements Serializable {
		private static final long serialVersionUID = 1L;
		/**
		 * 每抽奖一次加1
		 */
		private int rate;
		private long teamId;

		public AtvLotteryLucky(long teamId) {
			super();
			this.teamId = teamId;
		}
		public int getRate() {
			return rate;
		}
		/**
		 * 增加概率
		 * @param rate
		 */
		public void addRate(int rate) {
			this.rate += rate;
		}
		public long getTeamId() {
			return teamId;
		}
		
	}
	
	/**
	 * 当前结束，抽取当天幸运玩家 
	 */
	@Override
	public void everyDayEnd(DateTime time) {
		super.everyDayEnd(time);
		if(getStatus(time) != EActiveStatus.进行中) return;
		if(getDay() < 0) return;		
		// 抽取昨天的幸运奖处理,保存幸运玩家数据,奖励发到邮箱
		Map<Long,Integer> rateMap = luckyMap.values().stream().collect(Collectors.toMap(AtvLotteryLucky::getTeamId, s-> s.getRate()));
		log.debug("步步高升幸运抽奖={}",rateMap);
		// 清空，保留1概率
		luckyMap.values().forEach(k-> {
			k.rate = 1;
		});
		if(rateMap.size() > 0) {
			Long teamId = RandomUtil.randMap(rateMap);
			List<PropSimple> awardList = PropSimple.getPropBeanByStringNotConfig(getConfigStr("luckyAward"), "[&]", "[_]");
			// 发邮件
			emailManager.sendEmailFinal(teamId, EEmailType.系统邮件.getType(), 0, "抽奖幸运奖励", "幸运玩家", PropSimple.getPropStringByList(awardList));
			// 因为是当天的结束，第二天要显示的幸运奖，所以+1 day
			String key = RedisKey.getDayKey(this.getId(), RedisKey.Lottery_Lucky_Team_Day, time.plusHours(12));
			redis.set(key, teamId+"", RedisKey.DAY);
		}
	}
	
	
	/**
	 * 抽奖主界面
	 */
	@Override
	public void showView() {
		long teamId = getTeamId();
		// 幸运玩家
		Long luckyTeamId = redis.getLong(RedisKey.getDayKey(this.getId(), RedisKey.Lottery_Lucky_Team_Day));
		Team luckyTeam = teamManager.getTeam(luckyTeamId);
		AtvLottery atvObj = getTeamData(teamId);
		//
		sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				// 抽次数
				.setValue(atvObj.getCount())
				// 幸运球队ID
				.setOther(luckyTeam==null?0L:luckyTeam.getTeamId())
				// 幸运球队名称
				.setExtend(luckyTeam==null?"":luckyTeam.getName())
				.build());
	}
	
	/**
	 * 抽奖
	 */
	@Override
	public void getDayAward() {
		if(getStatus() != EActiveStatus.进行中) {
			sendMessage(getLotteryData(ErrorCode.Active_1, -1));
			return;
		}
		long teamId = getTeamId();
		int needFK = getConfigInt("lotteryFK", 88);
		TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
		if(!teamMoney.hasMoney(EMoneyType.Money, needFK)) {
			log.debug("球券不足");
			sendMessage(getLotteryData(ErrorCode.Active_1, -1));
			return;
		}
		// 扣球券
		teamMoney.updateMoney(EMoneyType.Money, -Math.abs(needFK));
		// 抽奖品
		AtvLottery atvObj = getTeamData(teamId);
		int randomLv = getMyRandom(atvObj.getCount());
		SystemActiveCfgBean cfgBean = getAwardConfigList().get(randomLv);
		if(cfgBean == null || !cfgBean.getAwardType().equals("PropRandom")) {
			log.debug("抽奖奖励配置异常");
			sendMessage(getLotteryData(ErrorCode.Error, -1));
			return;
		}
		PropRandomHit hit = sendLotteryAward(teamId, randomLv, null);
		//
		atvObj.addLottery(needFK);
		atvObj.save();
		//
		if(randomLv >= 1) {
			AtvLotteryLucky atvLucky = getLotteryLucky(teamId);
			atvLucky.addRate(1);
			saveAtvLotteryLucky(teamId, atvLucky);
		}
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(randomLv)
				.setCode(ErrorCode.Success.code)
				.setValue(hit.getIndex())
				.addAllAwardList(PropManager.getPropSimpleListData(hit.getItem().getPropSimpleList()))
				.build());
	}
	
	/**
	 * 根据抽奖次数来得到级别
	 * @param count
	 * @return
	 */
	private int getMyRandom(int count) {
		Map<Integer, SystemActiveCfgBean> map = getAwardConfigList();
		int lv = 0;
		for(int key : map.keySet()) {
			int needCount = Integer.valueOf(map.get(key).getConditionMap().get("count"));
			if(needCount == -1 || count < needCount) {
				lv = key;
				break;
			}
		}
		return lv;
	}
	
	/**
	 * 抽奖协议
	 * @param code
	 * @param index
	 * @return
	 */
	public AtvAwardData getLotteryData(ErrorCode code, int index) {
		return AtvAwardData.newBuilder().setAtvId(this.getId()).setAwardId(-1).setCode(code.code).setValue(index).build();
	}
	
	/**
	 * 不能领奖
	 */
	@Override
	public ErrorCode checkGetAwardCustom(long teamId, ActiveBase atvObj, int id) {
		return ErrorCode.Error;
	}
}
