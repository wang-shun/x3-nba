package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * 比赛得分行为
 * T_得分
 *
 * @author tim.huang
 * @author luch
 */
public class PtsAction extends BasePtsAction {

    public PtsAction(EActionType type) {
        super(type);
    }

    @Override
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        if (ctx.bean().getScore() > 0) {
            updateScore(ctx, ctx.bean().getScore());
            ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.fgm, 1));
            updateRunPointPlayer(ctx, 2);
        }
        ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.fga, 1));
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.pts, ctx.bean().getScore(), 0, 0,ctx.bp().isForce());
    }
}
