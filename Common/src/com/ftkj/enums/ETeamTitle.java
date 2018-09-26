package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月6日
 * 称号枚举
 */
public enum ETeamTitle {
	世界第一帅("1"),
	NULL("");
	
	private String val;

	private ETeamTitle(String val) {
		this.val = val;
		
	}
	
	//通过ID，取对应的战术枚举
	public String getVal() {
		return val;
	}


	public static final Map<String,ETeamTitle> tacticsEnumMap = new HashMap<String, ETeamTitle>();
	static{
		for(ETeamTitle et : ETeamTitle.values()){
			tacticsEnumMap.put(et.getVal(), et);
		}
	}
	
	public static ETeamTitle getETeamTitle(String key){
		ETeamTitle title = tacticsEnumMap.get(key);
		return title==null?ETeamTitle.NULL:title;
	}
}
