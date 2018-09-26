package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_盖帽
 */
public class BlkAction extends BaseSubAction {

    public BlkAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        calcAndFindDefensePlayer(ctx);
        doAction0(ctx);
        if (isTriggerByChance(ctx, 50)) {
            ctx.report().setNextBall(ctx.otherBall().getTeamId());
        } else {
            ctx.report().setNextBall(ctx.ball().getTeamId());
        }
        ctx.ball().updateMorale(-2);
        ctx.otherBall().updateMorale(3);
        if (ctx.roundSkill().getTeamId() > 0 && ctx.roundSkill().getTeamId() != ctx.report().getNextBall()) {
            ctx.report().setNextBall(ctx.roundSkill().getTeamId());
        }
    }

    @Override
	protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.otherBall().getTeamId(), ctx.bpr().getPlayerId(), EActionType.blk, 1, 0, 0,ctx.bp().isForce());
    }

}
