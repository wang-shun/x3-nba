package com.ftkj.manager.skill.buff;

import java.io.Serializable;
import java.util.Map;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;

public abstract class SkillBuffer implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 技能效果ID*/
    private int bid;
    /** 技持续回合数*/
    private int runRound;
    /** 技能字符配置   filed=a,*/
    private String val;
    /** 使用方效果*/
    private boolean home;
    /** 是否需要在触发的时候立马执行一次*/
    private boolean now;
    /** 是否减益效果*/
    private boolean debuff;

    public SkillBuffer(SkillBufferVO vo) {
        this.bid = vo.getBid();
        this.runRound = vo.getRunRound();
        this.val = vo.getVal();
        this.home = vo.getHome() == 1;
        this.now = vo.getNow() == 1;
        this.valMap = Maps.newHashMap();
        this.debuff = vo.getDebuff() == 1;
        String[] tmp = StringUtil.toStringArray(this.val, StringUtil.DEFAULT_ST);
        String[] tmpArr;
        for (String tt : tmp) {
            tmpArr = StringUtil.toStringArray(tt, StringUtil.DEFAULT_EQ);
            this.valMap.put(tmpArr[0], tmpArr[1]);
        }
        initVal();
    }

    public boolean isDebuff() {
        return debuff;
    }

    public void setDebuff(boolean debuff) {
        this.debuff = debuff;
    }

    private Map<String, String> valMap;

    public Map<String, String> getValMap() {
        return valMap;
    }

    public void setValMap(Map<String, String> valMap) {
        this.valMap = valMap;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getRunRound() {
        return runRound;
    }

    public void setRunRound(int runRound) {
        this.runRound = runRound;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    public boolean isNow() {
        return now;
    }

    public void setNow(boolean now) {
        this.now = now;
    }

    public abstract void execute(long teamId, BattleSource bs);

    protected abstract void initVal();
}
