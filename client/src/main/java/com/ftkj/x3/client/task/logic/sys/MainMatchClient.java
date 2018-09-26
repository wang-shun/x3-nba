package com.ftkj.x3.client.task.logic.sys;

import com.ftkj.cfg.MMatchDivisionBean;
import com.ftkj.cfg.MMatchLevBean;
import com.ftkj.cfg.MMatchLevBean.LevType;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.MainMatchConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.match.MainMatch;
import com.ftkj.manager.match.MainMatch.ChampionshipMatchTemp;
import com.ftkj.manager.match.MainMatch.ChampionshipTarget;
import com.ftkj.manager.match.MainMatchDivision;
import com.ftkj.manager.match.MainMatchLevel;
import com.ftkj.manager.match.TeamMainMatch;
import com.ftkj.proto.CommonPB.TeamSimpleData;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.MainMatchPB.MMatchAllResp;
import com.ftkj.proto.MainMatchPB.MMatchBaseResp;
import com.ftkj.proto.MainMatchPB.MMatchBuyNumResp;
import com.ftkj.proto.MainMatchPB.MMatchChampionshipResp;
import com.ftkj.proto.MainMatchPB.MMatchChampionshipTeamResp;
import com.ftkj.proto.MainMatchPB.MMatchDivResp;
import com.ftkj.proto.MainMatchPB.MMatchLevelResp;
import com.ftkj.proto.MainMatchPB.MMatchQuickMatchPushResp;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.ServiceCode;
import com.ftkj.x3.client.model.ClientTeamMainMatch;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.logic.LogicTask;
import com.ftkj.x3.client.task.logic.team.TeamClient;
import com.ftkj.xxs.client.net.XxsUserClient.AsyncWaitLatch;
import com.ftkj.xxs.net.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主线赛程 client.
 *
 * @author luch
 */
@Component
public class MainMatchClient extends LogicTask {
    private static final Logger log = LoggerFactory.getLogger(MainMatchClient.class);
    @Autowired
    private TeamClient teamClient;

    private int lastLevelRid = 0;
    private int lastDivId = 0;

    public static void main(String[] args) {
        new MainMatchClient().run();
    }

    @Override
    protected Ret run0(String[] args) {
        UserClient uc = loginMainAccount();
        ClientUser cu = uc.user();
        return moduleTest(uc, cu);
    }

    private Ret moduleTest(UserClient uc, ClientUser cu) {
        info(uc, cu);
        return succ();
    }

