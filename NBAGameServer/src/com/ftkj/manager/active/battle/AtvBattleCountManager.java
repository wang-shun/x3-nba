package com.ftkj.manager.active.battle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
 * @author Jay
 * @time:2018年2月5日 下午5:46:46
 */
@EventRegister({EEventType.比赛结束})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.端午活动_比赛得积分)
public class AtvBattleCountManager extends ActiveBaseManager {

	@IOC
	private PlayerManager playerManager;
	
	/**
	 * 统计的比赛类型
	 */
	private List<String> pkTypes = Arrays.stream(new String[]{
			EBattleType.Ranked_Match.getType(),
			EBattleType.Main_Match_Normal.getType(),
			EBattleType.联盟组队赛.getType(),
			EBattleType.Arena.getType(),
			EBattleType.多人赛_100.getType(),
			EBattleType.即时比赛跨服.getType(),
	}).collect(Collectors.toList());
	
	
	@Subscribe
    private void pkEnd(PKParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		if(!pkTypes.contains(param.type.getType())) return;
		// 统计完成
		ActiveBase atvObj = getTeamData(param.teamId);
		int index = pkTypes.indexOf(param.type.getType());
		atvObj.getPropNum().setValueAdd(index, 1);
		//
		String cond = param.type.getType();
		for(SystemActiveCfgBean cfg : getAwardConfigList().values()) {
			if(!cfg.getConditionMap().containsKey(cond)) continue;
			int need = Integer.valueOf(cfg.getConditionMap().get(cond));
			if(atvObj.getPropNum().getValue(index) < need) {
				break;
			}
			if(atvObj.getFinishStatus().containsValue(cfg.getId())) {
				continue;
			}
			atvObj.getFinishStatus().addValue(cfg.getId());
		}
		atvObj.save();
		//
		redPointPush(param.teamId);
	}

}
