package com.ftkj.manager.logic;

import java.util.Date;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.team.Team;
import com.google.common.eventbus.Subscribe;

public class LoginManager extends BaseManager {
	
	@IOC
	private TeamManager teamManager;

	@Override
	public void instanceAfter() {
		EventBusManager.register(EEventType.活动定时器, this);
	}
	
	@Subscribe
	public final void onSecondCall(Date date) {
		DateTime now = new DateTime(date);
		// 0点0秒
		if(!now.withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).equals(now)) {
			return;
		}
		// 所有在线用户发出登录事件
		for(Team team : teamManager.getAllOnlineTeam()) {
			team.login();
		}
	}

}
