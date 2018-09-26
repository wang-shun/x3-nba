package com.ftkj.manager.active.battle;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.PKParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.logic.PlayerManager;
import com.google.common.eventbus.Subscribe;

/**
 * 公测天梯赛PK之王
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@EventRegister({EEventType.比赛结束})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.公测_天梯赛PK之王)
public class AtvBetaBattleManager extends ActiveBaseManager {

	@IOC
	private PlayerManager playerManager;
	
	@Subscribe
    private void pkEnd(PKParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		if(param.type != EBattleType.Ranked_Match) return;
		teamPKEnd(param.teamId, param.isWin);
	}
	
	/**
	 * 球队比赛结束
	 * @param teamId
	 * @param isWin
	 */
	private void teamPKEnd(long teamId, boolean isWin) {
		ActiveBase atvObj = getTeamData(teamId);
		atvObj.setiData1(atvObj.getiData1() + 1);
		// 奖励ID列表
		for(SystemActiveCfgBean cfg : getAwardConfigList().values()) {
			if(!cfg.getConditionMap().containsKey("count")) continue;
			int count = Integer.valueOf(cfg.getConditionMap().get("count"));
			if(atvObj.getiData1() < count) {
				break;
			}
			if(atvObj.getFinishStatus().containsValue(cfg.getId())) {
				continue;
			}
			atvObj.getFinishStatus().addValue(cfg.getId());
		}
		//
		atvObj.save();
		redPointPush(teamId);
	}
	

}
