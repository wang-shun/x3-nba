package com.ftkj.manager.tactics;

import com.ftkj.cfg.TacticsCapBean;
import com.ftkj.cfg.TacticsPowerBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.TacticZone;

import java.io.Serializable;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年2月24日
 * 战术配置类
 */
public class TacticsBean implements Serializable {
    private static final long serialVersionUID = 2L;
    private TacticId id;
    private TacticId restrain;//克制的目标战术
    /** cap加成 */
    private Map<Integer, TacticsCapBean> cap;
    /** 体力加成 */
    private TacticsPowerBean power;

    public TacticsBean(TacticId id, TacticId restrain,
                       Map<Integer, TacticsCapBean> cap, TacticsPowerBean power) {

        super();
        this.id = id;
        this.restrain = restrain;
        this.cap = cap;
        this.power = power;
    }

    public TacticsCapBean getCap(int lev) {
        return cap.get(lev);
    }

    public Map<Integer, TacticsCapBean> getCap() {
        return cap;
    }

    public float getPower(EPlayerPosition position) {
        return power.getPower(position);
        //		return 5;
    }

    public Float getPosCapRate(int level, EPlayerPosition position) {
        return cap.get(level).getPosCapRate(position);
    }

    public float getPosCapRate(int level, EPlayerPosition position, float defaultValue) {
        Float r = cap.get(level).getPosCapRate(position);
        return r != null ? r : defaultValue;
    }

    public TacticId getId() {
        return id;
    }

    public TacticType getType() {
        return id.getType();
    }

    public TacticZone getZone() {
        return id.getZone();
    }

    public TacticId getRestrain() {
        return restrain;
    }

    /** 是否克制目标战术 */
    public boolean isRestrain(TacticId target) {
        if (id == TacticId.全场紧逼 && target == TacticId.跑轰战术) {
            return true;
        }
        if (id == TacticId.跑轰战术 && target == TacticId.全场紧逼) {
            return true;
        }
        return restrain == target;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"type\":" + id.getType() +
                ", \"restrain\":" + restrain +
                '}';
    }
}
