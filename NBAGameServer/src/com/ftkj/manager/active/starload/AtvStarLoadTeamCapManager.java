package com.ftkj.manager.active.starload;

import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.TeamCapManager;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_全队攻防
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.全队攻防})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_全队攻防, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadTeamCapManager extends AtvStarLoadBaseManager {
	
	@IOC
	private TeamCapManager capManager;
	
	/**
	 * 登录的时候调用一次， 主界面打开调用
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
		TeamAbility ability = new TeamAbility(AbilityType.Team, capManager.getTeamAllPlayerAbilities(teamId));
		int teamCap = ability.getTotalCap();
		int oldValue = atvObj.getRankValue();
		checkUpdateRank(atvObj, teamCap, oldValue, false);
		//		
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer needCap = Integer.valueOf(cfgs.get(id).getConditionMap().get("cap"));
			if(teamCap < needCap) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		//
		if(teamCap > atvObj.getCount()) {
			atvObj.setCount(teamCap);
		}
		atvObj.save();
		//
		redPointPush(teamId);
	}
	
	@Override
	protected int getRankCondition() {
		return getConfigInt("teamCap", 0);
	}
	
	/**
	 * 打开主界面也调用一次
	 */
	@Override
	public void showView() {
		callback(getTeamId());
		super.showView();
	}
	
}
