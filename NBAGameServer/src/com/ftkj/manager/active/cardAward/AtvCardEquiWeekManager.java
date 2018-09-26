package com.ftkj.manager.active.cardAward;

import org.joda.time.DateTime;

import com.ftkj.enums.EBuffKey;
import com.ftkj.enums.EBuffType;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.buff.TeamBuff;
import com.google.common.eventbus.Subscribe;

/**
 * 装备经验月卡
 * @author Jay
 * @time:2017年9月15日 下午2:23:41
 */
//@EventRegister({EEventType.购买装备强化周卡})
//@ActiveAnno(redType=ERedType.活动, atv = EAtv.装备强化周卡, clazz=AtvCardAward.class)
public class AtvCardEquiWeekManager extends AtvCardAwardBaseManager {

	@Subscribe
	public void callback(RechargeParam param) {
		// 添加购买记录
		addBuyTeam(param.teamId);
	}

	@Override
	public void addBuff(long teamId) {
		TeamBuff buff = new TeamBuff(EBuffKey.周卡加成, EBuffType.主线赛程快速结束比赛, new int[]{1}, DateTime.now().plusDays(7), false);
		buffManager.addBuff(teamId, buff);
	}
	
	
}
