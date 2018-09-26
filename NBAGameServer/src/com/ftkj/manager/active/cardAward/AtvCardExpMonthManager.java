package com.ftkj.manager.active.cardAward;

import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.google.common.eventbus.Subscribe;

/**
 * 经验手册月卡
 * @author Jay
 * @time:2017年9月15日 下午2:23:41
 */
//@EventRegister({EEventType.购买经验手册月卡})
//@ActiveAnno(redType=ERedType.活动, atv = EAtv.经验手册月卡, clazz=AtvCardAward.class)
public class AtvCardExpMonthManager extends AtvCardAwardBaseManager {

	@Subscribe
	public void callback(RechargeParam param) {
		// 添加购买记录
		addBuyTeam(param.teamId);
	}

	@Override
	public void addBuff(long teamId) {
	}
	
	
}
