package com.ftkj.manager.rank;

public class GroupTierRank extends Rank {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int leagueId;
	private int groupId;
	private String groupName;
	private String leagueName;
	private int jf;
	private int winNum;
	private int lossNum;
	
	public GroupTierRank(int leagueId, int groupId, String groupName, String leagueName, int jf, int winNum,
			int lossNum) {
		super();
		this.leagueId = leagueId;
		this.groupId = groupId;
		this.groupName = groupName;
		this.leagueName = leagueName;
		this.jf = jf;
		this.winNum = winNum;
		this.lossNum = lossNum;
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
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public int getJf() {
		return jf;
	}
	public void setJf(int jf) {
		this.jf = jf;
	}
	public int getWinNum() {
		return winNum;
	}
	public void setWinNum(int winNum) {
		this.winNum = winNum;
	}
	public int getLossNum() {
		return lossNum;
	}
	public void setLossNum(int lossNum) {
		this.lossNum = lossNum;
	}
	@Override
	public void updateScore() {
		// TODO Auto-generated method stub
		
	}
	
}
