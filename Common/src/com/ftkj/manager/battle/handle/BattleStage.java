package com.ftkj.manager.battle.handle;

import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleSource;

/**
 * 旧主线赛程
 *
 * @author tim.huang
 * 2017年3月2日
 * 普通战斗逻辑
 */
public class BattleStage extends BaseBattleHandle {
    private boolean help;

    public BattleStage(BattleSource battleSource, BattleEnd end, BattleRoundReport round) {
        super(battleSource, end, round);
        this.help = battleSource.getAway().getTeamId() == 10001 || battleSource.getAway().getTeamId() == 10002;
    }

}
