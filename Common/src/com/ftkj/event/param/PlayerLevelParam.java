package com.ftkj.event.param;

/**
 * 球队升级
 * @author Jay
 * @time:2017年9月8日 下午4:14:52
 */
public class PlayerLevelParam {

	public long teamId;
	public int playerId;
	public int level;
	
	public PlayerLevelParam(long teamId, int playerId, int level) {
		super();
		this.teamId = teamId;
		this.playerId = playerId;
		this.level = level;
	}

}
