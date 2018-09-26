package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;

/**
 * 比赛三分行为
 * T_3分
 *
 * @author tim.huang
 * @author luch
 */
public class ThreePtsAction extends BasePtsAction {

    public ThreePtsAction(EActionType type) {
        super(type);
    }

    @Override
    protected void updatePlayerActStat(SubActionContext ctx, EActionType act, int actVal) {
        BattleSource bs = ctx.bs();
        BattlePlayer player = ctx.bpr();
        if (ctx.bean().getScore() >= 3) {//三分行为，并且命中增加三分命中统计
            updateScore(ctx, ctx.bean().getScore());
            ctx.bs().stats().upRtAndStep(ctx.bpr(), ctx.step(), act(EActionType._3pm, 1), act(EActionType.fgm, 1));
            updateRunPointPlayer(ctx, 3);
        } else {//3分不中
            bs.stats().upRtAndStep(player, act(EActionType.brick, 1));
        }
        bs.stats().upRtAndStep(player, ctx.step(), act(EActionType._3pa, 1), act(EActionType.fga, 1));
    }

    @Override
    protected void addReportAction(SubActionContext ctx, float finalPower) {
        ctx.report().addSubAct(ctx.ball().getTeamId(), ctx.bpr().getPlayerId(), EActionType._3p, ctx.bean().getScore(), 0, 0,ctx.bp().isForce());
    }

}
