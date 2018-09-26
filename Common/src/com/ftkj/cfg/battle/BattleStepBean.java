package com.ftkj.cfg.battle;

import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.util.IntervalInt;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年2月21日
 * 比赛小节配置数据
 */
public class BattleStepBean implements Serializable {
    private static final long serialVersionUID = 7250801310004651130L;
    private EBattleStep step;
    /** 小节轮数 */
    private int round;
    /** 论数区间. 包括上界和下界 */
    private final IntervalInt roundInterval;

    public BattleStepBean(EBattleStep step, int round, IntervalInt roundInterval) {
        this.step = step;
        this.round = round;
        this.roundInterval = roundInterval;
    }

    public int getRound() {
        return round;
    }

    public EBattleStep getStep() {
        return step;
    }

    public IntervalInt getRoundInterval() {
        return roundInterval;
    }

    @Override
    public String toString() {
        return "{" +
                "\"step\":" + step +
                ", \"round\":" + round +
                ", \"interval\":" + roundInterval +
                '}';
    }
}
