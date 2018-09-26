package com.ftkj.manager.logic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.MMatchDivisionBean;
import com.ftkj.cfg.MMatchDivisionBean.StarAward;
import com.ftkj.cfg.MMatchLevBean;
import com.ftkj.cfg.MMatchLevBean.LevType;
import com.ftkj.cfg.MMatchLevBean.SpecialHandleMatch;
import com.ftkj.cfg.MMatchLevBean.Star;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.cfg.battle.BattleCustomBean.CustomBean;
import com.ftkj.console.BattleCustomConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.MainMatchConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.dao.logic.MainMatchDao;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.StagePassParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.handle.AutoBattleQuickHandle;
import com.ftkj.manager.battle.handle.BattleDesignCustomHandle;
import com.ftkj.manager.battle.handle.BattleMainMatch;
import com.ftkj.manager.battle.handle.BattleMainMatch.PreChampionshipAvatar;
import com.ftkj.manager.battle.model.ActionStatistics;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.battle.model.PlayerStepActStat;
import com.ftkj.manager.battle.model.ReadOnlyActionStats;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.match.MainMatch;
import com.ftkj.manager.match.MainMatch.ChampionshipMatchTemp;
import com.ftkj.manager.match.MainMatch.ChampionshipTarget;
import com.ftkj.manager.match.MainMatchDivision;
import com.ftkj.manager.match.MainMatchLevel;
import com.ftkj.manager.match.MainMatchSystemLev;
import com.ftkj.manager.match.MainMatchSystemLev.SysChampionshipTeam;
import com.ftkj.manager.match.MainMatchSystemLev.SysLevRank;
import com.ftkj.manager.match.TeamMainMatch;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.MainMatchPB.MMatchAllResp;
import com.ftkj.proto.MainMatchPB.MMatchBaseResp;
import com.ftkj.proto.MainMatchPB.MMatchBuyNumResp;
import com.ftkj.proto.MainMatchPB.MMatchChampionshipResp;
import com.ftkj.proto.MainMatchPB.MMatchChampionshipResp.Builder;
import com.ftkj.proto.MainMatchPB.MMatchChampionshipTeamResp;
import com.ftkj.proto.MainMatchPB.MMatchDivResp;
import com.ftkj.proto.MainMatchPB.MMatchLevelResp;
import com.ftkj.proto.MainMatchPB.MMatchQuickMatchPushResp;
import com.ftkj.proto.MainMatchPB.MMatchReceiveStarAwardResp;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.FixedArrayList;
import com.ftkj.util.ListsUtil;
import com.ftkj.util.Random;
import com.google.common.collect.ImmutableSet;

/**
 * 主线赛程.
 *
 * @author luch
 */
