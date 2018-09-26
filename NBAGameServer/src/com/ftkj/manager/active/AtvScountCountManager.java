package com.ftkj.manager.active;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.ScountParam;
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
@EventRegister({EEventType.招募})
@ActiveAnno(redType=ERedType.活动, atv = EAtv.端午活动_招募得积分)
public class AtvScountCountManager extends ActiveBaseManager {

	@IOC
	private PlayerManager playerManager;
	
	@Subscribe
    private void pkEnd(ScountParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		// 统计完成
		ActiveBase atvObj = getTeamData(param.teamId);
		atvObj.getPropNum().setValueAdd(param.type, param.count);
		//
		String cond = "roll_" + param.type;
		for(SystemActiveCfgBean cfg : getAwardConfigList().values()) {
			if(!cfg.getConditionMap().containsKey(cond)) continue;
			int need = Integer.valueOf(cfg.getConditionMap().get(cond));
			if(atvObj.getPropNum().getValue(param.type) < need) {
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
