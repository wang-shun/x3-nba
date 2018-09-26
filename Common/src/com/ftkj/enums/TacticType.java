package com.ftkj.enums;

public enum TacticType {
    Offense(1),
    Defense(2),
	
	;
	
	private int type;
	
	private TacticType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}
}
