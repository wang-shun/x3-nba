package com.ftkj.manager.street;

import com.ftkj.db.domain.StreetBallPO;

public class StreetBall {

	private StreetBallPO streetBallPO;
	
	public StreetBall(StreetBallPO po) {
		this.streetBallPO = po;
	}

	public long getTeamId() {
		return this.streetBallPO.getTeamId();
	}
	
	public void save() {
		this.streetBallPO.save();
	}
	
	/**
	 * 设置副本类型最新挑战关卡
	 * @param type
	 * @param stage
	 */
	public void setTypeStage(int type, int stage) {
		if(type == 1) {
			this.streetBallPO.setType1(stage);
		} else if(type == 2) {
			this.streetBallPO.setType2(stage);
		} else if(type == 3) {
			this.streetBallPO.setType3(stage);
		} else if(type == 4) {
			this.streetBallPO.setType4(stage);
		} else if(type == 5) {
			this.streetBallPO.setType5(stage);
		}
	}
	
	/**
	 * 取副本类型当前关卡
	 * @param type
	 */
	public int getTypeStage(int type) {
		if(type == 1) {
			return this.streetBallPO.getType1();
		}
		if(type == 2) {
			return this.streetBallPO.getType2();
		}
		if(type == 3) {
			return this.streetBallPO.getType3();
		}
		if(type == 4) {
			return this.streetBallPO.getType4();
		}
		if(type == 5) {
			return this.streetBallPO.getType5();
		}
		return 0;
	}
	
}
