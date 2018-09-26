package com.ftkj.manager.battle;

import com.ftkj.manager.battle.model.BattleSource;

@FunctionalInterface
public interface BattleEnd {
    /** 比赛结束. 处理数据 */
    void end(BattleSource bs);

}
