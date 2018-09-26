package com.ftkj.manager.battle.subaction;

import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.RoundReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 犯规和被犯规
 */
public abstract class BaseFoulAction extends BaseSubAction {
    private static final Logger log = LoggerFactory.getLogger(BaseFoulAction.class);
    /** 最大犯规次数 */
    public static final int PF_MAX = 6;

    BaseFoulAction(EActionType type) {
        super(type);
    }

    /** 处理6犯下场 */
    void dqPlayer(BattleSource bs, RoundReport report, BattleTeam ball, BattleTeam otherBall, BattlePosition bp) {
        BattlePlayer pr = bp.getPlayer();
        float fgCount = bp.getPlayer().getRealTimeActionStats().getValue(EActionType.pf);
        if (fgCount >= PF_MAX) {//6犯下场
            List<BattlePlayer> players = ball.getPlayers().stream().filter(p -> p.getPlayerId() != pr.getPlayerId())
                    .filter(p -> p.getLineupPosition() == EPlayerPosition.NULL)
                    .filter(p -> p.getPlayerPosition() == bp.getPosition())
                    .filter(p -> p.getRealTimeActionStats().getValue(EActionType.pf) < PF_MAX)
                    .sorted(so)
                    .collect(Collectors.toList());

            BattlePlayer rpr = null;
            if (players.size() <= 0) {//没有指定位置球员
                //从替补中取攻防最高的球员
                List<BattlePlayer> tmp = ball.getPlayers().stream().filter(p -> p.getPlayerId() != pr.getPlayerId())
                        .filter(p -> p.getLineupPosition() == EPlayerPosition.NULL)
                        .filter(p -> p.getRealTimeActionStats().getValue(EActionType.pf) < PF_MAX)
                        .sorted(so)
                        .collect(Collectors.toList());
                if (tmp.size() > 0) {
                    rpr = tmp.get(0);
                }
            } else {
                rpr = players.get(0);
            }
            if (log.isDebugEnabled()) {
                log.debug("subact foul sub. bid {} btid {} prid {} pos {} pf {} nextpr {}", bs.getId(), ball.getTeamId(),
                        pr.getPlayerId(), bp.getPosition(), fgCount, ball.getNextPlayer());
                if (rpr != null) {
                    log.debug("subact foul sub pr. bid {} tid {} prid {} pid {} pos {} nextpr {}", bs.getId(), ball.getTeamId(),
                            rpr.getPlayerId(), rpr.getPid(), rpr.getPlayerPosition(), ball.getNextPlayer());
                }
            }

            if (ball.getNextPlayer() == pr.getPlayerId() && rpr != null) {
                ball.setNextPlayer(rpr.getPlayerId());//替换球员
            }
            //强制判定，如果下回合是由该球员执行技能，那么6犯不下场
            if (ball.getNextPlayer() != pr.getPlayerId() && rpr != null) {
                report.addSubAct(ball.getTeamId(), pr.getPlayerId(), EActionType.disqualification,
                        rpr.getPlayerId(), 0, 0,bp.isForce());
                ball.updatePlayerPosition(pr.getPlayerId(), rpr.getPlayerId()
                        , true, otherBall.getPkTactics(TacticType.Offense),
                        otherBall.getPkTactics(TacticType.Defense));
                log.debug("subact foul sub pos. bid {} tid {} p1 {} {} p2 {} {}", bs.getId(), ball.getTeamId(),
                        pr.getPid(), pr.getRid(), rpr.getPid(), rpr.getRid());
            }
            ball.updateMorale(-5);
        }
    }

    private static Comparator<BattlePlayer> so = (a, b) -> {
        int aCap = a.getBaseCap();
        int bCap = b.getBaseCap();
        return Integer.compare(bCap, aCap);
    };

}
