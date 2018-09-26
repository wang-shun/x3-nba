package com.ftkj.enums;

public enum EProp {

	// 道具枚举start
	<#list props as x> 
	${x} 
	</#list> 
	// 道具枚举end
	;
	private int id;
	
	private EProp(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
}
