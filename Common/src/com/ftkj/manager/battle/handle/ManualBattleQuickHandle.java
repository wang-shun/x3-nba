package com.ftkj.manager.battle.handle;

import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.model.BattleSource;

public class ManualBattleQuickHandle extends AutoBattleQuickHandle {

    public ManualBattleQuickHandle(BattleSource bs) {
        super(bs, true);
    }

    public ManualBattleQuickHandle(BattleSource bs, BattleEnd end) {
        super(bs, end, false);
    }

    public ManualBattleQuickHandle(BattleSource bs, BattleEnd end, boolean pause) {
        super(bs, end, pause);
    }

}
