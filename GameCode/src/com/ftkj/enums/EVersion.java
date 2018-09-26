package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum EVersion {
	zh("国内"),
	tw("台湾"),
	us("美国");
	
	public String name;
//	public static Map<String,EVersion> map = new HashMap<>();
	private EVersion(String name) {
		this.name = name;
//		map.put(name, this);
	}

	public String getName() {
		return name;
	}
	
}
