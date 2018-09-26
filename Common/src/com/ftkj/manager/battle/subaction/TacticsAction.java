package com.ftkj.manager.battle.subaction;

import com.ftkj.console.NPCConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTactics;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.tactics.TacticsBean;
import com.ftkj.server.GameSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动换战术
 */
public class TacticsAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(TacticsAction.class);

    TacticsAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        TacticsBean offenseTb = TacticsConsole.getBean(TacticId.convert(ctx.subBean().getVi1()));
        TacticsBean defenseTb = TacticsConsole.getBean(TacticId.convert(ctx.subBean().getVi2()));
        if (offenseTb == null && defenseTb == null) {
            return;
        }
        BattlePosition bp = calcAndFindActPlayer(ctx);//取该行为的球员
        doAction0(ctx);
        BattlePlayer pr = bp.getPlayer();
        ErrorCode ret = updateTactic(ctx.bs(), pr.getTid(), offenseTb, defenseTb, false);
        if (log.isDebugEnabled()) {
            log.debug("subact tactics up. bid {} tid {} prid {} attid {} bttid {}. ret {}", ctx.bs().getId(),
                    pr.getTid(), ctx.subBean().getVi1(), ctx.subBean().getVi2(), ret);
        }
    }

    /** 战斗中修改战术 */
    public static ErrorCode updateTactic(BattleSource bs, long teamId, TacticsBean otb, TacticsBean dtb, boolean ignoreCD) {
        if (bs.getStage() != EBattleStage.PK) {
            return ErrorCode.Battle_Stage_Pk;//比赛当前阶段不是PK阶段
        }
        BattleTeam team = bs.getTeam(teamId);
        if (!ignoreCD && (!team.getStat().isCanUseTactics() || team.getStat().getReplaceTacticCD() > 0)) {
            return ErrorCode.Battle_Up_Tactics_CD;//比赛当前被封印了战术更换操作
        }
        if (otb == null) {
            BattleTactics tt = team.getPkTactics(TacticType.Offense);
            if (tt == null) {
                return ErrorCode.Battle_Up_Tactics_Null;
            }
            otb = tt.getTactics();
        }
        if (dtb == null) {
            BattleTactics tt = team.getPkTactics(TacticType.Defense);
            if (tt == null) {
                return ErrorCode.Battle_Up_Tactics_Null;
            }
            dtb = tt.getTactics();
        }
        if (otb.getType() != TacticType.Offense || dtb.getType() != TacticType.Defense) {
            return ErrorCode.Battle_Up_Tactics_Type;//比赛中更换的战术类型不是对应的进攻或防守战术
        }
        BattleTactics ot = team.getTactic(otb.getId());
        BattleTactics dt = team.getTactic(dtb.getId());
        log.debug("subact tactics uptactics. bid {} tid {} atid {} dtid {} ot {} dt {}",
                bs.getId(), teamId, otb.getId(), dtb.getId(), ot != null, dt != null);
        if (ot == null) {
            addTactic(team, otb);
            ot = team.getTactic(otb.getId());
        }
        if (dt == null) {
            addTactic(team, dtb);
            dt = team.getTactic(dtb.getId());
        }
        if (ot == null || dt == null) {
            return ErrorCode.Battle_Up_Tactics_Null;
        }

        BattleTeam other = bs.getOtherTeam(team.getTeamId());
        BattleTactics oot = other.getPkTactics(TacticType.Offense);
        BattleTactics odt = other.getPkTactics(TacticType.Defense);
        team.updateTactics(ot, dt, oot, odt);
        other.updateTactics(oot, odt, ot, dt);

        bs.getReport().addSubAct(teamId, 0, EActionType.change_tactics, otb.getId().getId(), dtb.getId().getId(), 0,false);
        bs.stats().upRtAndStep(team, EActionType.change_tactics, 1);
        team.getStat().updateTacticCdAndNum();
        return ErrorCode.Success;
    }

    private static void addTactic(BattleTeam bt, TacticsBean tb) {
        if (!GameSource.isNPC(bt.getTeamId())) {
            return;
        }
        NPCBean npc = NPCConsole.getNPC(bt.getTeamId());
        int lev = npc != null ? Math.min(1, npc.getTacticsLevel()) : 1;
        BattleTactics newbt = new BattleTactics(tb, lev);
        bt.addTactics(newbt);
    }

}
