package com.ftkj.manager.league.leagueArean;

import java.io.Serializable;

/**
 * 联盟赛馆数据
 * @author qin.jiang
 */
public class LeagueAreanTeam implements Serializable {

    private static final long serialVersionUID = -7483941727247491751L;

    /** 球队ID*/
    private long teamId;

    /** 联盟ID*/
    private int leagueId;

    /** 胜利积分(累计)*/
    private int winScore;

    /** 胜利次数*/
    private int winCount;

    /** 失败次数*/
    private int failCount;

    /** 下次可挑战时间*/
    private long wartime;

    /** 个人排名*/
    private int rank;

    public LeagueAreanTeam() {

    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getWartime() {
        return wartime;
    }

    public void setWartime(long wartime) {
        this.wartime = wartime;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public int getWinScore() {
        return winScore;
    }

    public void setWinScore(int winScore) {
        this.winScore = winScore;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String toString() {
        return "teamId=" + teamId + ", leagueId=" + leagueId + ", winScore=" + winScore + ", winCount=" + winCount + ", failCount=" + failCount + ", wartime=" + wartime + ", rank=" + rank;
    }

    public String redisString() {
        return teamId + "," + leagueId + "," + winScore + "," + winCount + "," + failCount + "," + wartime + "," + rank;
    }

    public static LeagueAreanTeam toLeagueAreanTeam(String string) {

        LeagueAreanTeam leagueAreanTeam = new LeagueAreanTeam();
        if (string == null)
            return leagueAreanTeam;

        String[] strArr = string.split(",");
        leagueAreanTeam = new LeagueAreanTeam();
        leagueAreanTeam.setTeamId(Long.valueOf(strArr[0]));
        leagueAreanTeam.setLeagueId(Integer.valueOf(strArr[1]));
        leagueAreanTeam.setWinScore(Integer.valueOf(strArr[2]));
        leagueAreanTeam.setWinCount(Integer.valueOf(strArr[3]));
        leagueAreanTeam.setFailCount(Integer.valueOf(strArr[4]));
        leagueAreanTeam.setWartime(Long.valueOf(strArr[5]));
        leagueAreanTeam.setRank(Integer.valueOf(strArr[6]));
        return leagueAreanTeam;
    }
}
