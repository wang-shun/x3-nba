package com.ftkj.manager.battle.handle;

import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleSource;

/** 跨服天梯赛 */
public class BattleRanked extends BattleCommon {
    public BattleRanked(BattleSource battleSource) {
        super(battleSource);
    }

    public BattleRanked(BattleSource battleSource, BattleEnd end, BattleRoundReport round) {
        super(battleSource, end, round);
    }
}
