package com.ftkj.manager.league.leagueArean;

import java.io.Serializable;

/**
 * 联盟赛馆位置数据
 * @author qin.jiang
 */ 
public class LeaguePostion implements Serializable{

    private static final long serialVersionUID = 785527674947452828L;
   
    /** 联盟馆配置id*/
    private int id;
   
    /** 联盟ID*/
    private int leagueId;
    
    /** 球队ID*/
    private long teamId; 
    
    /** 比赛ID*/
    private long battleId;

	public LeaguePostion() {
	
	}	
	
	public LeaguePostion(int id, int leagueId, long teamId, long battleId) {
	    super();
	    this.id = id;
	    this.leagueId = leagueId;
	    this.teamId = teamId;
	    this.battleId = battleId;
    }
  
    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
    }  
}
