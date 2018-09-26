package com.ftkj.x3.client.model;

import com.ftkj.manager.arena.Arena;

import java.util.Map;
import java.util.TreeMap;

public class ClientArena extends Arena {
    private static final long serialVersionUID = 2038998858688166207L;
    //已经使用比赛次数
    private int usedMatchNum;
    //每日免费次数配置
    private int freeMatchNumCfg;
    //已经购买的付费次数
    private int buyMatchNum;
    //挑战倒计时(毫秒)
    private int matchCd;
    /** 对手 */
    private Map<Integer, Long> targetRankAndTid = new TreeMap<>();

    public int getUsedMatchNum() {
        return usedMatchNum;
    }

    public void setUsedMatchNum(int usedMatchNum) {
        this.usedMatchNum = usedMatchNum;
    }

    public int getFreeMatchNumCfg() {
        return freeMatchNumCfg;
    }

    public void setFreeMatchNumCfg(int freeMatchNumCfg) {
        this.freeMatchNumCfg = freeMatchNumCfg;
    }

    public int getBuyMatchNum() {
        return buyMatchNum;
    }

    public void setBuyMatchNum(int buyMatchNum) {
        this.buyMatchNum = buyMatchNum;
    }

    public int getMatchCd() {
        return matchCd;
    }

    public void setMatchCd(int matchCd) {
        this.matchCd = matchCd;
    }

    public Map<Integer, Long> getTargetRankAndTid() {
        return targetRankAndTid;
    }

}
