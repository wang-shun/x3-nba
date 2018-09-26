package com.ftkj.manager.active.starload;

import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.EquiParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.EquiManager;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_装备升级
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.装备进化})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_装备进阶, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadEquiUpQuaManager extends AtvStarLoadBaseManager {
	
	@IOC
	private EquiManager equiManager;
	
	@Subscribe
	public void callback(EquiParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		AtvPlayerStarRoad atvObj = getTeamData(team.getTeamId());
		// 完成情况
		Map<Integer, Long> equiCountMap = getTeamEquiCount(team.getTeamId());
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		// 检查是否满足上榜
		int needLv = getConfigInt("quality", 0);
		int strlvCount = (int) getMapCountEqId(needLv, equiCountMap);
		int oldValue = atvObj.getRankValue();
		checkUpdateRank(atvObj, strlvCount, oldValue, false);
		//		
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer lv = Integer.valueOf(cfgs.get(id).getConditionMap().get("qua"));
			Integer num = Integer.valueOf(cfgs.get(id).getConditionMap().get("num"));
			if(getMapCountEqId(lv, equiCountMap) < num) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		//
		atvObj.setCount(strlvCount);
		atvObj.save();
		//
		redPointPush(team.getTeamId());
	}
	
	/**
	 * 返回装备   等级：数量  的map
	 * @return
	 */
	private Map<Integer, Long> getTeamEquiCount(long teamId) {
		return equiManager.getTeamEqui(teamId).getTeamAllEqui().stream().collect(Collectors.groupingBy(e->e.getQuality() , Collectors.counting()));
	}
	
}
