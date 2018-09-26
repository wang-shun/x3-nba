package com.ftkj.db.domain.bean;

/**
 * @author tim.huang
 * 2017年5月24日
 * 联盟成就配置
 */
public class LeagueHonorBeanVO {

	private int honorId;
	private String name;
	private int level;
	private int honorConsume;
	private String values;
	private int weekHonor;
	/**成就升级,需要的联盟等级*/
	private int leagueLv;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeekHonor() {
		return weekHonor;
	}
	public void setWeekHonor(int weekHonor) {
		this.weekHonor = weekHonor;
	}
	public int getHonorId() {
		return honorId;
	}
	public void setHonorId(int honorId) {
		this.honorId = honorId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getHonorConsume() {
		return honorConsume;
	}
	public void setHonorConsume(int honorConsume) {
		this.honorConsume = honorConsume;
	}
	
	public String getValues() {
		return values;
	}
	public void setValues(String values) {
		this.values = values;
	}
	
	/**获取成就升级,需要的联盟等级*/
	public int getLeagueLv() {
		return leagueLv;
	}
	
	public void setLeagueLv(int leagueLv) {
		this.leagueLv = leagueLv;
	}
	
	
}
