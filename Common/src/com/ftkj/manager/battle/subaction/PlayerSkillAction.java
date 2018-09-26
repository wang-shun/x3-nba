package com.ftkj.manager.battle.subaction;

import com.ftkj.cfg.battle.BaseSubActionBean;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePlayerSkill;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.Round;
import com.ftkj.manager.battle.model.RoundSkill;
import com.ftkj.manager.battle.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 球员技能行为
 */
public class PlayerSkillAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(PlayerSkillAction.class);

    public PlayerSkillAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        BattlePosition bp = calcAndFindActPlayer(ctx);//取该行为的球员
        BattlePlayer pr = bp.getPlayer();
        boolean attack = ctx.subBean().getVi1() == BaseSubActionBean.Vi1_Player_SKill_Attack;
        ErrorCode ret = pkUseSkill(ctx.bs(), pr.getTid(), pr, attack);
        if (ret.isError()) {
            log.debug("subact skill. bid {} tid {} prid {} user attack {} skill. ret {}", ctx.bs().getId(),
                    pr.getTid(), pr.getRid(), attack, ret);
        }
    }

    public static ErrorCode pkUseSkill(BattleSource bs, long teamId, BattlePlayer player, boolean isAttackSkill) {
        BattlePlayerSkill playerSkill = player.getPlayerSkill();
        Skill skill = isAttackSkill ? playerSkill.getAttack() : playerSkill.getDefend();
        if (skill == null) {
            return ErrorCode.Battle_Skill_Null;
        }
        Round round = bs.getRound();
        RoundSkill rs = round.getSkill();
        boolean home = teamId == bs.getHome().getTeamId();
        int roundCounter = round.getCounter().get();
        Skill mySkill = home ? rs.getHomeSkill() : rs.getAwaySkill();
        if (mySkill != null || !playerSkill.useSkill(isAttackSkill)) {//玩家已经使用技能,或者技能使用条件不满足
            return ErrorCode.Battle_Skill_Used;
        }
        Skill oldSkill = home ? rs.getAwaySkill() : rs.getHomeSkill();
        if (oldSkill != null && rs.getStartRound() != -1 && roundCounter > rs.getStartRound()) {//超过响应时间了，无法触发或者响应技能
            return ErrorCode.Battle_Skill_Timeout;
        }
        //获得对方使用的技能类型
        int max;
        if (oldSkill != null) {
            if (oldSkill.attackSkill() == isAttackSkill) {// 使用相同类型的技能。操作有误
                return ErrorCode.Battle_Skill_Type;
            }
            if (!oldSkill.attackSkill()) {//对方防守技能，取对方的当前上限为我方进攻技能使用的上限
                max = oldSkill.getExecuteLevel();
            } else {//对方进攻技能，取我方的能量上限
                max = playerSkill.getCurMaxSkillPower();
            }
        } else {
            max = playerSkill.getCurMaxSkillPower();
        }

        if (player.getPlayerSkill().getSkillPower() < max) {
            return ErrorCode.Battle_Skill_Power;
        }

        //		int total = this.battleSource.getStepConfig().getTotalRound();
        //		if(this.battleSource.getRound().getCurRound()>=total-2){//5回合内无法使用技能
        //			log.debug("回合结束前3回合无法使用{}-{}", this.battleSource.getRound().getCurRound(),total-3);
        //			return ErrorCode.Error;
        //		}
        //
        skill.setExecuteLevel(max / 10000);
        boolean trigger = rs.updateSkill(skill, home, bs.getInfo().getSkillStrategy(), roundCounter);
        if (trigger) {
            //双方触发，即时展现结果
            rs.calcSkillWin();
            rs.playerAnimation();
            rs.updateStartRound();
            if (rs.isCalc() && !isAttackSkill) {
                max = Math.max(0, max / 2);//防守技能使用成功, 反还一半能量
            }
        }
        bs.stats().upSkillPower(player, -max);
        return ErrorCode.Success;
    }
}
