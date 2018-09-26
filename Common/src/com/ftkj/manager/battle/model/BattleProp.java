package com.ftkj.manager.battle.model;

import com.ftkj.console.PropConsole;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年2月16日
 * 比赛物品数据
 */
public abstract class BattleProp implements Serializable {
    private static final long serialVersionUID = 1L;
    private long teamId;
    /** 道具配置 */
    private PropBean prop;
    /** 剩余数量 */
    private int num;
    /** 赛前准备的原始数量 */
    private int baseNum;

    public BattleProp(long teamId, PropSimple ps) {
        this.prop = PropConsole.getProp(ps.getPropId());
        this.baseNum = ps.getNum();
        this.num = this.baseNum;
        this.teamId = teamId;
    }

    protected boolean checkNum(int val) {
        return this.num >= val;
    }

    public void updateNum(int val) {
        this.num += val;
    }

    public long getTeamId() {
        return teamId;
    }

    public int getPropId() {
        return this.prop.getPropId();
    }

    @SuppressWarnings("unchecked")
    public <T extends PropBean> T getProp() {
        return (T) prop;
    }

    public int getNum() {
        return num;
    }

    public int getBaseNum() {
        return baseNum;
    }

}
