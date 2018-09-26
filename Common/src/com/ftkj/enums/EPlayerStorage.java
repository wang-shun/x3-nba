package com.ftkj.enums;

public enum EPlayerStorage {

	//是否在仓库，0在阵容中， 1在仓库中 2在交易市场中  3已删除 4训练馆
	阵容(0),
	仓库(1),
	交易(2),
	删除(3),
	训练馆(4),	
	;
	
	private int type;
	
	EPlayerStorage(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
	
}