    /** 赛程信息 */
    public Ret info(UserClient uc, ClientUser cu) {
        int initId = ConfigConsole.global().mMatchDefaultOpenLev;
        MMatchLevBean initLev = MainMatchConsole.getLevBean(initId);
        teamClient.upgradeLev(uc, cu, initLev.getTeamLevel());

        AsyncWaitLatch awl = uc.createAysnWaitLatch(code(ServiceCode.MMatch_Info_Push));
        Message msg = uc.writeAndGet(createReq(ServiceCode.MMatch_Info));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} mmatch info. fail {}", uc.tid(), ret(resp));
            return ret(resp);
        }
        uc.waitAysnLatchRelease(awl);
        log.info("tid {} mmatch info succ", uc.tid());
        return succ();
    }

    /** 开始比赛 */
    public Ret startMatch(UserClient uc, ClientUser cu, int levelrid) {
        MMatchLevBean levelb = MainMatchConsole.getLevBean(levelrid);
        if (levelb.getTeamLevel() > cu.getTeam().getLevel()) {
            teamClient.upgradeLev(uc, cu, levelb.getTeamLevel());
        }
        TeamMainMatch tmm = cu.getTeamMainMatch();
        MainMatch mm = tmm.getMainMatch();
        if (mm.getMatchNum() <= 0) {
            Ret ret = buyNum(uc, cu);
            if (ret.isErr()) {
                return ret;
            }
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.MMatch_Start_Match, levelrid));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} start match levrid {}. fail {}", uc.tid(), levelrid, ret(resp));
            return ret(resp);
        }
        log.info("tid {} start match levrid {} succ", uc.tid(), levelrid);
        if (levelb.isRegular() || mm.getChampionshipWinNum() <= 0) {
            mm.setMatchNum(mm.getMatchNum() - 1);
        }
        return succ();
    }

    /** 扫荡 */
    public Ret quickMatch(UserClient uc, ClientUser cu, int levelrid) {
        MMatchLevBean levelb = MainMatchConsole.getLevBean(levelrid);
        if (levelb.getTeamLevel() > cu.getTeam().getLevel()) {
            teamClient.upgradeLev(uc, cu, levelb.getTeamLevel());
        }
        MainMatch mm = cu.getTeamMainMatch().getMainMatch();
        if (mm.getMatchNum() <= 0) {
            Ret ret = buyNum(uc, cu);
            if (ret.isErr()) {
                return ret;
            }
        }
        Ret ret = uc.reqCommon(uc, cu, ServiceCode.MMatch_Quick_Match, ServiceCode.MMatch_Quick_Match_Push, levelrid);
        if (ret.isSucc()) {
            mm.setMatchNum(mm.getMatchNum() - 1);
        }
        return ret;
    }

    /** 获取锦标赛信息 */
    public Ret championshipInfo(UserClient uc, ClientUser cu, int levelrid) {
        MMatchLevBean levelb = MainMatchConsole.getLevBean(levelrid);
        if (levelb.getTeamLevel() > cu.getTeam().getLevel()) {
            teamClient.upgradeLev(uc, cu, levelb.getTeamLevel());
        }
        Message msg = uc.writeAndGet(createReq(ServiceCode.MMatch_Championship_Info, levelrid));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} ChampionshipInfo levrid {}. fail {}", uc.tid(), levelrid, ret(resp));
            return ret(resp);
        }
        log.info("tid {} ChampionshipInfo levrid {} succ", uc.tid(), levelrid);
        return succ();
    }

    /** 购买挑战次数 */
    public Ret buyNum(UserClient uc, ClientUser cu) {
        ClientTeamMainMatch tmm = cu.getTeamMainMatch();
        int currNum = tmm.getMainMatch().getMatchNum();
        int maxNum = ConfigConsole.getIntVal(EConfigKey.MMatch_NUM_MAX, Integer.MAX_VALUE);
        if (currNum >= maxNum) {
            return succ();
        }
        Ret ret = teamClient.gmSetBuyNumAndAddCurrency(uc, cu, TeamNumType.Main_Match_Num, tmm.getBuyMatchNum(), 0,
            tnb -> tmm.setBuyMatchNum(0));
        if (ret.isErr()) {
            return ret;
        }

        Message msg = uc.writeAndGet(createReq(ServiceCode.MMatch_Buy_Match_Num));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} buyNum. fail {}", uc.tid(), ret(resp));
            return ret(resp);
        }
        log.info("tid {} buyNum succ {}", uc.tid(), tmm.getMainMatch().getMatchNum());
        return succ();
    }

    public void championshipInfoPush(UserClient uc, ClientUser cu, Message msg) {
        MMatchChampionshipResp resp = parseFrom(MMatchChampionshipResp.getDefaultInstance(), msg);
        int cslevelRid = cu.getTeamMainMatch().getMainMatch().getChampionshipLevelRid();
        if (isSimpleLog()) {
            log.info("tid {} championshipInfoPush. curr championship levelrid {} resprid {} size {}", uc.tid(),
                cslevelRid, resp.getLevRid(), resp.getTargetsCount());
        } else {
            log.info("tid {} championshipInfoPush.curr championship levelrid {} resp {}", uc.tid(),
                cslevelRid, shortDebug(resp));
        }
    }

    /** 购买挑战次数成功推送 */
    public void basePush(UserClient uc, ClientUser cu, Message msg) {
        MMatchBuyNumResp resp = parseFrom(MMatchBuyNumResp.getDefaultInstance(), msg);
        log.info("tid {} basePush.resp {}", uc.tid(), shortDebug(resp));
        MMatchBaseResp base = resp.getBase();
        MainMatch mm = cu.getTeamMainMatch().getMainMatch();
        setBase(mm, base);
        cu.getTeamMainMatch().setBuyMatchNum(base.getBuyMatchNum());
    }

    /** 扫荡推送 */
    public void quickMatchPush(UserClient uc, ClientUser cu, Message msg) {
        MMatchQuickMatchPushResp resp = parseFrom(MMatchQuickMatchPushResp.getDefaultInstance(), msg);
        log.info("tid {} quickMatchPush. resp {}", uc.tid(), shortDebug(resp));
    }

    /** 获得的装备模块的经验推送 */
    public void equipExpPush(UserClient uc, ClientUser cu, Message msg) {
        MMatchQuickMatchPushResp resp = parseFrom(MMatchQuickMatchPushResp.getDefaultInstance(), msg);
        log.info("tid {} equipExpPush. resp {}", uc.tid(), shortDebug(resp));
    }

    /** 使用gm命令直接开启到指定关卡 */
    public Ret gmEnablelevel(UserClient uc, ClientUser cu, int levelrid) {
        Map<Integer, MainMatchLevel> levels = cu.getTeamMainMatch().getLevels();
        Map<Integer, MainMatchDivision> divs = cu.getTeamMainMatch().getDivs();
        if (levels.containsKey(levelrid)) {
            return succ();
        }
        int maxStar = ConfigConsole.global().mMatchMaxStar;
        List<Integer> levelRids = new ArrayList<>();
        List<Integer> enableDivs = new ArrayList<>();
        MMatchLevBean le = MainMatchConsole.getLevBean(levelrid);
        while (le != null &&
            !levels.containsKey(le.getId()) &&
            le.getEnablePreId() > 0) {
            levelRids.add(le.getId());
            enableDivs.add(le.getDivId());
            if (le.getEnablePreId() == 0) {
                break;
            }
            le = MainMatchConsole.getLevBean(le.getEnablePreId());
            if (le == null) {
                break;
            }
        }

        Message msg = uc.writeAndGet(createGM(GmCommand.MainMatch_Enable_Lev_Full_Star, levelRids.toArray()));
        DefaultData resp = parseFrom(msg);
        if (uc.isError(resp)) {
            log.warn("tid {} gmEnablelevel levelrids {} star {} divs {}. fail {}", uc.tid(), levelRids, maxStar, enableDivs, ret(resp));
            return ret(resp);
        }
        log.info("tid {} gmEnablelevel levelrid {} star {} divs {}. succ", uc.tid(), levelRids, maxStar, enableDivs);
        for (Integer levelRid : levelRids) {
            levels.put(levelRid, new MainMatchLevel(levelRid, maxStar));

        }
        for (Integer divId : enableDivs) {
            if (!divs.containsKey(divId)) {
                divs.put(divId, new MainMatchDivision(divId));
            }
        }
        return succ();
    }

    public void infoPush(UserClient uc, ClientUser cu, Message msg) {
        MMatchAllResp resp = parseFrom(MMatchAllResp.getDefaultInstance(), msg);
        if (isSimpleLog()) {
            MMatchChampionshipResp tresp = resp.getChampionship();
            log.info("tid {} mmatch info push. levs {} levels {} Championship rid {} size {}", uc.tid(), resp.getDivsCount(),
                resp.getLevsCount(), tresp.getLevRid(), tresp.getTargetsCount());
        } else {
            log.info("tid {} mmatch info push. {}", uc.tid(), shortDebug(resp));
        }
        ClientTeamMainMatch tmm = createTeamMainMatch(cu.tid(), resp);
        cu.setTeamMainMatch(tmm);
    }

    private ClientTeamMainMatch createTeamMainMatch(long tid, MMatchAllResp resp) {
        MainMatch mainMatch = createMainMatch(tid, resp.getMatchBase(), resp.getChampionship());
        Map<Integer, MainMatchDivision> divs = createDivs(resp.getDivsList());
        Map<Integer, MainMatchLevel> levels = createlevels(resp.getLevsList());
        ClientTeamMainMatch tmm = new ClientTeamMainMatch(tid, mainMatch, divs, levels);
        tmm.setBuyMatchNum(resp.getMatchBase().getBuyMatchNum());
        return tmm;
    }

    private MainMatch createMainMatch(long tid, MMatchBaseResp br, MMatchChampionshipResp tr) {
        MainMatch mm = new MainMatch();
        mm.setTeamId(tid);
        setBase(mm, br);
        setChampionship(mm, tr);
        return mm;
    }

    private void setBase(MainMatch mm, MMatchBaseResp br) {
        mm.setMatchNum(br.getMatchNum());
        mm.setMatchNumLastUpTime(br.getMatchNumLastUpTime());

        mm.setLastLevelRid(br.getRegularLastLevRid());
        mm.setLastMatchEndTime(br.getRegularLastMatchEndTime());
    }

    private void setChampionship(MainMatch mm, MMatchChampionshipResp tr) {

        mm.setChampionshipLevelRid(tr.getLevRid());
        mm.setChampionshipWinNum(tr.getWinNum());

        List<ChampionshipTarget> ChampionshipTargets = new ArrayList<>(tr.getTargetsCount());
        for (TeamSimpleData tsd : tr.getTargetsList()) {
            ChampionshipTargets.add(new ChampionshipTarget(tsd.getTeamId()));
        }
        mm.setChampionshipTargets(ChampionshipTargets);

        List<List<ChampionshipMatchTemp>> wins = new ArrayList<>(tr.getWinTeamsCount());
        for (MMatchChampionshipTeamResp resp : tr.getWinTeamsList()) {
            log.trace("tid {} Championship resp. rid {} winnum {} winsize {}, lev {} teamcount {}",
                mm.getTeamId(), tr.getLevRid(), tr.getWinNum(), tr.getWinTeamsCount(), resp.getLev(), resp.getTeamsCount());
            List<ChampionshipMatchTemp> win1 = new ArrayList<>(resp.getTeamsCount());
            for (int i = 0; i < resp.getTeamsCount(); i += 2) {
                win1.add(new ChampionshipMatchTemp(new ChampionshipTarget(resp.getTeams(i), 0),
                    new ChampionshipTarget(resp.getTeams(i + 1), 0)));
            }
            wins.add(win1);
        }
        mm.setTempChampionshipWins(wins);
    }

    private Map<Integer, MainMatchLevel> createlevels(List<MMatchLevelResp> levResps) {
        Map<Integer, MainMatchLevel> levs = new ConcurrentHashMap<>(levResps.size());
        for (MMatchLevelResp lr : levResps) {
            levs.put(lr.getRId(), new MainMatchLevel(lr.getRId(), lr.getStar()));
        }
        return levs;
    }

    private Map<Integer, MainMatchDivision> createDivs(List<MMatchDivResp> divResps) {
        Map<Integer, MainMatchDivision> divs = new ConcurrentHashMap<>(divResps.size());
        for (MMatchDivResp dr : divResps) {
            MainMatchDivision div = new MainMatchDivision();
            div.setResourceId(dr.getRId());
            for (Integer starAwardId : dr.getStarAwardsList()) {
                div.addStarAward(starAwardId);
            }
            divs.put(dr.getRId(), div);
        }
        return divs;
    }

    private static final EBattleType[] battleTypes = new EBattleType[]{EBattleType.Main_Match_Championship, EBattleType.Main_Match_Normal};

    public EBattleType[] battleTypes() {
        return battleTypes;
    }

    /** 获取策划配置的最后一个常规赛关卡 */
    public int getLastCfgRegularDiv() {
        if (lastDivId > 0) {
            return lastDivId;
        }
        int initrid = ConfigConsole.global().mMatchDefaultOpenLev;
        MMatchLevBean levelb = MainMatchConsole.getLevBean(initrid);
        MMatchDivisionBean devb = MainMatchConsole.getDivBean(levelb.getDivId());

        outer:
        while (!devb.getNextDivs().isEmpty()) {
            int elnum = 0;
            for (Integer lev : devb.getNextDivs()) {
                MMatchDivisionBean nlev = MainMatchConsole.getDivBean(lev);
                if (MainMatchConsole.getLevsOfDiv(lev) == null) {
                    break outer;
                }
                //                log.trace("nl {} lev {} next {}", devb.getId(), lev, nlev.getNextLevs());
                devb = nlev;
            }
            if (devb.getNextDivs().size() == elnum) {
                break;
            }
        }

        lastDivId = devb.getId();
        log.info("lastRegularDivId {}", lastDivId);
        return lastDivId;
    }

    /** 获取策划配置的最后一个常规赛关卡 */
    public int getLastCfgRegularLev() {
        if (lastLevelRid > 0) {
            return lastLevelRid;
        }
        MMatchLevBean levb = getLastLevelBean(getLastCfgRegularDiv(), LevType.Regular);
        lastLevelRid = levb.getId();
        log.info("lastRegularlevelRid {}", lastLevelRid);
        return lastLevelRid;
    }

    private MMatchLevBean getLastLevelBean(int div, LevType lt) {
        MMatchLevBean nb = null;
        for (MMatchLevBean levelb : MainMatchConsole.getLevsOfDiv(div)) {
            //            log.trace("div {} nt {} level {} type {} next {}", div, nt, levelb.getId(), levelb.getType(), levelb.getNextlevels());
            if (levelb.getNextLevs().isEmpty()) {
                nb = levelb;
            } else {
                for (Integer nextlevelRid : levelb.getNextLevs()) {
                    MMatchLevBean nextlevel = MainMatchConsole.getLevBean(nextlevelRid);
                    if (nextlevel.getType() == lt) {
                        nb = nextlevel;
                    }
                }
            }
        }
        return nb;
    }

    public int getLastEnableLevel(TeamMainMatch tmm) {
        final int initRid = ConfigConsole.global().mMatchDefaultOpenLev;
        Map<Integer, MainMatchLevel> levels = tmm.getLevels();
        Map<Integer, MainMatchDivision> divs = tmm.getDivs();

        log.trace("tid {} all enabled divs {} levels {}", tmm.getTeamId(), divs.keySet(), levels.keySet());
        MMatchLevBean levelb = null;
        //常规赛查找(线性向前)
        int lastRegRid = getLastCfgRegularLev();
        levelb = MainMatchConsole.getLevBean(lastRegRid);
        MainMatchLevel level = levels.get(lastRegRid);
        int count = 0;
        while (count <= 2048 && level == null && levelb.getEnablePreId() > 0) {
            levelb = MainMatchConsole.getLevBean(levelb.getEnablePreId());
            level = levels.get(levelb.getEnablePreId());
            count++;
        }
        if (count >= 2048) {
            log.error("tid {} loop max {}", tmm.getTeamId(), count);
        }
        log.trace("tid {} last reg level {} levelb {} next {} count {}",
            tmm.getTeamId(), level, levelb.toSimpleString(), levelb.getNextLevs(), count);
        MMatchLevBean nextlevelb = getNextLevelBean(level, levelb);
        if (nextlevelb != null) {
            levelb = nextlevelb;
        }
        log.trace("tid {} last levelb {}", tmm.getTeamId(), levelb.toSimpleString());
        return levelb != null ? levelb.getId() : initRid;
    }

    private MMatchLevBean getNextLevelBean(MainMatchLevel level, MMatchLevBean levelb) {
        MMatchLevBean ret = null;
        if (level != null) {
            log.trace("level {} star {} rid {} nextlevel {}", level.getResourceId(), level.getStar(), levelb.getId(), levelb.getNextLevs());
            for (Integer nextRid : levelb.getNextLevs()) {
                MMatchLevBean nextlevel = MainMatchConsole.getLevBean(nextRid);
                if (level.getStar() >= nextlevel.getEnablePreStar()) {
                    ret = nextlevel;
                    break;
                }
            }
        }
        return ret;
    }
}
