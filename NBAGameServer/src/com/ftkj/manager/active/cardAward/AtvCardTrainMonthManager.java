package com.ftkj.manager.active.cardAward;

import org.joda.time.DateTime;

import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.buff.TeamBuff;
import com.google.common.eventbus.Subscribe;

/**
 * 训练点月卡
 * @author Jay
 * @time:2017年9月15日 下午2:23:41
 */
//@EventRegister({EEventType.购买训练点月卡})
//@ActiveAnno(redType=ERedType.活动, atv = EAtv.训练点月卡, clazz=AtvCardAward.class)
public class AtvCardTrainMonthManager extends AtvCardAwardBaseManager {

	@Subscribe
	public void callback(RechargeParam param) {
		addBuyTeam(param.teamId);
	}

	@Override
	public void addBuff(long teamId) {
		int rate = getConfigInt("rate", 0);
		int rtn = getConfigInt("rtn", 0);
		TeamBuff buff = new TeamBuff(EBuffKey.月卡加成, EBuffType.训练有X概率返还X比例的训练点, new int[]{rate, rtn}, DateTime.now().plusDays(30), false);
		buffManager.addBuff(teamId, buff);
	}
	
	
}
