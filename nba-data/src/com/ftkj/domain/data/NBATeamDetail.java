package com.ftkj.domain.data;

import java.io.Serializable;

public class NBATeamDetail implements Serializable{
	
	private static final long serialVersionUID = -7725804045684389583L;
	
	private int teamId;
	private String espnName;
	private String teamName;
	private String teamEname;
	private String shortName;
	private String area;
	private String areaShort;
	private int win;
	private int loss;
	private int winLoss;
	private int rank;
	
	public NBATeamDetail() {}
	
	public NBATeamDetail(String temp){
		String[] x = temp.split("[|]");
		this.espnName = x[0].toLowerCase();
		this.rank = Integer.parseInt(x[1]);
		this.win = Integer.parseInt(x[2]);
		this.loss = Integer.parseInt(x[3]);
		if(x[4].indexOf("L")!=-1){
			this.winLoss = 0-Integer.parseInt(x[4].replace("L", ""));
		}else{
			this.winLoss = Integer.parseInt(x[4].replace("W", ""));
		}
		//this.winLoss = Integer.parseInt(x[4]);
		//System.out.println(this.toString());
		//System.out.println("update team_info set win="+this.win+",loss="+this.loss+",winloss="+this.winLoss+",rank="+this.rank+" where espn_name='"+this.espnName+"';");
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getEspnName() {
		return espnName;
	}

	public void setEspnName(String espnName) {
		this.espnName = espnName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamEname() {
		return teamEname;
	}

	public void setTeamEname(String teamEname) {
		this.teamEname = teamEname;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getAreaShort() {
		return areaShort;
	}

	public void setAreaShort(String areaShort) {
		this.areaShort = areaShort;
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

	public int getWinLoss() {
		return winLoss;
	}

	public void setWinLoss(int winLoss) {
		this.winLoss = winLoss;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
}
