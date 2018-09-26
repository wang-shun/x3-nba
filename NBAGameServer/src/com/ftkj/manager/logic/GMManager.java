package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.DateTime;
import org.jredis.ri.alphazero.support.DefaultCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.annotation.UnCheck;
import com.ftkj.cfg.KnockoutMatchBean;
import com.ftkj.cfg.SystemActiveBean;
import com.ftkj.cfg.TeamExpBean;
import com.ftkj.console.CM;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.KnockoutMatchConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.SystemActiveConsole;
import com.ftkj.console.TaskConsole;
import com.ftkj.db.ao.common.IActiveAO;
import com.ftkj.db.domain.active.base.ActiveBase;
import com.ftkj.db.domain.active.base.SystemActivePO;
import com.ftkj.enums.EEmailType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.EPayType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.RechargeParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.active.SystemActiveManager;
import com.ftkj.manager.active.base.ActiveBaseManager;
import com.ftkj.manager.battle.BattleManager;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.task.TaskBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.manager.train.Train;
import com.ftkj.proto.AtvCommonPB;
import com.ftkj.proto.AtvCommonPB.AtvCommonData;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.ListsUtil;
import com.ftkj.util.StringUtil;
import com.ftkj.util.tuple.Tuple3;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;

/**
 * @author tim.huang
 * 2017年5月2日
 * GM管理
 */
public class GMManager extends AbstractBaseManager {
    private static final Logger log = LoggerFactory.getLogger(GMManager.class);
    private static final Logger paylog = LoggerFactory.getLogger("com.ftkj.manager.logic.GMManagerPay");
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private TeamDayStatsManager dayStatsManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerCardManager playerCardManager;
    @IOC
    private TeamEmailManager teamEmailManager;
    @IOC
    private TeamCapManager teamCapManager;
    @IOC
    private PlayerGradeManager playerGradeManager;
    @IOC
    private MainMatchManager mainMatchManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private LcRankedMatchManager lcRankedMatchManager;
    @IOC
    private GameManager gameManager;
    @IOC
    private IActiveAO activeAO;
    @IOC
    private AllStarManager allStarManager;
    @IOC
    private KnockoutMatchPKManager knockoutMatchPKManager;
    @IOC
    private ArenaService arenaService;
    @IOC
    private SystemActiveManager systemActiveManager;
    @IOC
    private CacheManager cacheManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private BattleManager battleManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private TrainManager trainManager;
    @IOC
    private LeagueArenaManager leagueArenaManager;
    @IOC
    private LocalDraftManager localDraftManager;
    @IOC
    private StarletManager starletManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private LimitChallengeManager limitChallengeManager;
    @IOC
    private HonorMatchManager honorMatchManager;

    private static final ModuleLog module = ModuleLog.getModuleLog(EModuleCode.GM, "");

