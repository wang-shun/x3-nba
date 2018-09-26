package com.ftkj.x3.client.task.helper;

import com.ftkj.console.CM;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ConfigConsole.GlobalBean;
import com.ftkj.console.GameConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.enums.EActionType;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.GameLoadPB.GameLoadDataMain;
import com.ftkj.proto.GameLoadPB.NBAPlayerAbilityData;
import com.ftkj.proto.GameLoadPB.NBAPlayerData;
import com.ftkj.proto.GameLoadPB.NBAPlayerDataMain;
import com.ftkj.proto.TeamPB.TeamData;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.MD5Util;
import com.ftkj.x3.client.ClientConfig;
import com.ftkj.x3.client.console.ClientConsole;
import com.ftkj.x3.client.model.ClientTeam;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.ClientRespMessage;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.net.X3ClientBootStrap;
import com.ftkj.x3.client.net.X3ClientChannelGroup;
import com.ftkj.x3.client.net.X3ClientChannelHolder;
import com.ftkj.x3.client.net.X3ClientMsgHandler;
import com.ftkj.x3.client.net.X3ClientNioSocketChannel;
import com.ftkj.x3.client.proto.ClientPbUtil;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.xxs.client.net.ClientChannelGroup;
import com.ftkj.xxs.net.Message;
import com.ftkj.xxs.net.XxsChannel;
import com.ftkj.xxs.util.ArrayUtil;
import com.ftkj.xxs.util.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 玩家登录
 *
 * @author luch
 */
@Component
public class LoginHelper extends X3TaskHelper {
    private static final Logger log = LoggerFactory.getLogger(LoginHelper.class);
    @Autowired
    private X3ClientBootStrap clientBootStrap;
    @Autowired
    private X3ClientChannelHolder channelHolder;
    @Autowired
    private X3ClientMsgHandler msgHandler;
    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    private ClientConsole clientConsole;

    //    private static final String accountToken = "测试帐号";
    public static final int Main_AccountId = 20000000;
    public static final long User_AccountId = 1000066850;
    //    private static final long Main_AccountId = 10_6004;
    private static final String teamName = "一个球队";

    //    private static final String accountToken1 = "另一个测试帐号";
    private static final int accountId1 = 20000001;
    private static final String teamName1 = "另一个球队";

    //    private static final String accountToken2 = "机器人测试帐号";
    private static final int accountId2 = 20000002;
    private static final String teamName2 = "假冒球队";
    public static final AtomicBoolean loadedNBAPlayer = new AtomicBoolean();

    /** 登录主测试帐号 */
    public UserClient loginMainAccount() {
        return login(Main_AccountId, teamName, true);
    }

    /** 登录主测试帐号 */
    public UserClient loginMainAccount(Server server) {
        return login(server, Main_AccountId, teamName, true);
    }

    /** 登录另一个测试帐号 */
    public UserClient loginAnotherAccount() {
        return login(accountId1, teamName1, false);
    }

    /** 登录另一个机器人测试帐号 */
    public UserClient loginBotAccount() {
        return login(accountId2, teamName2, false);
    }

    /**
     * 登录帐号和球队, 如果球队不存在使用 teamName 创建角色
     *
     * @param accountId account id
     * @param teamName  球队名称
     * @return 和玩家关联的客户端
     */
    public UserClient login(long accountId,
                            String teamName) {
        return login(accountId, teamName, false);
    }

    public UserClient login(Server server,
                            long accountId,
                            String teamName) {
        return login(server, accountId, teamName, false);
    }

    /**
     * 登录帐号和球队, 如果球队不存在使用 teamName 创建角色
     *
     * @param accountId account id
     * @param teamName  球队名称
     * @return 和玩家关联的客户端
     */
    public UserClient login(long accountId,
                            String teamName,
                            boolean printSplitLineLog) {
        Resp<UserClient> tp = login2(accountId, teamName, printSplitLineLog);
        return tp.getObj();
    }

    public UserClient login(Server server,
                            long accountId,
                            String teamName,
                            boolean printSplitLineLog) {
        Resp<UserClient> tp = login2(server, accountId, teamName, printSplitLineLog, false);
        return tp.getObj();
    }

