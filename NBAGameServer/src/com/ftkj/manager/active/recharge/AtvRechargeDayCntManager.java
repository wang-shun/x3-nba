package com.ftkj.manager.active.recharge;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.active.longtime.AtvRechargeStatisticsManager;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.google.common.eventbus.Subscribe;

/**
 * 累计充值天数领奖，可领取奖励
 * @author Jay
 * @time:2018年2月1日 下午5:32:26
 */
@EventRegister({EEventType.充值活动统计事件})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.累计充值天数领奖, clazz=ActiveBase.class)
public class AtvRechargeDayCntManager extends ActiveBaseManager {

	@IOC
	private AtvRechargeStatisticsManager rechargeManager;
	
	@Override
	public void instanceAfter() {
		super.instanceAfter();
	}
	
	/**
	 * 主界面
	 * 返回累计充值天数和累计充值金额
	 */
	@Override
	public void showView() {
		long teamId = getTeamId();
		SystemActiveBean bean = getBean();
		int day = rechargeManager.getRechargeDayBetweenDay(teamId, bean.getStartDateTime(), bean.getEndDateTime());
		int sum = rechargeManager.getRechargeTotalBetweenDay(teamId, bean.getStartDateTime(), bean.getEndDateTime());
		ActiveBase atvObj = getTeamData(teamId);
		sendMessage(AtvCommonData.newBuilder()
				.setAtvId(this.getId())
				.setValue(sum)
				.setOther(day)
				.addAllFinishStatus(atvObj.getFinishStatus().getList())
				.addAllAwardStatus(atvObj.getAwardStatus().getList())
				.build());
	}
	
	/**
	 * 充值回调
	 * @param param
	 */
	@Subscribe
	public void addMoneyResult(RechargeParam param) {
		long teamId = param.teamId;
		SystemActiveBean bean = getBean();
		int day = rechargeManager.getRechargeDayBetweenDay(teamId, bean.getStartDateTime(), bean.getEndDateTime());
		ActiveBase atvObj = getTeamData(teamId);
		bean.getAwardConfigList().values().forEach(s->{
			int needDay = new Integer(s.getConditionMap().get("rechargeDay"));
			if(day >= needDay && !atvObj.getFinishStatus().containsValue(s.getId())) {
				atvObj.getFinishStatus().addValue(s.getId());
			}
		});
		atvObj.save();
		redPointPush(teamId);
	}
}
