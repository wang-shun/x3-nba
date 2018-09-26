package com.ftkj.manager.active.starload;

import java.util.Map;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_累计充值
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.充值})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_累计充值, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadRechargeManager extends AtvStarLoadBaseManager {
	
	@Subscribe
	public void callback(RechargeParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus(param.time) != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		// 完成情况
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		// 检查是否满足上榜
		int addMeoney = atvObj.getCount() + param.fk;
		int oldValue = atvObj.getRankValue();
		atvObj.setCount(addMeoney);
		checkUpdateRank(atvObj, addMeoney, oldValue, false);
		//		
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer needMoney = Integer.valueOf(cfgs.get(id).getConditionMap().get("money"));
			if(addMeoney < needMoney) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		//
		atvObj.save();
		//
		redPointPush(team.getTeamId());
	}
	
	@Override
	protected int getRankCondition() {
		return getConfigInt("money", 0);
	}
	
}
