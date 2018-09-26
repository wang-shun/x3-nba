package com.ftkj.manager.league;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.manager.prop.PropSimple;

/**
 * @author tim.huang
 * 2017年5月24日
 * 联盟成就配置
 */
public class LeagueHonorBean {
	private int honorId;
	private int level;
	private String name;
	private int honorConsume;
	private int weekHonor;//每周荣誉
	private int[] values;
	/**需求联盟等级*/
	private int leaguelv;
	
	public LeagueHonorBean(int honorId, String name,int level, int honorConsume,
			int[] values,int weekHonor, int leaguelv) {
		super();
		this.honorId = honorId;
		this.level = level;
		this.name = name;
		this.honorConsume = honorConsume;
		this.values = values;
		this.weekHonor = weekHonor;
		this.leaguelv = leaguelv;
	}

	public int getActiveHonor() {
		int week = DateTime.now().getDayOfWeek();
		int day = 0;
		if(week>=6){//跨周
			day = 13-week;
		}else{
			day= 6-week;
		}
		int activeHonor = Math.round(this.weekHonor/7f*day);
		return activeHonor;
	}

	public String getHonorName() {
		return name;
	}

	public int getWeekHonor() {
		return weekHonor;
	}

	public int getHonorId() {
		return honorId;
	}
	public int getLevel() {
		return level;
	}
	public int getHonorConsume() {
		return honorConsume;
	}
	
	public int getLeaguelv() {
		return leaguelv;
	}

	public int[] getValues() {
		return values;
	}
	
	
	
	
	
}
