package com.ftkj.manager.battle.handle;

import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.RoundReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoBattleQuickHandle extends BattleCommon {
    private static final Logger log = LoggerFactory.getLogger(AutoBattleQuickHandle.class);

    public AutoBattleQuickHandle(BattleSource bs) {
        this(bs, null, false);
    }

    public AutoBattleQuickHandle(BattleSource bs, boolean pause) {
        this(bs, null, pause);
    }

    public AutoBattleQuickHandle(BattleSource bs, BattleEnd end) {
        this(bs, end, false);
    }

    public AutoBattleQuickHandle(BattleSource bs, BattleEnd end, boolean pause) {
        super(bs, end, (source, report) -> {});
        bs.updateStage(EBattleStage.PK);
        bs.getRound().setPause(pause);
        bs.getInfo().setDisablePushMessage(true);
        bs.getRound().setRoundDelay(0);
        bs.getRound().setSpeedMod(1);
    }

    @Override
    public synchronized RoundReport pk() {
        BattleSource bs = getBattleSource();
        log.debug("quick match. bid {} htid {} atid {}", bs.getId(), bs.getHomeTid(), bs.getAwayTid());
        super.quickEnd();
        log.debug("quick match end. bid {} htid {} atid {} round {} score {}:{}", bs.getId(), bs.getHomeTid(),
                bs.getAwayTid(), bs.getRound().getCurRound(), bs.getHomeScore(), bs.getAwayScore());
        return null;
    }

    @Override
    public boolean checkBeforeTimeOut() {
        return true;
    }

}
