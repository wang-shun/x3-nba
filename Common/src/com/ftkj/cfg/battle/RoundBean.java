package com.ftkj.cfg.battle;

import java.io.Serializable;

/**
 * 回合配置信息.
 */
public interface RoundBean extends Serializable {
    int getId();

    /**
     * 本行为执行后增加的动画轮数.
     * 时长 = 轮数 * 200ms
     */
    int getPostRoundDelay();

    /**
     * 使用技能时增加的动画轮数.
     * 时长 = 轮数 * 200ms
     */
    int getSkillRoundDelay();

}
