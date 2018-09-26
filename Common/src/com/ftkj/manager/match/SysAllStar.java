package com.ftkj.manager.match;

import java.io.Serializable;

/** 系统全明星信息 */
public final class SysAllStar implements Serializable {
    private static final long serialVersionUID = -2081528507495800046L;
    /** 配置id */
    private int id;
//    /** 随机到的 action */
//    private EActionType act;
    /** 当前难度 */
    private volatile int npcLev;
    /** 当前血量 */
    private volatile int hp;
    
    /** 是否激活(每天只有一个全明星球队是激活的)*/
    private boolean isActivate;

    /** 是否已发奖励*/
    private boolean hasSendedReward;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public EActionType getAct() {
//        return act;
//    }
//
//    public void setAct(EActionType act) {
//        this.act = act;
//    }

    public int getNpcLev() {
        return npcLev;
    }

    public void setNpcLev(int npcLev) {
        this.npcLev = npcLev;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public boolean isActivate() {
      return isActivate;
    }

    public void setActivate(boolean isActivate) {
      this.isActivate = isActivate;
    }
    

    public boolean isHasSendedReward() {
      return hasSendedReward;
    }

    public void setHasSendedReward(boolean hasSendedReward) {
      this.hasSendedReward = hasSendedReward;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"lev\":" + npcLev +
                ", \"hp\":" + hp +
                '}';
    }
}
