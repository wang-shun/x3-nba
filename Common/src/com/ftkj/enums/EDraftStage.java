package com.ftkj.enums;

/**
 * @author tim.huang
 * 2017年5月4日
 * 选秀阶段
 */
public enum EDraftStage {
	等待玩家(1),
	抽签阶段(2),
	选人阶段(3),
	结束(4)
	;
	
	private int id;
	
	private EDraftStage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
