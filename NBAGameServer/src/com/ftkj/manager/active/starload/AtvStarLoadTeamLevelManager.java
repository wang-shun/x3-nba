package com.ftkj.manager.active.starload;

import java.util.Map;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LevelupParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_球队等级
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.球队升级})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_球队等级, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadTeamLevelManager extends AtvStarLoadBaseManager {
	
	@Subscribe
	public void callback(LevelupParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		// 检查是否满足上榜
		int oldValue = atvObj.getRankValue();
		checkUpdateRank(atvObj, team.getLevel(), oldValue, false);
		//
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer needLv = Integer.valueOf(cfgs.get(id).getConditionMap().get("level"));
			if(param.level < needLv) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		atvObj.setCount(team.getLevel());
		atvObj.save();
		//
		redPointPush(param.teamId);
	}
	
	@Override
	protected int getRankCondition() {
		return getConfigInt("level", 0);
	}
	
	/**
	 * 入口只检查这个活动，创建号大于15天就隐藏掉入口.
	 */
	@Override
	public boolean checkHideWindow(long teamId) {
		Team team = teamManager.getTeam(teamId);
		if(team == null) return false;
		return team.getCreateDay()>15;
	}
	
}
