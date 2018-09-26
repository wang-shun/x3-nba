package com.ftkj.event.param;

/**
 * 球队升级
 * @author Jay
 * @time:2017年9月8日 下午4:14:52
 */
public class StagePassParam {

	public long teamId;
	public int stageId;
	
	public StagePassParam(long teamId, int stageId) {
		super();
		this.teamId = teamId;
		this.stageId = stageId;
	}

}
