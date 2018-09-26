package com.ftkj.manager.league.leagueArean;

/**
 * 联盟总积分排行数据
 * @author qin.jiang
 */
public class LeagueTotalMScoreRank {

    /** 联盟id*/
    private int leagueId;

    /** 联盟比赛总积分*/
    private int matchScoreSum;

    /** 联盟积分排名*/
    private int rank;

    /** 联盟总胜利次数*/
    private int winCountSum;

    /** 联盟总失败次数*/
    private int failCountSum;

    public int getMatchScoreSum() {
        return matchScoreSum;
    }

    public void setMatchScoreSum(int matchScoreSum) {
        this.matchScoreSum = matchScoreSum;
    }

    public int getWinCountSum() {
        return winCountSum;
    }

    public void setWinCountSum(int winCountSum) {
        this.winCountSum = winCountSum;
    }

    public int getFailCountSum() {
        return failCountSum;
    }

    public void setFailCountSum(int failCountSum) {
        this.failCountSum = failCountSum;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "LeagueTotalMScoreRank [leagueId=" + leagueId + ", " + "matchScoreSum=" + matchScoreSum + ", rank=" + rank + ", winCountSum=" + winCountSum + ", failCountSum=" + failCountSum + "]";
    }
}
