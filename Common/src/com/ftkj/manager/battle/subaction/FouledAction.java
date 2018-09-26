package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePosition;

/**
 * @author tim.huang
 * 2017年3月1日
 * T_被犯规
 */
public class FouledAction extends BaseFoulAction {
    public FouledAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        BattlePosition bp = calcAndFindDefensePlayer(ctx);//取该行为的球员
        doAction0(ctx);
        ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType.pf, 1));
        dqPlayer(ctx.bs(), ctx.report(), ctx.otherBall(), ctx.ball(), bp);
    }

    /** 添加子行为报表 */
    @Override
	protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType.fouled, 1, 0, 0,ctx.bp().isForce());
    }

}
