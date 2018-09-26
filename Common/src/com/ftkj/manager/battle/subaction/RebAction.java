package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;

/**
 * 篮板
 *
 * @author tim.huang
 * @author luch
 */
public class RebAction {

    public static class BaseRebAction extends BaseSubAction {
        BaseRebAction(EActionType type) {
            super(type);
        }

        protected void reb(SubActionContext ctx, BattlePosition bp) {
            BattlePlayer pr = bp.getPlayer();
            //下轮需要必定命中技能，将球权强制命中给进攻技能的球队
            if (ctx.roundSkill().getTeamId() > 0 && ctx.roundSkill().getTeamId() != pr.getTid()) {
                bp = calcAndFindActPlayer(ctx);
                pr = bp.getPlayer();
            }

            updatePlayerRoundPower(ctx, pr);
            updatePlayerSkillPower(ctx, getType());
            updatePlayerActStat(ctx, getType(), 1);
            addReportAction(ctx, pr.getPower());
            //变更球权
            ctx.report().setNextBall(pr.getTid());
        }
    }

    /** 随机篮板 */
    public static final class RandomRebAction extends BaseRebAction {
        RandomRebAction(EActionType type) {
            super(type);
        }

        @Override
        public void doAction(SubActionContext ctx) {
            //取该行为的球员
            BattlePosition bp;
            if (isTriggerByChance(ctx, 20)) {//前场篮板
                bp = calcAndFindActPlayer(ctx);
            } else {//后场篮板
                bp = calcAndFindDefensePlayer(ctx);
            }
            reb(ctx, bp);
        }
    }

    public static final class FixedRebAction extends BaseRebAction {

        public FixedRebAction(EActionType type) {
            super(type);
        }

        @Override
        public void doAction(SubActionContext ctx) {
            //取该行为的球员
            BattlePosition bp = calcAndFindActPlayer(ctx);
            reb(ctx, bp);
        }
    }

}
