package com.ftkj.event.param;

/**
 * 球队升级
 * @author Jay
 * @time:2017年9月8日 下午4:14:52
 */
public class LevelupParam {

	public long teamId;
	public int level;
	
	public LevelupParam(long teamId, int level) {
		super();
		this.teamId = teamId;
		this.level = level;
	}

}
