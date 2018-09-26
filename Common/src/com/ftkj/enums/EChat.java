package com.ftkj.enums;

public enum EChat {
	
	世界聊天(1),
	联盟聊天(2),
	比赛聊天(3),
	私聊(4),
	系统(99),
	跑马灯(199),
	
	;
	
	
	
	
	private int type;
	
	private EChat(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
	
}
