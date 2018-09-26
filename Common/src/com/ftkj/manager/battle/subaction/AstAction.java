package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tim.huang
 * 2017年3月1日
 * 比赛助攻行为
 */
public class AstAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(AstAction.class);

    public AstAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        //取该行为的球员
        BattlePosition opr = ctx.report().getOffensePlayer();
        final int limitPrid = opr == null ? 0 : opr.getPlayer().getRid();
        BattlePosition bp = null;
        int count = 0;
        while (count < 32) {
            bp = calcAndFindActPlayer(ctx);
            if (bp.getPlayer().getRid() != limitPrid) {
                break;
            }
            count++;
            if (log.isDebugEnabled()) {
                log.debug("subact act. bid {} tid {} target prid {} == limitprid {}, find next. count {}",
                        ctx.bs().getId(), bp.getPlayer().getTid(), bp.getPlayer().getRid(), limitPrid, count);
            }
        }

        //首次行为决定进攻球员
        if (opr == null) {
            ctx.report().setOffensePlayer(bp);
        }
        BattlePlayer pr = bp.getPlayer();
        updatePlayerRoundPower(ctx, pr);
        updatePlayerActStat(ctx, getType(), 1);
        updatePlayerSkillPower(ctx, getType());
        addReportAction(ctx, pr.getPower());
    }

}
