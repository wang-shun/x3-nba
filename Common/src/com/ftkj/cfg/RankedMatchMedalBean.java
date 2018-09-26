package com.ftkj.cfg;

/** 天梯赛. 段位配置 */
public class RankedMatchMedalBean {
    /** 段位id */
    private int id;
    /** 上一段位 */
    private int preId;
    /** 下一段位 */
    private int nextId;
    /** 赛季段位奖励 掉落包 */
    private int seasonDrop;
    /** 每日段位奖励 掉落包 */
    private int dailyDrop;
    /** 排行榜显示几个球队信息 */
    private int rankViewNum;

    public int getId() {
        return id;
    }

    public int getPreId() {
        return preId;
    }

    public void setPreId(int preId) {
        this.preId = preId;
    }

    public int getNextId() {
        return nextId;
    }

    public int getSeasonDrop() {
        return seasonDrop;
    }

    public int getDailyDrop() {
        return dailyDrop;
    }

    public int getRankViewNum() {
        return rankViewNum;
    }

    public void setRankViewNum(int rankViewNum) {
        this.rankViewNum = rankViewNum;
    }
}
