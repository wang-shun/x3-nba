package com.ftkj.manager.battle;

import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;

public interface BattleRoundReport {
    /** 每轮比赛计算完毕后, 处理 pk report */
    void round(BattleSource bs, RoundReport report);
}
