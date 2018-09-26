package com.ftkj.manager.active;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LevelupParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerManager;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

/**
 * 等级礼包
 * @author Jay
 * @time:2018年5月15日 下午2:46:49
 */
@EventRegister({EEventType.球队升级})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.等级礼包)
public class AtvTeamLevelManager extends ActiveBaseManager {
	@IOC
    private PlayerManager playerManager;

	@Subscribe
	public void callback(LevelupParam param) {
		Team team = teamManager.getTeam(param.teamId);
		if(getStatus() != EActiveStatus.进行中) {
			return;
		}
		ActiveBase atvObj = getTeamData(team.getTeamId());
		checkFinish(atvObj, param.level);
		redPointPush(param.teamId);
	}
	
	private void checkFinish(ActiveBase atvObj, int level) {
		Map<Integer, SystemActiveCfgBean> map = getAwardConfigList();
		for(SystemActiveCfgBean cfg : map.values()) {
			if(atvObj.getFinishStatus().containsValue(cfg.getId())) {
				continue;
			}
			if(level < Integer.valueOf(cfg.getConditionMap().get("level"))) {
				continue;
			}
			atvObj.getFinishStatus().addValue(cfg.getId());
		}
		atvObj.save();
	}
	
	@Override
	public <T extends ActiveBase> void createInit(T t) {
		Team team = teamManager.getTeam(t.getTeamId());
		checkFinish(t, team.getLevel());
	}
	

	@Override
	public ErrorCode checkGetAwardCustom(long teamId, ActiveBase atvObj, int id) {
		int awardPlayerNum = 0;
    	Map<Integer, List<PropSimple>> map = getAwardPlayersNum(id);
    	if (map.get(1) != null) {
			awardPlayerNum = map.get(1).size();
		}
    	
        if (playerManager.getStorageSize(teamId) < awardPlayerNum) {
            return ErrorCode.Player_Storage_Full;
        }
        return ErrorCode.Success;
	}

	@Override
	public List<PropSimple> sendAward(long teamId, ActiveBase activeBase, int id) {
		sendAward(teamId, id);
        SystemActiveCfgBean cfg = getAwardConfigList().get(id);
        return new ArrayList<PropSimple>(cfg.getPropSimpleList());
	}
	
	
	
}
