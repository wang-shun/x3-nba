package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

import org.apache.mina.core.session.IoSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.annotation.UnCheck;
import com.ftkj.cfg.TeamPriceMoneyBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.GameConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.console.ShopConsole;
import com.ftkj.console.TaskConsole;
import com.ftkj.db.ao.common.INBADataAO;
import com.ftkj.db.ao.logic.ITeamAO;
import com.ftkj.db.domain.NBAPKSchedule;
import com.ftkj.db.domain.NBAPKScoreBoard;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.db.domain.PropPO;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EChat;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.TeamDayNumType;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.User;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.active.SystemActiveManager;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.manager.equi.TeamEqui;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.TeamDayStatsManager.TeamNums;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.logo.PlayerLogoMT;
import com.ftkj.manager.logo.TeamPlayerLogo;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerSimple;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.tactics.TeamTactics;
import com.ftkj.manager.task.Task;
import com.ftkj.manager.task.TaskStarAwardBean;
import com.ftkj.manager.task.TeamTask;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.ChatPB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.GameLoadPB;
import com.ftkj.proto.PlayerCardPB.PlayerCardMainData;
import com.ftkj.proto.PlayerLogoPB;
import com.ftkj.proto.PlayerPB;
import com.ftkj.proto.PropPB;
import com.ftkj.proto.PropPB.TeamPropsData;
import com.ftkj.proto.TargetPB.DraftData;
import com.ftkj.proto.TargetPB.KnockoutMatchPKData;
import com.ftkj.proto.TargetPB.MainScheduleData;
import com.ftkj.proto.TargetPB.TargetMainData;
import com.ftkj.proto.TargetPB.TaskDayData;
import com.ftkj.proto.TargetPB.TaskStepData;
import com.ftkj.proto.TargetPB.TeamMatchData;
import com.ftkj.proto.TeamPB;
import com.ftkj.proto.TradePB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.server.http.HttpClient;
import com.ftkj.server.http.bean.FTXLogin;
import com.ftkj.server.http.bean.FTXLoginResponse;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.JsonUtil;
import com.ftkj.util.MD5Util;
import com.ftkj.util.MapsUtils;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author tim.huang
 * 2017年3月2日
 * 游戏管理
 */
public class GameManager extends BaseManager {
    private static final Logger log = LoggerFactory.getLogger(GameManager.class);

    @IOC
    private ITeamAO teamAO;
    @IOC
    private TeamManager teamManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TacticsManager tacticsManager;
    @IOC
    private EquiManager teamEquiManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerCardManager playerCardManager;
    @IOC
    private PlayerLogoManager playerLogoManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private TradeManager tradeManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private KnockoutMatchPKManager matchManager;
    @IOC
    private TeamEmailManager teamEmailManager;
    @IOC
    private FriendManager friendManager;
    @IOC
    private LeagueHonorManager leagueHonorManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private SystemActiveManager systemActiveManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private PlayerGradeManager playerGradeManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private RedPointManager redPointManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private StageManager stageManager;
    @IOC
    private INBADataAO nbaDataAO;
    @IOC
    private TeamDayStatsManager teamDayStatsManager;
    @IOC
    private LocalDraftManager localDraftManager;
    @IOC
    private StreetballManager streetballManager;
    @IOC
    private BattlePVPManager battlePVPManager;
    @IOC
    private LcRankedMatchManager lcRankedMatchManager;
    @IOC
    private TrainManager trainManager;

    private int playerVersion;
    private GameLoadPB.NBAPlayerDataMain nbaData;
    /**
     * 今日赛程
     */
    private GameLoadPB.NBAPKDataMain nbaPKData;
    private Map<Integer, GameLoadPB.NBAPKInfoDataMain> nbaPKInfoMap;
    private Map<Integer, PlayerPB.NBAPlayerAvgData> nbaPlayerAvgMap;

    private PlayerPB.NBAPlayerAvgData maxPlayerAvgData;

    private Set<Long> _backTeamIdList;

    private List<PropSimple> _initPropList;

    @ClientMethod(code = ServiceCode.GameManager_loadNBAPKMain)
    public void loadNBAPKMain() {
        sendMessage(nbaPKData);
    }

    @ClientMethod(code = ServiceCode.GameManager_loadNBAPKInfoMain)
    public void loadNBAPKInfoMain(int gameId) {
        sendMessage(nbaPKInfoMap.get(gameId));
    }

    @ClientMethod(code = ServiceCode.GameManager_showMaxPlayerAvgData)
    public void showMaxPlayerAvgData() {
        sendMessage(maxPlayerAvgData);
    }

