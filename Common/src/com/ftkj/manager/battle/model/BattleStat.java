package com.ftkj.manager.battle.model;

import com.ftkj.util.DateTimeUtil;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月9日
 * 比赛状态
 */
public class BattleStat implements Serializable {
    private static final long serialVersionUID = 2L;
    //
    private int propCD;
    private DateTime replacePlayerCD;
    private DateTime replaceTacticCD;
    /** 当前士气 */
    private int morale;
    /** 教练技能CD */
    private Map<Integer, Integer> coachCDMap;
    private DateTime coachCDTime;
    /** 是否可以使用战术 */
    private boolean canUseTactics;
    /** 使用战术次数 */
    private int useTacticsNum;
    /** 是否可以更换球员 */
    private boolean canSubPlayer;
    /** 是否可以使用教练技能 */
    private boolean canUseCoach;

    private static final int _coachCD = 20;
    private static final int _replacePlayerCD = 10;
    private static final int _replaceTacticCD = 10;

    public BattleStat(int morale) {
        super();
        this.morale = morale;
    }

    public BattleStat() {
        coachCDMap = Maps.newHashMap();
        clear();
        coachCDTime = DateTime.now();
        replacePlayerCD = DateTime.now();
        replaceTacticCD = DateTime.now();
    }

    public void clear() {
        this.canUseTactics = true;
        this.canSubPlayer = true;
        this.canUseCoach = true;
    }

    public int getCoachCD() {
        //		DateTime cd = this.coachCDMap.get(sid);
        int cc = 0;
        if (this.coachCDTime != null) {
            cc = DateTimeUtil.secondBetween(DateTime.now(), this.coachCDTime);
        }
        return cc < 0 ? 0 : cc;
    }

    public int getReplacePlayerCD() {
        int cc = 0;
        if (this.replacePlayerCD != null) {
            cc = DateTimeUtil.secondBetween(DateTime.now(), this.replacePlayerCD);
        }
        return cc < 0 ? 0 : cc;
    }

    public int getReplaceTacticCD() {
        int cc = 0;
        if (this.replaceTacticCD != null) {
            cc = DateTimeUtil.secondBetween(DateTime.now(), this.replaceTacticCD);
        }
        return cc < 0 ? 0 : cc;
    }

    public int getUseTacticsNum() {
        return useTacticsNum;
    }

    public int getCoachConut(int sid) {
        int cd = this.coachCDMap.getOrDefault(sid, 0);
        return cd;
    }

    public void updateCoachCD() {
        this.coachCDTime = DateTimeUtil.addSecond(_coachCD);
    }

    public void updateTacticCdAndNum() {
        this.replaceTacticCD = DateTimeUtil.addSecond(_replaceTacticCD);
        this.useTacticsNum++;
    }

    public void updateReplacePlayerCD() {
        this.replacePlayerCD = DateTimeUtil.addSecond(_replacePlayerCD);
    }

    public void updateCoachCount(int sid) {
        int curCount = getCoachConut(sid);
        this.coachCDMap.put(sid, ++curCount);
    }

    public boolean isCanUseTactics() {
        return canUseTactics;
    }

    public void setCanUseTactics(boolean canUseTactics) {
        this.canUseTactics = canUseTactics;
    }

    public boolean isCanSubPlayer() {
        return canSubPlayer;
    }

    public void setCanSubPlayer(boolean canSubPlayer) {
        this.canSubPlayer = canSubPlayer;
    }

    public boolean isCanUseCoach() {
        return canUseCoach;
    }

    public void setCanUseCoach(boolean canUseCoach) {
        this.canUseCoach = canUseCoach;
    }

    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = morale;
    }

    public int getPropCD() {
        return propCD;
    }

    public void setPropCD(int propCD) {
        this.propCD = propCD;
    }

    public Map<Integer, Integer> getCoachCDMap() {
        return coachCDMap;
    }

}
