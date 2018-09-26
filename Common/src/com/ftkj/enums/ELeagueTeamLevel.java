package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年5月18日
 *
 */
public enum ELeagueTeamLevel {
	盟主(1),
	副盟主(2),
	理事(3),
	精英(4),
	成员(5);
	
	private ELeagueTeamLevel(int id){
		this.id = id;
	}
	
	private int id;
	
	public int getId(){
		return this.id;
	}
	
	//通过ID，取对应的战术枚举
	public static final Map<Integer,ELeagueTeamLevel> leagueLevelEnumMap = new HashMap<Integer, ELeagueTeamLevel>();
	static{
		for(ELeagueTeamLevel et : ELeagueTeamLevel.values()){
			leagueLevelEnumMap.put(et.getId(), et);
		}
	}
	
	public static ELeagueTeamLevel getELeagueTeamLevel(int id){
		return leagueLevelEnumMap.get(id);
	}
	
	
}
