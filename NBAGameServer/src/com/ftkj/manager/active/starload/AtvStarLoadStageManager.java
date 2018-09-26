package com.ftkj.manager.active.starload;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.StagePassParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_主线赛程
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.主线赛程通关})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_主线赛程, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadStageManager extends AtvStarLoadBaseManager {
	
	@Subscribe
	public void callback(StagePassParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		int newValue = param.stageId;
		int oldValue = atvObj.getRankValue();
		// 检查是否满足上榜
		checkUpdateRank(atvObj, newValue, oldValue, false);
		// 检查是否可完成
		SystemActiveCfgBean awardCfg = getAwardConfigList().values().stream()
				.filter(m-> m.getConditionMap().containsKey("stage"))
				.filter(m-> Integer.valueOf(m.getConditionMap().get("stage")) == param.stageId).findFirst().orElse(null);
		if(!checkFinish(awardCfg, team.getCreateDay(), atvObj)) {
			return;
		}
		//
		atvObj.getStatus().setValue(awardCfg.getId()-1, 1);
		atvObj.setCount(param.stageId);
		atvObj.save();
		//
		redPointPush(param.teamId);
	}
	
	@Override
	protected int getRankCondition() {
		return getConfigInt("stage", 0);
	}
	
}
