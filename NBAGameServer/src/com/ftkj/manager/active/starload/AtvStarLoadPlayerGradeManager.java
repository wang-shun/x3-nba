package com.ftkj.manager.active.starload;

import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.PlayerLevelParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerGradeManager;
import com.ftkj.manager.player.TeamPlayerGrade;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 巨星之路_球员升级
 * @author Jay
 * @time:2017年9月7日 下午2:46:44
 */
@EventRegister({EEventType.球员升级})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.巨星之路_球员等级, clazz=AtvPlayerStarRoad.class)
public class AtvStarLoadPlayerGradeManager extends AtvStarLoadBaseManager {
	
	@IOC
	private PlayerGradeManager PlayerGradeManager;
	
	@Subscribe
	public void callback(PlayerLevelParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus() != EActiveStatus.进行中 || team.getCreateDay()>15) return;
		TeamPlayerGrade tpg = PlayerGradeManager.getTeamPlayerGrade(param.teamId);
		AtvPlayerStarRoad atvObj = getTeamData(param.teamId);
		// 检查是否满足上榜
		int needGrade = getConfigInt("grade", 0);
		int oldValue = atvObj.getRankValue();
		int newValue = getGradeSum(tpg, needGrade);
		checkUpdateRank(atvObj, newValue, oldValue, false);
		//
		Map<Integer, SystemActiveCfgBean> cfgs = getAwardConfigList();
		for(int id:cfgs.keySet()) {
			if(id> 14) {
				continue;
			}
			Integer grade = Integer.valueOf(cfgs.get(id).getConditionMap().get("grade"));
			Integer num = Integer.valueOf(cfgs.get(id).getConditionMap().get("num"));
			int hasNum = getGradeCount(tpg, grade);
			if(hasNum < num) {
				continue;
			}
			if(!checkFinish(cfgs.get(id), team.getCreateDay(), atvObj)) {
				continue;
			}
			atvObj.getStatus().setValue(id-1, 1);
		}
		atvObj.setCount(newValue);
		atvObj.save();
		//
		redPointPush(team.getTeamId());
	}	
	
	private int getGradeSum(TeamPlayerGrade tpg, int grade) {
		return tpg.getPlayerGradeMap().values().stream().filter(s-> s.getGrade() >= grade).mapToInt(s-> s.getGrade()).sum();
	}
	private int getGradeCount(TeamPlayerGrade tpg, int grade) {
		return (int) tpg.getPlayerGradeMap().values().stream().filter(s-> s.getGrade() >= grade).mapToInt(s-> s.getGrade()).count();
	}
	
}
