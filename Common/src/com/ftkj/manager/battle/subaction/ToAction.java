package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_失误
 */
public class ToAction extends BaseSubAction {

    public ToAction(EActionType type) {
        super(type);
    }

    @Override
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        super.updatePlayerActStat(ctx, act, actVal);
        BattlePlayer bpr = ctx.bpr();
        BattleTeam ball = ctx.ball();
        if (ball.updateTurnoverStreakPlayerId(bpr.getPlayerId())) {//更新单个连续失误
            ball.updateMorale(-5);
            ctx.report().addSubAct(ball.getTeamId(), bpr.getRid(), EActionType.continuity_to, 0, 0, 0,ctx.bp().isForce());
        }
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.to, 1, 0, 0,ctx.bp().isForce());
    }

}