    public Resp<UserClient> login2(long accountId,
                                   String teamName) {
        return login2(clientConfig.getDefaultServer(), accountId, teamName, false, false);
    }

    private Resp<UserClient> login2(long accountId,
                                    String teamName,
                                    boolean printSplitLineLog) {
        return login2(clientConfig.getDefaultServer(), accountId, teamName, printSplitLineLog, false);
    }

    public Resp<UserClient> login2(Server server,
                                   long accountId,
                                   String teamName,
                                   boolean printSplitLineLog,
                                   boolean forceLogin) {
        synchronized (getAccountLock(String.valueOf(accountId))) {
            return loginLock(server, accountId, teamName, printSplitLineLog, forceLogin);
        }
    }

    /**
     * 登录帐号和球队, 如果球队不存在使用 teamName 创建角色
     *
     * @param accountId account id
     * @param teamName  球队名称
     * @return 和玩家关联的客户端
     */
    private Resp<UserClient> loginLock(final Server server,
                                       final long accountId,
                                       final String teamName,
                                       final boolean printSplitLineLog,
                                       final boolean forceLogin) {
        if (server == null) {
            log.error("login aid [{}] server 服务器配置不存在", accountId);
            return null;
        }
        String aid = String.valueOf(accountId);
        X3ClientChannelGroup ccg = channelHolder.channelGroup();
        UserClient olduc = ccg.getUserClientByAccountId(aid);
        if (!forceLogin) {
            if (olduc != null) {
                log.info("account team[{}]logined, return", olduc.uid());
                return succ(olduc);
            }
        }
        //登录
        UserClient uc = new UserClient(msgHandler);
        X3ClientNioSocketChannel channel = clientBootStrap.newChannel(clientConfig.isLoginUseWanIp(), server);
        ccg.tempAccountLogin(channel, aid);
        long curr = System.currentTimeMillis();
        //        long reqtid = GameSource.getTeamId(server.getShardId(), accountId);
        long reqtid = accountId;
        String md5 = MD5Util.encodeMD5(reqtid, curr).toLowerCase();
        if (!clientConfig.isAuthAccount()) {//关闭了帐号有效性校验
            md5 += "_windows";
        }
        GlobalBean gb = ConfigConsole.global();
        Message loginRespMsg = uc.writeAndGet(channel, createReq(ServiceCode.GameManager_loginPC, reqtid, md5, curr,
            gb.versionMajor.iterator().next(), gb.versionMinor));
        DefaultData loginResp = parseFrom(loginRespMsg);

        if (loginResp.getCode() == GameConsole.Game_Load_Code_Create) {//需要创建球队
            log.debug("account aid {} tid {} need create user", accountId, loginResp.getBigNum());
            loadNbaPlayerData(uc, channel);
            Ret createResp = createTeam(accountId, teamName, uc, channel);
            if (createResp.isError()) {
                log.warn("account login error. aid {} ret {}", accountId, createResp);
                return error(createResp);
            }
        } else if (loginResp.getCode() == GameConsole.Game_Load_Code_Error) {
            log.warn("account login error. aid {} ret {}", accountId, "Game_Load_Code_Error");
            return error(loginResp);
        }
        loadNbaPlayerData(uc, channel);
        ccg.removeTempAccountChannel(aid); //移除帐号channel
        //加载球队数据
        ClientRespMessage loadRespMsg = (ClientRespMessage) uc.writeAndGet(channel, createReq(ServiceCode.GameManager_loadData));
        //        log.debug("msg zip {}", loadRespMsg.isZip());
        GameLoadDataMain loadResp = parseFrom(GameLoadDataMain.getDefaultInstance(), loadRespMsg);
        TeamData td = loadResp.getTeamData();
        log.info("account aid[{}] tid[{}]:[{}] team login success. length {}",
            accountId, td.getTeamId(), td.getTeamName(), loadRespMsg.getBodyLength());
        ClientUser cu = createUserClient(loadResp);
        cu.setAccountId(accountId);
        uc.setUser(cu);
        ccg.userLogin(channel, aid, cu.getUserId(), uc);
        if (printSplitLineLog) {
            log.info("\n===========================================================>" +
                " 模块测试日志开始 <====================\n");
        }
        return succ(uc);
    }

