package com.ftkj.manager.league.leagueArean;


/**
 * 联盟积分数据
 * @author qin.jiang
 */ 
public class PostionScoreBean {

    /** 联盟馆配置id*/
    private int id;
    /** 比赛结算积分配置*/
    private int endScore;
    /** 比赛胜利积分*/
    private int winScore;
    /** 比赛失败积分*/
    private int failScore;
	
	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getEndScore() {
        return endScore;
    }
    public void setEndScore(int endScore) {
        this.endScore = endScore;
    }
    public int getWinScore() {
        return winScore;
    }
    public void setWinScore(int winScore) {
        this.winScore = winScore;
    }
    public int getFailScore() {
        return failScore;
    }
    public void setFailScore(int failScore) {
        this.failScore = failScore;
    }
 }
