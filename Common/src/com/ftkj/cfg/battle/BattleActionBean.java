package com.ftkj.cfg.battle;

import com.ftkj.enums.battle.EBattleAction;

import java.util.List;

/**
 * 父行为配置信息
 */
public interface BattleActionBean extends RoundBean {
    //    int getId();
    //
    List<BaseSubActionBean> getSubActions();
    //
    //    /**
    //     * 本行为执行后增加的动画轮数.
    //     * 时长 = 轮数 * 200ms
    //     */
    //    int getPostRoundDelay();
    //
    //    /**
    //     * 使用技能时增加的动画轮数.
    //     * 时长 = 轮数 * 200ms
    //     */
    //    int getSkillRoundDelay();

    boolean isSucessAction();

    int getFtNum();

    int getScore();

    int getNormalTacticsWeight();

    int getInsideTacticsWeight();

    int getOuterTacticsWeight();

    EBattleAction getAction();
}