    private void loadNbaPlayerData(UserClient uc, X3ClientNioSocketChannel channel) {
        if (!loadedNBAPlayer.get()) {
            synchronized (this) {
                if (!loadedNBAPlayer.get()) {
                    log.info("load nba player data");
                    Message nbaplayerMsg = uc.writeAndGet(channel, createReq(ServiceCode.GameManager_loadNBAPlayer));
                    NBAPlayerDataMain nbaPlayerData = parseFrom(NBAPlayerDataMain.getDefaultInstance(), nbaplayerMsg);
                    initNbaPlayer(nbaPlayerData);
                    loadedNBAPlayer.set(true);
                }
            }
        }
    }

    private void initNbaPlayer(NBAPlayerDataMain npdm) {
        List<PlayerBeanVO> pblist = new ArrayList<>(npdm.getPlayerDatasCount());
        for (NBAPlayerData pb : npdm.getPlayerDatasList()) {
            PlayerBeanVO po = createPlayerBean(pb);
            pblist.add(po);
        }
        CM.playerBeanList = pblist;
        //初始化球员配置
        PlayerConsole.init(pblist, Collections.emptyList(), Collections.emptyList());
        clientConsole.afterReloadNbaPlayer();
        log.info("client confg done. xplayer size {} resp size {}", PlayerConsole.getCreateTeamXList().size(), pblist.size());
    }

    //map[ordinal, act]
    private static Map<Integer, EActionType> acts = null;

    public Map<Integer, EActionType> getActs() {
        if (acts != null) {
            return acts;
        }
        //map[ordinal, act]
        Map<Integer, EActionType> acts = new HashMap<>();
        for (EActionType act : EActionType.values()) {
            acts.put(act.getType(), act);
        }
        this.acts = acts;
        return acts;
    }

    private PlayerBeanVO createPlayerBean(NBAPlayerData pb) {
        PlayerBeanVO po = new PlayerBeanVO();
        po.setBeforePrice(pb.getBeforePrice());
        po.setGrade(pb.getGrade());
        po.setInjured(pb.getInjured() ? 1 : 0);
        po.setPlayerId(pb.getPlayerId());
        po.setPlayerType(pb.getPlayerTitle());
        po.setPrice(pb.getPrice());
        po.setTeamId(pb.getTeamId());
        po.setBeforeCap(pb.getBeforeCap());
        po.setPosition(pb.getPosition());

        Map<Integer, EActionType> acts = getActs();
        Map<EActionType, Float> ability = new HashMap<>();
        for (NBAPlayerAbilityData ad : pb.getAbilitysList()) {
            ability.put(acts.get(ad.getType()), ad.getValue());
        }

        po.setFtm(ability(ability, EActionType.ftm));
        po.setPts(ability(ability, EActionType.pts));
        po.setThreePm(ability(ability, EActionType._3pm));
        po.setOreb(ability(ability, EActionType.reb));
        //        po.setDreb(ability(ability,EActionType.T_篮板));
        po.setStl(ability(ability, EActionType.stl));
        po.setBlk(ability(ability, EActionType.blk));
        po.setTo(ability(ability, EActionType.to));
        po.setMin(ability(ability, EActionType.min));
        po.setPf(ability(ability, EActionType.pf));
        po.setAttrCap(ability(ability, EActionType.ocap));
        po.setGuaCap(ability(ability, EActionType.dcap));
        po.setAst(ability(ability, EActionType.ast));
        po.setSkill(ability(ability, EActionType.skill_power));
        po.setFgm(ability(ability, EActionType.fgm));
        return po;
    }

    private int ability(Map<EActionType, Float> ability, EActionType act) {
        return ability.getOrDefault(act, 0f).intValue();
    }

