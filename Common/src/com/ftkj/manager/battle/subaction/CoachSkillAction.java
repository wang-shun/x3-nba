package com.ftkj.manager.battle.subaction;

import com.ftkj.console.CoachConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.coach.CoachSkillBean;
import com.ftkj.manager.skill.buff.SkillBuffer;
import com.ftkj.server.MessageManager;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.tuple.Tuple2;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * 教练技能行为
 */
public class CoachSkillAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(CoachSkillAction.class);

    public CoachSkillAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        BattlePosition bp = calcAndFindActPlayer(ctx);//取该行为的球员
        doAction0(ctx);
        BattlePlayer pr = bp.getPlayer();
        int skillId = ctx.subBean().getVi1();
        Tuple2<ErrorCode, List<BattleBuffer>> ret = useCoach(ctx.bs(), pr.getTid(), skillId, false);
        if (ret._1().isError()) {
            log.debug("subact coach use. bid {} tid {} prid {} user coach skill {}. ret {}", ctx.bs().getId(),
                    pr.getTid(), pr.getRid(), skillId, ret);
        }
    }

    public static Tuple2<ErrorCode, List<BattleBuffer>> useCoach(BattleSource bs, long teamId, int sid, boolean ignoreCDAndNum) {
        BattleTeam team = bs.getTeam(teamId);
        BattleTeam other = bs.getOtherTeam(teamId);
        CoachSkillBean cs = CoachConsole.getCoachSkillBean(sid);
        if (cs == null) {
            return Tuple2.create(ErrorCode.Battle_CoachSkill_Bean);
        }
        if (!ignoreCDAndNum && (!team.getStat().isCanUseCoach() || team.getStat().getCoachCD() > 0)) {//技能CD
            return Tuple2.create(ErrorCode.Battle_CoachSkill_CD);
        }
        int count = team.getStat().getCoachConut(sid);
        if (!ignoreCDAndNum && count >= cs.getCount()) {
            return Tuple2.create(ErrorCode.Battle_CoachSkill_Num);
        }
        int curRound = bs.getRound().getCurRound();
        //执行一次需要立即执行的状态
        cs.getBuff().stream().filter(SkillBuffer::isNow).forEach(buff -> buff.execute(teamId, bs));
        //将技能状态添加到玩家自身的状态表中
        List<BattleBuffer> result = Lists.newArrayList();
        addBuff(team, cs, curRound, SkillBuffer::isHome, result);
        addBuff(other, cs, curRound, buff -> !buff.isHome(), result);
        team.getStat().updateCoachCount(sid);
        team.getStat().updateCoachCD();

        if (!bs.getInfo().isDisablePushMessage()) {
            MessageManager.sendMessage(ServiceConsole.getBattleKey(bs.getId())
                    , BattlePb.coachSkill(teamId, cs, result)
                    , ServiceCode.Push_Battle_Coach);
        }
        return Tuple2.create(ErrorCode.Success, result);
    }

    private static void addBuff(BattleTeam bt, CoachSkillBean cs, int curRound,
                                Predicate<SkillBuffer> bufferPredicate, List<BattleBuffer> result) {
        cs.getBuff().stream().filter(bufferPredicate)
                .map(buff -> new BattleBuffer(bt.getTeamId(), buff, curRound))
                .peek(result::add)
                .forEach(buff -> bt.getBuffers().add(buff));
    }
}
