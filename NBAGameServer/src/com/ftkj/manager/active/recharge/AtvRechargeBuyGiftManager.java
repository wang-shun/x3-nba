package com.ftkj.manager.active.recharge;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.data.AtvDayAwardData;
import com.ftkj.manager.logic.PropManager;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvAwardData;
import com.ftkj.util.DateTimeUtil;

/**
 * 福袋_累计充值后购买，然后每天领奖
 * @author Jay
 */
@ActiveAnno(redType=ERedType.活动, atv = EAtv.福袋_累计充值后购买, clazz=AtvDayAwardData.class)
public class AtvRechargeBuyGiftManager extends ActiveBaseManager {
	private static final Logger log = LoggerFactory.getLogger(AtvRechargeBuyGiftManager.class);
	/**
	 * 已充值金额
	 * 购买状态 0没买， 非0对应已买档次
	 * 今天领取状态
	 */
	@Override
	public void showView() {
		long teamId = getTeamId();
		AtvDayAwardData atvObj = getTeamData(teamId);
		SystemActiveBean bean = getBean();
		int rechargeSum = rechargeManager.getRechargeTotalBetweenDay(teamId, bean.getStartDateTime(), bean.getEndDateTime());
		sendMessage(AtvCommonPB.AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				// 购买的哪一档
				.setValue(atvObj.getValue())
				// 充值金额
				.setOther(rechargeSum)
				// 可领奖励
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				// 已领取奖励
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	@Override
	public void buyFinish(int tp) {
		long teamId = getTeamId();
		AtvDayAwardData atvObj = getTeamData(teamId);
		TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
		ErrorCode code = checkBuyFinish(atvObj, teamMoney, tp);
		// 自定义校验
		if(atvObj.getValue() != 0) {
			log.debug("不能重复购买");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(code == ErrorCode.Success) {
			// 直接完成,第一天可领
			atvObj.getFinishStatus().addValue(atvObj.getGetDay());
			atvObj.setMaxDay(6);
			atvObj.setValue(tp);
			atvObj.save();
		}
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(tp)
				.setValue(tp)
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.setCode(code.code).build());
		redPointPush(teamId);
	}
	
	@Override
	public void getDayAward() {
		long teamId = getTeamId();
		AtvDayAwardData atvObj = getTeamData(teamId);
		if(atvObj.getValue() == 0) {
			log.debug("还没购买");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(atvObj.getMaxDay()-atvObj.getGetDay() < 1) {
			log.debug("已领取完");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		String lastDate = DateTimeUtil.getString(DateTime.now());
		if(atvObj.getLastGetDate().equals(lastDate)) {
			log.debug("今天已领取过");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		SystemActiveCfgBean cfgBean = getAwardConfigList().get(atvObj.getValue());
		if(cfgBean == null) {
			log.debug("没有该档奖励");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		if(atvObj.getAwardStatus().containsValue(atvObj.getGetDay())) {
			log.debug("今天已领取过");
			sendMessage(AtvAwardData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		// 今天的奖励已领
		atvObj.getAwardStatus().addValue(atvObj.getGetDay());
		// 当天的奖励
		int awardDay = atvObj.getGetDay();
		atvObj.setLastGetDate(lastDate);
		atvObj.save();
		// 发奖
		List<PropSimple> awardList = cfgBean.getAwardList().get(awardDay).getPropSimpleList();
		propManager.addPropList(teamId, awardList, true, getActiveModuleLog());
		//
		sendMessage(AtvAwardData.newBuilder()
				.setAtvId(this.getId())
				.setAwardId(-1)
				.setCode(ErrorCode.Success.code)
				.addAllAwardList(PropManager.getPropSimpleListData(awardList))
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends ActiveBase> T getTeamData(long teamId) {
		AtvDayAwardData atvObj = super.getTeamData(teamId);
		// 判断是否新增新的一天可领
		if(atvObj.getValue()>0 
				&& !DateTimeUtil.getString(DateTime.now()).equals(atvObj.getLastGetDate())
				&& atvObj.getMaxDay() - atvObj.getGetDay() > 0
				// 并且上一天的已领
				&& atvObj.getAwardStatus().containsValue(atvObj.getGetDay())) {
			// 下一天可领
			atvObj.addGetDay();
			atvObj.getFinishStatus().addValue(atvObj.getGetDay());
			atvObj.save();
		}
		return (T) atvObj;
	}
	
}