    /** 创建球队 */
    private Ret createTeam(long accountId, String teamName, UserClient uc, X3ClientNioSocketChannel channel) {
        String logo = "";
        ThreadLocalRandom tlr = ThreadLocalRandom.current();
        //        final int xPlayerId = ArrayUtil.random(tlr, PlayerConsole.getCreateTeamXList());
        final int xPlayerId = ArrayUtil.random(tlr, logoPlayers);
        final PlayerBean xplayer = PlayerConsole.getPlayerBean(xPlayerId);
        log.debug("aid {} create team name {} xpid {} xplayer {} prs {}", accountId, teamName, xPlayerId, xplayer != null, PlayerConsole.getCreateTeamXList());
        final int ctpSize = PlayerConsole.getCreateTeamPlayerSize(xplayer.getPosition()[0]);
        final int index = tlr.nextInt(ctpSize + 1);
        final int secId = tlr.nextInt(100);

        Message msg = uc.writeAndGet(channel, createReq(ServiceCode.GameManager_createTeam, teamName, logo, xPlayerId, index, secId));
        DefaultData createResp = parseFrom(msg);
        if (uc.isError(createResp)) {
            log.error("account aid {} create user {} fail.mid {} {}",
                accountId, teamName, msg == null ? "null" : msg.getId(), ret(createResp));
            return ret(createResp);
        }
        log.debug("account aid {} create team {} success", accountId, teamName);
        return success();
    }

    private static final List<Integer> logoPlayers = Arrays.asList(9000300, 9000301, 9000302, 9000303, 9000304);

    private static ClientUser createUserClient(GameLoadDataMain resp) {
        TeamData td = resp.getTeamData();

        ClientUser cu = new ClientUser();
        cu.setUserId(td.getTeamId());
        cu.setCreateDay(td.getCreateDay());

        cu.setTeam(createTeam(td));
        cu.setMoney(ClientPbUtil.createMoney(resp.getTeamMoneyData()));
        cu.setEquips(ClientPbUtil.createEquip(resp.getTeamEquiData()));
        cu.setFriends(ClientPbUtil.createFriends(resp.getTeamFriends()));
        cu.setPlayers(ClientPbUtil.createPlayer(resp.getPlayerGrade(), resp.getTeamPlayerListList(), resp.getStroagePlayerListList()));
        //        cu.setTrainPlayers(ClientPbUtil.createTrainPlayer(resp.getTradeListList()));
        cu.setEmails(ClientPbUtil.createEmail(resp.getTeamEmail()));
        cu.setProps(ClientPbUtil.createProp(cu.getTid(), resp.getTeamPropData()));
        cu.setVip(ClientPbUtil.createVip(resp.getVip()));
        cu.setBuffs(ClientPbUtil.createBuff(resp.getBuffListList()));
        cu.setStatus(ClientPbUtil.createStatus(resp.getTeamStatus()));
        log.info("tid {} lev {} money {} viplev {} player {} StroagePlayer {}", cu.getTid(),
            resp.getTeamData().getLevel(),
            resp.getTeamMoneyData().getMoney(),
            resp.getVip().getLevel(),
            resp.getTeamPlayerListCount(), resp.getStroagePlayerListCount());
        return cu;
    }

    private static ClientTeam createTeam(TeamData td) {
        ClientTeam team = new ClientTeam();
        team.setTeamId(td.getTeamId());
        team.setUserId(td.getTeamId());
        team.setLevel(td.getLevel());
        team.setLogo(td.getTeamLogo());
        team.setName(td.getTeamName());
        team.setTitle(td.getTitle());
        team.setHelp(td.getHelp());
        team.setLineupCount(td.getLineupCount());
        return team;
    }

    public void logout(UserClient uc) {
        X3ClientChannelGroup cg = channelHolder.channelGroup();
        String name = uc.user().getName();
        XxsChannel channel = cg.getChannelByUser(uc.uid());

        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        String aid = uc.getUser().getAccountId();
        String uid = uc.getUser().getUserId();

        cg.removeAll(aid, uid);
        log.info("account team {}:{}:{} logout", aid, uid, name);
    }

    private static ReentrantLock getAccountLock(String accountId) {
        return ClientChannelGroup.getAccountLock(accountId);
    }
}
