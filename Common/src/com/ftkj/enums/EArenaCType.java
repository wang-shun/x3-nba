package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

public enum EArenaCType {
	主体(1,1),
	吉祥物(2,2),
	酒店(3,3),
	交通工具(4,4),
	雕像(5,5);
	
	public static final Map<Integer,EArenaCType> arenaCEnumMap = new HashMap<Integer, EArenaCType>();
	static{
		for(EArenaCType et : EArenaCType.values()){
			arenaCEnumMap.put(et.getType(), et);
		}
	}
	
	public static EArenaCType getEArenaCType(int key){
		EArenaCType title = arenaCEnumMap.get(key);
		return title;
	}
	
	private int type;
	private int position; 	
	
	private EArenaCType(int type,int position) {
		this.type = type;
		this.position = position;
	}
	
	public int getType() {
		return this.type;
	}

	public int getPosition() {
		return position;
	}
	
}
