package com.ftkj.manager.rank;

public class LeagueGroupRank {

	private int leagueId;
	private String name;
	private int jf;
	private int win;
	private int loss;
	public LeagueGroupRank(int leagueId, String name, int jf, int win, int loss) {
		super();
		this.leagueId = leagueId;
		this.name = name;
		this.jf = jf;
		this.win = win;
		this.loss = loss;
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
	public int getWin() {
		return win;
	}
	public void setWin(int win) {
		this.win = win;
	}
	public int getLoss() {
		return loss;
	}
	public void setLoss(int loss) {
		this.loss = loss;
	}
	public int getJf() {
		return jf;
	}
	public void setJf(int jf) {
		this.jf = jf;
	}
	
	public void updateScore(int jf2, int win2, int loss2) {
		this.jf = jf2;
		this.win = win2;
		this.loss = loss2;
	}
	
}
