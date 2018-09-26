package com.ftkj.manager.active.starload;

import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_球队工资
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.球队现有工资})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_球队工资, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadTeamPriceManager extends AtvStarLoadBaseManager {
	
	@IOC
	private PlayerManager playerManager;
	
	/**
	 * 工资变动
	 * @param teamId
	 */
	@Subscribe
	public void callback(Long teamId) {
		Team team = teamManager.getTeam(teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		// 完成情况
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		// 检查是否满足上榜
		int teamPrice = playerManager.getTeamPlayer(teamId).getPlayerPrice();
		int oldValue = atvObj.getRankValue();
		checkUpdateRank(atvObj, teamPrice, oldValue, false);;
		//		
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer needPrice = Integer.valueOf(cfgs.get(id).getConditionMap().get("price"));
			if(teamPrice < needPrice) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		//
		atvObj.setCount(teamPrice);
		atvObj.save();
		//
		redPointPush(teamId);
	}
	
	@Override
	protected int getRankCondition() {
		return getConfigInt("price", 0);
	}
	
}