public class MainMatchManager extends AbstractBaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(MainMatchManager.class);
    /** com.ftkj.manager.logic.MainMatchManager.Calcstar */
    private static final Logger starLog = LoggerFactory.getLogger("com.ftkj.manager.logic.MainMatchManagerCalcstar");
    @IOC
    private MainMatchDao mainMatchDao;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private LocalBattleManager localBattleManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private JedisUtil redis;
    /** 比赛结束处理 */
    private BattleEnd battleEnd = this::endMatch0;
    /** 操作模块 */
    private ModuleLog mc = ModuleLog.getModuleLog(EModuleCode.MainMatch, "主线赛程");
    /** 球队主线赛程信息. map[team.id, MainMatch] */
    private ConcurrentMap<Long, TeamMainMatch> teams = new ConcurrentHashMap<>();
    /** 系统主线赛程信息. map[关卡配置id, MainMatch]. 目前放在 redis */
    private ConcurrentMap<Integer, MainMatchSystemLev> sys = new ConcurrentHashMap<>();

    @SuppressWarnings("unused")
    public MainMatchManager() {
        super();
    }

    @SuppressWarnings("WeakerAccess")
    public MainMatchManager(boolean init) {
        super(init);
    }

    /** 系统启动时, 加载关卡排行榜 */
    @Override
    public void instanceAfter() {
        redis.del("main_match_sys_v4_");//old key
        Map<Integer, MainMatchSystemLev> map = redis.getMapAllKeyValues(RedisKey.Main_Match_Sys);
        sys.putAll(map);
        if (log.isDebugEnabled()) {
            log.debug("mmatch sys init. syslevs lev size {}", sys.size());
        }
    }

    /** 获取球队全部主线赛程信息, 没有则初始化 */
    private TeamMainMatch getTMM(long teamId) {
        TeamMainMatch tmm = teams.get(teamId);
        if (tmm == null) {
            MainMatch mm = mainMatchDao.findMMatch(teamId);
            List<MainMatchDivision> divs = mainMatchDao.findDivs(teamId);
            List<MainMatchLevel> levs = mainMatchDao.findLevs(teamId);
            setChampionshipTargetNpc(mm);
            tmm = new TeamMainMatch(teamId, mm, divs, levs);
            TeamMainMatch old = teams.putIfAbsent(teamId, tmm);
            if (old != null) {
                tmm = old;
            }
            LocalDate now = LocalDate.now();
            tmm.setLimitPropsDay(now);
            String limitPropsKey = RedisKey.getDayKey(teamId, RedisKey.Main_Match_Daily_Prop, now);
            tmm.setLimitPropsRedisKey(limitPropsKey);
            tmm.setLimitProps(redis.hgetallIKIV(limitPropsKey));
            GameSource.checkGcData(teamId);
        }
        return tmm;
    }

    private Map<Integer, Integer> getLimitProps(TeamMainMatch tmm) {
        LocalDate now = LocalDate.now();
        if (tmm.getLimitPropsDay() == null || tmm.getLimitProps() == null || !now.isEqual(tmm.getLimitPropsDay())) {
            tmm.setLimitPropsDay(now);
            String limitPropsKey = RedisKey.getDayKey(tmm.getTeamId(), RedisKey.Main_Match_Daily_Prop, now);
            tmm.setLimitPropsRedisKey(limitPropsKey);
            tmm.setLimitProps(new HashMap<>());
            return tmm.getLimitProps();
        }
        return tmm.getLimitProps();
    }

    /** 设置锦标赛关卡对手的替身npcid */
    private void setChampionshipTargetNpc(MainMatch mm) {
        if (mm == null || mm.getChampionshipLevelRid() <= 0 || mm.getChampionshipTargetSize() <= 0) {
            return;
        }
        MMatchLevBean levb = MainMatchConsole.getLevBean(mm.getChampionshipLevelRid());
        if (levb == null) {
            return;
        }
        List<ChampionshipTarget> targets = mm.getChampionshipTargets();
        for (int i = 0; i < targets.size(); i++) {
            ChampionshipTarget target = targets.get(i);
            if (target != null && target.getNpc() <= 0 && !GameSource.isNPC(target.getTeamId())) {
                Integer npc = ListsUtil.get(levb.getChampionNpc(), i);
                if (npc != null) {
                    target.setNpc(npc);
                    log.trace("mmatch getTmm. set target npc. tid {} target idx {} ttid {} npc {}", mm.getTeamId(),
                        i, target.getTeamId(), npc);
                }
            }
        }
    }

    /** 球队退出 */
    @Override
    public void offline(long teamId) {
        teams.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teams.remove(teamId);
    }

    /** 赛程信息 */
    @ClientMethod(code = ServiceCode.MMatch_Info)
    public void info() {
        long teamId = getTeamId();
        ErrorCode ret = info0(teamId);
        sendMsg(ret);
        log.debug("mmatch info. tid {} ret {}", teamId, ret);
    }

    /** 赛程信息 */
    private ErrorCode info0(long teamId) {
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        ErrorCode ret = checkInit(tmm);
        if (ret.isError()) {
            return ret;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm != null) {
            final int maxNumCfg = getCfgMaxNum();
            calcMathNum(mm, maxNumCfg, mm.getMatchNum());
        }

        sendMessage(teamId, allResp(tmm), ServiceCode.MMatch_Info_Push);
        if (mm != null) {
            calcAndUpdateEquipExp(teamId, mm);
        }
        return ErrorCode.Success;
    }

    /** 结算装备经验. 以分钟为单位结算奖励 */
    private void calcAndUpdateEquipExp(long tid, MainMatch mm) {
        if (mm == null || mm.getLastLevelRid() <= 0) {
            return;
        }
        final int itemId = ConfigConsole.global().mMatchEquipExpItem;
        if (itemId <= 0) {
            return;
        }
        final int maxSec = ConfigConsole.global().mMatchEquipExpMaxTime;
        final int levRid = mm.getLastLevelRid();
        final long curr = System.currentTimeMillis();
        final long lasttime = mm.getLastMatchEndTime();
        final int duration = (int) (curr - lasttime);
        final int unitMillis = DateTimeUtil.MINUTE;//以分钟为单位结算奖励
        if (duration < unitMillis) {
            return;
        }
        MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
        if (levb == null || levb.getEquipExp() <= 0) {
            return;
        }

        final int minutes = Math.min(maxSec * 1000 / unitMillis, duration / unitMillis);
        final int exp = levb.getEquipExp() * minutes;
        log.debug("mmatch calc equipexp. tid {} levrid {} curr {} last {} duration {} minuts {} maxsec {}. item {}. exp cfg {} final {}",
            tid, levRid, curr, lasttime, duration, minutes, maxSec, itemId, levb.getEquipExp(), exp);
        mm.setLastMatchEndTime(lasttime + minutes * unitMillis);
        mm.save();
        if (exp <= 0) {
            return;
        }
        PropSimple ps = new PropSimple(itemId, exp);
        propManager.addPropList(tid, Collections.singletonList(ps), true, mc);
        MMatchQuickMatchPushResp.Builder resp = quickMatchResp(levRid, mm);
        resp.addProps(PropManager.getPropData(ps));
        sendMessage(tid, resp.build(), ServiceCode.MMatch_Equip_Exp_Push);
    }

    /** 购买挑战次数 */
    @ClientMethod(code = ServiceCode.MMatch_Buy_Match_Num)
    public void buyMatchNum() {
        long teamId = getTeamId();
        ErrorCode ret = buyMatchNum0(teamId);
        sendMsg(ret);
        log.debug("mmatch buynum. tid {} ret {}", teamId, ret);
    }

    /** 购买挑战次数 */
    private ErrorCode buyMatchNum0(long teamId) {
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.MMatch_MM_Null;
        }
        final int maxNumCfg = getCfgMaxNum();
        //        final int numMoneyCfg = ConfigConsole.global().mMatchNumMoney;
        //        final TeamMoney money = moneyManager.getTeamMoney(teamId);
        final int currNum = mm.getMatchNum();
        if (currNum >= maxNumCfg) {
            return ErrorCode.MMatch_Num_Max;
        }

        ErrorCode ret = calcMathNum(mm, maxNumCfg, currNum);
        if (ret.isError()) {
            return ret;
        }
        ret = teamNumManager.consumeNumCurrency(teamId, TeamNumType.Main_Match_Num, 1, mc);
        if (ret.isError()) {
            return ret;
        }
        final int fnum = Math.min(maxNumCfg, currNum + 1);
        mm.setMatchNumLastUpTime(System.currentTimeMillis());
        mm.setMatchNum(fnum);
        mm.save();
        if (log.isDebugEnabled()) {
            log.debug("mmatch buynum. tid {}. num curr {} final {} buynum {}", teamId, currNum, fnum,
                teamNumManager.getUsedNum(teamId, TeamNumType.Main_Match_Num));
        }
        MMatchBuyNumResp.Builder resp = MMatchBuyNumResp.newBuilder();
        resp.setBase(baseResp(mm));
        sendMessage(teamId, resp.build(), ServiceCode.MMatch_Buy_Match_Num_Push);
        return ErrorCode.Success;
    }

    /**
     * 主线赛程当前可挑战次数
     *
     * @param teamId
     * @return
     */
    public int getMatchNum(long teamId) {
        TeamMainMatch tmm = getTMM(teamId);
        MainMatch mm = tmm.getMainMatch();
        return mm.getMatchNum();
    }

    /** 增加挑战次数 */
    public ErrorCode addMatchNum(long teamId, int addNum) {
        if (addNum == 0) {
            return ErrorCode.Success;
        }
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.MMatch_MM_Null;
        }
        final int maxNumCfg = getCfgMaxNum();
        //        final int numMoneyCfg = ConfigConsole.global().mMatchNumMoney;
        //        final TeamMoney money = moneyManager.getTeamMoney(teamId);
        final int currNum = mm.getMatchNum();
        if (currNum >= maxNumCfg) {
            return ErrorCode.MMatch_Num_Max;
        }
        final int fnum = Math.min(maxNumCfg, currNum + addNum);
        //        mm.setMatchNumLastUpTime(System.currentTimeMillis());
        mm.setMatchNum(fnum);
        mm.save();
        if (log.isDebugEnabled()) {
            log.debug("mmatch addMatchNum. tid {}. num curr {} final {} addnum {}", teamId, currNum, fnum, addNum);
        }
        MMatchBuyNumResp.Builder resp = MMatchBuyNumResp.newBuilder();
        resp.setBase(baseResp(mm));
        sendMessage(teamId, resp.build(), ServiceCode.MMatch_Buy_Match_Num_Push);
        return ErrorCode.Success;
    }

    /** 计算冷却时间增加的挑战次数 */
    private ErrorCode calcMathNum(MainMatch mm, int maxNumCfg, int currNum) {
        final int numCdCfg = ConfigConsole.global().mMatchNumCd;
        final long curr = System.currentTimeMillis();

        final long lastUpTime = mm.getMatchNumLastUpTime();
        final long duration = curr - lastUpTime;
        if (log.isTraceEnabled()) {
            log.trace("mmatch calcmnum. tid {} lasttime {} {} duration {} currnum {}",
                mm.getTeamId(), lastUpTime, new Date(lastUpTime), duration, currNum);
        }
        if (duration >= numCdCfg) {
            final int num = (int) (duration / numCdCfg);
            final int fnum = Math.min(currNum + num, maxNumCfg);
            final long lastTime = fnum >= maxNumCfg ? curr : (lastUpTime + num * numCdCfg);

            mm.setMatchNum(fnum);
            mm.setMatchNumLastUpTime(lastTime);
            mm.save();
            if (log.isDebugEnabled()) {
                log.debug("mmatch calcmnum. tid {}. num curr {} max {} add {} final {} cdcfg {}," +
                        " time curr {} last {} duration {} final {} {}",
                    mm.getTeamId(), currNum, maxNumCfg, num, fnum, numCdCfg,
                    curr, lastUpTime, duration, lastTime, new Date(lastTime));
            }
            if (mm.getMatchNum() >= maxNumCfg) {
                return ErrorCode.MMatch_Num_Max_1;
            }
        }
        return ErrorCode.Success;
    }

    /** 领取星级礼包 */
    @ClientMethod(code = ServiceCode.MMatch_Receive_Star_Award)
    public void receiveStarAward(int divId, int starId) {
        long teamId = getTeamId();
        ErrorCode ret = receiveStarAward0(teamId, divId, starId);
        sendMsg(ret);
        log.debug("mmatch receiveStarAward. tid {} divId {} starId {} ret {}", teamId, divId, starId, ret);
    }

    /** 领取星级礼包 */
    private ErrorCode receiveStarAward0(long teamId, int divId, int starId) {
        MMatchDivisionBean divb = MainMatchConsole.getDivBean(divId);
        if (divb == null) {
            return ErrorCode.MMatch_Div_Bean;
        }
        StarAward sa = divb.getStarAwards(starId);
        if (sa == null) {
            return ErrorCode.MMatch_Div_Bean_Star;
        }
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatchDivision div = tmm.getDivs().get(divId);
        if (div == null) {
            return ErrorCode.MMatch_Div_Null;
        }
        if (div.hasStarAward(starId)) {
            return ErrorCode.MMatch_Div_Star_Received;
        }
        final int starNum = MainMatchConsole.sumDivStarNum(tmm.getLevels(), divId);
        log.debug("mmatch receiveStarAward. tid {} divId {}. star id {} num {} cfg {}", teamId, divId, starId, starNum, sa);
        if (starNum < sa.getNum()) {
            return ErrorCode.MMatch_Div_Star_Num;
        }

        div.addStarAward(starId);
        div.save();
        List<PropSimple> props = propManager.addPropList(teamId, sa.getProps(), Collections.singletonList(sa.getDrop()), true, mc);
        log.trace("mmatch receiveStarAward. calc star award. tid {} divId {} props {}", teamId, divId, props);
        MMatchReceiveStarAwardResp.Builder resp = MMatchReceiveStarAwardResp.newBuilder();
        resp.setDiv(divResp(div));
        resp.addAllProps(PropManager.getPropSimpleListData(props));
        sendMessage(teamId, resp.build(), ServiceCode.MMatch_Receive_Star_Award_Push);
        return ErrorCode.Success;
    }

    /** 开始比赛 */
    @ClientMethod(code = ServiceCode.MMatch_Start_Match)
    public void startMatch(int levRid) {
        long teamId = getTeamId();
        ErrorCode ret = startMatch0(teamId, levRid);
        sendMsg(ret);
        log.debug("mmatch startMatch. tid {} levRid {} ret {}", teamId, levRid, ret);
        //taskManager.updateTask(teamId, ETaskCondition.主线赛程, 1, EModuleCode.主线赛程.getName());
    }

    /** 开始比赛 */
    private ErrorCode startMatch0(long teamId, final int levRid) {
        if (isInBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        MMatchLevBean levelb = MainMatchConsole.getLevBean(levRid);
        if (levelb == null) {
            return ErrorCode.MMatch_Lev_Bean;
        }
        Team team = teamManager.getTeam(getTeamId());
        if (team.getLevel() < levelb.getTeamLevel()) {
            return ErrorCode.Team_Level;
        }
        if (levelb.isChampionship()) {//休赛期
            int stopTime = ConfigConsole.global().mMatchCsStopTime;
            if (System.currentTimeMillis() > DateTimeUtil.midnight() + stopTime) {
                return ErrorCode.MMatch_Num_Time_Stop;
            }
        }
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        ErrorCode ret = checkInit(tmm);
        if (ret.isError()) {
            return ret;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.MMatch_MM_Null;
        }
        //校验赛区
        MMatchDivisionBean divb = MainMatchConsole.getDivBean(levelb.getDivId());
        if (divb == null) {
            return ErrorCode.MMatch_Div_Bean;
        }
        MainMatchDivision div = tmm.getDivs().get(levelb.getDivId());
        if (div == null) {
            ret = MainMatchConsole.checkPreDiv(tmm, divb);
            if (ret.isError()) {
                return ret;
            }
            addNewDiv(levelb, tmm);
        }
        //校验关卡
        if (levelb.getEnablePreId() > 0) {//本关卡有开启条件
            MainMatchLevel preLev = tmm.getLevels().get(levelb.getEnablePreId());
            if (preLev == null) {
                return ErrorCode.MMatch_Lev_Pre_Null;
            }
            if (levelb.getEnablePreStar() > 0 && preLev.getStar() < levelb.getEnablePreStar()) {
                return ErrorCode.MMatch_Lev_Pre_Star;
            }
        }

        MainMatchLevel lev = tmm.getLevels().get(levRid);
        if (lev == null) {
            lev = addNewLev(levelb, 0, tmm);
        }
        //扣除次数
        final int maxNumCfg = getCfgMaxNum();
        calcMathNum(mm, maxNumCfg, mm.getMatchNum());
        final int currNum = mm.getMatchNum();
//        final boolean inChampionshipMatch = levelb.isChampionship() && mm.getChampionshipLevelRid() > 0;
        if (currNum <= 0) {//2018年8月23日10:59:19 修改锦标赛每次挑战都扣次数 
            return ErrorCode.MMatch_Match_Num;
        }

        ChampionshipTarget away;//获取对手id
        if (levelb.isChampionship()) {//玩家
            if (mm.getChampionshipLevelRid() > 0 && levRid != mm.getChampionshipLevelRid()) {//和上次不是同一个关卡, 重置
                log.debug("mmatch start. tid {} championship levrid pre {} != curr {}", teamId, mm.getChampionshipLevelRid(), levRid);
                cleanChampionshipTarget(mm);
            }
            mm.setChampionshipLevelRid(levRid);
            away = getNextChampionshipTarget(levelb, mm);
            if (away == null) {
                return ErrorCode.MMatch_Match_Target;
            }
            mm.setChampionshipLastMatchStartTime(System.currentTimeMillis());
        } else {//npc
            away = new ChampionshipTarget(levelb.getNpcId(), levelb.getNpcId());
        }
//        if (!inChampionshipMatch) {
        if (maxNumCfg == currNum) {
            mm.setMatchNumLastUpTime(System.currentTimeMillis());
        }
        mm.setMatchNum(currNum - 1);
        lev.setMatchCount(lev.getMatchCount() + 1);
        lev.save();
//        }
        mm.save();
        startBattle(teamId, levelb, lev, away);
        return ErrorCode.Success;
    }

    /** 是否在比赛中 */
    private boolean isInBattle(long teamId) {
        return TeamStatus.inBattle(teamStatusManager.get(teamId), EBattleType.Main_Match_Normal, EBattleType.Main_Match_Championship);
    }

    /** 开始比赛 */
    private void startBattle(long teamId, MMatchLevBean levb, MainMatchLevel lev, ChampionshipTarget away) {
        BattleSource bs = localBattleManager.buildBattle(bt(levb.getType()), teamId, away.getTeamId(), teamId);
        BattleContxt bc = localBattleManager.defaultContext(battleEnd);

        BattleAttribute ba = new BattleAttribute(teamId);
        ba.addVal(EBattleAttribute.Main_Match_LevRid, levb.getId());
        if (LevType.Championship.equals(levb.getType())) {//玩家
            PreChampionshipAvatar pta = new PreChampionshipAvatar(away.getTeamId(), away.getNpc());
            ba.addVal(EBattleAttribute.Main_Match_Away_Npc_Avatar, pta);
        }
        bs.addBattleAttribute(ba);
        SpecialHandleMatch shm = levb.getSpecialHandle(lev.getMatchCount());
        //        if (lev.getResourceId() == 1101 || lev.getResourceId() == 1102) {// 强制第一场定制化比赛, 测试完毕后删除
        //            shm = levb.getSpecialHandle(1);
        //        }
        if (log.isDebugEnabled()) {
            log.debug("mmatch start. tid {} levrid {} away tid {} npcid {} mc {} sh {}", teamId, lev.getId(),
                away.getTeamId(), away.getNpc(), lev.getMatchCount(), shm != null ? shm.getBattleCustomId() : 0);
        }
        BaseBattleHandle bh = null;
        if (shm != null) {
            CustomBean cb = BattleCustomConsole.getBean(shm.getBattleCustomId());
            if (cb != null) {
                bh = new BattleDesignCustomHandle(bs, cb);
            }
        }
        if (bh == null) {
            bh = new BattleMainMatch(bs);
        }

        localBattleManager.initBattleWithContext(bh, bs, bc);
        localBattleManager.start(bs, bc, bh);
    }

    /** 比赛结束 */
    private void endMatch0(BattleSource bs) {
        try {
            endMatch1(bs);
        } catch (Exception e) {
            log.error("mmatch " + e.getMessage(), e);
        }
    }

    /** 比赛结束 */
    private void endMatch1(BattleSource bs) {
        EndReport report = bs.getEndReport();
        long teamId = report.getHomeTeamId();
        BattleAttribute ba = bs.getAttributeMap(teamId);
        int levrid = ba.getVal(EBattleAttribute.Main_Match_LevRid);
        log.debug("mmatch end match. htid {} atid {} levrid {} score {}:{}", report.getHomeTeamId(), report.getAwayTeamId(),
            levrid, report.getHomeScore(), report.getAwayScore());
        MMatchLevBean levb = MainMatchConsole.getLevBean(levrid);
        if (GameSource.isNPC(teamId)) {
            localBattleManager.sendEndMain(bs, true);
            return;
        }
        TeamMainMatch tmm = getTMM(teamId);
        if (levb == null || tmm == null) {
            localBattleManager.sendEndMain(bs, true);
            return;
        }
        MainMatchLevel lev = tmm.getLevels().get(levrid);
        if (lev == null) {
            return;
        }
        final boolean win = report.getWinTeamId() == report.getHomeTeamId();
        if (win) {//胜利
            endMatchWin1(teamId, bs, levb, tmm, lev);
            try {
                EventBusManager.post(EEventType.主线赛程通关, new StagePassParam(teamId, levrid));
                taskManager.updateTask(report.getHomeTeamId(), ETaskCondition.Main_Match, 1, "" + levb.getId());
            } catch (Exception e) {
                log.error("mmatch " + e.getMessage(), e);
            }
        } else {//失败
            MainMatch mm = tmm.getMainMatch();
            if (levb.isChampionship() && mm != null) {//如果是锦标赛, 失败时重置对手, 结算奖励
                log.debug("mmatch end match. home tid {} lose match. win num {}", teamId, mm.getChampionshipWinNum());
                int currStar = mm.getChampionshipWinNum();
                cleanChampionshipTarget(mm);
                report.appendLossAwardList(calcStarAward(teamId, levb, lev.getStar(), currStar));
                if (lev.getStar() < currStar) {
                    lev.setStar(currStar);
                    lev.save();
                }
                mm.save();
            }
        }

        limitProps(tmm, report.getAwards(teamId));//限制掉落
        localBattleManager.sendEndMain(bs, true);
        if (win) {
            sendMessage(teamId, allResp(tmm), ServiceCode.MMatch_Match_Win_Push);
        }
    }

    /** 比赛胜利. 更新状态, 结算奖励 */
    private void endMatchWin1(long teamId, BattleSource bs, MMatchLevBean levb, TeamMainMatch tmm, MainMatchLevel lev) {
        EndReport report = bs.getEndReport();
        int levrid = levb.getId();
        final MainMatch mm = tmm.getMainMatch();
        final int maxStar = ConfigConsole.global().mMatchMaxStar;
        int currStar = 0;
        if (levb.isChampionship()) {//锦标赛胜利, 直接晋级
            if (mm.getChampionshipLevelRid() != levrid) {
                log.warn("mmatch end. tid {} curr championship rid {} != levrid {}", teamId, mm.getChampionshipLevelRid(), levrid);
                return;
            }
            final int nextWinNum = Math.min(maxStar, mm.getChampionshipWinNum() + 1);
            final int forceTime = ConfigConsole.global().mMatchCsForceEndTime;
            final LocalDate startDate = mm.getChampionshipLastMatchStartDate();
            if (nextWinNum == maxStar ||
                !LocalDate.now().isEqual(startDate) ||
                System.currentTimeMillis() > DateTimeUtil.midnight() + forceTime) {//3星, 或者 已经超过结束时间, 重置对手
                currStar = nextWinNum;
                cleanChampionshipTarget(mm);
                log.debug("mmatch end. tid {} reset championship. star {} maxstar {} startdate {}", teamId, currStar, maxStar, startDate);
            } else {
                mm.setChampionshipWinNum(nextWinNum);
                log.debug("mmatch end. tid {} championship match not full end. win num {} maxstar {} startdate {}",
                    teamId, nextWinNum, maxStar, startDate);
            }
        } else {//常规赛
            currStar = calcStar(teamId, maxStar, levb, bs);
        }
        report.addAdditional(EBattleAttribute.Main_Match_Star, currStar);

        //更新星级, 计算星级奖励
        if (levb.isRegular() || currStar == maxStar) {//锦标赛满星结算奖励
            report.appendWinAwardList(calcStarAward(teamId, levb, lev.getStar(), currStar));
            if (mm.getLastLevelRid() < levb.getId()) {//更新装备经验节点
                mm.setLastLevelRid(levb.getId());
                mm.setLastMatchEndTime(System.currentTimeMillis());
            }
        }

        if (lev.getStar() < currStar) {
            lev.setStar(currStar);
            lev.save();
        }

        log.debug("mmatch end. tid {} levrid {} type {} final star {}", teamId, levrid, levb.getType(), currStar);
        mm.save();
    }

    /** 计算星级奖励 */
    private List<PropSimple> calcStarAward(long tid, MMatchLevBean levb, int maxStar, int currStar) {
        Star onecfg = levb.getStar(1);
        List<PropSimple> props = propManager.getPropSimples(onecfg.getProps(), onecfg.getDrops());
        log.trace("mmatch end match. calc star award. home tid {} lev {}. star max {} curr {}, get star {} award",
            tid, levb.getId(), maxStar, currStar, 1);
        for (int i = Math.max(2, maxStar + 1); i <= currStar; i++) {//非1星奖励只发放一次
            log.trace("mmatch end match. calc star award. home tid {} lev {} get star {} award", tid, levb.getId(), i);
            Star ocfg = levb.getStar(i);
            if (ocfg != null) {
                props.addAll(propManager.getPropSimples(ocfg.getProps(), ocfg.getDrops()));
            }
        }
        log.trace("mmatch end match. calc star award. tid {} levrid {} props {}", tid, levb.getId(), props);
        return props;
    }

    /** 扫荡 */
    @ClientMethod(code = ServiceCode.MMatch_Quick_Match)
    public void quickMatch(int levRid) {
        long teamId = getTeamId();
        ErrorCode ret = quickMatch0(teamId, levRid);
        sendMsg(ret);
        log.debug("mmatch quickMatch. tid {} levRid {} ret {}", teamId, levRid, ret);
    }

    /** 扫荡 */
    private ErrorCode quickMatch0(long teamId, int levRid) {
        if (isInBattle(teamId)) {
            return ErrorCode.Battle_In;
        }
        MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
        if (levb == null) {
            return ErrorCode.MMatch_Lev_Bean;
        }
        Team team = teamManager.getTeam(getTeamId());
        if (team.getLevel() < levb.getTeamLevel()) {
            return ErrorCode.Team_Level;
        }

        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.MMatch_MM_Null;
        }
        if (levb.isChampionship()) {
            int stopTime = ConfigConsole.global().mMatchCsStopTime;
            if (System.currentTimeMillis() > DateTimeUtil.midnight() + stopTime) {
                return ErrorCode.MMatch_Num_Time_Stop;
            }
            if (mm.getChampionshipWinNum() > 0) {
                return ErrorCode.MMatch_Quick_Championship;
            }
        }
        MMatchDivisionBean divb = MainMatchConsole.getDivBean(levb.getDivId());
        if (divb == null) {
            return ErrorCode.MMatch_Div_Bean;
        }
        MainMatchLevel lev = tmm.getLevels().get(levRid);
        if (lev == null) {
            return ErrorCode.MMatch_Lev_Null;
        }
        if (lev.getStar() < ConfigConsole.global().mMatchQuickMinStar) {
            return ErrorCode.MMatch_Quick_Star;
        }
        //扣除次数
        final int maxNumCfg = getCfgMaxNum();
        calcMathNum(mm, maxNumCfg, mm.getMatchNum());
        final int currNum = mm.getMatchNum();
        if (currNum <= 0) {
            return ErrorCode.MMatch_Match_Num;
        }
        mm.setMatchNum(currNum - 1);
        mm.save();

        BattleSource bs = localBattleManager.buildBattle(bt(levb.getType()), teamId, levb.getNpcId(), teamId);
        BaseBattleHandle bh = new AutoBattleQuickHandle(bs, bs1 -> {
            log.debug("mmatch battle quick match end. bid {}", bs1.getId());
            quickEndMatch(teamId, levRid, levb, tmm, bs1);
        }, false);
        localBattleManager.start(bs, bh);
        return ErrorCode.Success;
    }

    private void quickEndMatch(long teamId, int levRid, MMatchLevBean levb, TeamMainMatch tmm, BattleSource bs) {
        MainMatch mm = tmm.getMainMatch();
        EndReport report = bs.getEndReport();
        report.setWinTeamId(teamId);
        Star star = levb.getStar(1);
        List<PropSimple> props = propManager.getPropSimples(star.getProps(), star.getDrops());
        report.appendWinAwardList(props);
        log.debug("mmatch quickMatch. calc star award. tid {} levrid {} props {}", teamId, levb.getId(), report.getWinAwardList());
        limitProps(tmm, report.getWinAwards());//限制掉落
        MMatchQuickMatchPushResp.Builder resp = quickMatchResp(levRid, mm);
        if (!ListsUtil.isEmpty(report.getWinAwardList())) {
            resp.addAllProps(PropManager.getPropSimpleListData(report.getWinAwardList()));
        }
        localBattleManager.sendEndMain(bs, true);
        sendMessage(teamId, resp.build(), ServiceCode.MMatch_Quick_Match_Push);
    }

    /** 限制掉落 */
    private void limitProps(TeamMainMatch tmm, Map<Integer, PropSimple> awards) {
        Map<Integer, Integer> limits = getLimitProps(tmm);//按道具组
        Iterator<Entry<Integer, PropSimple>> it = awards.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, PropSimple> e = it.next();
            PropSimple ps = e.getValue();
            PropBean pb = PropConsole.getProp(ps.getPropId());
            if (pb == null || pb.getDailyMaxNum() <= 0) {
                continue;
            }
            Integer num = limits.getOrDefault(pb.getGroupId(), 0);
            log.debug("mmatch limitprops tid {} pid {} gid {} pnum {} num {}/{}",
                tmm.getTeamId(), ps.getPropId(), pb.getGroupId(), ps.getNum(), num, pb.getDailyMaxNum());
            if (num >= pb.getDailyMaxNum()) {//达到每日上限, 禁止掉落
                it.remove();
            } else {
                int fnum = Math.min(pb.getDailyMaxNum() - num, ps.getNum());
                num += ps.getNum();
                if (fnum > ps.getNum()) {
                    ps.setNum(fnum);
                }

                limits.put(pb.getGroupId(), num);//更新道具组获取数量
                redis.hset(tmm.getLimitPropsRedisKey(), String.valueOf(pb.getGroupId()), String.valueOf(num));
                redis.expire(tmm.getLimitPropsRedisKey(), RedisKey.DAY2);
            }
        }
    }

    /** 获取锦标赛关卡的信息 */
    @ClientMethod(code = ServiceCode.MMatch_Championship_Info)
    public void championshipInfo(int levRid) {
        long teamId = getTeamId();
        ErrorCode ret = championshipInfo0(teamId, levRid);
        sendMsg(ret);
        log.debug("mmatch championshipInfo. tid {} levRid {} ret {}", teamId, levRid, ret);
    }

    /** 获取锦标赛关卡的信息 */
    private ErrorCode championshipInfo0(long teamId, int levRid) {
        MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
        if (levb == null) {
            return ErrorCode.MMatch_Lev_Bean;
        }
        Team team = teamManager.getTeam(getTeamId());
        if (team.getLevel() < levb.getTeamLevel()) {
            return ErrorCode.Team_Level;
        }
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.MMatch_MM_Null;
        }
        if (!levb.isChampionship()) {
            return ErrorCode.MMatch_Championship_Lev_Type;
        }
        MMatchDivisionBean divb = MainMatchConsole.getDivBean(levb.getDivId());
        if (divb == null) {
            return ErrorCode.MMatch_Div_Bean;
        }
        final int maxStar = ConfigConsole.global().mMatchMaxStar;
        TeamStatus ts = teamStatusManager.get(teamId);
        boolean change = false;
        if (ts != null && ts.getBattle(EBattleType.Main_Match_Championship) == null &&
            mm.getChampionshipTargetSize() > 0) {//锦标赛没有比赛
            int stopTime = ConfigConsole.global().mMatchCsStopTime;
            if (mm.getChampionshipWinNum() == maxStar ||
                !LocalDate.now().isEqual(mm.getChampionshipLastMatchStartDate()) ||
                System.currentTimeMillis() > DateTimeUtil.midnight() + stopTime) {//3星, 或者 已经超过结束时间, 重置对手
                log.debug("mmatch championship info. reset. tid {} win num {}", teamId, mm.getChampionshipWinNum());
                cleanChampionshipTarget(mm);
                change = true;
            }
        }

        log.debug("mmatch championship info. tid {} win num {}. levrid curr {} req {}", teamId, mm.getChampionshipWinNum(),
            mm.getChampionshipLevelRid(), levRid);
        MMatchChampionshipResp resp;
        if (levRid != mm.getChampionshipLevelRid()) {
            List<ChampionshipTarget> targets = getSysTargets(teamId, levb, getSysLev(levRid));
            resp = championshipResp(levRid, targets);
        } else {
            change = checkAndGenerateTarget(mm.getTeamId(), levb, mm);
            resp = championshipResp(mm);
        }
        if (change) {
            mm.save();
        }
        sendMessage(teamId, resp, ServiceCode.MMatch_Championship_Info_Push);
        return ErrorCode.Success;
    }

    private MainMatchLevel addNewLev(MMatchLevBean levb, int initStar, TeamMainMatch tmm) {
        MainMatchLevel lev = tmm.newMainMatchLevel();
        lev.setResourceId(levb.getId());
        lev.setStar(initStar);
        lev.save();
        MainMatchLevel old = tmm.addLevel(lev);
        if (old != null) {
            log.error("mmatch newlev, lev exists. tid {} levRid {}", tmm.getTeamId(), old.getResourceId());
        }
        return lev;
    }

    private void addNewDiv(MMatchLevBean levb, TeamMainMatch tmm) {
        MainMatchDivision div = tmm.newMainMatchDiv();
        div.setResourceId(levb.getDivId());
        div.save();
        MainMatchDivision old = tmm.addDiv(div);
        if (old != null) {
            log.error("mmatch newdiv, div exists. tid {} div {}", tmm.getTeamId(), levb.getDivId());
        }
    }

    /** 获取下一个锦标赛对手, 用于比赛 */
    private ChampionshipTarget getNextChampionshipTarget(MMatchLevBean levb, MainMatch mm) {
        checkAndGenerateTarget(mm.getTeamId(), levb, mm);
        List<List<ChampionshipMatchTemp>> targets = mm.getTempChampionshipWins();
        if (mm.getChampionshipWinNum() > targets.size()) {
            log.warn("mmatch championship target. tid {} championship win num {} > target size {}", mm.getTeamId(), mm.getChampionshipWinNum(), targets.size());
            return null;
        } else {
            List<ChampionshipMatchTemp> levt = targets.get(mm.getChampionshipWinNum());
            if (levt.isEmpty()) {
                log.warn("mmatch championship target. tid {} target is empty. win num {}", mm.getTeamId(), mm.getChampionshipWinNum());
                return null;
            }
            ChampionshipMatchTemp t = levt.get(0);
            if (t.getHome().getTeamId() != mm.getTeamId()) {
                log.warn("mmatch championship target. tid {} target home {} != self {}", mm.getTeamId(), t, mm.getTeamId());
            }
            log.debug("mmatch championship target. tid {} win num {} target {} ", mm.getTeamId(), mm.getChampionshipWinNum(), t);
            return t.getAway();
        }
    }

    /** 生成锦标赛关卡对手 */
    private boolean checkAndGenerateTarget(long teamId, MMatchLevBean levb, MainMatch mm) {
        final int levrid = levb.getId();
        final MainMatchSystemLev syslev = getSysLev(levrid);
        final int currTargetSize = mm.getChampionshipTargetSize();
        boolean change = false;
        final int maxStar = ConfigConsole.global().mMatchMaxStar;
        final int tnum = MainMatchConsole.getTargetNum();
        if (currTargetSize == 0 || currTargetSize != tnum) {//对手不存在, 初始化对手
            if (log.isTraceEnabled()) {
                log.trace("mmatch generate targets. tid {} target size curr {} cfg {}", teamId, currTargetSize, tnum);
            }
            long curr = System.currentTimeMillis();
            mm.setChampionshipRndSeed(curr);
            List<ChampionshipTarget> targets = getSysTargets(teamId, levb, syslev);
            mm.setChampionshipTargets(targets);
            mm.setTempChampionshipWins(Collections.emptyList());
            if (log.isTraceEnabled()) {
                log.trace("mmatch generate targets. tid {} seed {} targets {}", teamId, curr, targets);
            }
            change = true;
        }
        if (ListsUtil.isEmpty(mm.getTempChampionshipWins())) { //生成胜利者
            mm.setTempChampionshipWins(generateWins(teamId, maxStar, mm.getChampionshipRndSeed(), mm.getChampionshipTargets()));
            if (log.isTraceEnabled()) {
                log.trace("mmatch generate wins. tid {} seed {} all wins {}", teamId, mm.getChampionshipRndSeed(), mm.getTempChampionshipWins());
            }
        }
        return change;
    }

    /** 生成锦标赛关卡胜利者 */
    private List<List<ChampionshipMatchTemp>> generateWins(long teamId, int maxStar, long seed, List<ChampionshipTarget> targets) {
        final Random rnd = new Random(seed);
        List<List<ChampionshipMatchTemp>> tempChampionshipWins = new ArrayList<>();
        //第一层
        List<ChampionshipMatchTemp> firstLev = new FixedArrayList<>(ChampionshipMatchTemp.class, targets.size() / 2);
        firstLev.set(0, new ChampionshipMatchTemp(targets.get(0), targets.get(1)));//0,1
        int idx = 1;
        for (int j = 2; j < targets.size(); j += 2) {//2个为一组
            firstLev.set(idx++, new ChampionshipMatchTemp(targets.get(j), targets.get(j + 1)));
        }
        tempChampionshipWins.add(firstLev);
        if (log.isTraceEnabled()) {
            log.trace("mmatch generate wins. tid {} first lev {}", teamId, tempChampionshipWins);
        }
        //其他层
        List<ChampionshipMatchTemp> preLev = firstLev;
        for (int winLev = 1; winLev < maxStar; winLev++) {// > 1 层
            List<ChampionshipMatchTemp> winTps = new FixedArrayList<>(ChampionshipMatchTemp.class, preLev.size() >> 1);
            assert winTps.size() > 0;
            int jdx = 0;
            for (int j = 0; j < preLev.size(); j += 2) {
                if (log.isTraceEnabled()) {
                    log.trace("mmatch generate wins. tid {} wintps size {} j {} and {}", teamId, winTps.size(), j, j + 1);
                }
                if (j == 0) {//自己
                    ChampionshipMatchTemp self = preLev.get(j);
                    ChampionshipMatchTemp target = preLev.get(j + 1);
                    if (rnd.nextBoolean()) {//随机胜利者
                        winTps.set(jdx++, new ChampionshipMatchTemp(self.getHome(), target.getHome()));
                    } else {
                        winTps.set(jdx++, new ChampionshipMatchTemp(self.getHome(), target.getAway()));
                    }
                } else {
                    ChampionshipMatchTemp t0 = preLev.get(j);
                    ChampionshipMatchTemp t1 = preLev.get(j + 1);
                    ChampionshipTarget t0win = rnd.nextBoolean() ? t0.getHome() : t0.getAway();//随机胜利者
                    ChampionshipTarget t1win = rnd.nextBoolean() ? t1.getHome() : t1.getAway();
                    winTps.set(jdx++, new ChampionshipMatchTemp(t0win, t1win));
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("mmatch generate wins. tid {} win lev {} tuple {}", teamId, winLev, winTps);
            }
            tempChampionshipWins.add(winTps);
            preLev = winTps;
        }
        return tempChampionshipWins;
    }

    /** 测试 */
    public static void main(String[] args) {
        MainMatchManager mm = new MainMatchManager(false);
        final int tnum = 1 << 3;
        long tid = 20210000041975L;
        List<List<ChampionshipMatchTemp>> ret = testGenerateWins(mm, tnum, tid);
        log.info("test wins {}", ret);
        testTargets(mm, tnum, tid);
        testStarAward();
    }

    /** 测试非1星奖励只发一次 */
    private static void testStarAward() {
        for (int max = 0; max <= 3; max++) {
            for (int curr = 1; curr <= 3; curr++) {
                testStarAward(max, curr);
            }
        }
    }

    /** 测试非1星奖励只发一次 */
    private static void testStarAward(int maxStar, int currStar) {
        log.trace("test max {} curr {} away 1 star award", maxStar, currStar);
        for (int i = Math.max(2, maxStar + 1); i <= currStar; i++) {//非1星奖励只发放一次
            log.trace("test max {} curr {} get away {} star award", maxStar, currStar, i);
        }
    }

    /** 测试 生成锦标赛关卡胜利者 */
    private static List<List<ChampionshipMatchTemp>> testGenerateWins(MainMatchManager mm, int tnum, long tid) {
        List<ChampionshipTarget> targets = new ArrayList<>();
        targets.add(testnpc(10001));
        targets.add(testnpc(10002));
        targets.add(testnpc(10003));
        targets.add(testnpc(10004));
        targets.add(testnpc(10005));
        targets.add(testnpc(10006));
        targets.add(testnpc(10007));
        targets.add(testnpc(10008));
        targets.add(testnpc(10009));
        targets.add(testnpc(10010));
        if (targets.size() > tnum) {
            targets = new ArrayList<>(targets.subList(0, tnum));
        }
        assert targets.size() == tnum;
        return mm.generateWins(tid, 3, 1514889894136L, targets);
    }

    private static ChampionshipTarget testnpc(long id) {
        return new ChampionshipTarget(id, id);
    }

    /** 测试 生成锦标赛关卡对手 */
    private static void testTargets(MainMatchManager mm, int tnum, long tid) {
        log.info("test targets\n");
        int levId = 1106;
        MMatchLevBean levb = new MMatchLevBean();
        levb.setId(levId);
        levb.setChampionNpc(Arrays.asList(10001, 10002, 10003, 10004, 10005, 10006, 10007, 10008, 10009, 10010));
        levb.setAssociateRegularLev(ImmutableSet.of(1101, 1102, 1103, 1104, 1105));
        Map<Integer, MMatchLevBean> levs = new HashMap<>();
        levs.put(1101, new MMatchLevBean(1101, 2));
        levs.put(1102, new MMatchLevBean(1102, 2));
        levs.put(1103, new MMatchLevBean(1103, 3));
        levs.put(1104, new MMatchLevBean(1104, 3));
        levs.put(1105, new MMatchLevBean(1105, 3));
        MainMatchConsole.setLevs(levs);
        final int rankSizeCfg = 10;

        MainMatchSystemLev syslev = new MainMatchSystemLev(levId);
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        for (Integer reglev : levb.getAssociateRegularLev()) {
            SysLevRank levrank = syslev.getOrCreateLevRank(reglev);
            int levRankSize = tlr.nextInt(0, rankSizeCfg + 1);//当前关卡排行榜需要填充的人数
            boolean includeself = levRankSize > 0 && tlr.nextBoolean();
            int selfIdx = includeself ? tlr.nextInt(levRankSize) : -1;
            for (int i = 0; i < levRankSize; i++) {
                long cstid = tlr.nextLong(tid - 10, tid + 10);
                if (includeself && i == selfIdx) {
                    cstid = tid;
                }
                levrank.addLastChampionshipTeam(new SysChampionshipTeam(cstid));
            }

            log.info("test data. reglev {} levRankSize {} includeself {} selfidx {} rank teams {}",
                reglev, levRankSize, includeself, selfIdx, levrank.getTeams());
        }

        log.info("test data. levb {}", levb);
        //        for (SysLevRank levrank : syslev.getCsRanks().values()) {
        //            log.info("test data. levrank size {} all {}", levrank.getTeams().size(), levrank);
        //        }
        log.info("test data. tid {} target cfg num {}, syslev rank size {}", tid, tnum, syslev.getRankSize());
        List<ChampionshipTarget> targets = mm.getSysTargets(tid, tnum, levb, syslev);

        assert targets.size() == tnum;
        assert targets.get(0).getTeamId() == tid;
        boolean tailIncludeSelf = false;
        for (int i = 1; i < targets.size(); i++) {
            tailIncludeSelf |= (targets.get(i).getTeamId() == tid);
        }
        assert !tailIncludeSelf;//后续不包括自己
    }

    /** 生成对手快照. 第一个是自己, 排行榜玩家跟在后面(过滤掉自己) */
    private List<ChampionshipTarget> getSysTargets(long teamId, MMatchLevBean levb, MainMatchSystemLev syslev) {
        if (!levb.isChampionship()) {
            log.error("mmatch systargets. tid {} lev {} not championship lev", teamId, levb.getId());
            return new ArrayList<>();
        }
        final int cfgTargetNum = MainMatchConsole.getTargetNum();
        return getSysTargets(teamId, cfgTargetNum, levb, syslev);
    }

    /** 生成球队的对手快照 */
    private List<ChampionshipTarget> getSysTargets(long teamId, int cfgTargetNum, MMatchLevBean levb, MainMatchSystemLev syslev) {
        int levid = levb.getId();
        Map<Long, ChampionshipTarget> targets = new LinkedHashMap<>(cfgTargetNum);
        List<Integer> sysnpcs = levb.getChampionNpc();
        targets.put(teamId, firstTarget(teamId, levid, sysnpcs));//第一个是自己
        //先常规获取对手
        genSysTargetForTeam(teamId, levb, syslev, targets, sysnpcs, MMatchLevBean::getChampionTargetNum);//其他对手
        if (log.isDebugEnabled()) {
            log.debug("mmatch systargets. mid. tid {} lev {} size self {} cfg {} sys {}",
                teamId, levid, targets.size(), cfgTargetNum, syslev.getRankSize());
        }
        if (targets.size() < cfgTargetNum) { //不满, 再按顺序取一次对手
            genSysTargetForTeam(teamId, levb, syslev, targets, sysnpcs, MMatchLevBean::getChampionRankNum);//其他对手
            if (log.isDebugEnabled()) {
                log.debug("mmatch systargets. retry. tid {} lev {} size self {} cfg {} sys {}",
                    teamId, levid, targets.size(), cfgTargetNum, syslev.getRankSize());
            }
        }

        List<ChampionshipTarget> targetlist = new ArrayList<>(targets.values());
        //填充剩余的对手
        if (targetlist.size() > cfgTargetNum) {
            targetlist = targetlist.subList(0, cfgTargetNum);
        } else {
            for (int i = targetlist.size(); i < cfgTargetNum; i++) {//
                Integer npc = ListsUtil.get(sysnpcs, i);
                if (npc == null) {
                    log.error("mmatch systargets. fill remaining. tid {} lev {} i {} npc is null", teamId, levid, i);
                    break;
                }
                targetlist.add(new ChampionshipTarget(npc, npc));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("mmatch systargets. final. tid {} lev {} size self {} cfg {} sys {}. targets {}",
                teamId, levid, targetlist.size(), cfgTargetNum, syslev.getRankSize(), targetlist);
        }
        return targetlist;
    }

    /**
     * 生成球队的对手快照
     *
     * @param regLevRankNum 获取常规赛关卡的排行榜可以作为对手的数量
     */
    private void genSysTargetForTeam(long teamId, MMatchLevBean levb, MainMatchSystemLev syslev,
                                     Map<Long, ChampionshipTarget> targets,
                                     List<Integer> sysnpcs,
                                     Function<MMatchLevBean, Integer> regLevRankNum) {
        //其他对手
        for (Integer regLev : levb.getAssociateRegularLev()) {
            SysLevRank levrank = syslev.getLevRank(regLev);
            if (levrank == null || levrank.getTeams().isEmpty()) {
                continue;
            }
            MMatchLevBean reglevb = MainMatchConsole.getLevBean(regLev);
            if (reglevb == null) {
                continue;
            }
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (levrank) {
                int addNum = 0;
                final Integer csRankNum = regLevRankNum.apply(reglevb);
                final int rankSize = levrank.getTeams().size();
                for (int i = 0; i < rankSize && addNum < csRankNum; i++) {
                    SysChampionshipTeam csteam = levrank.getTeams().get(i);
                    if (csteam == null) {
                        continue;
                    }
                    if (csteam.getTeamId() == teamId || targets.containsKey(csteam.getTeamId())) {//是自己或者对手已经存在
                        log.trace("mmatch systargets exists. tid {} reglev {} ttid {}", teamId, regLev, csteam.getTeamId());
                        continue;
                    }
                    ChampionshipTarget cstt = new ChampionshipTarget(csteam.getTeamId());
                    Integer npc = ListsUtil.get(sysnpcs, targets.size());
                    if (npc != null) {
                        cstt.setNpc(npc);
                    }
                    targets.put(cstt.getTeamId(), cstt);
                    addNum++;
                }
                log.trace("mmatch systargets addreglev. tid {} lev curr {} reg {}, num cfg {} rank {} add {}",
                    teamId, levb.getId(), regLev, csRankNum, rankSize, addNum);
            }
        }
    }

    /** 第一个对手是自己 */
    private ChampionshipTarget firstTarget(long teamId, int levid, List<Integer> npcs) {
        ChampionshipTarget first = new ChampionshipTarget(teamId);
        Integer firstNpc = npcs.get(0);
        if (firstNpc == null) {
            log.error("mmatch systargets. tid {} lev {} idx {} npc is null", teamId, levid, 0);
        } else {
            first.setNpc(npcs.get(0));
        }
        log.trace("mmatch systargets. first. tid {} lev {} first {}", teamId, levid, first);
        return first;
    }

    /** 比赛类型 */
    private EBattleType bt(LevType type) {
        switch (type) {
            case Championship: return EBattleType.Main_Match_Championship;
            default: return EBattleType.Main_Match_Normal;
        }
    }

    /** 重置锦标赛信息 */
    private void cleanChampionshipTarget(MainMatch mm) {
        mm.setChampionshipRndSeed(0);
        mm.setChampionshipLevelRid(0);
        mm.setChampionshipWinNum(0);
        mm.setChampionshipTargets(Collections.emptyList());
        mm.setTempChampionshipWins(Collections.emptyList());
    }

    /** 比较比赛数据, 更新排行榜 */
    private void updateChampionshipRank(long teamId, MMatchLevBean levb, EndReport report) {
        if (log.isTraceEnabled()) {
            log.trace("mmatch update rank. tid {} levrid {} assocCsLev {} csranknum {}",
                teamId, levb.getId(), levb.getAssocChampionLev(), levb.getChampionRankNum());
        }
        if (levb.getEnablePreId() > 0) {
            MMatchLevBean prelevb = MainMatchConsole.getLevBean(levb.getEnablePreId());
            if (prelevb == null) {
                return;
            }
        }
        if (!levb.isRegular() || levb.getChampionRankNum() <= 0 || levb.getAssocChampionLev() <= 0) {
            return;
        }
        MMatchLevBean cslevb = MainMatchConsole.getLevBean(levb.getAssocChampionLev());
        if (cslevb == null || !cslevb.isChampionship()) {//本关卡没有关联锦标赛
            return;
        }
        final int scoreAbs = report.getHomeScore() - report.getAwayScore();
        //本关卡关联了锦标赛, 更新排行榜
        MainMatchSystemLev syslev = getSysLev(cslevb.getId());

        boolean change = updateRank(teamId, levb, syslev, scoreAbs);
        if (change) {
            redis.hmset(RedisKey.Main_Match_Sys, sys);
        }
    }

    /** 比较比赛数据, 更新排行榜 */
    private boolean updateRank(long teamId, MMatchLevBean levb, MainMatchSystemLev syslev, int scoreAbs) {
        boolean change = true;
        SysLevRank levrank = syslev.getOrCreateLevRank(levb.getId());
        final int rankNumCfg = levb.getChampionRankNum();
        if (log.isTraceEnabled()) {
            log.trace("mmatch update rank before. tid {} levrid cs {} curr {}. cfgNum {} score {} levrank {}", teamId,
                syslev.getLevRid(), levb.getId(), rankNumCfg, scoreAbs, levrank);
        }
        long curr = System.currentTimeMillis();
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (levrank) {
            //            if (levrank.getTeams().size() < rankNumCfg) {
            //                levrank.fillTeamsNull(rankNumCfg);
            //            }
            SysChampionshipTeam last = ListsUtil.last(levrank.getTeams());
            if (last != null) {
                int ret = Integer.compare(scoreAbs, last.getRankScore());
                log.trace("mmatch update rank compare. tid {} levrid sys {} curr {}. lasttid {} comp ret {}", teamId,
                    syslev.getLevRid(), levb.getId(), last.getTeamId(), ret);
                if (ret > 0) {//windata > last, 数据更好
                    SysChampionshipTeam self = last.getTeamId() == teamId ? last : levrank.getChampionshipTeam(teamId);
                    if (self != null) {//自己已经在排行榜中, 只更新数据
                        int ret1 = Integer.compare(scoreAbs, self.getRankScore());
                        if (log.isTraceEnabled()) {
                            log.trace("mmatch update rank update self. tid {} date curr {} pre {} comp ret {}", teamId, scoreAbs, self.getRankScore(), ret1);
                        }
                        if (ret1 > 0) {//比最好的要好
                            self.setRankScore(scoreAbs);
                        }
                    } else {//自己不在排行榜中, 放在最后一个
                        self = new SysChampionshipTeam(teamId, scoreAbs, curr);
                        levrank.addLastChampionshipTeam(self);
                    }
                    self.setLastUpTime(curr);
                } else if (levrank.getTeams().size() < rankNumCfg) {//排行榜没满, 放在最后一个
                    SysChampionshipTeam self = new SysChampionshipTeam(teamId, scoreAbs, curr);
                    levrank.addLastChampionshipTeam(self);
                } else {
                    change = false;//比最后一名数据差, 不处理
                }
            } else {//空, 放到最后一个
                SysChampionshipTeam self = new SysChampionshipTeam(teamId, scoreAbs, curr);
                self.setLastUpTime(curr);
                levrank.addLastChampionshipTeam(self);
            }

            if (change) {
                levrank.sort(Collections.reverseOrder(Integer::compare));//重新排序
                int oldsize = levrank.resizeTeams(rankNumCfg);
                if (log.isTraceEnabled() && oldsize > rankNumCfg) {
                    log.trace("mmatch update rank. resize. tid {} lev {} size {} -> {}", teamId, levrank.getLevRid(),
                        oldsize, levrank.getTeams().size());
                }

                if (log.isTraceEnabled()) {
                    log.trace("mmatch update rank after, change. tid {} levrank {}", teamId, levrank);
                }
            }
        }
        return change;
    }

    /** 根据比赛数据计算星级 */
    private int calcStar(long teamId, int maxStar, MMatchLevBean levb, BattleSource bs) {
        log.trace("mmatch calcstar. tid {} lev {}. battle id {} home {} away {} score {}:{}", teamId, levb.getId(),
            bs.getInfo().getBattleId(), bs.getHome().getTeamId(), bs.getAway().getTeamId(),
            bs.getHome().getScore(), bs.getAway().getScore());
        int star = 1;
        EndReport report = bs.getEndReport();
        for (Star starCfg : levb.getStars().values()) {
            ScoreResult sr = new ScoreResult();

            for (Integer wcid : starCfg.getWinConditionId()) {
                MMatchConditionBean wcb = MainMatchConsole.getWinCondition(wcid);
                if (wcb == null) {
                    sr.allMatch = false;
                    break;
                }
                starLog.trace("mmatch calcstar. tid {} star {} wc {}", teamId, starCfg.getStar(), wcb);
                sr.allMatch = calcMatchData(teamId, wcb, bs, report);
                if (!sr.allMatch) {// false
                    break;
                }
            }

            if (sr.allMatch) {//匹配当前星级所有要求
                star = starCfg.getStar();
                break;
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("mmatch calcstar. tid {} star {}", teamId, star);
        }

        BattleAttribute ba = bs.getAttributeMap(0);//gm handle
        if (ba != null) {
            Boolean homeWin = ba.getVal(EBattleAttribute.GM_FORCE_END_MATCH_HOME_WIN);//EBattleAttribute.GM_FORCE_END_MATCH
            if (Boolean.TRUE.equals(homeWin)) {
                star = maxStar;
                log.warn("mmatch calcstar. tid {} trigger force end match and home win. star {}", teamId, maxStar);
            }
        }

        if (star == maxStar) {//更新排行榜
            updateChampionshipRank(teamId, levb, report);
        }
        return star;
    }

    /** 计算和匹配比赛数据 */
    public static boolean calcMatchData(long teamId, MMatchConditionBean wcb, BattleSource bs, EndReport report) {
        switch (wcb.getType()) {
            //（球队型）
            case Team_Win_Point: //球队赢 vi1 分
                return starTeamWinPoint(teamId, report, wcb);
            case Team_Overtime_Win: //比赛进入加时并获胜
                return false;
            //                return starTeamWinWithAttr(teamId, report, bs.getOrCreateAttributeMap(0), EBattleAttribute.加时, 1);
            case Team_Last_Shot: //绝杀比赛获胜
                return false;
            //                return starTeamWinWithAttr(teamId, report, bs.getOrCreateAttributeMap(0), EBattleAttribute.绝杀, 1);
            case Team_Point_Win: //球队获胜并达到 vi1 分
                return starTeamPointWin(teamId, wcb, report);
            case Team_Comeback: //落后 vi1 分反超获胜
                return starTeamWinCondition(teamId, report, wcb, bs);
            //（球员型）
            case Multi_Player_Any_Num_Action_Type: //vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个.
                return starMultiPlayerAnyNumActionType(teamId, wcb, bs);
            case Multi_Player_Multi_Action_Type: //vi1 名球员每人满足 N 个 tuple 数据对(类型和值)
                return starMultiPlayerMultiActionType(teamId, wcb, bs);
            case Multi_Player_Any_Num_Action_Type_Of_Any_Quarter: //vi1 名球员每人在任意一节中满足 N 个 tuple 数据对(类型和值), n个数据必须都在同一节
                return starMultiPlayerAnyNumActionTypeOfAnyQuarter(teamId, wcb, bs);
            case All_Player_Any_Num_Action_Type: //所有上场球员每人满足 N 个 tuple 数据对(类型和值)
                return starMultiPlayerMultiActionType(teamId, wcb, bs, bs.getHome().getPlayers().size());
        }
        return false;
    }

    /** vi1 名球员每人在任意一节中满足 N 个 tuple 数据对(类型和值), n个数据必须都在同一节 */
    private static boolean starMultiPlayerAnyNumActionTypeOfAnyQuarter(long teamId, MMatchConditionBean wcb, BattleSource bs) {
        final int minPlayerNumCfg = wcb.getVi1();
        int currPlayerNum = 0;
        for (BattlePlayer bp : bs.getHome().getPlayers()) {
            PlayerStepActStat bprs = bp.getStepActionStats();
            if (bprs.getStepActions() == null) {
                continue;
            }

            for (Map.Entry<EBattleStep, ActionStatistics> e : bprs.getStepActions().entrySet()) {//单个球员每节统计数据
                EBattleStep step = e.getKey();
                ActionStatistics pks = e.getValue();
                boolean matchAllInOneStep = true;
                for (ActionCondition cond : wcb.getConditions().values()) {
                    final float currVal = getActionValue(pks, cond.getAct());
                    starLog.trace("mmatch calcstar. tid {} prid {} step {}. cond cfg {}. curr {} prnum {}",
                        teamId, bp.getRid(), step, cond, currVal, currPlayerNum);
                    if (!cond.match(currVal)) {//不满足条件
                        matchAllInOneStep = false;
                        break;
                    }
                }//end for cond

                if (matchAllInOneStep) {//同一节中满足所有条件
                    currPlayerNum++;
                    break;
                }
            }//end for all step

            if (currPlayerNum >= minPlayerNumCfg) {
                break;
            }
        }//end for player
        log.trace("mmatch calcstar. tid {} wc {} prnum {}", teamId, wcb.baseInfo(), currPlayerNum);
        return currPlayerNum >= minPlayerNumCfg;
    }

    /** vi1 名球员每人满足 N 个 tuple 数据对(类型和值) */
    private static boolean starMultiPlayerMultiActionType(long teamId, MMatchConditionBean wcb, BattleSource bs) {
        final int minPlayerNumCfg = wcb.getVi1();
        return starMultiPlayerMultiActionType(teamId, wcb, bs, minPlayerNumCfg);
    }

    private static boolean starMultiPlayerMultiActionType(long teamId, MMatchConditionBean wcb, BattleSource bs, final int minPlayerNumCfg) {
        int currPlayerNum = 0;
        for (BattlePlayer bp : bs.getHome().getPlayers()) {
            PlayerActStat pks = bp.getRealTimeActionStats();
            boolean matchAll = true;
            for (ActionCondition cond : wcb.getConditions().values()) {
                final float currVal = getActionValue(pks, cond.getAct());
                starLog.trace("mmatch calcstar. tid {} prid {}. cond cfg {}. curr {}. prnum {}",
                    teamId, bp.getRid(), cond, currVal, currPlayerNum);
                if (!cond.match(currVal)) {//不满足条件
                    matchAll = false;
                    break;
                }
            }
            if (matchAll) {
                currPlayerNum++;
            }
            if (currPlayerNum >= minPlayerNumCfg) {
                break;
            }
        }
        log.trace("mmatch calcstar. tid {} wc {} prnum {}", teamId, wcb.baseInfo(), currPlayerNum);
        return currPlayerNum >= minPlayerNumCfg;
    }

    private static boolean starTeamMultiActionType(long teamId, MMatchConditionBean wcb, BattleSource bs) {
        ReadOnlyActionStats pks = bs.getHome().getRtActionStats();
        boolean matchAll = true;
        for (ActionCondition cond : wcb.getConditions().values()) {
            final float currVal = getActionValue(pks, cond.getAct());
            starLog.trace("mmatch calcstar. tid {} . cond cfg {}. curr {}. prnum {}", teamId, cond, currVal);
            if (!cond.match(currVal)) {//不满足条件
                matchAll = false;
                break;
            }
        }
        log.trace("mmatch calcstar. tid {} wc {} ret {}", teamId, wcb.baseInfo(), matchAll);
        return matchAll;
    }

    /**
     * 比赛主场球队获胜, 并满足球队行为条件.
     */
    private static boolean starTeamWinCondition(long teamId, EndReport report, MMatchConditionBean wcb, BattleSource bs) {
        final int hs = report.getHomeScore();
        final int as = report.getAwayScore();
        return (hs - as) > 0 && starTeamMultiActionType(teamId, wcb, bs);
    }

    /** vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个 */
    private static boolean starMultiPlayerAnyNumActionType(long teamId, MMatchConditionBean wcb, BattleSource bs) {
        return starMultiPlayerMinNumActionType(teamId, wcb, bs, wcb.getVi1(), wcb.getVi2());
    }

    /** vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个 */
    private static boolean starMultiPlayerMinNumActionType(long teamId, MMatchConditionBean wcb, BattleSource bs,
                                                    final int minPlayerNumCfg, final int minActNumCfg) {
        int currPlayerNum = 0;
        for (BattlePlayer bp : bs.getHome().getPlayers()) {
            PlayerActStat pks = bp.getRealTimeActionStats();
            int currActNum = 0;

            for (ActionCondition cond : wcb.getConditions().values()) {
                final float currVal = getActionValue(pks, cond.getAct());
                if (cond.match(currVal)) {//满足条件
                    currActNum++;
                }
                starLog.trace("mmatch calcstar. tid {} prid {}. cond cfg {} curr {}. actnum {} prnum {}",
                    teamId, bp.getRid(), cond, currVal, currActNum, currPlayerNum);
                if (currActNum >= minActNumCfg) {//当前球员数据满足 min num 个
                    currPlayerNum++;
                    break;
                }
            }

            if (currPlayerNum >= minPlayerNumCfg) {
                break;
            }
        }
        log.trace("mmatch calcstar. tid {}. wc {}. prnum {}", teamId, wcb.baseInfo(), currPlayerNum);
        return currPlayerNum >= minPlayerNumCfg;
    }

    private static float getActionValue(ReadOnlyActionStats pks, EActionType act) {
        return pks.getActionValue(act);
    }

    /** 球队获胜并达到 vi1 分 */
    private static boolean starTeamPointWin(long teamId, MMatchConditionBean wcb, EndReport report) {
        int hs = report.getHomeScore();
        int as = report.getAwayScore();
        log.trace("mmatch calcstar. tid {} wc {}, score {}:{}", teamId, wcb.baseInfo(), hs, as);
        return (hs > as && hs >= wcb.getVi1());
    }

    /**
     * 比赛进入加时并获胜
     * 绝杀比赛获胜
     */
    private boolean starTeamWinWithAttr(long teamId, EndReport report, BattleAttribute ba,
                                        EBattleAttribute attr, int attrMinValueCfg) {
        final int hs = report.getHomeScore();
        final int as = report.getAwayScore();
        Integer attrVal = ba.getVal(attr);
        attrVal = attrVal == null ? 0 : Math.abs(attrVal);
        log.trace("mmatch calcstar. tid {} wc vi1 {}. score {}:{}. attr {} val {}/{}", teamId, attrMinValueCfg,
            hs, as, attr, attrVal, attrMinValueCfg);
        return (hs - as) > 0 && attrVal >= attrMinValueCfg;
    }

    /** 球队赢 vi1 分 */
    private static boolean starTeamWinPoint(long teamId, EndReport report, MMatchConditionBean wcb) {
        int hs = report.getHomeScore();
        int as = report.getAwayScore();
        log.trace("mmatch calcstar. tid {} wc vi1 {}, score {}:{}", teamId, wcb.getVi1(), hs, as);
        return (hs - as) >= wcb.getVi1();
    }

    /** 星级计算结果 */
    public static final class ScoreResult {
        /** 比赛数据是否匹配所有配置 */
        public boolean allMatch;
    }

    /** 获取关卡的系统全局信息, 没有则初始化 */
    private MainMatchSystemLev getSysLev(int levRid) {
        MainMatchSystemLev syslev = sys.get(levRid);
        if (syslev == null) {
            syslev = new MainMatchSystemLev(levRid);
            log.trace("mmatch init syslev. lev rid {}", levRid);
            MainMatchSystemLev old = sys.putIfAbsent(levRid, syslev);
            if (old != null) {
                syslev = old;
            }
        }
        return syslev;
    }

    /** 检测是否已经初始化, 没有则初始化 */
    private ErrorCode checkInit(TeamMainMatch tmm) {
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            long curr = System.currentTimeMillis();
            mm = tmm.setMainMatch(new MainMatch(tmm.getTeamId()));

            mm.setMatchNum(ConfigConsole.global().mMatchNumInit);
            mm.setMatchNumLastUpTime(curr);
            mm.save();
            log.debug("mmatch checkinit init mm. tid {}", tmm.getTeamId());
        }

        if (tmm.getLevels().isEmpty()) {//初始化
            final int defaultOpen = ConfigConsole.global().mMatchDefaultOpenLev;
            MMatchLevBean initLevb = MainMatchConsole.getLevBean(defaultOpen);
            if (initLevb == null) {
                return ErrorCode.Fail;
            }

            addNewLev(initLevb, 0, tmm);
            if (tmm.getDivs().get(initLevb.getDivId()) == null) {
                addNewDiv(initLevb, tmm);
            }
        }
        return ErrorCode.Success;
    }

    /** 获取配置. 挑战次数最大次数 */
    private int getCfgMaxNum() {
        return ConfigConsole.global().mMatchNumMax;
    }

    private MMatchAllResp allResp(TeamMainMatch tmm) {
        MMatchAllResp.Builder resp = MMatchAllResp.newBuilder();
        resp.setMatchBase(baseResp(tmm.getMainMatch()));
        resp.setChampionship(championshipResp(tmm.getMainMatch()));
        resp.addAllDivs(divResp(tmm.getDivs()));
        resp.addAllLevs(levResp(tmm.getLevels()));
        return resp.build();
    }

    private MMatchBaseResp baseResp(MainMatch mm) {
        MMatchBaseResp.Builder resp = MMatchBaseResp.newBuilder();
        if (mm == null) {
            return resp.build();
        }
        resp.setMatchNum(mm.getMatchNum());
        resp.setMatchNumLastUpTime(mm.getMatchNumLastUpTime());
        resp.setBuyMatchNum(teamNumManager.getUsedNum(mm.getTeamId(), TeamNumType.Main_Match_Num));

        resp.setRegularLastLevRid(mm.getLastLevelRid());
        resp.setRegularLastMatchEndTime(mm.getLastMatchEndTime());
        return resp.build();
    }

    private List<MMatchLevelResp> levResp(Map<Integer, MainMatchLevel> levs) {
        if (levs == null || levs.isEmpty()) {
            return Collections.emptyList();
        }
        List<MMatchLevelResp> list = new ArrayList<>(levs.size());
        for (MainMatchLevel lev : levs.values()) {
            list.add(levResp(lev));
        }
        return list;
    }

    private MMatchLevelResp levResp(MainMatchLevel lev) {
        MMatchLevelResp.Builder resp = MMatchLevelResp.newBuilder();
        resp.setRId(lev.getResourceId());
        resp.setStar(lev.getStar());
        return resp.build();
    }

    private List<MMatchDivResp> divResp(Map<Integer, MainMatchDivision> levels) {
        if (levels == null || levels.isEmpty()) {
            return Collections.emptyList();
        }
        List<MMatchDivResp> list = new ArrayList<>(levels.size());
        for (MainMatchDivision div : levels.values()) {
            list.add(divResp(div));
        }
        return list;
    }

    private MMatchDivResp divResp(MainMatchDivision div) {
        MMatchDivResp.Builder resp = MMatchDivResp.newBuilder();
        resp.setRId(div.getResourceId());
        MMatchDivisionBean divb = MainMatchConsole.getDivBean(div.getResourceId());
        if (divb != null) {
            for (Integer starId : divb.getStarAwards().keySet()) {
                if (div.hasStarAward(starId)) {
                    resp.addStarAwards(starId);
                }
            }
        }
        return resp.build();
    }

    private MMatchChampionshipResp championshipResp(int levrid, List<ChampionshipTarget> targets) {
        MMatchChampionshipResp.Builder resp = MMatchChampionshipResp.newBuilder();
        addTargetsResp(resp, targets);
        resp.setLevRid(levrid);
        return resp.build();
    }

    private MMatchQuickMatchPushResp.Builder quickMatchResp(int levRid, MainMatch mm) {
        MMatchQuickMatchPushResp.Builder resp = MMatchQuickMatchPushResp.newBuilder();
        resp.setLevRid(levRid);
        resp.setBase(baseResp(mm));
        return resp;
    }

    private void addTargetsResp(Builder resp, List<ChampionshipTarget> targets) {
        if (targets != null) {
            for (ChampionshipTarget target : targets) {
                Team team = teamManager.getTeam(target.getTeamId());
                if (team != null) {
                    resp.addTargets(teamManager.teamResp(team));
                }
            }
        }
    }

    private MMatchChampionshipResp championshipResp(MainMatch mm) {
        MMatchChampionshipResp.Builder resp = MMatchChampionshipResp.newBuilder();
        if (mm == null) {
            return resp.build();
        }
        resp.setLevRid(mm.getChampionshipLevelRid());
        resp.setWinNum(mm.getChampionshipWinNum());

        addTargetsResp(resp, mm.getChampionshipTargets());

        if (mm.getTempChampionshipWins() != null) {
            for (int i = 0; i < mm.getChampionshipWinNum(); i++) {
                int lev = i + 1;
                if (lev >= mm.getTempChampionshipWins().size()) {
                    break;
                }
                MMatchChampionshipTeamResp.Builder tr = MMatchChampionshipTeamResp.newBuilder();
                tr.setLev(lev);
                List<ChampionshipMatchTemp> list = mm.getTempChampionshipWins().get(lev);
                for (ChampionshipMatchTemp tl : list) {
                    tr.addTeams(tl.getHome().getTeamId());
                    tr.addTeams(tl.getAway().getTeamId());
                }
                resp.addWinTeams(tr);
            }
        }
        return resp.build();
    }

    /** gm命令. 清空所有关卡的排行榜 */
    ErrorCode gmResetSysRank() {
        sys.clear();
        redis.del(RedisKey.Main_Match_Sys);
        return ErrorCode.Success;
    }

    /** gm命令. 重置球队当前锦标赛对手 */
    ErrorCode gmResetTeamCSTarget(Team team) {
        TeamMainMatch tmm = getTMM(team.getTeamId());
        if (tmm == null) {
            return ErrorCode.Success;
        }
        MainMatch mm = tmm.getMainMatch();
        if (mm == null) {
            return ErrorCode.Success;
        }
        cleanChampionshipTarget(mm);
        mm.save();
        return ErrorCode.Success;
    }

    /** gm命令. 开启关卡(开启时会连带开启赛区) */
    ErrorCode gmEnableLev(Team team, int levRid, int star) {
        return gmEnableLevsWithStar(team, Collections.singletonList(levRid), star);
    }

    /** gm命令. 主线赛程.开启关卡到最大星级(开启时会连带开启赛区) */
    ErrorCode gmEnableLevsFullStar(Team team, List<Integer> levRids) {
        return gmEnableLevsWithStar(team, levRids, ConfigConsole.global().mMatchMaxStar);
    }

    /** gm命令. 主线赛程.开启关卡到最大星级(开启时会连带开启赛区) */
    private ErrorCode gmEnableLevsWithStar(Team team, List<Integer> levRids, int star) {
        long teamId = team.getTeamId();
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        checkInit(tmm);
        for (Integer levRid : levRids) {
            MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
            if (levb == null) {
                continue;
            }
            MMatchDivisionBean divb = MainMatchConsole.getDivBean(levb.getDivId());
            if (divb == null) {
                continue;
            }
            MainMatchDivision div = tmm.getDivs().get(levb.getDivId());
            if (div == null) {
                addNewDiv(levb, tmm);
            }
            MainMatchLevel lev = tmm.getLevels().get(levRid);
            if (lev == null) {
                addNewLev(levb, star, tmm);
            }
        }
        return ErrorCode.Success;
    }

    /** gm命令. 设置关卡星级 */
    ErrorCode gmLevStar(Team team, int levRid, int star) {
        long teamId = team.getTeamId();
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
        if (levb == null) {
            return ErrorCode.MMatch_Lev_Bean;
        }
        MainMatchLevel lev = tmm.getLevels().get(levRid);
        if (lev == null) {
            return ErrorCode.MMatch_Lev_Null;
        }
        lev.setStar(star);
        lev.save();
        return ErrorCode.Success;
    }

    /** gm命令. 设置关卡总比赛次数 */
    ErrorCode gmMatchCount(Team team, int levRid, int matchCount) {
        long teamId = team.getTeamId();
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MMatchLevBean levb = MainMatchConsole.getLevBean(levRid);
        if (levb == null) {
            return ErrorCode.MMatch_Lev_Bean;
        }
        MainMatchLevel lev = tmm.getLevels().get(levRid);
        if (lev == null) {
            return ErrorCode.MMatch_Lev_Null;
        }
        lev.setMatchCount(matchCount);
        lev.save();
        return ErrorCode.Success;
    }

    /** gm命令. 重置赛区奖励领取状态 */
    ErrorCode gmResetLevAwards(Team team, int levId) {
        long teamId = team.getTeamId();
        TeamMainMatch tmm = getTMM(teamId);
        if (tmm == null) {
            return ErrorCode.MMatch_TMM_Null;
        }
        MainMatchDivision div = tmm.getDivs().get(levId);
        if (div == null) {
            return ErrorCode.MMatch_Div_Null;
        }
        div.setStarAwards(0);
        div.save();
        return ErrorCode.Success;
    }
}
