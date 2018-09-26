package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.CoachConsole;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.console.SkillConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.db.dao.logic.BattleDAO;
import com.ftkj.db.domain.BattleInfoPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.PKParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleHandle;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.BattleStartPush;
import com.ftkj.manager.battle.BattleStep;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.handle.ManualBattleQuickHandle;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTactics;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.AllStarMatchEnd;
import com.ftkj.manager.battle.model.EndReport.ArenaMatchEnd;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd.RankedTeam;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.battle.model.Skill;
import com.ftkj.manager.coach.Coach;
import com.ftkj.manager.coach.TeamCoach;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.logo.TeamPlayerLogo;
import com.ftkj.manager.logo.bean.Logo;
import com.ftkj.manager.logo.bean.PlayerLogo;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.player.api.PlayerAbilityAPI;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.manager.skill.SkillTree;
import com.ftkj.manager.skill.TeamSkill;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.tactics.TeamTactics;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleConfig;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.BattlePB.BattleStartData;
import com.ftkj.proto.CommonPB.BattleHisListResp;
import com.ftkj.proto.CommonPB.BattleHisResp;
import com.ftkj.proto.CommonPB.BattleHisResp.Builder;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.GameLogPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.server.rpc.RpcTask.RpcResp;
import com.ftkj.util.Page;
import com.ftkj.util.ThreadPoolUtil;
import com.ftkj.util.tuple.Tuple2Long;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ftkj.enums.EActionType.dcap;
import static com.ftkj.enums.EActionType.ocap;

/**
 * @author tim.huang
 * 2017年4月18日
 * 本地比赛管理
 */
public class LocalBattleManager extends BaseManager {
    private static final Logger log = LoggerFactory.getLogger(LocalBattleManager.class);
    @IOC
    private TeamManager teamManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TacticsManager tacticsManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private PlayerLogoManager playerLogoManager;
    @IOC
    private TeamCapManager teamCapManager;
    @IOC
    private CoachManager coachManager;
    @IOC
    private SkillManager skillManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private TeamDayStatsManager teamDayStatisticsManager;
    @IOC
    private BattleDAO battleDAO;

    //默认基础比赛的处理。如有特殊处理，可以自己实现
    private BattleEnd end = this::end;
    private BattleRoundReport roundReport = this::roundReport;
    private BattleStartPush startPush = this::startPush;
    private volatile long refreshBattleTime = 0;
    private LinkedBlockingQueue<Long> battleIds = new LinkedBlockingQueue<>();

