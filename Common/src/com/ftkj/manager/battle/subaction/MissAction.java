package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * @author tim.huang
 * 2017年3月2日
 * T_2分投不中
 */
public class MissAction extends BaseSubAction {

    public MissAction(EActionType type) {
        super(type);
    }

    @Override
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.fga, 1), act(EActionType.brick, 1));
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.pts, ctx.bean().getScore(), 0, 0,ctx.bp().isForce());
    }

}
