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
import com.ftkj.manager.logic.PlayerCardManager;
import com.ftkj.manager.team.Team;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_球星卡星级
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
//@EventRegister({EEventType.球星卡品质})
//@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_球星卡品质, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadCardQuaManager extends AtvStarLoadBaseManager {
	
	@IOC
	private PlayerCardManager cardManager;
	
	@Subscribe
	public void callback(Long teamId) {
		Team team = teamManager.getTeam(teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		// 完成情况
		Map<Integer, Long> countMap = getCardQuaCount(team.getTeamId());
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		// 检查是否满足上榜
		int needLv = getConfigInt("quality", 0);
		int starlvCount = (int) getMapCountEqId(needLv, countMap);
		int oldValue = atvObj.getRankValue();
		checkUpdateRank(atvObj, starlvCount, oldValue, false);
		//		
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer lv = Integer.valueOf(cfgs.get(id).getConditionMap().get("qua"));
			Integer num = Integer.valueOf(cfgs.get(id).getConditionMap().get("num"));
			if(getMapCountEqId(lv, countMap) < num) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		//
		atvObj.setCount(starlvCount);
		atvObj.save();
	}
	
	/**
	 * 返回装备   等级：数量  的map
	 * @return
	 */
	private Map<Integer, Long> getCardQuaCount(long teamId) {
		//Map<Integer,Long> countMap = cardManager.getTeamPlayerCard(teamId).getAllCollectCard().stream().collect(Collectors.groupingBy(e->e.getType() , Collectors.counting()));
		//return countMap;
		return Maps.newHashMap();
	}

	@Override
	protected int getRankCondition() {
		return getConfigInt("num", 0);
	}
	
}
