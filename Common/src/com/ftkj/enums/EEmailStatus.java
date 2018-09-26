package com.ftkj.enums;

public enum EEmailStatus {

	未读(0),
	已读(1),
	已领(2),
	已删(3),
	;
	
	private int status;
	
	private EEmailStatus(int sta) {
		this.status = sta;
	}

	public int getStatus() {
		return status;
	}
	
}
