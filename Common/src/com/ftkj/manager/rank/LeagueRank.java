package com.ftkj.manager.rank;

/**
 * @author tim.huang
 * 2017年6月1日
 *
 */
public class LeagueRank extends Rank implements Comparable<LeagueRank>{

	private static final long serialVersionUID = 1L;
	private int leagueId;
	private String name;
	private int level;
	private int totalLevel;
	private int peopleCount;
	private long realScore;	//联盟累计贡献值
	
	public LeagueRank(int leagueId, String name, int level, int totalLevel,
			int peopleCount, long score) {
		this.leagueId = leagueId;
		this.name = name;
		this.level = level;
		this.totalLevel = totalLevel;
		this.peopleCount = peopleCount;
		this.realScore = score;
	}
	
	
	/**
	 * 排序: 联盟等级,贡献值降序
	 * @param level
	 * @param totalLevel 成就等级
	 * @param score 联盟贡献值
	 */
	public void updateScore(int level,int totalLevel, long score){
		this.level = level;
		this.totalLevel = totalLevel;
		this.realScore = score;
		super.setScore(level);// Redis排序按联盟等级降序来排序
	}
	
	
	public long getRealScore() {
		return realScore;
	}

	public void setRealScore(long realScore) {
		this.realScore = realScore;
	}

	public int getTotalLevel() {
		return totalLevel;
	}
	public void setTotalLevel(int totalLevel) {
		this.totalLevel = totalLevel;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getPeopleCount() {
		return peopleCount;
	}
	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}


	@Override
	public void updateScore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "LeagueRank [leagueId=" + leagueId + ", name=" + name
				+ ", level=" + level + ", totalLevel=" + totalLevel+", getScore=" +getScore()
				+ ", peopleCount=" + peopleCount + ", realScore=" + realScore
				+ "]";
	}

	// 默认按等级,贡献值排序
	@Override
	public int compareTo(LeagueRank o) {
		if (o.getScore() > this.getScore()) {
			return 1;
		}else if (o.getScore() == this.getScore()) {
			return (int)(o.getRealScore() - this.realScore);
		}else {
			return -1;
		}
	}
	
}
