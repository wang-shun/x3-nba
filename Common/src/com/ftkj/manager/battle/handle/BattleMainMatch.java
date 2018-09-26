package com.ftkj.manager.battle.handle;

import com.ftkj.console.NPCConsole;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.server.GameSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 主线赛程.
 */
public class BattleMainMatch extends BattleCommon {
    private static final Logger log = LoggerFactory.getLogger(BattleMainMatch.class);

    public BattleMainMatch(BattleSource bs) {
        super(bs);
    }

    public BattleMainMatch(BattleSource bs, BattleEnd end, BattleRoundReport round) {
        super(bs, end, round);
    }

    @Override
    protected void initPre() {
        BattleSource bs = getBattleSource();
        BattleInfo bi = bs.getInfo();
        if (bi.getBattleType() == EBattleType.Main_Match_Championship) {
            PreChampionshipAvatar pta = bs.getOrCreateAttributeMap(bs.getHome().getTeamId()).getVal(EBattleAttribute.Main_Match_Away_Npc_Avatar);
            log.debug("btmmatch init. bid {} hometid {} awaytid {}", bi.getBattleId(), bs.getHome().getTeamId(), bs.getAway().getTeamId());
            if (pta != null) {
                log.debug("btmmatch init. bid {} pta tid {} npc {}", bi.getBattleId(), pta.awayTeamId, pta.awayNpcId);
                BattleTeam awayTeam = bs.getAway();
                if (!GameSource.isNPC(pta.awayTeamId) && awayTeam.getTeamId() == pta.awayTeamId) {//替换真实球队(对手)球员cap为npc. pta.awayPlayerCaps
                    awayTeam.getPlayers().forEach(BattlePlayer::clearCap);
                    awayTeam.getAbility().clearCap();

                    int totalAttack = awayTeam.getPlayers().stream().mapToInt(p -> (int) p.getPlayer().getAbility(EActionType.ocap)).sum();
                    int totalDefend = awayTeam.getPlayers().stream().mapToInt(p -> (int) p.getPlayer().getAbility(EActionType.dcap)).sum();
                    awayTeam.getPlayers().forEach(p -> {
                        PlayerAbility pa = new PlayerAbility(AbilityType.Npc_Buff, p.getPlayerId());
                        float jg = p.getPlayer().getAbility(EActionType.ocap);
                        float fs = p.getPlayer().getAbility(EActionType.dcap);
                        NPCBean npc = NPCConsole.getNPC(pta.awayNpcId);
                        if (npc != null) {
                            pa.setAttr(EActionType.ocap, jg / totalAttack * npc.getPlayerAttack());
                            pa.setAttr(EActionType.dcap, fs / totalDefend * npc.getPlayerDefend());
                            log.debug("btmmatch replace cap. bid {} tid {} npcid {} pid {} after cap {}",
                                    bi.getBattleId(), pta.awayTeamId, pta.awayNpcId, p.getPlayerId(), pa);
                        }
                        p.clearCap();
                        p.addPlayerAbility(pa);
                    });
                    bs.getAway().setTeamId(pta.awayNpcId);
                }//end !npc
            }//end pta
        }//end bt
    }

    /** 主线赛程. 锦标赛的防守方替身数据 */
    public static final class PreChampionshipAvatar implements Serializable {
        private static final long serialVersionUID = 7248308412147222156L;
        /** 对手球队id */
        private final long awayTeamId;
        /** npc id */
        private final long awayNpcId;

        public PreChampionshipAvatar(long awayTeamId, long awayNpcId) {
            this.awayTeamId = awayTeamId;
            this.awayNpcId = awayNpcId;
        }
    }
}
