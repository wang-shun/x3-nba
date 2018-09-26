package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleTeam;

/**
 * 得分行为
 */
public abstract class BasePtsAction extends BaseSubAction {

    public BasePtsAction(EActionType type) {
        super(type);
    }

    void updateRunPointPlayer(SubActionContext ctx, int addScore) {
        BattleTeam team = ctx.ball();
        BattlePlayer pr = ctx.bpr();
        if (team.upRunPointPlayerId(pr.getPlayerId(), addScore)) {//更新单个连续得分
            team.updateMorale(5);
            ctx.report().addSubAct(team.getTeamId(), pr.getPlayerId(), EActionType.pr_run, team.getRunPointNumWithPlayer(), 0, 0,ctx.bp().isForce());
            ctx.bs().stats().setRtAndStep(pr, ctx.step(), act(EActionType.pr_run, addScore));
        }
    }

}
