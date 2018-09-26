package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.enums.battle.EBattleType;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.BattlePB.BattleEndMainData;
import com.ftkj.proto.BattlePB.BattleEndTeamData;
import com.ftkj.proto.BattlePB.BattleInfoData;
import com.ftkj.proto.BattlePB.BattleMainData;
import com.ftkj.proto.BattlePB.BattlePKTeamData;
import com.ftkj.proto.BattlePB.BattleRoundMainData;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.GameLogPB.BattleEndLogData;
import com.ftkj.proto.GameLogPB.BattlePlayerSourceData;
import com.ftkj.proto.GameLogPB.BattleStepScoreData;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.concurrent.HandlingExceptionScheduledExecutor;
import com.ftkj.x3.client.model.ClientTeamBattleStatus;
import com.ftkj.x3.client.model.ClientTeamStatus;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.player.PlayerClient;
import com.ftkj.xxs.client.net.XxsUserClient.AsyncWaitLatch;
import com.ftkj.xxs.core.util.XxsThreadFactory;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

/**
 * 比赛 client.
 *
 * @author luch
 */
@Component
public class BattleClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(BattleClient.class);
    @Autowired
    private PlayerClient playerClient;

    private ScheduledExecutorService executorService = new HandlingExceptionScheduledExecutor(
        Math.min(Runtime.getRuntime().availableProcessors() * 3, 20),
        new XxsThreadFactory("cu-async"));

    public static void main(String[] args) {
        new BattleClient().run();
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

    /** 开始比赛 */
    public interface StartMatchCall {
        Ret start();
    }

    public void battleStats(UserClient uc, ClientUser cu) {
        for (ClientTeamBattleStatus bs : cu.getStatus().getBattles().values()) {
            log.info("battleStats tid {} bid {} type {} stage {} node {}", uc.tid(), bs.getBattleId(), bs.getType(),
                bs.getStage(), bs.getNodeIp());
        }
    }

    /** 正常比赛 */
    public Ret matchSlow(UserClient uc, ClientUser cu, StartMatchCall startMatch, EBattleType bt) {
        Ret ret1 = playerClient.fillLineupPlayer(uc, cu, 5);
        if (ret1.isErr()) {
            return ret1;
        }
        //        playerClient.upgradeLineupLev(uc, cu, cu.getTeam().getLevel());
        if (cu.getStatus().inBattle(bt)) {
            gmForceEndMatch(uc, cu, bt);
        }
        log.info("tid {} slowMatch bt {}", cu.tid(), bt);
        Ret ret = startMatch.start();
        if (ret.isErr()) {
            return ret;
        }
        attchMatch(uc, cu, bt);
        return succ();
    }

    /**
     * 开始比赛并强制结束
     *
     * @return 比赛id
     */
    public long matchAndForceEnd(UserClient uc, ClientUser cu, StartMatchCall startMatch, EBattleType bts) {
        Ret ret1 = playerClient.fillLineupPlayer(uc, cu, 5);
        if (ret1.isErr()) {
            return 0;
        }
        playerClient.upgradeLineupLev(uc, cu, cu.getTeam().getLevel());
        if (cu.getStatus().inBattle(bts)) {
            Ret ret = quickMatch(uc, cu, bts);
            if (ret.isErr()) {
                gmForceEndMatch(uc, cu, bts);
            }
        }
        log.info("tid {} matchAndForceEnd bt {}", cu.tid(), bts);
        Ret ret = startMatch.start();
        if (ret.isErr()) {
            return 0;
        }
        if (cu.getStatus().inBattle(bts)) {
            ClientTeamBattleStatus tbs = cu.getStatus().getInBattle(bts);
            quickOrForceEndMatch(uc, cu, tbs.getBattleId());
            return tbs.getBattleId();
        }
        return 0;
    }

    public Ret quickOrForceEndMatch(UserClient uc, ClientUser cu, EBattleType... bt) {
        Ret ret = quickMatch(uc, cu, bt);
        if (ret.isErr()) {
            ret = gmForceEndMatch(uc, cu, bt);
        }
        return ret;
    }

    public Ret quickOrForceEndMatch(UserClient uc, ClientUser cu, long battleId) {
        Ret ret = quickMatch(uc, cu, battleId);
        if (ret.isErr()) {
            ret = gmForceEndMatch(uc, cu, battleId);
        }
        return ret;
    }

    /** gm 强制结束比赛 */
    public Ret gmForceEndMatch(UserClient uc, ClientUser cu, EBattleType... bt) {
        return gmForceEndMatch(uc, cu, false, bt);
    }

    /** gm 强制结束比赛 */
    public Ret gmForceEndMatch(UserClient uc, ClientUser cu, boolean forceHomeWin, EBattleType... bt) {
        ClientTeamBattleStatus bs = cu.getStatus().getInBattle(bt);
        log.info("forceEndMatch tid {} bid {} type {} stage {} node {}", uc.tid(), bs.getBattleId(), bs.getType(),
            bs.getStage(), bs.getNodeIp());
        if (bs.getBattleId() <= 0) {
            return succ();
        }
        return gmForceEndMatch(uc, cu, bs.getBattleId(), forceHomeWin);
    }

    /** gm 强制结束比赛 */
    public Ret gmForceEndMatch(UserClient uc, ClientUser cu, long battleId) {
        return gmForceEndMatch(uc, cu, battleId, false);
    }

    /** gm 强制结束比赛 */
    public Ret gmForceEndMatch(UserClient uc, ClientUser cu, long battleId, boolean forceHomeWin) {
        AsyncWaitLatch awl = uc.createAysnWaitLatch(code(ServiceCode.Push_Battle_End));
        Message msg = uc.writeAndGet(createGM(GmCommand.Match_Force_End, battleId, forceHomeWin ? 1 : 0));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} forceEndMatch battleId {}. fail {}", cu.tid(), battleId, ret(resp));
            return ret(resp);
        }
        uc.waitAysnLatchRelease(awl, 3);
        log.info("tid {} forceEndMatch battleId {} succ", uc.tid(), battleId);
        return succ();
    }

    /** 快速结束比赛 */
    public Ret quickMatch(UserClient uc, ClientUser cu, EBattleType... bt) {
        ClientTeamBattleStatus bs = cu.getStatus().getInBattle(bt);
        log.info("quickMatch tid {} bid {} type {} stage {} node {}", uc.tid(), bs.getBattleId(), bs.getType(),
            bs.getStage(), bs.getNodeIp());
        if (bs.getBattleId() <= 0) {
            return succ();
        }
        return quickMatch(uc, cu, bs.getBattleId());
    }

    /** 快速结束比赛 */
    public Ret quickMatch(UserClient uc, ClientUser cu, long battleId) {
        return uc.reqCommon(uc, cu, ServiceCode.Battle_Quick_End, ServiceCode.Push_Battle_End, battleId);
    }

    /** 接收比赛推送 */
    public void attchMatch(UserClient uc, ClientUser cu, EBattleType bt) {
        ClientTeamBattleStatus tbs = cu.getStatus().getInBattle(bt);
        if (tbs == null) {
            log.error("no battle in match. tid {} bt {}", cu.getTid(), bt);
            return;
        }
        matchBeforeData(uc, cu, tbs.getBattleId());
        log.info("tid {} attchMatch bid {}", cu.getTid(), tbs.getBattleId());
        uc.writeAndGet(createReq(ServiceCode.Battle_All, tbs.getBattleId()));
    }

    /** 赛前数据 */
    private void matchBeforeData(UserClient uc, ClientUser cu, long bid) {
        BattlePB.BattleBeforeData resp = uc.req(uc, cu, ServiceCode.Battle_Before_Team_Data,
            BattlePB.BattleBeforeData.getDefaultInstance(), bid);
        if (!isSimpleLog()) {
            log.info("tid {} match {} before data {} <-> {}", cu.tid(), bid, resp.getHome().getTeamName(),
                resp.getAway().getTeamName());
        } else {
            log.info("tid {} match {} before data {}", cu.tid(), bid, resp.getBattleType());
        }
    }

    /** 获取比赛统计数据 */
    public Ret playerStats(UserClient uc, ClientUser cu, long bid) {
        Message msg = uc.writeAndGet(createReq(ServiceCode.Battle_PK_showPlayerSource, bid));
        BattleEndLogData resp = parseFrom(BattleEndLogData.getDefaultInstance(), msg);
        if (uc.isError(resp.getCode())) {
            log.warn("tid {} match player stats bid {}. fail {}", uc.tid(), bid, Ret.convert(msg.getRet()));
            return Ret.convert(msg.getRet());
        }
        log.info("tid {} match player stats bid {} succ", uc.tid(), bid);
        if (!isSimpleLog()) {
            log.info("tid {} match player stats bid {}" +
                    " home {} score {} {} players score {}" +
                    " away {} score {} {} players score {}",
                uc.tid(), bid,
                resp.getHome().getTeamId(), resp.getHome().getScore(), stepScore(resp.getHome().getStepScore()),
                resp.getHome().getPlayersList().stream().mapToInt(p -> p.getDf()).sum(),
                resp.getAway().getTeamId(), resp.getAway().getScore(), stepScore(resp.getAway().getStepScore()),
                resp.getAway().getPlayersList().stream().mapToInt(p -> p.getDf()).sum());

            for (BattlePlayerSourceData prsd : resp.getHome().getPlayersList()) {
                log.info("bid {} htid {}player stats {}", bid, resp.getHome().getTeamId(), shortDebug(prsd));
            }
            for (BattlePlayerSourceData prsd : resp.getAway().getPlayersList()) {
                log.info("bid {} atid {} player stats {}", bid, resp.getAway().getTeamId(), shortDebug(prsd));
            }
        }
        return succ();
    }

    private int stepScore(BattleStepScoreData steps) {
        return steps.getStep1() + steps.getStep2() + steps.getStep3() + steps.getStep4() + steps.getStepot();
    }

    public void mainPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        BattleMainData resp = parseFrom(BattleMainData.getDefaultInstance(), msg);
        BattleInfoData info = resp.getBattleInfo(); //比赛基础数据
        //        BattleTeamData team = resp.getTeamData();
        //        BattleCustomData custom = resp.getCustomData();  //比赛玩家数据
        BattlePKTeamData home = info.getHomeData();  //主场玩家数据
        BattlePKTeamData away = info.getAwayData();//客场玩家数据
        log.info("battle main push. tid {} bid {} type {} atid {} btid {}" +
                " score {}:{} acap {} {} bcap {} {} step {} stepround {} round {}",
            cu.tid(), info.getBattleId(), info.getBattleType(), home.getTeamId(), away.getTeamId(),
            home.getScore(), away.getScore(), home.getAttackCap(), home.getDefendCap(),
            away.getAttackCap(), away.getDefendCap(),
            info.getStep(), info.getRound(), info.getAllRound());
    }

    public void startPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        DefaultData dd = parseFrom(msg);
        ClientTeamStatus ts = cu.getStatus();
        ClientTeamBattleStatus tbs = ts.getBattle(dd.getBigNum());
        if (tbs == null) {
            tbs = new ClientTeamBattleStatus();
            tbs.setBattleId(dd.getBigNum());
            ts.getBattles().put(tbs.getBattleId(), tbs);
            log.info("battle start push. tid {} battleid {} type {}", uc.tid(), dd.getBigNum(), dd.getCode());
        }
        tbs.setType(EBattleType.getBattleType(dd.getCode()));
        //        tbs.updateStatus(EBattleRoomStatus.比赛中);
    }

    public void endPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        DefaultData dd = parseFrom(msg);
        ClientTeamStatus ts = cu.getStatus();
        ClientTeamBattleStatus tbs = ts.getBattles().remove(dd.getBigNum());
        log.info("battle end push. tid {} battleid {} last status {} -> end", uc.tid(), dd.getBigNum(), tbs != null ? tbs.getStatus() : null);
    }

    /** 回合推送 */
    public void roundPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        BattleRoundMainData resp = parseFrom(BattleRoundMainData.getDefaultInstance(), msg);
        log.info("battle round push. bid {} bt {} round {} stepround {} step {} act {} score {}:{} " +
                "cap ho {} hd {} ao {} ad {} histsize {} subactsize {} subacts [{}]",
            resp.getBattleId(), resp.getBattleType(), resp.getAllRound(), resp.getRound(), resp.getStep(),
            resp.getActionType(), resp.getHomeScore(), resp.getAwayScore(),
            resp.getHomeAttackCap(), resp.getHomeDefendCap(),
            resp.getAwayAttackCap(), resp.getAwayDefendCap(),
            resp.getHintsCount(), resp.getActionListCount()/*, shortDebug(resp.getActionListList())*/);
    }

    public void roundEndPush(UserClient uc, ClientUser cu, ClientRespMessage msg) {
        BattleEndMainData resp = parseFrom(BattleEndMainData.getDefaultInstance(), msg);
        BattleEndTeamData home = resp.getEndInfo().getHomeTeam();
        BattleEndTeamData away = resp.getEndInfo().getAwayTeam();
        if (isSimpleLog()) {
            log.info("battle round end push. tid {} bid {} wintid {} score {}:{}",
                cu.getTid(), resp.getBattleId(), resp.getWinTeamId(), home.getScore(), away.getScore());
        } else {
            log.info("battle round end push. tid {} bid {} wintid {} score {}:{} resp {}", cu.getTid(),
                resp.getBattleId(), resp.getWinTeamId(), home.getScore(), away.getScore(), shortDebug(resp));
        }
    }
}
