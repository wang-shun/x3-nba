package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;

/**
 * @author tim.huang
 * 2017年3月2日
 * T_进球转换球权
 */
public class PossessionAction extends BaseSubAction {

    public PossessionAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        ctx.report().setNextBall(ctx.otherBall().getTeamId());
    }
}
