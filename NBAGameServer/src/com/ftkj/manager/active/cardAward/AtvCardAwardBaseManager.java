package com.ftkj.manager.active.cardAward;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.logic.BuffManager;
import com.ftkj.proto.AtvCardAwardPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.util.DateTimeUtil;

/**
 * 月卡周卡类基类
 * @author Jay
 * @time:2017年9月15日 下午2:44:44
 */
public abstract class AtvCardAwardBaseManager extends ActiveBaseManager implements ICardWardManager {
	private static final Logger log = LoggerFactory.getLogger(AtvCardAwardBaseManager.class);
	@IOC
	protected BuffManager buffManager;

	public void addBuyTeam(long teamId) {
		// 就是保存一条记录，有记录才可以领奖，如果记录已存在，新的购买时间会替换
		AtvCardAward atvObj = getTeamData(teamId);
		// 今天购买就重置一下
		if(atvObj.isNull()) {
			atvObj.clear();
		}
		//
		atvObj.setDayCount(atvObj.getDayCount() + getConfigInt("days", 0));
		atvObj.setCreateTime(DateTime.now());
		atvObj.save();
		// 第一次购买奖励
		propManager.addPropList(teamId, getAwardConfigList().get(0).getPropSimpleList(), true, getActiveModuleLog());
		//
		addBuff(teamId);
	}
	
	/**
	 * 添加Buff,子类各自实现
	 */
	public abstract void addBuff(long teamId);
	
	/**
	 * 领取本天的奖励
	 */
	@Override
	public void getCardAward() {
		// 返回今天是否可领取即可. 待活动ID返回
		long teamId = getTeamId();
		AtvCardAward atvObj = getTeamData(teamId);
		if(atvObj.isNull()) {
			log.debug("请先购买福利再领取奖励");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		int dayIndex = DateTimeUtil.getDaysBetweenNum(atvObj.getCreateTime(), DateTime.now(), 0);
		//
		if(dayIndex >= atvObj.getDayCount()) {
			log.debug("已超过领取天数!");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(atvObj.getStatus().getValue(dayIndex) == 1) {
			log.debug("今天已领取过奖励");
			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 已领天数
		atvObj.setiData2(atvObj.getiData2()+1);
		atvObj.getStatus().setValue(dayIndex, 1);
		atvObj.save();
		// 奖励
		propManager.addPropList(teamId, getAwardConfigList().get(1).getPropSimpleList(), true, getActiveModuleLog());
		sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}

	
	private AtvCardAwardPB.AtvCardData getTeamCardData(int atvId, AtvCardAward atvObj) {
		int days = DateTimeUtil.getDaysBetweenNum(atvObj.getCreateTime(), DateTime.now(), 0);
		int hasCount = atvObj.getDayCount() - days - 1;
		// 今天是否已领
		int status = hasCount >= 0 ? atvObj.getStatus().getValue(days) : 1;
		return AtvCardAwardPB.AtvCardData.newBuilder()
				.setAtvId(atvId)
				// 剩余天数，最后1天买了之后，就是剩余0了
				.setDays(atvObj.getDayCount() - days - (status==1?1:0))
				// 当天是否已领
				.setStatus(status)
				.setBuy(atvObj.isNull())
				.build();
	}
	
	@Override
	public AtvCardAwardPB.AtvCardData getTeamActiveData(long teamId) {
		return getTeamCardData(this.getId(), this.getTeamData(teamId));
	}
}
