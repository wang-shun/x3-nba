package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePosition;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_犯规
 */
public class PfAction extends BaseFoulAction {
    public PfAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        BattlePosition bp = calcAndFindActPlayer(ctx);//取该行为的球员
        doAction0(ctx);
        dqPlayer(ctx.bs(), ctx.report(), ctx.ball(), ctx.otherBall(), bp);
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.pf, 1, 0, 0,ctx.bp().isForce());
    }

}
