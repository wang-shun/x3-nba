package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_抢断
 */
public class StlAction extends BaseSubAction {

    public StlAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        calcAndFindDefensePlayer(ctx);
        doAction0(ctx);
        ctx.otherBall().updateMorale(3);
        ctx.ball().updateMorale(-2);
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.otherBall().getTeamId(), ctx.bpr().getPlayerId(), EActionType.stl, 1, 0, 0,ctx.bp().isForce());
    }
}
