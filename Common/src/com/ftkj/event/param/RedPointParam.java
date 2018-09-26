package com.ftkj.event.param;

public class RedPointParam {
	
	public long teamId;
	public int modeule;
	public int statusNum;
	
	public RedPointParam(long teamId, int modeule, int statusNum) {
		super();
		this.teamId = teamId;
		this.modeule = modeule;
		this.statusNum = statusNum;
	}

	@Override
	public String toString() {
		return "RedPointParam [teamId=" + teamId + ", modeule=" + modeule + ", statusNum=" + statusNum + "]";
	}

}
