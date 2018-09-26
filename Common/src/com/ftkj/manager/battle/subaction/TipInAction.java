package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * T_补篮
 *
 * @author tim.huang
 * @author luch
 */
public class TipInAction extends BasePtsAction {

    public TipInAction(EActionType type) {
        super(type);
    }

    @Override
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        if (ctx.bean().getScore() > 0) {
            updateScore(ctx, ctx.bean().getScore());
            ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.reb, 1),
                    act(EActionType.fgm, 1), act(EActionType.fga, 1));
            updateRunPointPlayer(ctx, ctx.bean().getScore());
        }
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.tip_in, ctx.bean().getScore(), 0, 0,ctx.bp().isForce());
    }

}