    @ClientMethod(code = ServiceCode.GMManager_seachTeamName)
    @UnCheck
    public void seachTeamIdByName(String name) {
        long teamId = teamManager.getTeamId(name);
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("" + teamId).build());
    }

    @ClientMethod(code = ServiceCode.GMManager_addMoney)
    @UnCheck
    public void addMoney(long teamId, int money, int gold, int exp) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        if (tm == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("参数信息有误。检查清楚啊大哥~").build());
            return;
        }
        teamMoneyManager.updateTeamMoneyUnCheck(tm, money, gold, exp, 0, true, ModuleLog.getModuleLog(EModuleCode.GM, ""));
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("添加成功，尽情的挥霍吧").build());
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_lockTeam)
    public void lockTeam0(long teamId, int type) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        teamManager.changeTeamUserStatus(teamId, type);
    }

    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_lockTeam, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void lockTeam1(long teamId, int type) {
        teamManager.changeTeamUserStatus(teamId, type);
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_chatController)
    public void chatController0(long teamId, int type) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        teamManager.changeTeamChatStatus(teamId, type);
    }

    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_chatController, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void chatController1(long teamId, int type) {
        teamManager.changeTeamChatStatus(teamId, type);
    }

    /**
     * 充值球券回调
     *
     * @param fk     球券数(rmb不详) 基本上保证fk的唯一性
     * @param type   充值类型
     * @param millis 时间戳
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_addMoneyCallback)
    public void addMoneyCallback0(long teamId, int fk, int type, long millis) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        addMoneyCallback1(teamId, fk, type, millis);
    }

    /**
     * 充值球券回调
     *
     * @param fk     球券数(rmb不详) 基本上保证fk的唯一性
     * @param type   充值类型
     * @param millis 时间戳
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_addMoney, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void addMoneyCallback1(long teamId, int fk, int type, long millis) {
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        if (tm == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("参数信息有误。检查清楚啊大哥~").build());
            return;
        }
        EPayType payType = EPayType.convertType(type);
        // 只有充值类型才是自由金额的加球券
        if (payType == EPayType.充值 || payType == EPayType.GM充值) {
            teamMoneyManager.updateTeamMoneyUnCheck(tm, fk, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.充值, payType.name()));
        }
        millis = millis == 0 ? DateTime.now().getMillis() : millis;
        // 触发事件
        RechargeParam param = new RechargeParam(teamId, payType, fk, millis);
        EventBusManager.post(payType.getEvent(), param);
        RPCMessageManager.responseMessage(0);
        paylog.info("pay 充值 tid {} type {} fk {} time {}", teamId, type, fk, DateTimeUtil.getStringSql(param.time));
        AtvCommonPB.AddMoneyItem rtnData = AtvCommonPB.AddMoneyItem.newBuilder().setType(type).setFk(fk).build();
        sendMessage(teamId, rtnData, ServiceCode.GMManager_addMoneyPush);
    }

    @ClientMethod(code = ServiceCode.GMManager_addPlayer)
    @UnCheck
    public void addPlayer(long teamId, int playerId, int price) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        ErrorCode ret = addPlayer0(teamId, playerId, price);
        String msg = (ret.getTip() == null || ret.getTip().equals("")) ?
            ret.name() : ret.getTip();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ret.code).setMsg(msg).build());
    }

    private ErrorCode addPlayer0(long teamId, int playerId, int price) {
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        if (tp == null) {
            return ErrorCode.Team_Null;
        }
        
        Player tmp = tp.getPlayer(playerId);
        if (tmp != null) {
            return ErrorCode.Player_0;// 场上存在相同球员，无法获得
        }
        
        PlayerBean playerBean = PlayerConsole.getPlayerBean(playerId);
        if (playerBean == null) {
            return ErrorCode.Player_Prop_Bean_Null;
        }
        if (price == 0) {
            price = playerBean.getPrice();
        }
        PlayerTalent pt = PlayerTalent.createPlayerTalent(teamId, playerId, tp.getNewTalentId(), PlayerManager._initDrop, true);
        Player player = tp.createPlayer(playerBean, price, EPlayerPosition.NULL.name(), pt);
        tp.putPlayerTalent(pt);
        return playerManager.addPlayer(teamId, tp, player, playerBean, true, ModuleLog.getModuleLog(EModuleCode.GM, ""));
    }

    @ClientMethod(code = ServiceCode.GMManager_addProp)
    @UnCheck
    public void addProp(long teamId, int propId, int num) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        List<PropSimple> psList = Lists.newArrayList();
        psList.add(new PropSimple(propId, num));
        propManager.addPropList(teamId, psList, true, ModuleLog.getModuleLog(EModuleCode.GM, ""));
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("添加成功，尽情的挥霍吧").build());
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_makeCard)
    public void makeCard(long teamId, int playerId) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        if (PlayerConsole.getPlayerBean(playerId) == null) {
            playerId = PlayerConsole.getRanPlayer(3).get(0).getPlayerRid();
        }
        playerCardManager.markCard(teamId, new int[]{playerId});
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("添加成功，点一次加一张卡。").build());
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_clearRedis)
    public void clearRedis(String key) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        if ("".equals(key)) {
            return;
        }
        try (Jedis j = redis.getJedis()) {
            Set<String> keys = j.keys("*" + key + "*");
            keys.forEach(k -> j.del(DefaultCodec.encode(k)));
            sendMessage(DefaultPB.DefaultData.newBuilder().setMsg(StringUtil.formatString("总共清理了{}个{}相关的缓存数据", keys.size(), key)).build());
        }
    }

    public void updateServerStatus(int type) {
        log.info("停止接收客户端请求");
        GameSource.updateOpen(type == 1);
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_addPlayerStar)
    public void addPlayerStar(long teamId) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        playerGradeManager._GMAddStar(teamId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg("添加成功，尽情的挥霍吧").build());
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_sendEmail)
    public void sendEmail(long teamId, int viewId, String title, String content, String awardConfig) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        teamEmailManager.sendEmailFinal(teamId, EEmailType.系统邮件.getType(), viewId, title, content, awardConfig);
    }

    /**
     * 攻防测试接口
     * 根据类型打印不同的攻防
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_Cap_Test)
    public void testCap(long teamId, int type) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        String val = teamCapManager.getTeamAllPlayerAbilities(teamId).toString();
        //log.warn("\n球队攻防：{} \n攻防明细：{}", val, teamCapManager.getTestTeamCap(teamId));
        if (type == 0) {
            log.warn("\n球队攻防：{}", val);
        } else if (type == 1) {
            log.warn("\n场下攻防：{}", teamCapManager.getTeamOtherCap(teamId));
        } else if (type == 2) {
            log.warn("\n装备攻防：{}", teamCapManager.getTestEquiCap(teamId));
        } else if (type == 3) {
            log.warn("\n总攻防：{}", teamManager.getTeamCap(teamId));
        } else if (type == 4) {
            log.warn("\n首发阵容：{}", teamCapManager.getLineupAbilities(teamId));
        } else if (type > 1000) {
            log.warn("\n球员{} 球星卡加成 {}", type, teamCapManager.getPlayerCardCap(playerCardManager.getTeamPlayerCard(teamId), type));
        }
        sendMessage(DefaultData.newBuilder().setCode(0).setMsg(val).build());
    }

    /** gm命令强制结束比赛 */
    @ClientMethod(code = ServiceCode.GMManager_End_Match)
    public void quickBattle(long battleId) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        ErrorCode ret = battleManager.gmQuickBattle0(getTeamId(), battleId, true);
        sendMsg(ret);
    }

    /** gm命令强制结束当前所有比赛 */
    private ErrorCode quickAllBattle(long teamId, boolean homeWin) {
        TeamStatus status = teamStatusManager.get(teamId);
        for (TeamBattleStatus bs : status.getStates()) {
            if (bs.getBattleId() > 0 && !bs.isTimeOut()) {
                if (bs.getNodeIp() != null && !bs.getNodeIp().isEmpty()) {
                    RpcTask.tell(CrossCode.Match_Force_End, bs.getNodeName(), teamId, bs.getBattleId(), homeWin);
                } else {
                    battleManager.gmQuickBattle0(teamId, bs.getBattleId(), homeWin);
                }
            }
        }
        return ErrorCode.Success;
    }

    /** gm命令. 字符串(易于打字输入的)gm命令 */
    @ClientMethod(code = ServiceCode.GM_By_Type)
    public void typeGMCommand(String commandStr) {
        long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        try {
            Tuple3<ErrorCode, GmCommand, String[]> tp = prepare(teamId, commandStr);
            ErrorCode retCode = tp._1();
            if (tp._1().isSuccess()) {
                retCode = executeGmCommand(teamId, team, commandStr, tp._3(), tp._2());
            }
            sendMsg(retCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMsg(ErrorCode.Error);
        }
    }

    /** 解析gm命令 */
    private Tuple3<ErrorCode, GmCommand, String[]> prepare(long tid, String commandStr) {
        log.warn("gm tid[{}] input gm command [{}]", tid, commandStr);
        if (!GameSource.GM) {
            return Tuple3.create(ErrorCode.GmDisable);
        }
        String[] arr = commandStr.split(" ");
        if (arr.length < 1) {
            log.error("gm command too short: {}", commandStr);
            return Tuple3.create(ErrorCode.Gm_Unknown);
        }

        GmCommand gc = GmCommand.convertTo(arr[0]);
        if (gc == null) {
            log.error("gm command cannot convert: {}", arr[0]);
            return Tuple3.create(ErrorCode.Gm_Unknown);
        }

        return Tuple3.create(ErrorCode.Success, gc, arr);
    }

    /** 执行gm命令 */
    private ErrorCode executeGmCommand(long teamId, Team team, String commandStr, String[] arr, GmCommand gc) {
        if (gc.isNeedTeamLogin() && team != null && !GameSource.isOline(team.getTeamId())) {
            return ErrorCode.Team_Offline;
        }
        if (GameSource.isNPC(teamId)) {
            return ErrorCode.Team_Npc;
        }
        ErrorCode ret = ErrorCode.Success;
        switch (gc) {
            case test: 
                trainManager.choiseTeam(1, 1);
                break;
            case add_player: 
               this.addPlayer(teamId, toInt(arr[1]), 0);
                break;
            case del_La_All: 
                leagueArenaManager.gmClearAll();
                break;
            case reLea_ScoreRanks:
                leagueArenaManager.refreshLeagueWeekScoreRank();
                break;
            case Train_trainUpLevel:
                Train train = trainManager.getTrainMapByTeamIdAndTrainId(teamId, toInt(arr[1]));
                if (train == null) { return ErrorCode.Train_Not_Exist; }
                trainManager.trainUpLevel(train, toInt(arr[2]));
                break;
            case Create_Task:
                TaskBean taskBean = TaskConsole.getTaskBean(toInt(arr[1]));
                if (taskBean == null) { return ErrorCode.Task_1; }
                taskManager.createTask(teamId, taskBean);
                break;
            case Finsh_Task:
                taskManager.finishTask(teamId, toInt(arr[1]));
                break;
            case Reload_NbaData:
                cacheManager.resetCache();
                playerManager.reloadPlayerMinPrice();
                gameManager.reloadNBAData();
                break;
            case qtl:
              taskManager.gmCompelete(teamId);
              break;
            case Team_Level:
                gmSetTeamLevel(team, toInt(arr[1]));
                break;
            case Team_Lev_Up:
                gmUpgradeTeamLevel(team, toInt(arr[1]));
                break;
            case Pay:
                addMoneyCallback0(teamId, toInt(arr[2]), toInt(arr[1]), 0);
                break;
            case Prop_Add:
                this.addProp(teamId, toInt(arr[1]), toInt(arr[2]));
                break;
            case Props_Add:
                propManager.addPropList(teamId, PropSimple.conertItem(toIntMap(arr)), true, module);
                break;
            case Team_Currency:
                teamMoneyManager.update(teamId, EMoneyType.convertByName(arr[1].toLowerCase()), toInt(arr[2]), true, module);
                break;
            case Team_Reset_Daily:
                ret = resetTeamDaily(teamId, team);
                break;
            case Team_Num_Set:
                ret = teamNumManager.gmResetNum(teamId, toInt(arr[1]), toInt(arr[2]));
                break;
            case Team_Day_Num_Set:
                dayStatsManager.saveTeamNums(teamId, TeamDayNumType.convert(toInt(arr[1])), toInt(arr[2]));
                break;
            case Match_Force_End_All:
                ret = quickAllBattle(teamId, toBool(arr[1]));
                break;
            case Match_Force_End:
                ret = battleManager.gmQuickBattle0(teamId, toInt(arr[1]), toBool(arr[2]));
                break;
            case MainMatch_Reset_Sys_Rank:
                ret = mainMatchManager.gmResetSysRank();
                break;
            case MainMatch_Reset_Team_CS_Target:
                ret = mainMatchManager.gmResetTeamCSTarget(team);
                break;
            case MainMatch_Enable_Lev:
                ret = mainMatchManager.gmEnableLev(team, toInt(arr[1]), getOr(arr, 2, 1));
                break;
            case MainMatch_Enable_Lev_Full_Star:
                ret = mainMatchManager.gmEnableLevsFullStar(team, toIntList(arr));
                break;
            case MainMatch_Lev_Star:
                ret = mainMatchManager.gmLevStar(team, toInt(arr[1]), toInt(arr[2]));
                break;
            case MainMatch_Lev_Match_Num:
                ret = mainMatchManager.gmMatchCount(team, toInt(arr[1]), toInt(arr[2]));
                break;
            case MainMatch_Reset_Div_Awards:
                ret = mainMatchManager.gmResetLevAwards(team, toInt(arr[1]));
                break;
            case RMatch_Refresh_Rank:
                ret = lcRankedMatchManager.gmRefreshRank();
                break;
            case RMatch_Join_Pool:
                ret = lcRankedMatchManager.gmJoin(getOrLong(arr, 1, teamId));
                break;
            case RMatch_Rating:
                ret = lcRankedMatchManager.gmUpdateRating(teamId, toInt(arr[1]));
                break;
            case RMatch_Season_Award:
                ret = lcRankedMatchManager.gmSeasonEndAward();
                break;
            case AllStar_Restart:
                ret = allStarManager.gmRestart(toInt(arr[1]), toInt(arr[2]));
                break;
            case AllStar_Hp:
                ret = allStarManager.gmSetHp(toInt(arr[1]), toInt(arr[2]));
                break;
            case AllStar_Award:
                ret = allStarManager.gmAward(toInt(arr[1]));
                break;
            case AllStar_Team_Hp:
                ret = allStarManager.gmTeamHp(teamId, toInt(arr[1]), toInt(arr[2]));
                break;
            case Arena_Reset_Cd:
                arenaService.gmResetCd(teamId);
                break;
            case Arena_Award:
                arenaService.gmRankAward();
                break;
            case Activity_Set:
                AtvCommonPB.AtvCommonData cd = convertAtvData(arr);//idx 2 -> max.
                systemActiveManager.gmResetAtv(teamId, toInt(arr[1]), cd);
                break;
            case Rank_Team:
                rankManager.gmRecalcTeamCap(teamId);
                break;
            case Draft_Join:
                int addNum = toInt(arr[2]);
                for (long tid : teamManager.getRanTeam(addNum)) {
                    localDraftManager.gmJoinRoom(toInt(arr[1]), tid);
                }
                break;
            case add_cks_npc:
              int npcNum = toInt(arr[1]);
              int cap = toInt(arr[2]);
              knockoutMatchPKManager.gmSignNpc(teamId, npcNum, cap);
              break;
            case Clear_Card:
                int type = toInt(arr[1]);
                int playerId = toInt(arr[2]);
                playerCardManager.clearCardData(teamId, type, playerId);
                break;
            case Add_Player_Low:
                PlayerBean pb = PlayerConsole.getPlayerBean(toInt(arr[1]));
                if (pb == null) {
                    break;
                }
                TeamPlayer tp = playerManager.getTeamPlayer(teamId);
                Player player = tp.createPlayer(pb, playerManager.getPlayerMinPrice(pb.getPlayerRid()), EPlayerPosition.NULL.name(), PlayerTalent.Empty);
                playerManager.addPlayerToStore(teamId, tp, player, pb, module);
                break;
            case honor:
              honorMatchManager.gmCompletePass(teamId, toInt(arr[1]), toInt(arr[2]), toInt(arr[3]));
              break;
            default:
                log.error("not found gm command: {}", commandStr);
                break;
        }

        return ret;
    }

    /** 转换 运营活动 gm命令 */
    private AtvCommonData convertAtvData(String[] arr) {
        Map<String, String> strtuples = toStrMap(arr, 2);
        AtvCommonPB.AtvCommonData.Builder cd = AtvCommonPB.AtvCommonData.newBuilder();
        cd.setAtvId(0);
        for (Entry<String, String> e : strtuples.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            switch (k.toLowerCase()) {
                case "extend":  // ext -> int,int,int,
                    cd.setExtend(v);
                    break;
                case "value":  // val -> int
                    cd.setValue(toInt(v));
                    break;
                case "other":  // val -> int
                    cd.setOther(toInt(v));
                    break;
                case "finishStatus":  // fs -> int,int,int,
                    cd.addAllFinishStatus(Arrays.asList(StringUtil.toIntegerArray(v)));
                    break;
                case "awardStatus":  // as -> int,int,int,
                    cd.addAllAwardStatus(Arrays.asList(StringUtil.toIntegerArray(v)));
                    break;
            }
        }
        return cd.build();
    }

    /** 重置球队每日状态 */
    private ErrorCode resetTeamDaily(long teamId, Team team) {
        lcRankedMatchManager.gmResetDaily(teamId);
        dayStatsManager.gmResetAllDailyNum(teamId);
        honorMatchManager.resetChallengeNum(teamId);
        return ErrorCode.Success;
    }

    private int getOr(String[] arr, int idx, int defaultValue) {
        if (idx < arr.length) {
            return toInt(arr[idx]);
        }
        return defaultValue;
    }

    private long getOrLong(String[] arr, int idx, long defaultValue) {
        if (idx < arr.length) {
            return toLong(arr[idx]);
        }
        return defaultValue;
    }

    private List<Integer> toIntList(String[] arr) {
        List<Integer> ints = new ArrayList<>(arr.length - 1);
        for (int i = 1; i < arr.length; i++) {
            ints.add(toInt(arr[i]));
        }
        return ints;
    }

    private Map<Integer, Integer> toIntMap(String[] arr) {
        Map<Integer, Integer> resourceIdAndNums = new HashMap<>();
        for (int i = 1; i < arr.length; i += 2) {
            resourceIdAndNums.put(toInt(arr[i]), toInt(arr[i + 1]));
        }
        return resourceIdAndNums;
    }

    private Map<String, String> toStrMap(String[] arr, int startIdx) {
        Map<String, String> strMap = new HashMap<>();
        for (int i = startIdx; i < arr.length; i += 2) {
            strMap.put(arr[i], arr[i + 1]);
        }
        return strMap;
    }

    /** 去掉双引号 */
    private String toStr(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(1, s.length() - 1);
    }

    private long toLong(String s) {
        return Long.parseLong(s);
    }

    private int toInt(String str) {
        return Integer.parseInt(str);
    }

    private boolean toBool(String str) {
        return Integer.parseInt(str) == 1;
    }

    private int getInt(String[] arr, int idx) {
        if (ListsUtil.rangeCheck(arr.length, idx)) {
            return toInt(arr[idx]);
        }
        return 0;
    }

    private long getLong(String[] arr, int idx) {
        if (ListsUtil.rangeCheck(arr.length, idx)) {
            return toLong(arr[idx]);
        }
        return 0;
    }

    /**
     * 同步活动配置
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_syncAllSystemActive)
    public void syncAllSystemActive0() {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        List<SystemActivePO> activePoList = activeAO.getSystemActiveList(GameSource.shardId);
        SystemActiveConsole.initActiveToDB(activePoList, CM.systemActiveList);
    }

    /**
     * 同步活动配置
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_syncAllSystemActive, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void syncAllSystemActive1() {
        List<SystemActivePO> activePoList = activeAO.getSystemActiveList(GameSource.shardId);
        SystemActiveConsole.initActiveToDB(activePoList, CM.systemActiveList);
    }

    /**
     * 批量更新活动时间，直接生效了
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_updateActiveTimeBatch)
    public void updateActiveTimeBatch0(String atvIds, long startMillis, long endMillis, int status) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        updateActiveTimeBatch1(atvIds, startMillis, endMillis, status);
    }

    /**
     * 批量更新活动时间，直接生效了
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_updateActiveTimeBatch, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void updateActiveTimeBatch1(String atvIds, long startMillis, long endMillis, int status) {
        DateTime startTime = new DateTime(startMillis);
        DateTime endTime = new DateTime(endMillis);
        for (String atvId : atvIds.split(",")) {
            SystemActiveBean bean = SystemActiveConsole.getSystemActive(Integer.valueOf(atvId));
            if (bean == null) { continue; }
            SystemActivePO po = bean.getActive();
            if (po == null) { continue;}
            po.setStartTime(startTime);
            po.setEndTime(endTime);
            po.setStatus(status);
            po.save();
        }

    }

    /**
     * 修改活动配置
     *
     * @param atvId
     * @param userConfig
     * @param event      触发事件是 1
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_updateActiveConfigBatch, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void updateActiveTimeBatch(int atvId, String userConfig, int event) {
        // 记得要setActivePO， 配置才会覆盖。
        SystemActiveBean bean = SystemActiveConsole.getSystemActive(atvId);
        if (bean == null) {
            return;
        }
        SystemActivePO po = bean.getActive();
        if (po == null) {
            return;
        }
        po.setJsonConfig(userConfig);
        po.save();
        bean.setActive(po);
        // 触发事件
        if (event == 1) {
            ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
            if (manager == null) {
                return;
            }
            manager.shootEvent();
        }
    }

    /**
     * 发布活动，推送活动时间到客户端
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_queryActiveData)
    public void queryActiveData0(long teamId, int atvId) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        queryActiveData1(teamId, atvId);
    }

    /**
     * 查询球队活动数据
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_queryActiveData, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void queryActiveData1(long teamId, int atvId) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        ActiveBase data = null;
        if (manager != null) {
            data = manager.getTeamData(teamId);
        }
        Gson gson = new Gson();
        RPCMessageManager.responseMessage(data == null ? "" : gson.toJson(data));
    }

    /**
     * 清空活动数据，重开活动用到。
     * 结束的活动才可以重置
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_clearActiveData)
    public void clearActiveData0(String atvIds) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        clearActiveData1(atvIds);
    }

    /**
     * 清空活动数据，重开活动用到。</BR>
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_clearActiveData, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void clearActiveData1(String atvIds) {
        for (String atvId : atvIds.split(",")) {
            ActiveBaseManager manager = ActiveBaseManager.getManager(Integer.valueOf(atvId));
            if (manager != null) {
                manager.clearActiveBase();
            }
        }
    }

    /**
     * 清指定球队的活动数据
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_clearTeamActiveData)
    public void clearTeamActiveData0(long teamId, int atvId) {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        clearTeamActiveData1(teamId, atvId);
    }

    /**
     * 清指定球队的活动数据
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_clearTeamActiveData, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void clearTeamActiveData1(long teamId, int atvId) {
        ActiveBaseManager manager = ActiveBaseManager.getManager(atvId);
        if (manager != null) {
            manager.clearActiveBase(teamId);
        }
    }

    /**
     * 创建数据库表
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_createActiveDataTable)
    public void createActiveDataTable0() {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        activeAO.createActiveDataTable();
    }

    /**
     * 创建数据库表
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_createActiveDataTable, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void createActiveDataTable1() {
        activeAO.createActiveDataTable();
    }

    /**
     * 运营后台清档操作
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_resetServerData)
    public void resetServerData0() {
        if (!GameSource.GM) {
            sendMsg(ErrorCode.GmDisable);
            return;
        }
        resetServerData1();
    }

    /**
     * 运营后台清档操作
     */
    @UnCheck
    @RPCMethod(code = CrossCode.WebManager_resetServerData, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void resetServerData1() {
        // FIXME 清档操作
        //前置要求： 球队总数20以内 ，大于20，则认为是已经正式开服
        Set<Long> teamSet = teamManager.getAllTeam();
        if (teamSet.size() > 20) {
            return;
        }
        // 清理redis
        for (long teamId : teamSet) {
            redis.delRedisCache("*" + teamId + "*");
        }
        // 下线
        teamManager.offlineAll();
        // 删DB
        teamManager.clearAllData();

    }

    /**
     * 重置多人赛
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GMManager_resetMatchPk)
    @RPCMethod(code = CrossCode.WebManager_resetMatch, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void resetMatchPk(int matchId, int seqId) {
        log.warn("GM重置多人赛 {} {}", matchId, seqId);
        //        if (!GameSource.GM) {
        //            sendMsg(ErrorCode.GmDisable);
        //            return;
        //        }
        KnockoutMatchBean bean = KnockoutMatchConsole.getMatchById(matchId);
        if (bean == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).setMsg("重置失败").build());
            return;
        }
        knockoutMatchPKManager.resetMatch(matchId, seqId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).setMsg("重置成功").build());
    }

    /**
     * 球队升级
     */
    private void gmUpgradeTeamLevel(Team team, int addlev) {
        gmSetTeamLevel(team, team.getLevel() + addlev);
    }

    /**
     * 设置队伍等级
     */
    private void gmSetTeamLevel(Team team, int level) {
        if (team == null || team.getLevel() > level) {
            return;
        }
        TeamExpBean eb = GradeConsole.getTeamExpBean(level - 1);
        if (eb == null) {
            return;
        }
        TeamMoney tm = teamMoneyManager.getTeamMoney(team.getTeamId());
        if (tm == null) {
            return;
        }
        int addexp = eb.getTotal() - tm.getExp() + 1;
        if (addexp < 0) {
            return;
        }
        teamMoneyManager.update(tm, EMoneyType.Exp, addexp, module);
    }  
}