    @RPCMethod(code = CrossCode.WebManager_pushAllGame, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void pushAllGame(String msg, String node) {
        if ("all".equals(node) || node.indexOf(GameSource.serverName) >= 0) {
            ChatPB.GameTipData data = ChatPB.GameTipData.newBuilder().setLevel(0).setModuleId(0).setVals(msg).build();
            sendMessage(ServiceConsole.getChatKey(EChat.世界聊天), data, ServiceCode.Push_Window_All);
            //            sendMessage(GameSource.getUsers(), DefaultPB.DefaultData.newBuilder().setMsg(msg).build(), ServiceCode.Push_Window_All);
        }
    }

    @ClientMethod(code = ServiceCode.GameManager_debugLogin)
    @UnCheck
    public void debugLogin(long userId) {
        //		if(!GameSource.isDebug) return;//不是测试环境，不允许调用该方法
        //
        long teamId = GameSource.getTeamId(userId);
        User user = GameSource.getUser(teamId);
        if (user != null && user.getSession().isActive()) {//帐号已登录，把对方踢下线
            user.getSession().setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, 0l);
            //			sendMessage(teamId,DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Login_Out);
            user.getSession().closeNow();
        } else {
            GameSource.offlineUser(teamId);
        }
        IoSession session = getSession();
        session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
        GameSource.online(teamId, session);
        if (teamManager.existTeam(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Success).setBigNum(teamId).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Create).setBigNum(teamId).build());
        }
    }

    /**
     * 登录验证
     */
    @ClientMethod(code = ServiceCode.GameManager_loginPC)
    @UnCheck
    public void login(long accountId, String md5, long time, String verToken, int version) {
        //	long t = System.currentTimeMillis() - time;
        //        if (!GameSource.isDebug && t > 5 * 60 * 1000) {//时间超时
        //            return;
        //        }
        GlobalBean gb = ConfigConsole.global();
        if (!gb.versionMajor.contains(verToken)) {
            loginResp(ErrorCode.Version_Major.code, accountId);
            return;
        }
        if (version < gb.versionMinor) {
            loginResp(ErrorCode.Version_Minor.code, accountId);
            return;
        }
        String m = MD5Util.encodeMD5(accountId, time).toLowerCase();
        long teamId = GameSource.getTeamId(accountId);
        if (_backTeamIdList.contains(teamId)) {
            log.error("黑名单玩家尝试登录:{}", teamId);
            loginResp(ErrorCode.UserLock.code, teamId);
            return;
        }
        IoSession session = getSession();
        //增加后缀判定. 防止在调试模式下登录外网创建有效球队
        boolean pass = GameSource.isDebug ? md5.endsWith("windows") : md5.toLowerCase().equals(m);
        if (pass) {
            User user = GameSource.getUser(teamId);

            if (user != null && user.getSession().isActive() && user.getSession().getId() != session.getId()) {
                //				user.getSession().setAttribute("teamId",0l);
                //				sendMessage(teamId,DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Login_Out);
                //				user.getSession().closeNow();
                //			}else{
                GameSource.offlineUser(teamId);
            }

            session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
            GameSource.online(teamId, session);
            if (teamManager.existTeam(teamId)) {
                loginResp(GameConsole.Game_Load_Code_Success, teamId);
                matchManager.loginMatchStartTipTopic(teamId);
            } else {
                loginResp(GameConsole.Game_Load_Code_Create, teamId);
            }
        } else {
            loginResp(GameConsole.Game_Load_Code_Error, teamId);
        }
    }

    public void addBackTeamId(long teamId, int type) {
        if (type == 1) {
            this._backTeamIdList.add(teamId);
        } else {
            this._backTeamIdList.remove(teamId);
        }
    }

    private void loginResp(int code, long teamId) {
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code).setBigNum(teamId).build());
    }

    @ClientMethod(code = ServiceCode.GameManager_debugLoginTeam)
    @UnCheck
    public void debugLoginTeam(long teamId) {
        //		if(!GameSource.isDebug) return;//不是测试环境，不允许调用该方法
        //
        User user = GameSource.getUser(teamId);
        if (user != null && user.getSession().isActive()) {//帐号已登录，把对方踢下线
            user.getSession().setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, 0l);
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Login_Out);
            //		}else{
            GameSource.offlineUser(teamId);
        }
        IoSession session = getSession();
        session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
        GameSource.online(teamId, session);
        if (teamManager.existTeam(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Success).setBigNum(teamId).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Create).setBigNum(teamId).build());
        }
    }

    /**
     * 登录验证
     */
    @UnCheck
    @ClientMethod(code = ServiceCode.GameManager_login)
    public void login(int pid, String token, String uid, String exInfo) {
        FTXLogin f = new FTXLogin(GameConsole.Game_App_Id, pid, token, uid, exInfo, "");
        String sign = MapsUtils.getSignKey(f);
        f.setSign(sign);
        String response = "";
        try {
            response = HttpClient.postAndDecode(GameConsole.Game_SDK_Server_URL
                , "appId", "" + f.getAppId()
                , "packageId", "" + f.getPackageId()
                , "token", "" + f.getToken()
                , "userId", "" + f.getUserId()
                , "exInfo", "" + f.getExInfo()
                , "sign", "" + f.getSign());
        } catch (Exception e) {
            return;
        }
        FTXLoginResponse res = JsonUtil.toObj(response, FTXLoginResponse.class);
        long teamId = 0;
        if (GameSource.isDebug || res.getCode() == FTXLoginResponse.OK) {
            long uuid = res.getUserId();
            teamId = GameSource.getTeamId(uuid);
            User user = GameSource.getUser(teamId);
            if (user != null && user.getSession().isActive()) {
                user.getSession().setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, 0l);
                sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Login_Out);
            } else {
                GameSource.offlineUser(teamId);
            }
            IoSession session = getSession();
            session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
            GameSource.online(teamId, session);
            if (teamManager.existTeam(teamId)) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Success).setBigNum(teamId).build());
            } else {
                // 正式环境加打印，验证新建用户问题.
                log.warn("user login pid={}, uid={}, userId={}, uuid={}, teamid={}", pid, uid, f.getUserId(), uuid, teamId);
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Create).setBigNum(teamId).build());
            }
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(-1).setBigNum(teamId).build());
        }
    }

    /**
     * 创建球队
     */
    @Deprecated
    @ClientMethod(code = ServiceCode.GameManager_debugCreateTeam)
    public void debugCreateTeam(String name, String logo) {
        long teamId = getTeamId();
        long userId = GameSource.getUserId(teamId);
        // 实例化玩家
        Team team = Team.createTeam(teamId, userId, GameSource.shardId, name, logo, 1);
        // 测试默认50级;
        team.setLevel(50);
        //实例化球队
        List<PlayerBean> playerBeanList = PlayerConsole.getRanPlayer(10);
        TeamPlayer tp = new TeamPlayer(teamId);
        Queue<EPlayerPosition> positionList = new ArrayBlockingQueue<>(12);
        positionList.add(EPlayerPosition.C);
        positionList.add(EPlayerPosition.PF);
        positionList.add(EPlayerPosition.PG);
        positionList.add(EPlayerPosition.SF);
        positionList.add(EPlayerPosition.SG);
        for (int i = positionList.size(); i < playerBeanList.size(); i++) {
            positionList.add(EPlayerPosition.NULL);
        }
        playerBeanList.stream().forEach(player -> tp.addPlayer(tp.createPlayer(player, player.getPrice(), positionList.poll().name(), null)));
        //将数据放入本地缓存
        teamManager.createTeam(teamId, team);
        playerManager.createTeamPlayer(teamId, tp);
        TeamTactics tt = tacticsManager.createTeamDefaultTactics(teamId);

        //执行save方法将数据放入DB队列
        team.save();
        tp.getPlayers().forEach(player -> player.save());
        tt.getTacticsMap().values().forEach(tc -> tc.save());
        taskManager.levelup(teamId, 0, 1);
        //创建成功，返回默认回包，等待前端调用加载玩家数据请求
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    /**
     * @param name
     * @param logo
     */
    @ClientMethod(code = ServiceCode.GameManager_createTeam)
    public synchronized void createTeam(String name, String logo, int xPlayerId, int index, int secId) {
        long teamId = getTeamId();
        long userId = GameSource.getUserId(teamId);
        name = getTeamName(name);
        if (teamManager.existTeamName(name)) {//球队名重复
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Exist.code).build());
            return;
        }

        PlayerBean X = PlayerConsole.getPlayerBean(xPlayerId);
        if (X == null || !PlayerConsole.existCreateXPlayer(xPlayerId)) {//X球员不存在或者是非法X球员
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Prop_Bean_Null.code).build());
            return;
        }
        if (chatManager.shieldText(name)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_1.code).build());
            return;
        }

        //实例化玩家
        Team team = Team.createTeam(teamId, userId, GameSource.shardId, name, logo, secId);

        //实例化球队
        List<PlayerSimple> playerList = Lists.newArrayList(PlayerConsole.getCreateTeamPlayers(X.getPosition()[0], index));

        playerList.add(PlayerSimple.newPlayerSimple(X.getPlayerRid(), X.getPosition()[0], X.getPosition()[0], 1, X.getPrice()));

        ModuleLog module = ModuleLog.getModuleLog(EModuleCode.球队, "创建球队");
        TeamPlayer tp = new TeamPlayer(teamId);
        playerList.stream().forEach(player -> playerManager.addCreatePlayer(teamId, tp, player, module));

        //将数据放入本地缓存
        teamManager.createTeam(teamId, team);
        playerManager.createTeamPlayer(teamId, tp);
        //实例化默认战术
        TeamTactics tt = tacticsManager.createTeamDefaultTactics(teamId);
        //创建X球员技能初始化
        //		skillManager.createTeamPlayerSkill(teamId, xPlayerId);

        //        propManager.addProp(teamId, new PropSimple(1119, 1), false, EModuleCode.球队);
        //        propManager.addProp(teamId, new PropSimple(1401, 160), false, EModuleCode.球队);
        propManager.addPropList(teamId, _initPropList, false, module);
        //
        //buffManager.addBuff(teamId, new TeamBuff(EBuffKey.新手引导加成, EBuffType.工资帽, new int[]{1000}, GameConsole.Max_Date, true));
        //执行save方法将数据放入DB队列
        team.save();
        tp.getPlayers().forEach(player -> player.save());
        tt.getTacticsMap().values().forEach(tc -> tc.save());

        teamMoneyManager.updateTeamMoney(teamId, 0, 2500, 0, 0, false, module);
        //		TeamMoney tm = teamMoneyManager.getTeamMoney(teaCmId);

        //创建成功，返回默认回包，等待前端调用加载玩家数据请求
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @ClientMethod(code = ServiceCode.GameManager_ranPlayer)
    public void ranPlayer(int xPlayerId) {
        PlayerBean X = PlayerConsole.getPlayerBean(xPlayerId);
        if (!PlayerConsole.existCreateXPlayer(xPlayerId)) { return; }
        //
        int size = PlayerConsole.getCreateTeamPlayerSize(X.getPosition()[0]);
        int ran = RandomUtil.randInt(size);

        List<PlayerSimple> players = PlayerConsole.getCreateTeamPlayers(X.getPosition()[0], ran);
        List<GameLoadPB.PlayerCreateData> playerList = Lists.newArrayList();
        players.forEach(player -> playerList.add(getPlayerCreateData(player)));
        sendMessage(GameLoadPB.PlayerCreateMain.newBuilder().setIndex(ran).addAllPlayers(playerList).build());
    }

    @ClientMethod(code = ServiceCode.GameManager_loadNBAPlayer)
    public void loadNBAPlayer() {
        //		NBAPlayerData data = nbaData.getPlayerDatasList().stream().filter(player->player.getPlayerId()==9000303).findFirst().orElse(null);
        //		log.error(""+data.getTeamId());
        sendMessage(nbaData);
    }

    private GameLoadPB.NBAPlayerData getNBAPlayerData(PlayerBean player) {
        List<GameLoadPB.NBAPlayerAbilityData> abilitys = Lists.newArrayList();
        for (EActionType type : player.getAbility().keySet()) {
            abilitys.add(GameLoadPB.NBAPlayerAbilityData
                .newBuilder()
                .setType(type.getType())
                .setValue(player.getAbility(type)).build());
        }
        //
        return GameLoadPB.NBAPlayerData.newBuilder().setBeforePrice(player.getBeforePrice())
            .setGrade(player.getGrade().getGrade())
            .setInjured(player.isInjured())
            .setPlayerId(player.getPlayerRid())
            .setPlayerTitle(player.getPlayerTitle().getTid())
            .setPrice(player.getPrice())
            .setTeamId(player.getTeam().getTid())
            .setBeforeCap(player.getBeforeCap())
            .setBasePrice(playerManager.getPlayerMinPrice(player.getPlayerRid()))
            .setPosition(Arrays.stream(player.getPosition()).filter(po -> po != EPlayerPosition.NULL)
                .map(po -> po.toString()).collect(Collectors.joining("/")))
            .setName(player.getName())
            .setShortName(player.getShortName())
            .setEnName(player.getEnName())
            .addAllAbilitys(abilitys).build();

    }

    /**
     * 加载玩家数据
     */
    @ClientMethod(code = ServiceCode.GameManager_loadData)
    public void loadData() {
        long teamId = getTeamId();
        Team team = teamManager.getTeamWithoutGC(teamId);
        log.info("team loaddata tid {} name {} lev {} login", teamId, team.getName(), team.getLevel());
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        TeamProp tprop = propManager.getTeamProp(teamId);
        TeamEqui te = teamEquiManager.getTeamEqui(teamId);
        PlayerCardMainData tpcData = playerCardManager.getPlayerCardMainData(teamId);
        TeamPlayerLogo tplData = playerLogoManager.getTeamPlayerLogo(teamId);
        TeamStatus status = teamStatusManager.get(teamId);
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        //注册该选秀房间的阶段服务
        if (roomId != 0) {
            ServiceManager.addService(ServiceConsole.getDraftStageKey(roomId), teamId);
        }
        team.login();
        EventBusManager.post(EEventType.全队攻防, teamId);
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        leagueManager.updateLeagueLogin(lt);
        //		matchManager.loginMatchStartTipTopic(teamId);
        //		if(lt!=null && lt.getLeagueId()>0)
        chatManager.registerChat(teamId, lt != null ? lt.getLeagueId() : 0);
        rankManager.teamLogin(teamId);
        User user = GameSource.getUser(teamId);
        if (user != null) {
            user.setVal(User.Level_Key, team.getLevel() + "");
        }
        sendMessage(getGameLoadDataMain(team, tm, tp, tprop, te, tpcData, tplData, status, lt, roomId));
        playerManager.loginCheck(teamId);
        // 检查任务目标
        checkTeamTeskStep(team);
        // 推送一次目标
        //topicMyTarget(teamId);
    }

    private GameLoadPB.GameLoadDataMain getGameLoadDataMain(Team team, TeamMoney tm, TeamPlayer tp,
                                                            TeamProp tprop, TeamEqui te, PlayerCardMainData tpc,
                                                            TeamPlayerLogo tplData, TeamStatus ts,
                                                            LeagueTeam lt, int roomId) {
        List<TeamPB.TeamAbilityData> abilityList = Lists.newArrayList();
        TeamAbility ta = teamManager.getTeamAllAbility(team.getTeamId());
        abilityList.add(teamManager.getTeamAbilityData(ta));

        List<TeamPB.TeamCapData> capList = Lists.newArrayList();
        capList.add(teamManager.getTeamCapData(EModuleCode.球队, team.getPrice()));
        TeamPriceMoneyBean priceBean = ShopConsole.getTeamPriceMoneyBean(team.getPriceCount());
        capList.add(teamManager.getTeamCapData(EModuleCode.商城, priceBean == null ? 0 : priceBean.getPrice()));
        int leagueCap = leagueHonorManager.getLeagueTeamPriceCap(lt);
        capList.add(teamManager.getTeamCapData(EModuleCode.联盟, leagueCap));
        //
        //		int buffAdd = buffManager.getBuffSet(team.getTeamId(), EBuffType.工资帽).getValueSum();
        //		capList.add(teamManager.getTeamCapData(EModuleCode.临时,buffAdd));

        List<PlayerPB.PlayerData> playerList = Lists.newArrayList();
        tp.getPlayers().forEach(player -> playerList.add(playerManager.getPlayerData(player)));

        List<PlayerPB.PlayerData> storagePlayerList = Lists.newArrayList();
        tp.getStoragesAndMarket().forEach(player -> storagePlayerList.add(playerManager.getPlayerData(player)));

        List<PropPB.PropData> propList = Lists.newArrayList();
        tprop.getPropList().forEach(prop -> propList.addAll(PropManager.getPropListData(prop.getPropList())));
        TeamPropsData tPropsD = PropPB.TeamPropsData.newBuilder().addAllPropList(propList).build();

        PlayerLogoPB.PlayerLogoData tpcDataMT = PlayerLogoMT.getPlayerLogoData(tplData, playerLogoManager.getLuckyValue(team.getTeamId()));
        // 交易
        List<TradePB.TradeData> tradeDatas = tradeManager.getTeamTradeDatas(team.getTeamId());
        //
        GameLoadPB.GameLoadDataMain.Builder build = GameLoadPB.GameLoadDataMain.newBuilder()
            .setTeamData(teamManager.getTeamData(team))
            .setTeamMoneyData(teamMoneyManager.getTeamMoneyData(tm))
            .setTeamEquiData(teamEquiManager.getTeamEquiData(te))
            .setTeamFriends(friendManager.getTeamFriendData(team.getTeamId()))
            .addAllTeamAbilityList(abilityList)
            .addAllTeamCapList(capList)
            .addAllTeamPlayerList(playerList)
            .setTeamEmail(teamEmailManager.getEmailListData(team.getTeamId()))
            .setTeamPropData(tPropsD)
            .setTpcData(tpc)
            .setTplData(tpcDataMT)
            .addAllStroagePlayerList(storagePlayerList)
            .setTeamStatus(getTeamStatusData(ts, roomId))
            .setServerStartTime(GameSource.openTime.getMillis())
            .setSystemActive(systemActiveManager.getAllActiveData())
            .setVip(vipManager.getVipData(team.getTeamId()))
            .setPlayerGrade(playerGradeManager.getPlayerGradeMainData(team.getTeamId()))
            .addAllBuffList(buffManager.getTeamBuffListData(team.getTeamId()))
            .setRedPoint(redPointManager.getTeamRedPointStatus(team.getTeamId()))
            .addAllTradeList(tradeDatas)
            .setLeagueTrain(trainManager.getLeagueTrainData(team.getTeamId()));
        if (lt != null && lt.getLeagueId() > 0) {
            League league = leagueManager.getLeague(lt.getLeagueId());
            build.setLeagueData(leagueManager.getGameLoadLeagueData(lt, league));
        }
        return build.build();
    }

    public PlayerPB.NBAPlayerAvgData getPlayerAvgData(int playerId) {
        return nbaPlayerAvgMap.get(playerId);
    }

    /**
     * 我的目标提醒
     * 推包
     */
    @ClientMethod(code = ServiceCode.GameManager_topicTarget)
    public void topicMyTarget() {
        long teamId = getTeamId();
        Team team = teamManager.getTeamWithoutGC(teamId);
        if (team == null) {
            return;
        }

        MainScheduleData mainSchedule = stageManager.getTargetData(teamId);
        // TrainData train = trainManager.getTargetData(teamId);

        TaskStepData taskStepData = null;
        int taskId = TaskConsole.getTaskStepId(team.getTaskStep());
        if (team.getTaskStep() == 0 || taskId == 0) {
            taskStepData = TaskStepData.newBuilder().setTaskId(0).setStatus(3).build();
        } else {
            TeamTask teamTask = taskManager.getTeamTask(teamId);
            Task task = teamTask.getTask(taskId);
            taskStepData = TaskStepData.newBuilder().setTaskId(taskId).setStatus(task == null ? 3 : task.getStatus()).build();
        }

        // 选秀数据
        DraftData.Builder draftData = DraftData.newBuilder();
        draftData.setUseNum(localDraftManager.getDayCount(teamId));
        draftData.setTotalNum(ConfigConsole.getIntVal(EConfigKey.Draft_Day_Count));

        // 任务数据
        TaskDayData.Builder taskDayData = TaskDayData.newBuilder();
        TaskStarAwardBean starAwardBean = TaskConsole.getTaskStarAwardBean(5);
        taskDayData.setStarNum(Math.min(taskManager.getTeamTask(teamId).getTaskDay().getTaskStarSum(), starAwardBean.getStarNum()));
        taskDayData.setTotalStarNum(starAwardBean.getStarNum());

        TargetMainData.Builder builder = TargetMainData.newBuilder();
        builder.setMainSchedule(mainSchedule);
        builderTeamMatchDataList(teamId).forEach(teamMatchData -> {
            builder.addMatchlist(teamMatchData);
        });
        builderKnockoutMatchList(teamId).forEach(knockoutMatchData -> {
            builder.addKnockMatch(knockoutMatchData);
        });
        //builder.setTrain(train);
        builder.setTask(taskStepData);
        builder.setDraft(draftData);
        builder.setTaskDay(taskDayData);

        sendMessage(teamId, builder.build(), ServiceCode.GameManager_topicTarget);
    }

    //多人赛数据
    private List<KnockoutMatchPKData.Builder> builderKnockoutMatchList(long teamId) {
        List<KnockoutMatchPKData.Builder> list = Lists.newArrayList();

        // 多人赛        
        matchManager.getMatchList(teamId).forEach(match -> {
            KnockoutMatchPKData.Builder builder = KnockoutMatchPKData.newBuilder();
            builder.setMatchId(match == null ? 0 : match.getMatchId());
            builder.setStartTime(match == null ? 0 : match.getMatchTime().getMillis());
            builder.setStatus(match == null ? 0 : match.getStatus());
            builder.setApplyState(match == null ? 0 : match.isSign(teamId) ? 1 : 0);
            list.add(builder);
        });

        return list;
    }

    // 各种常规比赛数据
    private List<TeamMatchData.Builder> builderTeamMatchDataList(long teamId) {
        GlobalBean gb = ConfigConsole.global();
        List<TeamMatchData.Builder> list = Lists.newArrayList();

        // 个人竞技赛次数       
        TeamNums tn = teamDayStatsManager.getNums(teamId);
        int usedNum = tn.getNum(TeamDayNumType.Arena_Match_Free_Num, 0);
        list.add(builderTeamMatchData(EBattleType.Arena.getId(), 0, 0, usedNum, gb.arenaFreeMatchNum));

        // 即时pk
        TeamStatus ts = teamStatusManager.get(teamId);
        TeamBattleStatus status = ts.getBattle(EBattleType.即时比赛跨服);
        int isApplyState = 0;
        if (status != null) {
            isApplyState = status.getStatus().ordinal();
        }
        list.add(builderTeamMatchData(EBattleType.即时比赛跨服.getId(), 0, isApplyState,
            battlePVPManager.getBattleDayCount(teamId), battlePVPManager.getMaxDayCount(teamId)));

        // 天梯赛
        long curr = System.currentTimeMillis();
        list.add(builderTeamMatchData(EBattleType.Ranked_Match.getId(),
            lcRankedMatchManager.inMatchTime(gb, curr) ? 1 : 0,
            lcRankedMatchManager.inBattle(teamId) ? 1 : 0,
            teamDayStatsManager.getTeamDayStatistics(teamId).getPkCount(EBattleType.Ranked_Match),
            0));

        // 街球赛
        list.add(builderTeamMatchData(EBattleType.街球副本.getId(),
            0,
            0,
            streetballManager.getTodayFight(teamId, 1),
            streetballManager.getMaxFightCount(teamId)));

        return list;
    }

    private TeamMatchData.Builder builderTeamMatchData(int battleType, int state, int applyState, int useNum, int totalNum) {
        TeamMatchData.Builder msg = TeamMatchData.newBuilder();
        msg.setBattleType(battleType);
        msg.setState(state);
        msg.setApplyState(applyState);
        msg.setTotalNum(totalNum);
        msg.setUseNum(useNum);
        return msg;
    }

    /**
     * 登录执行
     * 任务目标处理
     */
    public void checkTeamTeskStep(Team team) {
        long teamId = getTeamId();
        taskManager.checkNextTaskTarget(teamId);
    }

    /**
     * 推送所有在线玩家的目标
     */
    public void topicOnlineTeamTarget() {
        for (User user : GameSource.getUsers()) {
            //topicMyTarget(user.getTeamId());
        }
    }

    private GameLoadPB.PlayerCreateData getPlayerCreateData(PlayerSimple player) {
        PlayerBean bean = PlayerConsole.getPlayerBean(player.getPlayerId());
        return GameLoadPB.PlayerCreateData.newBuilder()
            .setPlayerId(player.getPlayerId())
            .setPosition(player.getPosition().getId())
            .setGrade(bean.getGrade().getGrade())
            .setPlayerPosition(player.getPlayerPosition().getId())
            .setPrice(bean.getPrice())
            .setAttack((int) bean.getAbility(EActionType.ocap))
            .setDefend((int) bean.getAbility(EActionType.dcap))
            .setPlayerName(bean.getShortName())
            .setTeamId(bean.getTeamId())
            .build();
    }

    private TeamPB.TeamStatusData getTeamStatusData(TeamStatus ts, int draftRoomId) {
        List<TeamPB.TeamBattleStatusData> battleDataList = Lists.newArrayList();
        ts.getStates().forEach(battle -> battleDataList.add(BattlePb.battleStat(battle)));
        return TeamPB.TeamStatusData.newBuilder()
            .setDraftRoomId(draftRoomId)
            .addAllBattleStatus(battleDataList)
            .build();

    }

    //	/**
    //	 * GM指令，添加道具
    //	 * @param teamId
    //	 * @param tid
    //	 * @param num
    //	 */
    //	@ClientMethod(code = ServiceCode.Prop_Add)
    //	public void debugAddProp(int tid, int num) {
    //		teamPropManager.addProp(getTeamId(), new PropSimple(tid, num));
    //		Prop prop = teamPropManager.getTeamProp(getTeamId()).getProp(tid);
    //		TeamPropsData tPropsD = PropPB.TeamPropsData.newBuilder().addAllPropList(teamPropManager.getPropListData(prop)).build();
    //		sendMessage(tPropsD);
    //	}
    //
    //	public void debugAddPropList(long teamId, List<PropSimple> propList) { 
    //		TeamProp teamProp = teamPropManager.getTeamProp(teamId);
    //		List<PropPB.PropData> propData = Lists.newArrayList();
    //		for(PropSimple p : propList) {
    //			teamPropManager.addProp(teamId, p);
    //			Prop prop = teamProp.getProp(p.getPropId());
    //			propData.addAll(teamPropManager.getPropListData(prop));
    //		}
    //		sendMessage(teamId, PropPB.TeamPropsData.newBuilder().addAllPropList(propData).build(), ServiceCode.Prop_Add);
    //	}

    @ClientMethod(code = ServiceCode.GameManager_getPlayerVersion)
    @UnCheck
    public void getPlayerVersion() {
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(playerVersion).build());
    }

    public void reloadNBAData() {
        playerVersion++;
//        playerManager.reloadPlayerMinPrice();

        List<GameLoadPB.NBAPlayerData> players = Lists.newArrayList();
        PlayerConsole.getPlayerBeanList().stream()
            .filter(player -> player.getGrade() != null)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
            .filter(player -> player.getPlayerTitle() != null)
            .forEach(player -> players.add(getNBAPlayerData(player)));
        nbaData = GameLoadPB.NBAPlayerDataMain.newBuilder().setVersion(playerVersion)
            .addAllPlayerDatas(players).build();
        //		sendMessage(ServiceConsole.getChatKey(type));
        sendMessage(GameSource.getUsers(), DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Push_Player_Price);
        playerManager.reloadPlayerMoney();
        reloadPlayerAvgData();
        PlayerBeanVO vo = nbaDataAO.getMaxPlayerAvgBean();
        PlayerBean pb = new PlayerBean(vo);
        maxPlayerAvgData = getNBAPlayerAvgData(pb);

        log.info("加载球员数据reloadNBAData, date={}, playerVersion={}", DateTimeUtil.getStringSql(DateTime.now()), playerVersion);
        EventBusManager.post(EEventType.身价更新, new Integer(playerVersion));
    }

    private void reloadPlayerAvgData() {
        List<PlayerPB.NBAPlayerAvgData> playerAvgList = Lists.newArrayList();
        PlayerConsole.getPlayerBeanList().stream()
            .filter(player -> player.getGrade() != null)
            .filter(player -> player.getTeam() != ENBAPlayerTeam.退役)
            .filter(player -> player.getPlayerTitle() != null)
            .forEach(player -> playerAvgList.add(getNBAPlayerAvgData(player)));
        nbaPlayerAvgMap = playerAvgList.stream().collect(Collectors.toMap(PlayerPB.NBAPlayerAvgData::getPlayerId, val -> val));
    }

    private PlayerPB.NBAPlayerAvgData getNBAPlayerAvgData(PlayerBean player) {
        List<PlayerPB.NBAPlayerAvgAbilityData> abilitys = Lists.newArrayList();
        for (EActionType type : player.getAvgAbility().keySet()) {
            abilitys.add(PlayerPB.NBAPlayerAvgAbilityData
                .newBuilder()
                .setType(type.getType())
                .setValue(player.getAvgAbility(type)).build());
        }

        return PlayerPB.NBAPlayerAvgData.newBuilder()
            .setPlayerId(player.getPlayerRid())
            .addAllAbilitys(abilitys)
            .build();
    }

    public void reloadNBAPKData() {
        List<NBAPKSchedule> allNBAPKSchedule = nbaDataAO.getAllNBAPKSchedule();
        List<NBAPKScoreBoard> allNBAPKScoreBoard = nbaDataAO.getAllNBAPKScoreBoard();
        List<NBAPKScoreBoardDetail> allNBAPKScoreBoardDetail = nbaDataAO.getAllNBAPKScoreBoardDetail();

        //
        List<GameLoadPB.NBAPKScheduleData> scheduleList = allNBAPKSchedule.stream().map(pk -> getNBAPKScheduleData(pk)).collect(Collectors.toList());
        Map<Integer, List<GameLoadPB.NBAPKScoreBoardData>> scoreBoardMap = allNBAPKScoreBoard.stream().map(pk -> getNBAPKScoreBoardData(pk))
            .collect(Collectors.groupingBy(GameLoadPB.NBAPKScoreBoardData::getGameId, Collectors.toList()));

        Map<Integer, List<GameLoadPB.NBAPKScoreBoardDetailData>> detailMap = allNBAPKScoreBoardDetail.stream()
            .map(pk -> getNBAPKScoreBoardDetailData(pk))
            .collect(Collectors.groupingBy(GameLoadPB.NBAPKScoreBoardDetailData::getGameId, Collectors.toList()));

        List<GameLoadPB.NBAPKScheduleInfoData> scheduleInfoList = scheduleList.stream().map(sc -> GameLoadPB.NBAPKScheduleInfoData.newBuilder()
            .setSchedule(sc).addAllScoreBoard(scoreBoardMap.get(sc.getGameId())).build())
            .collect(Collectors.toList());

        nbaPKData = GameLoadPB.NBAPKDataMain.newBuilder()
            .addAllScheduleList(scheduleInfoList)
            .build();
        nbaPKInfoMap = Maps.newHashMap();
        for (GameLoadPB.NBAPKScheduleData data : scheduleList) {
            GameLoadPB.NBAPKInfoDataMain.Builder pkidm = GameLoadPB.NBAPKInfoDataMain.newBuilder();
            if (detailMap.containsKey(data.getGameId())) {
                pkidm.addAllPlayerDetail(detailMap.get(data.getGameId()));
            }
            if (scoreBoardMap.containsKey(data.getGameId())) {
                pkidm.addAllScoreboard(scoreBoardMap.get(data.getGameId()));
            }
            nbaPKInfoMap.put(data.getGameId(), pkidm.build());
        }
        //更改所有比赛获胜状态
//        allNBAPKSchedule.stream().filter(sch -> sch.getStatus() == 1).forEach(pk -> playerManager.updateNBAGuessWinTeam(pk.getGameId(), pk.getWinTeamId()));
//        if (!allNBAPKSchedule.stream().filter(sch -> sch.getStatus() != 1).findFirst().isPresent()
//            && !hasGuessGiftStatus()) {
//            //今日赛程全部结束，开始发奖
//            log.info("今日赛程全部结束，开始发奖!");
//            playerManager.sendBattleGuessGifts();
//        }
        log.info("今日赛程重新加载{}", allNBAPKSchedule);
    }

    private boolean hasGuessGiftStatus() {
        return redis.exits(RedisKey.getDayKey(0, RedisKey.Battle_Guess_Gift));
    }

    private GameLoadPB.NBAPKScoreBoardDetailData getNBAPKScoreBoardDetailData(NBAPKScoreBoardDetail nbaPKScoreBoardDetail) {

        return GameLoadPB.NBAPKScoreBoardDetailData.newBuilder()
            .setAst(nbaPKScoreBoardDetail.getAst())
            .setBlk(nbaPKScoreBoardDetail.getBlk())
            .setDreb(nbaPKScoreBoardDetail.getDreb())
            .setEffectPoint(nbaPKScoreBoardDetail.getEffectPoint())
            .setFga(nbaPKScoreBoardDetail.getFga())
            .setFgm(nbaPKScoreBoardDetail.getFgm())
            .setFta(nbaPKScoreBoardDetail.getFta())
            .setFtm(nbaPKScoreBoardDetail.getFtm())
            .setGameId(nbaPKScoreBoardDetail.getGameId())
            .setId(nbaPKScoreBoardDetail.getId())
            .setIsStarter(nbaPKScoreBoardDetail.getIsStarter())
            .setMin(nbaPKScoreBoardDetail.getMin())
            .setOreb(nbaPKScoreBoardDetail.getOreb())
            .setPf(nbaPKScoreBoardDetail.getPf())
            .setPlayerId(nbaPKScoreBoardDetail.getPlayerId())
            .setPts(nbaPKScoreBoardDetail.getPts())
            .setReb(nbaPKScoreBoardDetail.getReb())
            .setStl(nbaPKScoreBoardDetail.getStl())
            .setTeamId(nbaPKScoreBoardDetail.getTeamId())
            .setThreePa(nbaPKScoreBoardDetail.getThreePa())
            .setThreePM(nbaPKScoreBoardDetail.getThreePm())
            .setTo(nbaPKScoreBoardDetail.getTo())
            .build();

    }

    private GameLoadPB.NBAPKScoreBoardData getNBAPKScoreBoardData(NBAPKScoreBoard nbaPKScoreBoard) {

        return GameLoadPB.NBAPKScoreBoardData.newBuilder()
            .setGameId(nbaPKScoreBoard.getGameId())
            .setO1(nbaPKScoreBoard.getOt1())
            .setO2(nbaPKScoreBoard.getOt2())
            .setO3(nbaPKScoreBoard.getOt3())
            .setO4(nbaPKScoreBoard.getOt4())
            .setO5(nbaPKScoreBoard.getOt5())
            .setO6(nbaPKScoreBoard.getOt6())
            .setO7(nbaPKScoreBoard.getOt7())
            .setQ1(nbaPKScoreBoard.getQ1())
            .setQ2(nbaPKScoreBoard.getQ2())
            .setQ3(nbaPKScoreBoard.getQ3())
            .setQ4(nbaPKScoreBoard.getQ4())
            .setTeamId(nbaPKScoreBoard.getTeamId())
            .setTotal(nbaPKScoreBoard.getTotal())
            .build();
    }

    private GameLoadPB.NBAPKScheduleData getNBAPKScheduleData(NBAPKSchedule nbaPKSchedule) {

        return GameLoadPB.NBAPKScheduleData.newBuilder()
            .setAwayTeamId(nbaPKSchedule.getAwayTeamId())
            .setAwayTeamScore(nbaPKSchedule.getAwayTeamScore())
            .setGameId(nbaPKSchedule.getGameId())
            .setGameTime(nbaPKSchedule.getGameTime().getMillis())
            .setGameType(nbaPKSchedule.getGameType())
            .setHomeTeamId(nbaPKSchedule.getHomeTeamId())
            .setHomeTeamScore(nbaPKSchedule.getHomeTeamScore())
            .setSeasonId(nbaPKSchedule.getSeasonId())
            .setStatus(nbaPKSchedule.getStatus())
            .build();

    }

    /**
     * @param status 推送指定游戏节点的球员身价更新
     */
    @RPCMethod(code = CrossCode.WebManager_reloadNBAData, pool = EServerNode.Node, type = ERPCType.NONE)
    public void reloadNBAData(int status) {
        CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
        manager.resetCache();
        playerManager.reloadPlayerMinPrice();
        reloadNBAData();
    }

    /**
     * @param status 推送所有游戏节点的球员身价更新
     */
    @RPCMethod(code = CrossCode.WebManager_reloadAllNodeNBAData, pool = EServerNode.Node, type = ERPCType.ALLNODE)
    public void reloadAllNodeNBAData(int status) {
        CacheManager manager = InstanceFactory.get().getInstance(CacheManager.class);
        manager.resetCache();
        playerManager.reloadPlayerMinPrice();
        reloadNBAData();
    }

    @RPCMethod(code = CrossCode.WebManager_reloadAllNodeNBAPKData, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void reloadAllNodeNBAPKData() {
        reloadNBAPKData();
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.GameManager_converCDKey)
    public void converCDKey(String key) {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CDKeyManager_conver, null, tid, new TeamNode(teamId), key);
        }).appendTask((tid, maps, args) -> {
            int code = (int) args[0];
            List<PropPO> result = null;
            if (args[1] != null) {
                List<PropSimple> props = (List<PropSimple>) args[1];
                result = propManager.addPropList(teamId, props, true, ModuleLog.getModuleLog(EModuleCode.CDKey, ""));
            }
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code).setMsg(PropConsole.getPropStr(result)).build(), ServiceCode.GameManager_converCDKey);
        }).start();
    }

    @Override
    public void initConfig() {
        ErrorCode.values();
        this._initPropList = PropSimple.getPropBeanByStringNotConfig(ConfigConsole.getVal(EConfigKey.Frist_Game));
    }

    @Override
    public void instanceAfter() {
        playerVersion = 0;
        _backTeamIdList = Sets.newHashSet();
        _backTeamIdList.addAll(teamAO.getLockBlackTeamList());
        //
        reloadNBAData();
        reloadNBAPKData();
    }

    private static final char[] Name_Postfix = {'*',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    /** 生成一个球队名称, 截取长度, 随机加后缀保证唯一性 */
    private String getTeamName(final String srcName) {
        String newName = srcName.trim();
        if (newName.length() < GameConsole.Team_Name_Min_Len) {
            newName += "*";
        } else if (newName.length() > GameConsole.Team_Name_Max_Len) {
            newName = newName.substring(0, GameConsole.Team_Name_Max_Len - 4);
        }
        if (!teamManager.existTeamName(newName)) {
            return newName;
        }

        for (char a : Name_Postfix) {
            String name = newName + a;
            if (!teamManager.existTeamName(name)) {
                return name;
            }
        }
        for (char a : Name_Postfix) {
            for (char b : Name_Postfix) {
                String name = newName + a + b;
                if (!teamManager.existTeamName(name)) {
                    return name;
                }
            }
        }// loop 4032 count

        //        for (char a : Name_Postfix) {
        //            for (char b : Name_Postfix) {
        //                for (char c : Name_Postfix) {
        //                    String name = newName + a + b + c;
        //                    if (!teamManager.hasExistName(name)) {
        //                        return name;
        //                    }
        //                }
        //            }
        //        }// loop 254079 count

        return newName;
    }
}
