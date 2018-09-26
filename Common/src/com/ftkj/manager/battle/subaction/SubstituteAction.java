package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自动换人, 自动把所有场下球员换到场上.
 */
public class SubstituteAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(SubstituteAction.class);

    public SubstituteAction(EActionType type) {
        super(type);
    }

    @Override
    public void doAction(SubActionContext ctx) {
        BattleSource bs = ctx.bs();
        BattleTeam bt = ctx.ball();
        long teamId = bt.getTeamId();
        if (!bt.getStat().isCanSubPlayer() || bt.getStat().getReplacePlayerCD() > 0) {
            log.debug("subact substitute cd. bid {} tid {} cd", bs.getId(), teamId);
            return;
        }
        BattleTeam other = bs.getOtherTeam(bt.getTeamId());

        for (EPlayerPosition position : EPlayerPosition.values()) {
            if (position == EPlayerPosition.NULL) {
                continue;
            }
            BattlePosition bp = bt.getLineupPlayer(position);
            BattlePlayer pr = bp.getPlayer();
            BattlePlayer target = bt.getPlayers().stream()
                    .filter(bpr -> !bpr.isLineupPos() && bpr.getPlayerPosition() == bp.getPosition()).findFirst()
                    .orElse(null); //先找替补对位球员
            if (log.isDebugEnabled()) {
                log.debug("subact substitute. bid {} tid {} prid {} pos {} target {}",
                        bs.getId(), teamId, pr.getRid(), bp.getPosition(), target);
            }
            if (target == null) {
                continue;
            }
            bs.getReport().addSubAct(teamId, 0, EActionType.substitute, pr.getPlayerId(), target.getRid(), 0,bp.isForce());
            bt.updatePlayerPosition(pr.getPlayerId(), target.getRid(), true,
                    other.getPkTactics(TacticType.Offense), other.getPkTactics(TacticType.Defense));
        }
        bs.stats().upRtAndStep(bt, EActionType.substitute, 1);
        bt.getStat().updateReplacePlayerCD();
    }

}
