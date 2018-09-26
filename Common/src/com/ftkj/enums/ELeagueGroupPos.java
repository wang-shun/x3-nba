package com.ftkj.enums;

public enum ELeagueGroupPos {

	大将(0),
	先锋(1),
	副将(2),
	;
	
	private int id;
	
	private ELeagueGroupPos(int pos) {
		this.id = pos;
	}

	public int getId() {
		return id;
	}
}
