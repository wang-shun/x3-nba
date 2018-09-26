package com.ftkj.enums;

public enum ELeaguePosType {
    
    空闲(1),
    占领中(2),
    比赛中(3),
    ;
    
	private int id;
	
	private ELeaguePosType(int pos) {
		this.id = pos;
	}

	public int getId() {
		return id;
	}
}
