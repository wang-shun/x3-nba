package com.ftkj.event.param;

/**
 * 招募
 * @author Jay
 * @time:2018年6月5日 上午11:32:21
 */
public class ScountParam {

	public long teamId;
	public int type;
	public int count;
	
	public ScountParam(long teamId, int type, int count) {
		super();
		this.teamId = teamId;
		this.type = type;
		this.count = count;
	}
	
	
}
