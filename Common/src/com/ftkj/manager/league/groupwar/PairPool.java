package com.ftkj.manager.league.groupwar;

public class PairPool {
	
	private int leagueId;
	
	private int groupId;
	
	private int score;
	
	private long startTime;
	
	/**
	 * 匹配次数
	 */
	private int count; 
	
	private boolean isNPC = false;
	
	/**
	 * 创建NPC
	 * @return
	 */
	public static PairPool CreateNPC() {
		PairPool p = new PairPool(0, 0, 0, 0, 0);
		p.isNPC = true;
		return p;
	}

	public PairPool(int leagueId, int groupId, int score, long startTime, int count) {
		super();
		this.leagueId = leagueId;
		this.groupId = groupId;
		this.score = score;
		this.startTime = startTime;
		this.count = count;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public int getCount() {
		return count;
	}

	public long getLeagueGroupId() {
		return (this.getLeagueId()) * 1000  + this.getGroupId();
	}

	@Override
	public String toString() {
		return "PairPool [id=" +getLeagueGroupId() + ", score=" + score + "]";
	}

	public boolean isNPC() {
		return isNPC;
	}
	
	public void addCount(int count) {
		this.count += count;
	}
}
