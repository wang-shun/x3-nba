package com.ftkj.manager.logic;

import com.ftkj.enums.ETeamConfig;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.setting.TeamConfig;
import com.ftkj.server.RedisKey;

/**
 * 用户设置接口
 * @author Jay
 * @time:2017年6月26日 下午4:30:52
 */
public class TeamConfigManager extends BaseManager {
	
	@Override
	public void instanceAfter() {
	}
	
	public TeamConfig getTeamConfig(long teamId) {
		String key = RedisKey.Team_Config + teamId;
		TeamConfig config = redis.getObj(key);
		if(config == null) {
			config = new TeamConfig(teamId);
			redis.set(key, config);
		}
		return config;
	}
	
	private void save(long teamId, TeamConfig config) {
		String key = RedisKey.Team_Config + teamId;
		redis.set(key, config);
	}
	
	/**
	 * 保存设置
	 * @param teamId
	 * @param tyep
	 * @param value
	 */
	public void setData(long teamId, ETeamConfig tyep, String value) {
		TeamConfig config = getTeamConfig(teamId);
		switch(tyep) {
		case 默认进攻战术: config.setTacticsJg(value); break;
		case 默认防守战术: config.setTacticsFs(value); break;
		}
		save(teamId, config);
	}
	
	/**
	 * 取用户数据
	 * @param teamId
	 * @param type
	 * @return
	 */
	public String getData(long teamId, ETeamConfig type) {
		TeamConfig config = getTeamConfig(teamId);
		switch(type) {
			case 默认进攻战术: return config.getTacticsJg();
			case 默认防守战术: return config.getTacticsFs();
		}
		return "";
	}
	
}
