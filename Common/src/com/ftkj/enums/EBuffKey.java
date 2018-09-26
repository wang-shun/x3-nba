package com.ftkj.enums;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 加成类型
 * @author Jay
 * @time:2017年9月22日 下午4:53:23
 */
public enum EBuffKey {
	
	球队加成(100),
	联盟球馆赛(200),
	VIP加成(300),
	月卡加成(400),
	联盟成就加成(500),
	周卡加成(600),
	特殊道具加成(700),
	联盟组队赛(800),	
	;
	private int startID;
	
	EBuffKey(int startID) {
		this.startID = startID;
	}

	public int getStartID() {
		return startID;
	}
	
	protected static Map<Integer, EBuffKey> keyMap;
	
	static {
		keyMap = Maps.newHashMap();
		for(EBuffKey t : EBuffKey.values()) {
			keyMap.put(t.getStartID(), t);
		}
	}
	
	public static EBuffKey valueOfKey(int startID) {
		return keyMap.get(startID);
	}
	
}