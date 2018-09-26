package com.ftkj.enums;

import java.util.Map;

import com.google.common.collect.Maps;

public enum EGameTip {
	装备强化2星(2),	
	装备强化3星(4),
	装备强化4星(6),
	装备强化4星10_12(7),
	装备强化5星(8),
	装备强化5星10_12(9),
	//
	装备染色紫色(10),
	装备染色橙色(11),
	装备染色红色(12),
	
	多人赛冠军(13),
	天梯赛积分(14),
	获得B_S级教练(15),	
	
	获得S加球员(16),
	领取首充奖励(17),
	充值6480球券(18),
	VIP升级(19),
	全明星赛击杀(20),
	
	
	;
	
	
	private int id;
	EGameTip(int id){
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	


	//通过ID，取对应的战术枚举
	public static final Map<Integer,EGameTip> moduleEnumMap = Maps.newHashMap();
	static{
		for(EGameTip et : EGameTip.values()){
			moduleEnumMap.put(et.getId(), et);
		}
	}
	
	
	
}
