package com.ftkj.manager.skill.buff;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * @author tim.huang
 * 2017年9月18日
 * 技能buff临时配置加载
 */
public class SkillBufferVO extends ExcelBean {

    /** 技能效果ID*/
    private int bid;
    /** 类型*/
    private int type;
    /** 技持续回合数*/
    private int runRound;
    /** 技能字符配置   filed=a,*/
    private String val;
    /** 使用方效果*/
    private int home;
    /** 是否需要在触发的时候立马执行一次*/
    private int now;
    /**是否减益效果*/
    private int debuff;

    public int getDebuff() {
        return debuff;
    }

    public void setDebuff(int debuff) {
        this.debuff = debuff;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }

    @Override
    public void initExec(RowData row) {

    }

}
