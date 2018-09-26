package com.ftkj.manager.battle;

import com.ftkj.manager.battle.model.BattleSource;

/**
 * @author tim.huang
 * 2017年3月15日
 * 赛前阶段结束，等待通知阶段
 */
public interface BattleTip {

    /** 赛前阶段结束，等待通知阶段 */
    void tipTeam(BattleSource bs);

}
