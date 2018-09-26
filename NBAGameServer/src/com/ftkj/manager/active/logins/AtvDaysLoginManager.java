package com.ftkj.manager.active.logins;

import com.ftkj.cfg.SystemActiveCfgBean;
import com.ftkj.enums.EActiveStatus;
import com.ftkj.event.EEventType;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.active.base.ActiveAnno;
import com.ftkj.manager.active.base.ActiveAnno.ERedType;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.active.base.EAtv;
import com.ftkj.manager.active.base.EventRegister;
import com.google.common.eventbus.Subscribe;

/**
 * 指定天登录领奖励
 * @author Jay
 * @time:2018年2月3日 下午3:06:43
 */
@EventRegister({EEventType.登录})
@ActiveAnno(redType = ERedType.活动, atv = EAtv.指定天登录领奖, clazz=AtvDaysLogin.class)
public class AtvDaysLoginManager extends ActiveBaseManager{
	
	/**
	 * 登录回调
	 * @param param
	 */
	@Subscribe
	public void login(LoginParam param) {
		if(getStatus() != EActiveStatus.进行中) return;
		long teamId = param.teamId;
		AtvDaysLogin atvObj = getTeamData(teamId);
		int day = getDay();
		// 当天没有配置则不处理
		SystemActiveCfgBean cfgBean = getAwardConfigList().get(day);
		if(cfgBean == null) return;
		// 状态只改变一次
		if(day < 0 || atvObj.getLoginStatus().containsValue(day)) {
			return;
		}
		//
		atvObj.getLoginStatus().addValue(day);
		atvObj.addLoginDay(1);
		atvObj.setLastTime(param.loginTime);
		atvObj.save();
		// 可领提示更新
		redPointPush(teamId);
	}

	
}
