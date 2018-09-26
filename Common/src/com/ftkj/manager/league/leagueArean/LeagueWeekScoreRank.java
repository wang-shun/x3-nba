package com.ftkj.manager.league.leagueArean;

import java.io.Serializable;

/**
 * 联盟贡献周排行数据
 * @author qin.jiang
 */ 
public class LeagueWeekScoreRank implements Serializable{

    private static final long serialVersionUID = -4487350291607000548L;

    /** 联盟id*/
    private int leagueId;
   
    /** 联盟周总贡献*/
    private int weekScoreSum;
    
    /** 联盟周排名*/
    private int rank;
	
	public LeagueWeekScoreRank() {
	
	}
	   
    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public int getWeekScoreSum() {
        return weekScoreSum;
    }

    public void setWeekScoreSum(int weekScoreSum) {
        this.weekScoreSum = weekScoreSum;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
    @Override
    public String toString() {
        return "LeagueWeekScoreRank [leagueId=" + leagueId + ", " + "weekScoreSum=" + weekScoreSum + ", rank=" + rank + "]";
    }
}