    /**
     * 取本地逻辑服务器的玩家比赛详细信息
     * 信息经过验证，如果超冒返回空对象
     */
    @RPCMethod(code = CrossCode.LocalBattleManager_getBattleTeam, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void getBattleTeam(long teamId) {
        log.debug("btloc cross node build battle team tid {}", teamId);
        BattleTeam bt = buildBattleTeam(teamId);
        RPCMessageManager.responseMessage(bt);
    }

    /**
     * 取本地逻辑服务器的玩家比赛详细信息
     * 信息经过验证，如果超冒返回空对象
     */
    @RPCMethod(code = CrossCode.GetBattleTeam, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void getBattleTeam2(long teamId) {
        log.debug("btloc cross node build battle team2 tid {}", teamId);
        BattleTeam bt = buildBattleTeam(teamId);
        if (bt == null) {
            RpcTask.resp(ErrorCode.Team_Null);
            return;
        }
        RpcTask.resp(bt);
    }

    /**
     * 创建比赛
     */
    public BattleSource buildBattle(EBattleType type, long teamId, long otherTeamId, long homeTeamId) {
        return buildBattle(type, teamId, otherTeamId, null, null, homeTeamId);
    }

    /**
     * 创建比赛
     */
    public BattleSource buildBattle(EBattleType type, long teamId, long otherTeamId, DropBean winDrop, DropBean loseDrop, long homeTeamId) {
        long bid = getNewBattleId();
        return buildBattle0(type, bid,
            teamManager.getTeam(teamId),
            teamManager.getTeam(otherTeamId),
            winDrop, loseDrop, homeTeamId);
    }

    /**
     * 创建比赛
     */
    public BattleSource buildBattle(EBattleType type, long battleId, long teamId, long otherTeamId, DropBean winDrop, DropBean loseDrop, long homeTeamId) {
        return buildBattle0(type, battleId,
            teamManager.getTeam(teamId),
            teamManager.getTeam(otherTeamId),
            winDrop, loseDrop, homeTeamId);
    }

    /**
     * 创建比赛
     */
    private BattleSource buildBattle0(EBattleType type, long bid, Team team, Team otherTeam, DropBean winDrop, DropBean loseDrop, long homeTeamId) {
        BattleSource bs = buildBattleSource(type, bid, team, otherTeam, winDrop, loseDrop, homeTeamId);
        BattleTeam homebt = buildBattleTeam(team);
        BattleTeam awaybt = buildBattleTeam(otherTeam);
        bs.addTeam(homebt, awaybt);
        return bs;
    }

    private BattleSource buildBattleSource(EBattleType bt, long battleId
        , Team home, Team away//球队信息
        , DropBean winDrop, DropBean loseDrop
        , long homeTeamId) {//默认赛前配置,不要取Team中的配置，要判断道具是否足够。
        BattleBean dbb = BattleConsole.getDefault();
        BattleBean bb = BattleConsole.getBattle(bt);
        if (homeTeamId == 0) {
            if (home.getTeamId() > 0) {
                homeTeamId = home.getTeamId();
            } else {
                homeTeamId = ThreadLocalRandom.current().nextBoolean() ? home.getTeamId() : away.getTeamId();
            }
        }
        return new BattleSource(battleId, bt,
            bb != null ? bb.getBaseBean().getSteps() : dbb.getBaseBean().getSteps(),
            bb != null ? bb.getBaseBean().getSpeed() : dbb.getBaseBean().getSpeed(),
            winDrop, loseDrop, homeTeamId);
    }

    private BattleTeam buildBattleTeam(long teamId) {
        Team team = teamManager.getTeam(teamId);
        if (team == null) {
            log.warn("btloc buildbt. tid {} team is null", teamId);
            return null;
        }
        return buildBattleTeam(team);
    }

    private BattleTeam buildBattleTeam(Team team) {
        long teamId = team.getTeamId();
        TeamPlayer tpr = playerManager.getTeamPlayer(teamId);
        TeamProp tp = propManager.getTeamProp(teamId);
        TeamTactics tt = tacticsManager.getTeamTactics(teamId);
        TeamBattleConfig tbc = teamManager.getTeamBattleConfig(teamId);
        TeamPlayerLogo tpl = playerLogoManager.getTeamPlayerLogo(teamId);
        TeamCoach tc = coachManager.getTeamCoach(teamId);
        TeamSkill ts = skillManager.getTeamSkill(teamId);
        Coach coach = tc.getDefaultCoach();
        Map<Integer, List<PlayerAbility>> prcaps = teamCapManager.getPlayerCap(teamId, tpr.getPlayers());
        // 计算最新攻防
        team.updateAbility(teamCapManager.getTeamOtherCap(team.getTeamId()).getTeamAbility());
        if (team.isNpc()) {
            BaseAbility npcAbility = team.getAbility().get(AbilityType.Npc_Buff);
            if (npcAbility != null) {
                team.getAbility().get(AbilityType.Team).addAttr(ocap, npcAbility.getAttrData(ocap));
                team.getAbility().get(AbilityType.Team).addAttr(dcap, npcAbility.getAttrData(dcap));
            }
        }
        boolean superCap = teamManager.isSalaryOverflow(team, tpr);
        //		tpr.getPlayerList().forEach(p-> p.updateAbility(teamCapManager.getPlayerBaseCap(p.getPlayerId()).getAbility()));
        BattleTeam bt = buildBattleTeam(team, tpr, tp, tt, tbc, tpl, ts, coach, prcaps, superCap);
        bt.setDayStatistics(teamDayStatisticsManager.getTeamDayStatistics(team.getTeamId()));
        return bt;
    }

    /**
     * 构建比赛球队信息,攻防修复
     *
     * @param overSalary 是否超帽
     */
    private BattleTeam buildBattleTeam(Team team,
                                       TeamPlayer players,
                                       TeamProp props,
                                       TeamTactics tactics,
                                       TeamBattleConfig battleConfig,
                                       TeamPlayerLogo playerLogo,
                                       TeamSkill teamSkill,
                                       Coach coach,
                                       Map<Integer, List<PlayerAbility>> playerCaps,
                                       boolean overSalary) {
        //初始化一个拥有基础数据的比赛玩家信息
        TeamAbility baseAbility = (TeamAbility) team.getAbility().get(AbilityType.Team);
        boolean isNpc = GameSource.isNPC(team.getTeamId());
        if (!isNpc && overSalary) {//超冒，基础攻防减低
            float srcocap = baseAbility.getAttrData(ocap);
            float srcdcap = baseAbility.getAttrData(dcap);
            float overSalaryRate = ConfigConsole.global().overSalary;
            baseAbility.setAttr(ocap, srcocap * overSalaryRate);
            baseAbility.setAttr(dcap, srcdcap * overSalaryRate);
            log.trace("btloc team. tid {} cap {} {} rate {} -> {} {}", team.getTeamId(), srcocap, srcdcap,
                overSalaryRate, baseAbility.get(ocap), baseAbility.get(dcap));
        }

        BattleTeam bt = new BattleTeam(team.getTeamId(), team.getName(), team.getLogo()
            , team.getLevel(), CoachConsole.getCoachBean(coach == null ? 0 : coach.getCid()), baseAbility);
        // 球队攻防打印
        if (log.isDebugEnabled()) {
            log.debug("btloc battleteam. tid {} tname {} overSalary {} cap {}",
                team.getTeamId(), team.getName(), overSalary, team.getAbility());
        }
        //添加玩家球员信息,初始化首发数据和攻防数据
        for (Player player : players.getPlayers()) {
            BattlePlayer bp = buildBattlePlayer(team, playerLogo, teamSkill, playerCaps, overSalary, player);
            bt.addPlayer(player.getLineupPosition(), bp);
        }

        //添加携带的战术相关信息
        bt.replaceAllTactics(getTactics(tactics));
        TacticId offenseTactics = battleConfig.getOffenseTactics(TacticId.强攻内线);
        TacticId defenseTactics = battleConfig.getDefenseTactics(TacticId.内线防守);
        if (isNpc) {
            NPCBean npc = NPCConsole.getNPC(team.getTeamId());
            if (npc != null) {
                TacticId ot = TacticId.convert(npc.getTacticOffense());
                TacticId dt = TacticId.convert(npc.getTacticDefense());
                if (ot != null && ot.getType() == TacticType.Offense && bt.hasTactics(ot)) {
                    offenseTactics = ot;
                }
                if (dt != null && dt.getType() == TacticType.Defense && bt.hasTactics(dt)) {
                    defenseTactics = dt;
                }

                bt.getAi().setCfgRid(npc.getAiGroupId());
            }
        }
        if (!bt.hasTactics(offenseTactics)) {
            bt.addTactics(new BattleTactics(TacticsConsole.getBean(offenseTactics), 1));
        }
        if (!bt.hasTactics(defenseTactics)) {
            bt.addTactics(new BattleTactics(TacticsConsole.getBean(defenseTactics), 1));
        }
        //设置进攻和防守的默认战术
        bt.updateTactics(offenseTactics, defenseTactics, null, null);
        //添加携带的道具数据
        //		if(team.isNpc()){//NPC 道具配置取AI道具
        //			NPCAIBean ai = NPCConsole.getNPC(team.getTeamId());
        //			bt.addProp(ai.getPropList());
        //		}
        //		else{
        //			bt.addProp(battleConfig.getProps());
        //		}
        return bt;
    }

    private BattlePlayer buildBattlePlayer(Team team,
                                           TeamPlayerLogo playerLogo,
                                           TeamSkill teamSkill,
                                           Map<Integer, List<PlayerAbility>> playerCaps,
                                           boolean overSalary,
                                           Player pr) {
        Map<AbilityType, PlayerAbility> caps = PlayerAbilityAPI.getPlayerAbilityMap(pr, team.isNpc()
            , playerCaps.get(pr.getPlayerRid()));
        if (!GameSource.isNPC(team.getTeamId()) && overSalary) {
            float overSalaryRate = ConfigConsole.global().overSalary;
            caps.forEach((k, pa) -> {
                float srcocap = pa.getAttrData(ocap);
                float srcdcap = pa.getAttrData(dcap);
                pa.setAttr(ocap, srcocap * overSalaryRate);
                pa.setAttr(dcap, srcdcap * overSalaryRate);
                log.trace("btloc pr. tid {} pr {} m {} cap {} {} rate {} -> {} {}", team.getTeamId(), pa.getPlayerId(),
                    k, srcocap, srcdcap, overSalaryRate, pa.get(ocap), pa.get(dcap));
            });
        }

        PlayerSkill skill = teamSkill.getPlayerSkill(pr.getPlayerRid());
        List<SkillPositionBean> stepList = getSkillBeans(pr, skill);
        Skill attackSkill = getBattleSkill(pr.getPlayerRid(), skill, stepList, sk -> skill.getAttack());
        Skill defendSkill = getBattleSkill(pr.getPlayerRid(), skill, stepList, sk -> skill.getDefend());
        int maxSkillPower = skill == null ? 10000 : skill.getMaxSkillPower();
        //					int playerId, int step,int level,boolean type,SkillBean skill
        PlayerLogo logo = playerLogo.getPlayerLogo(pr.getPlayerRid());
        Logo lg = playerLogo.getLogo(logo.getLogoId());
        PlayerBean pb = PlayerConsole.getPlayerBean(pr.getPlayerRid());
        BattlePlayer bp = new BattlePlayer(team.getTeamId(), pr.getId(), pb, pr.getPlayerPosition(), pr.getLineupPosition(),
            logo.getLogoId(), lg == null ? 0 : lg.getQuality()
            , attackSkill, defendSkill, caps.get(AbilityType.Player_Base), maxSkillPower);
        bp.putPlayerAbility(caps);//put all abilities
        if (log.isDebugEnabled()) {
            log.debug("btloc battleplayer. tid {} pid {} prid {} overSalary {} cap {} {} bp lppos {} pos {}",
                team.getTeamId(), pr.getId(), pr.getPlayerRid(), overSalary,
                bp.getAbility().get(ocap), bp.getAbility().get(dcap),
                bp.getLpPos(), bp.getPlayerPosition());
        }
        return bp;
    }

    private Skill getBattleSkill(int pid, PlayerSkill skill, List<SkillPositionBean> spbs,
                                 Function<PlayerSkill, Integer> sidMapper) {
        if (spbs == null || skill == null || spbs.isEmpty()) {
            return null;
        }
        int skillId = sidMapper.apply(skill);
        SkillPositionBean attackBean = spbs.stream().filter(sk -> sk.hasSkill(skillId)).findFirst().orElse(null);
        if (attackBean == null) {
            return null;
        }
        SkillBean skillBean = null;
        boolean type = false;
        int lev = 0;
        SkillTree st = skill.getSkillTree(attackBean.getStep());
        if (attackBean.getS1() == skillId) {
            lev = st.getS1();
            skillBean = attackBean.getSkill1();
        } else if (attackBean.getS2() == skillId) {
            lev = st.getS2();
            skillBean = attackBean.getSkill2();
        } else if (attackBean.getS3() == skillId) {
            lev = st.getS3();
            type = true;
            skillBean = attackBean.getSkill3();
        } else if (attackBean.getS4() == skillId) {
            lev = st.getS4();
            skillBean = attackBean.getSkill4();
            type = true;
        }
        if (skillBean != null) {
            return new Skill(pid, attackBean.getStep(), lev, type, skillBean);
        }
        return null;
    }

    private List<BattleTactics> getTactics(TeamTactics tactics) {
        return tactics.getTacticsMap().values().stream()
            .map(t -> new BattleTactics(TacticsConsole.getBean(t.getTactics()), t.getLevel()))
            .collect(Collectors.toList());
    }

    private List<SkillPositionBean> getSkillBeans(Player player, PlayerSkill skill) {
        if (skill == null) {
            return Collections.emptyList();
        }
        return skill.getSkillTree().stream()
            .map(sk -> SkillConsole.getSkillPositionBean(player.getPlayerPosition(), sk.getStep(), player.getPlayerRid()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /** 开始快速比赛 */
    void startQuick(BattleSource bs, BattleEnd end) {
        ManualBattleQuickHandle battle = new ManualBattleQuickHandle(bs, end, true);
        synchronized (battle) {
            try {
                battle.pk();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            battle.getEnd().end(bs);
        }
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleHandle handle) {
        start0(bs, null, handle);
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleEnd end) {
        BattleHandle battle = createBaseBattle(bs, end, roundReport, startPush);
        start0(bs, null, battle);
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleEnd end, BattleRoundReport round) {
        BattleHandle battle = createBaseBattle(bs, end, round, startPush);
        start0(bs, null, battle);
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleEnd end, BattleStep step) {
        BattleHandle battle = createBaseBattle(bs, end, roundReport, startPush);
        start0(bs, step, battle);
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleContxt bc) {
        BattleHandle battle = createBaseBattle(bs, bc.end, bc.round, bc.startPush);
        start0(bs, bc.step, battle);
    }

    /** 启动比赛 */
    public void start(BattleSource bs, BattleContxt bc, BattleHandle battle) {
        start0(bs, bc.step, battle);
    }

    private void start0(BattleSource bs, BattleStep step, BattleHandle battle) {
        // 添加监听
        long battleId = bs.getInfo().getBattleId();
        if (step != null && step.hasSteps()) {
            bs.setBattleStep(step);
        }
        if (bs.getHomeTid() == bs.getAwayTid()) {
            log.warn("lcbt start. bid {} type {} htid {} == atid {}. ret", bs.getId(), bs.getType(),
                bs.getHomeTid(), bs.getAwayTid());
            return;
        }
        //放入队列并开始运行比赛
        BattleAPI.getInstance().putBattle(battle);
        updateTeamBattleStatus(bs.getInfo().getBattleType(), bs.getHome(), battleId, "", EBattleRoomStatus.比赛中);
        updateTeamBattleStatus(bs.getInfo().getBattleType(), bs.getAway(), battleId, "", EBattleRoomStatus.比赛中);
        if (battle.getStartPush() != null) {//开始比赛通知
            battle.getStartPush().startPush(bs);
        }
    }

    public <T extends BaseBattleHandle> void initBattleWithContext(T bh, BattleSource bs, BattleContxt bc) {
        bh.setBattleSource(bs);
        bh.setEnd(bc.end);
        bh.setRound(bc.round);
        bh.setStartPush(bc.startPush);
        bh.init();
        //        return battle;
    }

    /** 比赛参数 */
    public static final class BattleContxt {
        private BattleEnd end;
        private BattleRoundReport round;
        private BattleStep step;
        private BattleStartPush startPush;

        public BattleContxt() {
        }

        public BattleContxt(BattleEnd end, BattleRoundReport round, BattleStep step, BattleStartPush startPush) {
            this.end = end;
            this.round = round;
            this.step = step;
            this.startPush = startPush;
        }

        public void setEnd(BattleEnd end) {
            if (end == null) {
                return;
            }
            this.end = end;
        }

        public void setStartPush(BattleStartPush startPush) {
            this.startPush = startPush;
        }
    }

    public BattleContxt defaultContext() {
        return new BattleContxt(end, roundReport, null, startPush);
    }

    public BattleContxt defaultContext(BattleEnd end) {
        return new BattleContxt(end, roundReport, null, startPush);
    }

    private BattleHandle createBaseBattle(BattleSource bs, BattleEnd end, BattleRoundReport round, BattleStartPush startPush) {
        BattleCommon bh = new BattleCommon(bs
            , end == null ? this.end : end
            , round == null ? this.roundReport : round);
        bh.setStartPush(startPush == null ? this.startPush : startPush);
        return bh;
    }

    private void updateTeamBattleStatus(EBattleType bt, BattleTeam team, long battleId, String nodeIp, EBattleRoomStatus status) {
        if (!team.getNodeName().equals(GameSource.serverName) || GameSource.isNPC(team.getTeamId())) {
            return;
        }
        TeamStatus ts = teamStatusManager.get(team.getTeamId());
        ts.putBattle(bt, battleId, nodeIp, "", status);
    }

    /** 比赛结束. 处理比赛数据, 球队数据, 发送消息 */
    public void sendEndMain(BattleSource source, boolean local) {
        sendEndMain(source.getInfo(), source.getHome(), source.getAway(), source.getEndReport(), local);
    }

    public void sendEndMain(BattleInfo info, BattleTeam home, BattleTeam away, EndReport endReport, boolean local) {
        long bid = info.getBattleId();
        if (local) {
            BattlePB.BattleEndMainData data = BattlePb.battleEndMainData(info.getBattleId(), endReport);
            sendMessage(ServiceConsole.getBattleKey(bid)
                , data, ServiceCode.Battle_PK_Stage_Round_Main_End);
        }
        log.debug("btloc end. bid {} local {}. nodename server {} home {} away {}", bid, local,
            GameSource.serverName, home.getNodeName(), away.getNodeName());
        //处理球队比赛数据
        battleEndTeam(bid, info, endReport, home, away);
        battleEndTeam(bid, info, endReport, away, home);
        saveBattle(bid, endReport, info, home, away); //将比赛数据保存到DB
        GameLogPB.BattleEndLogData endLog = BattlePb.getBattleEndLogData(home, away);
        redis.set(RedisKey.Battle_End_Source + bid, endLog, RedisKey.DAY2);
        // 通知比赛结束
        EventBusManager.post(EEventType.比赛结束, new PKParam(info.getBattleType(), home.getTeamId(), home.getTeamId() == endReport.getWinTeamId()));
        EventBusManager.post(EEventType.比赛结束, new PKParam(info.getBattleType(), away.getTeamId(), away.getTeamId() == endReport.getWinTeamId()));
        // 统计任务
        taskManager.updateTask(home.getTeamId(), ETaskCondition.打比赛, 1, info.getBattleType().getType());
        taskManager.updateTask(away.getTeamId(), ETaskCondition.打比赛, 1, info.getBattleType().getType());

    }

    /** 处理球队比赛数据 */
    private void battleEndTeam(long bid, BattleInfo info, EndReport report, BattleTeam selfTeam, BattleTeam otherTeam) {
        if (!selfTeam.getNodeName().equals(GameSource.serverName) || GameSource.isNPC(selfTeam.getTeamId())) {
            return;
        }
        updateTeamBattleStatus(info.getBattleType(), selfTeam, 0, "", EBattleRoomStatus.比赛结束);
        propManager.addPropList(selfTeam.getTeamId(), report.getAwardList(selfTeam.getTeamId()), true, ModuleLog.getModuleLog(EModuleCode.比赛, info.getBattleType().name()));
        sendMessage(selfTeam.getTeamId(), DefaultPB.DefaultData.newBuilder().setBigNum(bid).build(), ServiceCode.Push_Battle_End);
        playerManager.updatePlayerSource(selfTeam.getTeamId(), selfTeam.getPlayers());
        taskManager.updateTask(selfTeam.getTeamId(), ETaskCondition.完成一场比赛, 1,
            selfTeam.getLevel() + "," + otherTeam.getLevel() + "," + selfTeam.getScore() + "," + otherTeam.getScore() + "," + info.getBattleType().getId());

        if (selfTeam.isWin()) {
            Team team = teamManager.getTeam(selfTeam.getTeamId());
            team.battleWin();

            // 竞技场获胜任务
            taskManager.updateTask(selfTeam.getTeamId(), ETaskCondition.比赛胜利次数, 1, info.getBattleType().getType());
        }
    }

    /** 将比赛数据保存到DB */
    private void saveBattle(long battleId, EndReport report, BattleInfo info, BattleTeam home, BattleTeam away) {
        BattleInfoPO bp = new BattleInfoPO(battleId, info.getBattleType().getId()
            , home.getTeamId(), home.getName(), home.getScore(), away.getTeamId()
            , away.getName(), away.getScore());
        if (report.getBattleType() == EBattleType.Ranked_Match) {
            RankedMatchEnd rme = report.getAdditional(EBattleAttribute.Ranked_Match_End);
            if (rme != null) {
                RankedTeam homert = rme.getHome();
                RankedTeam awayrt = rme.getAway();
                bp.setVi1(homert.getNewTier());
                bp.setVi2(homert.getFinalRating());
                bp.setVi3(awayrt.getNewTier());
                bp.setVi4(awayrt.getFinalRating());

                bp.setVl1(homert.getChangeRating());
                bp.setVl2(awayrt.getChangeRating());
            }
        } else if (report.getBattleType() == EBattleType.AllStar) {
            AllStarMatchEnd asme = report.getAdditional(EBattleAttribute.All_Star_Match_End);
            if (asme != null) {
                bp.setVi1(asme.getSubTotalHp());
            }
        } else if (report.getBattleType() == EBattleType.Arena) {
            ArenaMatchEnd arend = report.getAdditional(EBattleAttribute.Arena_Match_End);
            if (arend != null) {
                bp.setVi1(arend.getSelfRank());
                bp.setVi2(arend.getTargetRank());
                bp.setVi3(arend.getOldMaxRank());
                bp.setVi4(arend.getMaxRankAward());
            }
        }
        bp.save();
    }

    /** 比赛历史记录类型 */
    public enum HistoryType {
        /** 主客场 */
        All,
        /** 主场 */
        Home,
        /** 客场 */
        Away
    }

    /**
     * 获取球队比赛历史记录
     *
     * @param pageSize 10 <= No. <= 50
     */
    public BattleHisListResp matchHistory(long teamId, EBattleType battleType,
                                          HistoryType ht,
                                          int pageSize, int pageNo,
                                          BiFunction<Builder, BattleInfoPO, Builder> handleHis) {
        Page page = new Page(Integer.MAX_VALUE, Math.min(Math.max(10, pageSize), 50), pageNo);
        List<BattleInfoPO> ret = Collections.emptyList();
        switch (ht) {
            case All:
                ret = battleDAO.findBattleHis(teamId, battleType, page.getOffset(), page.getPageSize());
                break;
            case Home:
                ret = battleDAO.findHomeBattleHis(teamId, battleType, page.getOffset(), page.getPageSize());
                break;
            case Away:
                ret = battleDAO.findAwayBattleHis(teamId, battleType, page.getOffset(), page.getPageSize());
                break;
        }
        log.debug("lcbt matchhis. tid {} bt {} ht {} ps {} pn {} off {} ret {}", teamId, battleType.getId(),
            ht.name(), page.getPageSize(), page.getPageNo(), page.getOffset(), ret.size());

        BattleHisListResp.Builder resp = BattleHisListResp.newBuilder()
            .setBt(battleType.getId())
            .setPageNo(pageNo);
        Set<Long> tids = new HashSet<>();
        for (BattleInfoPO bp : ret) {
            BattleHisResp.Builder his = BattlePb.historyResp(bp);
            if (handleHis != null) {
                his = handleHis.apply(his, bp);
            }
            resp.addHis(his.build());
            tids.add(bp.getHomeTeamId());
            tids.add(bp.getAwayTeamId());
        }
        for (Long tid : tids) {
            resp.addTeams(teamManager.teamResp(tid));
        }
        return resp.build();
    }

    /** 获取比赛id */
    public long getNewBattleId() {
        checkOrLoadBattleId();
        return battleIds.poll();
    }

    private void checkOrLoadBattleId() {
        if (battleIds.size() <= 1000) {
            if (System.currentTimeMillis() - refreshBattleTime >= 1000 * 5) {
                synchronized (this) {
                    loadbattleId();
                    //                    if (battleIds == null || battleIds.size() == 0) {
                    //                        log.warn("BattleId size is 0, pknode 拉取比赛ID异常!!!");
                    //                    }
                }
            }
        }
    }

    private synchronized void loadbattleId() {
        log.info("gen battleid ...");
        if (battleIds.size() > 1000) {
            return;
        }
        RpcResp<Tuple2Long> resp = RpcTask.ask0(CrossCode.BattleManager_getBattleIdList, "", 10, 10000);
        if (resp == null || resp.isError()) {
            log.error("gen battleid error {}", resp);
            return;
        } else {
            log.info("gen battleid resp {}", resp.t);
            Tuple2Long id = resp.t;
            for (long i = id._1() + 1; i <= id._2(); i++) {
                battleIds.add(i);
            }
            log.info("gen battleid resp {} done. size {} first {}", resp.t, battleIds.size(), battleIds.peek());
            refreshBattleTime = System.currentTimeMillis();
        }
    }

    public void end(BattleSource bs) {
        sendEndMain(bs.getInfo(), bs.getHome(), bs.getAway(), bs.getEndReport(), true);
    }

    public void roundReport(BattleSource bs, RoundReport report) {
        //            log.debug("比赛回合数据进行推送--->{}", source.getBattleInfo().getBattleId());
        sendMessage(ServiceConsole.getBattleKey(bs.getInfo().getBattleId())
            , BattlePb.battleRoundMainData(bs, report), ServiceCode.Battle_Round_Push);
    }

    public void startPush(BattleSource bs) {
        BattleStartData battleStartResp = BattlePb.battleStartResp(bs);
        //比赛开启成功推送，前端初始化比赛信息
        sendMessage(bs.getHomeTid(), battleStartResp, ServiceCode.Battle_Start_Push);
        sendMessage(bs.getAwayTid(), battleStartResp, ServiceCode.Battle_Start_Push);
    }

    @Override
    public void instanceAfter() {
        EventBusManager.register(EEventType.服务器节点注册, this);

        // 缓存本地battleId
        ThreadPoolUtil.newScheduledPool("loadbid", 1).schedule(() -> checkOrLoadBattleId(), 3000, TimeUnit.MILLISECONDS);
    }

    /** 节点注册回调 */
    @Subscribe
    private void nodeRegisterCall(RPCServer server) {
        // 若果是逻辑节点收到PK节点的注册信息，则检查是否拉取BattleId
        if (EServerNode.Battle.equals(server.getPool()) && GameSource.pool.equals(EServerNode.Logic)) {
            log.info("游戏服{}收到PK节点注册, 当前BattleId size={}", GameSource.serverName, battleIds.size());
            checkOrLoadBattleId();
        }
    }
}
