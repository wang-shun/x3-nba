package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.TeamPriceMoneyBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.ShopConsole;
import com.ftkj.console.TacticsConsole;
import com.ftkj.db.ao.logic.ITeamAO;
import com.ftkj.db.domain.TeamPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.job.logic.TeamOfflineJob;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.cap.FieldCap;
import com.ftkj.manager.coach.TeamCoach;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.log.GameHelpStepLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleConfig;
import com.ftkj.manager.team.TeamDaily;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.manager.vip.TeamVip;
import com.ftkj.proto.CommonPB.TeamSimpleData;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerPB;
import com.ftkj.proto.PropPB;
import com.ftkj.proto.TeamPB;
import com.ftkj.proto.TeamPB.TeamData;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RpcTask;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tim.huang
 * 2017年3月2日
 * 球队管理
 */
public class TeamManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(TeamManager.class);
    @IOC
    private ITeamAO teamAO;
    @IOC
    private TacticsManager tacticsManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private EquiManager teamEquiManager;
    @IOC
    private TeamCapManager teamCapManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private LeagueHonorManager leagueHonorManager;
    @IOC
    private CoachManager coachManager;
    @IOC
    private PlayerArchiveManager playerArchiveManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private RankManager rankManager;
    @IOC
    private PropManager propManager;
    @IOC
    private GameManager gameManager;
    @IOC
    private TeamDayStatsManager teamDayStatsManager;
    @IOC
    private KnockoutMatchPKManager knockoutMatchPKManager;
    @IOC
    private TeamNumManager teamNumManager;
    @IOC
    private LocalDraftManager localDraftManager;
    @IOC
    private StreetballManager streetballManager;
    @IOC
    private BattlePVPManager battlePVPManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private LcRankedMatchManager lcRankedMatchManager;

    /** 包括在线和离线的 玩家及npc */
    private Map<Long, Team> teams;
    private Map<Long, TeamDaily> teamDailys;
    private Map<Long, TeamBattleConfig> teamBattleConfigMap;
    private BiMap<Long, String> cacheMap;

    public Team getTeam(long teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            TeamPO tp = teamAO.getTeam(teamId);
            if (tp != null) {
                team = new Team(tp);
                teams.put(teamId, team);
            }
            GameSource.checkGcData(teamId);
        }
        return team;
    }

    public TeamDaily getTeamDaily(long teamId) {
        TeamDaily teamDaily = teamDailys.get(teamId);
        if (teamDaily == null) {
            teamDaily = teamAO.getTeamDaily(teamId);
            if (teamDaily == null) {
                teamDaily = TeamDaily.createTeamDaily(teamId, 0);
            }

            teamDailys.put(teamId, teamDaily);
        }
        return teamDaily;
    }

    public Team getTeamWithoutGC(long teamId) {
        Team team = teams.get(teamId);
        if (team == null) {
            TeamPO tp = teamAO.getTeam(teamId);
            if (tp != null) {
                team = new Team(tp);
                teams.put(teamId, team);
            }
        }
        return team;
    }

    /**
     * 随机取N个玩家
     *
     * @param num
     * @return
     */
    public List<Long> getRanTeam(int num) {
        int size = cacheMap.size();
        num = Ints.min(num, size);
        List<Long> result = Lists.newArrayList();
        List<Integer> ranList = Stream.generate(() -> RandomUtil.randInt(size)).distinct().limit(num).collect(Collectors.toList());
        Collections.sort(ranList);
        int index = 0;
        int ran = 0;
        int i = 0;
        ran = ranList.get(index);
        //
        Set<Long> tSet = cacheMap.inverse().values();
        Iterator<Long> ll = tSet.iterator();
        while (ll.hasNext()) {
            long t = ll.next();
            if (i == ran) {
                result.add(t);
                index++;
                if (index >= ranList.size()) { break; }
                ran = ranList.get(index);
            }
            i++;
        }
        return result;
    }

    /**
     * 取球队工资帽
     *
     * @param team
     * @return
     */
    public int getTeamPrice(Team team) {
        int price = team.getPrice();
        TeamPriceMoneyBean priceBean = ShopConsole.getTeamPriceMoneyBean(team.getPriceCount());
        int buyPrice = priceBean == null ? 0 : priceBean.getPrice();
        int buffAdd = buffManager.getBuffSet(team.getTeamId(), EBuffType.工资帽).getValueSum();
        int leagueAdd = leagueHonorManager.getLeagueTeamPriceCapByTeamId(team.getTeamId());
        log.info("工资帽加成:price={},buyPrice={},buffAdd={},leagueAdd={}", price, buyPrice, buffAdd, leagueAdd);
        return price + buyPrice + buffAdd + leagueAdd;
    }

    /**
     * 是否超帽
     *
     * @param team
     * @param teamPlayer
     * @return npc 永不超帽
     */
    public boolean isSalaryOverflow(Team team, TeamPlayer teamPlayer) {
        if (GameSource.isNPC(team.getTeamId())) {
            return false;
        }
        int price = getTeamPrice(team);
        int playerPrice = teamPlayer.getPlayers().stream().mapToInt(p -> p.getPrice()).sum();
        return playerPrice > price;
    }

    /**
     * 是否超帽
     *
     * @param teamId
     * @return true 超帽. npc 永不超帽
     */
    public boolean isSalaryOverflow(long teamId) {
        if (teamId == 0 || GameSource.isNPC(teamId)) {
            return false;
        }
        Team team = getTeam(teamId);
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        return isSalaryOverflow(team, tp);
    }

    public void addNPC(Team team) {
        this.teams.put(team.getTeamId(), team);
    }

    public TeamBattleConfig getTeamBattleConfig(long teamId) {
        TeamBattleConfig config = teamBattleConfigMap.get(teamId);
        if (config == null) {
            config = redis.getObj(RedisKey.getKey(teamId, RedisKey.Team_Battle_Config));
            if (config == null) {
                String ts = TacticsConsole.getDefaultStudyStr();
                int[] j = StringUtil.toIntArray(ts, StringUtil.DEFAULT_ST);
                TeamCoach coach = coachManager.getTeamCoach(teamId);
                config = new TeamBattleConfig("", ts, j[0], j[1], coach.getDefaultCoach());
            }
            saveTeamBattleConfig(teamId, config);
        }
        return config;
    }

    private void saveTeamBattleConfig(long teamId, TeamBattleConfig config) {
        redis.set(RedisKey.getKey(teamId, RedisKey.Team_Battle_Config), config);
        teamBattleConfigMap.put(teamId, config);
    }

    public long getTeamId(String name) {
        return this.cacheMap.inverse().getOrDefault(name, 0l);
    }

    /**
     * 尽量少用这个取球队名，一些球队ID可能已经不存在，引起空指针
     *
     * @param teamId
     * @return
     */
    public String getTeamName(long teamId) {
        return this.cacheMap.get(teamId);
    }

    /**
     * 包括可以取NPC的名字
     *
     * @param teamId
     * @return
     */
    public String getTeamNameById(long teamId) {
        Team team = getTeam(teamId);
        return team == null ? "" : team.getName();
    }

    @ClientMethod(code = ServiceCode.GameManager_checkTeamName)
    public void checkTeamName(String name) {
        boolean exist = existTeamName(name);

        if (chatManager.shieldText(name)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_1.code).build());
            return;
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(exist ? ErrorCode.Team_2.code : ErrorCode.Success.code).build());
    }

    /**
     * 更换队徽
     *
     * @param logo
     */
    @ClientMethod(code = ServiceCode.Team_Change_Logo)
    public void changeLogo(String logo) {
        long teamId = getTeamId();
        Team t = getTeam(teamId);
        t.changeLogo(logo);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 更新小秘书
     *
     * @param id
     */
    @ClientMethod(code = ServiceCode.Team_Change_Sec)
    public void changeSec(int id) {
        long teamId = getTeamId();
        Team t = getTeam(teamId);
        t.changeSec(id);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 更新当前赛前配置
     *
     * @param props
     * @param jg
     * @param fs
     * @param ts
     */
    @ClientMethod(code = ServiceCode.TeamManger_updateTeamBattleConfig)
    public void updateTeamBattleConfig(String props, int jg, int fs, String ts) {
        long teamId = getTeamId();
        TeamCoach coach = coachManager.getTeamCoach(teamId);
        TeamBattleConfig tbc = new TeamBattleConfig(props, ts, jg, fs, coach.getDefaultCoach());
        saveTeamBattleConfig(teamId, tbc);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.TeamManger_showTeamBattleConfig)
    public void showTeamBattleConfig() {
        long teamId = getTeamId();
        TeamBattleConfig tbc = getTeamBattleConfig(teamId);
        sendMessage(getTeamBattleConfigData(tbc));
    }

    /**
     * 购买球卷工资帽
     */
    @ClientMethod(code = ServiceCode.TeamManger_buyTeamPriceMoney)
    public void buyTeamPriceMoney() {
        long teamId = getTeamId();
        Team team = getTeam(teamId);
        //取下一级的配置
        int nextPriceCount = team.getPriceCount() + 1;
        TeamPriceMoneyBean priceBean = ShopConsole.getTeamPriceMoneyBean(nextPriceCount);
        if (priceBean == null) {//已经提升到最高等级
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_3.code).build());
            return;
        }

        // 先消耗加帽卷
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(ConfigConsole.global().teamAddPriceItem, priceBean.getMoney())) {
            // 道具不足，球卷来筹
            int needMoney = priceBean.getMoney() - teamProp.getPropNum(ConfigConsole.global().teamAddPriceItem);

            boolean ok = teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球队, "购买工资帽"));
            if (!ok) {//球卷不足
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_4.code).build());
                return;
            }

            // 删除道具
            PropSimple needProp = new PropSimple(ConfigConsole.global().teamAddPriceItem, teamProp.getPropNum(ConfigConsole.global().teamAddPriceItem));
            propManager.delProp(teamId, needProp, true, true);
        } else {

            // 删除道具
            PropSimple needProp = new PropSimple(ConfigConsole.global().teamAddPriceItem, priceBean.getMoney());
            propManager.delProp(teamId, needProp, true, true);
        }

        team.updatePriceCount(nextPriceCount);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

    }

    @ClientMethod(code = ServiceCode.TeamManger_showTeamAllAbility)
    public void showTeamAllAbility() {
        long teamId = getTeamId();
        TeamAbility ab = getTeamAllAbility(teamId);
        sendMessage(TeamPB.TeamAbilityData.newBuilder()
            .setAbilityType(AbilityType.Team.getType())
            .setAttack(Math.round(ab.getAttrData(EActionType.ocap)))
            .setDefend(Math.round(ab.getAttrData(EActionType.dcap)))
            .build());
    }

    @ClientMethod(code = ServiceCode.TeamManger_showTeamInfo)
    public void showTeamInfo(long seachTeamId) {
        Team team = getTeam(seachTeamId);
        if (team != null) { sendMessage(getTeamData(team)); }
    }

    /**
     * 查看球队明细
     */
    @ClientMethod(code = ServiceCode.TeamManger_viewTeamInfo)
    public void viewTeamInfo(long targetTid) {
        Team team = getTeam(targetTid);
        long teamId = getTeamId();
        if (team != null) {
            int vip = vipManager.getVip(targetTid).getLevel();
            String leagueName = leagueManager.getLeagueName(targetTid);
            int rank = rankManager.getTeamCapRank(targetTid);
            // 对方的
            int totalCap = 0;
            List<TeamPB.TeamAbilityData> targetAllCapResp = Lists.newArrayList();
            Collection<BaseAbility> targetAllCaps = getAbilities(targetTid);
            for (BaseAbility ab : targetAllCaps) {
                targetAllCapResp.add(getTeamAbilityData(ab));
                totalCap += ab.getTotalCap();
            }
            // 我的
            List<TeamPB.TeamAbilityData> selfAllCapResp = Lists.newArrayList();
            Collection<BaseAbility> selfAllCaps = getAbilities(teamId);
            for (BaseAbility ab : selfAllCaps) {
                selfAllCapResp.add(getTeamAbilityData(ab));
            }
            // 阵容
            List<Player> targetPlayers = playerManager.getTeamPlayer(targetTid).getPlayers();
            List<PlayerPB.PlayerData> targetPrResp = Lists.newArrayList();
            for (Player p : targetPlayers) {
                targetPrResp.add(playerManager.getPlayerData(p));
            }
            sendMessage(TeamPB.TeamInfoData.newBuilder()
                .setTeamInfo(getTeamData(team))
                .setLeague(leagueName)
                .setVip(vip)
                .setRank(rank)
                .setTotalCap(totalCap)
                .addAllPlayerList(targetPrResp)
                .addAllCapList(targetAllCapResp)
                .addAllMyCapList(selfAllCapResp)
                .build());
        } else {
            sendMessage(TeamPB.TeamInfoData.newBuilder()
                .setTeamInfo(TeamData.newBuilder()
                    .setTeamId(targetTid))
                .build());
        }
    }

    /**
     * 球队和阵容球员攻防, 按类型汇总.
     */
    private Collection<BaseAbility> getAbilities(long teamId) {
        List<PlayerAbility> lps = teamCapManager.getLineupAbilities(teamId);//球员攻防
        Map<AbilityType, BaseAbility> all = new HashMap<>();
        for (PlayerAbility ab : lps) {
            if (!all.containsKey(ab.getType())) {
                all.put(ab.getType(), ab);
                continue;
            }
            all.get(ab.getType()).addSameInfo(ab);
        }
        FieldCap teamcap = teamCapManager.getTeamOtherCap(teamId);//球队攻防
        all.put(teamcap.getGym().getType(), teamcap.getGym());
        all.put(teamcap.getLeague().getType(), teamcap.getLeague());
        //all.put(teamcap.getPlayerCard().getType(), teamcap.getPlayerCard());
        return all.values();
    }

    /**
     * 查看整容明细
     *
     * @param teamId
     */
    @ClientMethod(code = ServiceCode.TeamManger_viewPlayerDetail)
    public void viewPlayerDetail(long teamId) {
        Team team = getTeam(teamId);
        if (team != null) {
            sendMessage(playerArchiveManager.showPlayerInfoDetail(teamId));
        }
    }

    @ClientMethod(code = ServiceCode.TeamManger_updateHelp)
    public void updateHelp(String help) {
        long teamId = getTeamId();
        Team team = getTeam(teamId);
        team.updateHelp(help);
        //		log.error("-------------->{}",help); 1140:40
        if (help.indexOf("l=10") >= 0) {
            taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.排行榜.getName());
        }
        //	    if (team.getHelp().indexOf("g=115")>=0) {
        //
        //	    }
        if (help.indexOf("g=1510") >= 0) {
            propManager.addProp(teamId, new PropSimple(1140, 40), true, ModuleLog.getModuleLog(EModuleCode.新手引导, "g=1510"));
        }

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        GameHelpStepLogManager.Log(teamId, help);
    }

    @ClientMethod(code = ServiceCode.TeamManger_buyLineupCount)
    public void buyLineupCount() {
        long teamId = getTeamId();
        Team team = getTeam(teamId);
        if (team.getLineupCount() < 4) {
            log.debug("玩家初始替补出现异常");
            return;
        }
        int curLineupCount = team.getLineupCount() + 1;
        if (curLineupCount + 5 > playerManager.getMaxPlayerCount()) {
            log.debug("超出最大替补上限");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.OtherError.code).build());
            return;
        }
        int needMoney = 50;
        if (!teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.球队替补, "购买替补位"))) {
            log.debug("球卷不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.OtherError.code).build());
            return;
        }
        if (team.getLevel() < 20) {
            log.debug("等级不足");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
            return;
        }
        team.updateLineupCount(curLineupCount);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    public int getTeamMaxPlayerCount(long teamId) {
        Team team = getTeam(teamId);
        return team.getLineupCount() + 5;
    }

    @RPCMethod(code = CrossCode.TeamManager_getTeamNodeInfo, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void getTeamNodeInfo(long teamId) {
        TeamNodeInfo info = getLocalTeamNodeInfo(teamId);
        RPCMessageManager.responseMessage(info);
    }

    @RPCMethod(code = CrossCode.Team_Simple_Data, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void getTeamSimpleData(long teamId) {
        Team team = getTeam(teamId);
        if (team == null) {
            RpcTask.resp(ErrorCode.Team_Null);
            return;
        }
        RpcTask.resp(teamResp(team));
    }

    /**
     * 更改球队名称
     *
     * @param String 新球队名称
     */
    @ClientMethod(code = ServiceCode.TeamManager_teamChangeName)
    public synchronized void changeName(String name) {
        if (chatManager.shieldText(name)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_1.code).build());
            return;
        }

        if (this.existTeamName(name)) {//球队名重复
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Exist.code).build());
            return;
        }

        long teamId = getTeamId();
        Team team = getTeam(teamId);
        TeamProp teamProp = propManager.getTeamProp(teamId);
        PropSimple needProp = new PropSimple(ConfigConsole.global().teamChangeNamePropId, 1);
        if (!teamProp.checkPropNum(needProp)) {
            log.debug("改名道具不足{}", needProp.toString());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }

        // 删除道具
        propManager.delProp(teamId, needProp, true, true);

        team.changeName(name);
        this.cacheMap.put(teamId, team.getName());

        // 同步联盟名字
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        League league = leagueManager.getLeague(lt.getLeagueId());
//        if (lt != null && lt.getLeagueId() != 0 && lt.getLevel() == ELeagueTeamLevel.盟主 && league != null) {
//            league.updateTeamName(name);
//            // 联盟成员列表信息更改
//            leagueManager.updateLeagueTeamName(teamId, name);
//        }
        
        if (lt != null && lt.getLeagueId() != 0 && league != null) {
        	// 修改联盟显示的盟主名字
        	if (lt.getLevel() == ELeagueTeamLevel.盟主) {
        		league.updateTeamName(name);
			}
            // 联盟成员列表信息更改
            leagueManager.updateLeagueTeamName(teamId, name);
        }

        //排行榜
        rankManager.updateTeamNameCap(teamId, name);
        rankManager.updateTeamNameLev(teamId, name);

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    public TeamSimpleData teamResp(long tid) {
        return teamResp(getTeam(tid));
    }

    public TeamSimpleData teamResp(Team team) {
        return teamRespBuilder(team).build();
    }

    public TeamSimpleData.Builder teamRespBuilder(long tid) {
        return teamRespBuilder(getTeam(tid));
    }

    public TeamSimpleData.Builder teamRespBuilder(Team team) {
        TeamSimpleData.Builder resp = TeamSimpleData.newBuilder();
        if (team == null) {
            return resp;
        }
        resp.setTeamId(team.getTeamId());
        resp.setLevel(team.getLevel());
        resp.setName(team.getName());
        resp.setLogo(team.getLogo());
        int lid = leagueManager.getLeagueId(team.getTeamId());
        if (lid > 0) {
            resp.setLeagueId(lid);
            resp.setLeagueName(leagueManager.getLeagueName(lid));
        }
        TeamVip vip = vipManager.getVip(team.getTeamId());
        if (vip != null) {
            resp.setVip(vip.getLevel());
        }
        return resp;
    }

    public TeamNodeInfo getLocalTeamNodeInfo(long teamId) {
        Team team = getTeam(teamId);
        TeamAbility ability = getTeamAllAbility(teamId);
        TeamNodeInfo info = new TeamNodeInfo(teamId, team.getName(), team.getLogo(), team.getLevel(), ability.getTotalCap());
        return info;
    }

    /**
     * 取球队攻防
     *
     * @param teamId
     * @return
     */
    public float getTeamCap(long teamId) {
        TeamAbility ab = getTeamAllAbility(teamId);
        return ab.getAttrData(EActionType.ocap) + ab.getAttrData(EActionType.dcap);
    }

    /**
     * 取球队真是攻防,计算超帽后的攻防
     *
     * @param teamId
     * @return
     */
    public float getTeamRealCap(long teamId) {
        return getTeamCap(teamId) * ConfigConsole.global().overSalary;
    }

    private TeamPB.TeamBattleConfigData getTeamBattleConfigData(TeamBattleConfig tbc) {
        List<PropPB.PropSimpleData> datas = Lists.newArrayList();
        tbc.getProps().forEach(ps -> datas.add(getPropSimpleData(ps)));
        List<Integer> tsData = Lists.newArrayList();
        tbc.getEquTacticsList().forEach(tc -> tsData.add(tc.getId()));
        return TeamPB.TeamBattleConfigData.newBuilder().setFs(tbc.getDefenseTactics().getId()).setJg(tbc.getOffenseTactics().getId())
            .addAllProps(datas).addAllTactics(tsData).build();
    }

    private PropPB.PropSimpleData getPropSimpleData(PropSimple ps) {
        return PropPB.PropSimpleData.newBuilder().setNum(ps.getNum()).setPropId(ps.getPropId()).build();
    }

    public boolean existTeamName(String name) {
        return cacheMap.containsValue(name);//判断名字是否存在
    }

    public void createTeam(long teamId, Team team) {
        this.teams.put(teamId, team);
        this.cacheMap.put(teamId, team.getName());
    }

    public void createTeamDaily(long teamId, TeamDaily teamDaily) {
        this.teamDailys.put(teamId, teamDaily);
    }

    public boolean existTeam(long teamId) {
        return cacheMap.containsKey(teamId);
    }

    /**
     * 非NPC
     *
     * @return
     */
    public int getTeamCount() {
        return (int) cacheMap.keySet().stream().filter(teamId -> !NPCConsole.isNPC(teamId)).count();
    }

    /**
     * 通过球队名称，取球队ID
     *
     * @param name
     * @return
     */
    public long getTeamIdByName(String name) {
        for (long teamId : cacheMap.keySet()) {
            if (cacheMap.get(teamId).equals(name)) {
                return teamId;
            }
        }
        return 0L;
    }

    /**
     * 通过球队名称，取球队
     *
     * @param name
     * @return
     */
    public Team getTeamByName(String name) {
        long teamId = getTeamIdByName(name);
        if (teamId == 0) { return null; }
        return getTeam(teamId);
    }

    public TeamPB.TeamData getTeamData(Team team) {
        return TeamPB.TeamData.newBuilder()
            .setLevel(team.getLevel())
            .setTeamId(team.getTeamId())
            .setTeamLogo(team.getLogo())
            .setTeamName(team.getName())
            .setTitle(team.getTitle().getVal())
            .setHelp(team.getHelp())
            .setCreateDay(team.getCreateDay())
            .setLineupCount(team.getLineupCount())
            .setCreateTime(team.getCreateTime().getMillis())
            .build();
    }

    public TeamPB.TeamAbilityData getTeamAbilityData(BaseAbility ability) {
        return TeamPB.TeamAbilityData.newBuilder()
            .setAbilityType(ability.getType().getType())
            .setAttack((int) ability.getAttrData(EActionType.ocap))
            .setDefend((int) ability.getAttrData(EActionType.dcap))
            .build();
    }

    public void updateTeamBattleCount(long teamId, boolean win) {
        Team team = getTeam(teamId);
        updateTeamBattleCount(team, win);
    }

    public void updateTeamBattleCount(Team team, boolean win) {
        //
        taskManager.updateTask(team.getTeamId(), ETaskCondition.累计胜场, 1, EModuleCode.球队.getName());
    }

    /**
     * 取当前球队总攻防信息
     *
     * @param teamId
     * @return
     */
    public TeamAbility getTeamAllAbility(long teamId) {
        TeamAbility ability = new TeamAbility(AbilityType.Team, teamCapManager.getTeamAllPlayerAbilities(teamId));
        log.debug("球队攻防={}", ability);
        return ability;
    }

    //    /**
    //     * 取当前球队首发攻防信息
    //     *
    //     * @param teamId
    //     * @return
    //     */
    //    public TeamAbility getTeamLineUpAbility(long teamId) {
    //        //        Team team = getTeam(teamId);
    //        TeamAbility ability = new TeamAbility(AbilityType.Team, teamCapManager.getStartingAbilities(teamId));
    //        //自增球队加成
    //        //		team.getAbility().values().forEach(ab->ability.sumCap(ab.getAttrData(EActionType.T_进攻), ab.getAttrData(EActionType.T_防守)));
    //        //		//自增球员加成
    //        //		TeamPlayer tp = playerManager.getTeamPlayer(teamId);
    //        //		tp.getLineupPlayerList().stream()
    //        //						.map(p->PlayerAbilityAPI.getPlayerAbility(p))
    //        //						.forEach(ab->ability.sumCap(ab.getAttrData(EActionType.T_进攻)
    //        //								, ab.getAttrData(EActionType.T_防守)));
    //        //
    //
    //        return ability;
    //    }

    /**
     * 设置球队世界聊天禁言状态
     *
     * @param teamId
     * @param status 0 正常  1 禁言
     */
    public void changeTeamChatStatus(long teamId, int status) {
        if (status < 0 || status > 1) {
            return;
        }
        Team team = getTeam(teamId);
        team.setChatStatus(status);
        team.save();
        chatManager.addBackTeam(teamId, status);
    }

    /**
     * 设置球队状态
     *
     * @param status 0正常 1封号
     */
    public void changeTeamUserStatus(long teamId, int status) {
        if (status < 0 || status > 1) {
            return;
        }
        Team team = getTeam(teamId);
        if (team == null) { return; }
        team.setUserStatus(status);
        team.save();
        gameManager.addBackTeamId(teamId, status);
        // 封号处理，踢下线
        if (status == 1) {
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Close_Connect);
            TeamOfflineJob.forceOffline(teamId);
            log.info("封号，踢用户下线，移除玩家信息->[{}] status {}", teamId, status);
            GameSource.offlineUser(teamId);
        }
    }

    public TeamPB.TeamCapData getTeamCapData(EModuleCode module, int cap) {
        return TeamPB.TeamCapData.newBuilder().setCap(cap).setCapType(module.getId()).build();
    }

    @Override
    public void instanceAfter() {
        teams = Maps.newConcurrentMap();
        teamDailys = Maps.newConcurrentMap();
        List<TeamDaily> teamDailyList = teamAO.getAllTeamDaily();
        teamDailyList.forEach(teamDaily -> teamDailys.put(teamDaily.getTeamId(), teamDaily));
        cacheMap = HashBiMap.create();
        teamBattleConfigMap = Maps.newConcurrentMap();
        List<TeamPO> teamList = teamAO.getAllSimpleTeam();
        //将所有玩家的ID与名称放入本地缓存中
        teamList.forEach(team -> cacheMap.put(team.getTeamId(), team.getName()));
    }

    @Override
    public void initConfig() {
        NPCConsole.getNpcs().values().forEach(npc -> addNPC(Team.createNPC(npc.getNpcId(), npc.getNpcName(),
            npc.getLogo(), npc.getAttack(), npc.getDefend(), npc.getLevel())));
    }

    /**
     * 取所有在线非NPC球队
     *
     * @return
     */
    public List<Team> getAllOnlineTeam() {
        return this.teams.values().stream().filter(t -> !NPCConsole.isNPC(t.getTeamId())).collect(Collectors.toList());
    }

    public Set<Long> getAllTeam() {
        return new HashSet<>(cacheMap.keySet());
    }

    @Override
    public void offline(long teamId) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        Team team = this.teams.remove(teamId);
        if (team == null) {
            return;
        }
        team.offline();
        this.teamBattleConfigMap.remove(teamId);
        this.teamDailys.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        Team team = teams.remove(teamId);
        if (team == null) {
            return;
        }
        teamBattleConfigMap.remove(teamId);
        this.teamDailys.remove(teamId);
    }

    @Override
    public int offlineOrder() {
        return Integer.MAX_VALUE;
    }

    void offlineAll() {
        for (long teamId : getAllTeam()) {
            offline(teamId);
        }
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Team.getOrder();
    }

    /**
     * 清档调用
     */
    public void clearAllData() {
        teamAO.clearAllData();
    }

    /**
     * 清理每日数据
     */
    public void clearDailyData() {
        teamAO.clearDailyData();
    }
}
