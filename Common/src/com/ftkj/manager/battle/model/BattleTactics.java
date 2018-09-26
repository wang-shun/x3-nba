package com.ftkj.manager.battle.model;

import com.ftkj.cfg.TacticsCapBean;
import com.ftkj.manager.tactics.TacticsBean;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年2月16日
 * 比赛战术对象
 */
public class BattleTactics implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 战术配置 */
    private TacticsBean tactics;
    /** 战术等级 */
    private int level;

    public BattleTactics(TacticsBean tactics, int level) {
        super();
        this.tactics = tactics;
        this.level = level;
    }

    public TacticsBean getTactics() {
        return tactics;
    }

    public TacticsCapBean getTacticsCapBean() {
        return tactics.getCap(level);
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "{" +
                "\"tt\":" + tactics +
                ", \"lev\":" + level +
                '}';
    }
}
