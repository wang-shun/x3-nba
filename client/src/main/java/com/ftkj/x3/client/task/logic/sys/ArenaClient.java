package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.proto.CommonPB.BattleHisListResp;
import com.ftkj.proto.CommonPB.BattleHisResp;
import com.ftkj.proto.RankArenaPb.ArenaInfoResp;
import com.ftkj.proto.RankArenaPb.ArenaOpponentResp;
import com.ftkj.proto.RankArenaPb.ArenaRanksResp;
import com.ftkj.proto.RankArenaPb.ArenaSelfInfoResp;
import com.ftkj.proto.RankArenaPb.ArenaStartMatchResp;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientArena;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 竞技场.
 *
 * @author luch
 */
@Component
public class ArenaClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(ArenaClient.class);
    @Autowired
    private TeamClient teamClient;

    public static void main(String[] args) {
        new ArenaClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    public Ret moduleTest(UserClient uc, ClientUser cu) {
        return succ();
    }

    /** 获取天梯赛信息 */
    public Ret info(UserClient uc, ClientUser cu) {
        return uc.reqCommon(uc, cu, ServiceCode.Arena_Info, ServiceCode.Arena_Info_Push);
    }

    /** 开始比赛 */
    public Ret startMatch(UserClient uc, ClientUser cu, Long targetTid, Integer targetRank) {
        ClientArena ar = cu.getArena();
        if (ar.getBuyMatchNum() + ar.getFreeMatchNumCfg() <= ar.getUsedMatchNum()) {//次数不足
            Ret ret = buyMatchNum(uc, cu, 1);
            if (ret.isErr()) {
                return ret;
            }
        }
        if (ar.getMatchCd() > 0) {
            uc.writeAndGet(createGM(GmCommand.Arena_Reset_Cd));
        }
        return uc.reqCommon(uc, cu, ServiceCode.Arena_Start_Match, 0, targetRank, targetTid);
    }

    /** 购买比赛次数 */
    public Ret buyMatchNum(UserClient uc, ClientUser cu, int num) {
        ClientArena ar = cu.getArena();
        Ret ret = teamClient.gmSetBuyNumAndAddCurrency(uc, cu, TeamNumType.Arena_Buy_Match_Num,
                ar.getBuyMatchNum(), 0, tnb -> {
                    ar.setBuyMatchNum(0);
                    if (tnb.getMaxNum() + ar.getFreeMatchNumCfg() >= ar.getUsedMatchNum()) {
                        Ret ret1 = teamClient.gmSetDayNum(uc, cu, TeamDayNumType.Arena_Match_Free_Num, 0);
                        if (ret1.isSucc()) {
                            ar.setUsedMatchNum(0);
                        }
                    }
                });

        if (ret.isErr()) {
            return ret;
        }
        return uc.reqCommon(uc, cu, ServiceCode.Arena_Buy_Match_Num, ServiceCode.Arena_Buy_Match_Num_Push, num);
    }

    /** 排行榜 */
    public ArenaRanksResp rankList(UserClient uc, ClientUser cu) {
        ArenaRanksResp resp = uc.req(uc, cu, ServiceCode.Arena_Rank, ArenaRanksResp.getDefaultInstance());
        log.info("rank list. tid {} size {}", cu.tid(), resp.getTeamsCount());
        return resp;
    }

    /** 比赛历史信息 */
    public BattleHisListResp matchHistory(UserClient uc, ClientUser cu) {
        BattleHisListResp resp = uc.req(uc, cu, ServiceCode.Arena_Match_His, BattleHisListResp.getDefaultInstance(), 1);
        StringBuilder sb = new StringBuilder();
        for (BattleHisResp mh : resp.getHisList()) {
            sb.append(String.format("\ndate %s htid %s atid %s score %s:%s rank %s %s", new Date(mh.getCreateTime()),
                    mh.getHomeTeamId(), mh.getAwayTeamId(), mh.getHomeScore(), mh.getAwayScore(),
                    mh.getVi1(), mh.getVi2()));
        }
        log.info("match history. tid {} list {} {}", cu.tid(), resp.getHisCount(), sb);
        return resp;
    }

    public void infoPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        ArenaInfoResp resp = parseFrom(ArenaInfoResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            log.info("tid {} arena info push. {}", uc.tid(), shortDebug(resp.getSelfArena()));
        } else {
            log.info("tid {} arena info push. {} target num {}", uc.tid(), shortDebug(resp.getSelfArena()),
                    resp.getOpponentsCount());
            ArenaOpponentResp npc = resp.getOpponentsList().stream()
                    .filter(t -> GameSource.isNPC(t.getTeamId())).findFirst().orElse(null);
            log.debug("tid {} first npc target {}", uc.tid(), shortDebug(npc));
        }

        ClientArena ar = new ClientArena();
        ar.setTeamId(cu.tid());
        upArena(ar, resp.getSelfArena());
        upTarget(ar, resp.getOpponentsList());
        cu.setArena(ar);
    }

    private void upTarget(ClientArena ar, List<ArenaOpponentResp> targets) {
        ar.getTargetRankAndTid().clear();
        for (ArenaOpponentResp target : targets) {
            if (ar.getTargetRankAndTid().containsKey(target.getRank())) {
                log.warn("tid {} arena target. targets {} rank {} duplicate with tid {}", ar.getTeamId(),
                        target.getTeamId(), target.getRank(), ar.getTargetRankAndTid().get(target.getRank()));
            }
            ar.getTargetRankAndTid().put(target.getRank(), target.getTeamId());
        }
        log.info("tid {} arena target. size {} targets {}", ar.getTeamId(),
                ar.getTargetRankAndTid().size(), ar.getTargetRankAndTid());
    }

    private void upArena(ClientArena ar, ArenaSelfInfoResp sr) {
        ar.setRank(sr.getRank());
        ar.setMaxRank(sr.getMaxRank());
        ar.setPreMatchTime(sr.getPreMatchTime());
        ar.setTotalMatchCount(sr.getTotalMatchCount());
        ar.setTotalWinCount(sr.getTotalWinCount());
        ar.setUsedMatchNum(sr.getUsedMatchNum());
        ar.setFreeMatchNumCfg(sr.getFreeMatchNumCfg());
        ar.setBuyMatchNum(sr.getBuyMatchNum());
        ar.setMatchCd(sr.getMatchCd());
    }

    public void targetChangePush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        ArenaStartMatchResp resp = parseFrom(ArenaStartMatchResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            log.info("tid {} targetChange info push. {}", uc.tid(), resp.getRank());
        } else {
            log.info("tid {} targetChange info push. {}", uc.tid(), shortDebug(resp));
        }

        ClientArena ar = cu.getArena();
        if (ar == null) {
            return;
        }
        ArenaOpponentResp target = resp.getNewTarget();
        ar.getTargetRankAndTid().put(resp.getRank(), target.getTeamId());
    }

    public void refreshTargetPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
    }

    public void matchEndPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        upSelf(uc, cu, parseFrom(ArenaSelfInfoResp.getDefaultInstance(), msg), "matchend");
    }

    public void buyNumPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        upSelf(uc, cu, parseFrom(ArenaSelfInfoResp.getDefaultInstance(), msg), "buynum");
    }

    private void upSelf(UserClient uc, ClientUser cu, ArenaSelfInfoResp resp, String funcname) {
        if (isSimpleLog()) {
            log.info("tid {} {} push. {}", uc.tid(), funcname, resp.getRank());
        } else {
            log.info("tid {} {} push. {}", uc.tid(), funcname, shortDebug(resp));
        }

        ClientArena ar = cu.getArena();
        if (ar == null) {
            return;
        }
        upArena(ar, resp);
    }
}
