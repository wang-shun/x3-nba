package com.ftkj.manager.active.logins;

import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.google.common.eventbus.Subscribe;

@EventRegister({EEventType.登录})
@ActiveAnno(redType = ERedType.活动, atv = EAtv.公测_累计登录礼包, clazz=AtvDaysLogin.class)
public class AtvBetaLoginCountManager extends ActiveBaseManager {
	
	/**
	 * 登录回调
	 * @param param
	 */
	@Subscribe
	public void login(LoginParam param) {
		long teamId = param.teamId;
		if(getStatus() != EActiveStatus.进行中) return;
		AtvDaysLogin atvObj = getTeamData(teamId);
		if(atvObj.getLastTime().getDayOfYear() == param.loginTime.getDayOfYear()) {
			return;
		}
		//
		atvObj.addLoginDay(1);
		atvObj.setLastTime(param.loginTime);
		// 检查可领奖励finish
		for(int awardId : getAwardConfigList().keySet()) {
			if(atvObj.getLoginDay() < awardId) {
				break;
			}
			if(atvObj.getFinishStatus().containsValue(awardId)) {
				continue;
			}
			atvObj.getFinishStatus().addValue(awardId);
		}
		atvObj.save();
		// 可领提示更新
		redPointPush(teamId);
	}
	
}
