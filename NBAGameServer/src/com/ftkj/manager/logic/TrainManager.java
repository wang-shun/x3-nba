package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.LeagueArenaConsole;
import com.ftkj.console.ModuleConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.TrainConsole;
import com.ftkj.db.ao.logic.ITrainAO;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ETrain;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.LoginParam;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.BaseAbility;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.active.base.EventRegister;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.manager.train.LeagueTrainBean;
import com.ftkj.manager.train.TeamTrain;
import com.ftkj.manager.train.Train;
import com.ftkj.manager.train.TrainArenaBean;
import com.ftkj.manager.train.TrainBean;
import com.ftkj.manager.train.TrainInfo;
import com.ftkj.manager.train.TrainNpcBean;
import com.ftkj.manager.train.TrainTypeBean;
import com.ftkj.manager.vip.TeamVip;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.TrainPB.ChangeTeamTrainData;
import com.ftkj.proto.TrainPB.ChangeTrainData;
import com.ftkj.proto.TrainPB.LeagueTrainData;
import com.ftkj.proto.TrainPB.LeagueTrainDataList;
import com.ftkj.proto.TrainPB.TeamTrainMainData;
import com.ftkj.proto.TrainPB.TrainData;
import com.ftkj.proto.TrainPB.TrainGrabInfoData;
import com.ftkj.proto.TrainPB.TrainGrabInfoList;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.quartz.QuartzServer;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.IDUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

/**
 * @author tim.huang
 * 2017年7月17日
 * 训练馆管理
 */
@EventRegister({
        EEventType.登录
})
public class TrainManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(TrainManager.class);
    @IOC
    private RedPointManager redPointManager;
    @IOC
    private ITrainAO trainAO;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamCapManager teamCapManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private LeagueArenaManager leagueArenaManager;

    /** 训练馆缓存数据 */
    private Map<Long, Map<Integer, Train>> trainMap;
    /** 球队训练数据 */
    private Map<Long, TeamTrain> teamTrainMap;
    /** 训练馆刷新列表初始球队 */
//    private int[] initNpc;
    /** 联盟训练馆数据 */
    private Map<Integer, LeagueTrain> leagueTrainMap;

    @Override
    public void instanceAfter() {
        EventBusManager.register(EEventType.登录, this);

        trainMap = Maps.newConcurrentMap();
        teamTrainMap = Maps.newConcurrentMap();
        leagueTrainMap = Maps.newConcurrentMap();

        // 删除训练馆数据
        redis.del(RedisKey.Train_Refrush_List);

        // 初始化数据库的数据到缓存
        List<Train> trainList = trainAO.getAllTrain();
        for (Train train : trainList) {
        	//判断是否参与训练,如果是存到Redis
            if (this.isTrain(train)) {
                redis.zadd(RedisKey.Train_Refrush_List, train.getPlayerCap(), str(train.getTeamId()) + "_" + str(train.getTrainId()));
            }

            Map<Integer, Train> map = trainMap.get(train.getTeamId());
            if (map == null) {
                map = Maps.newConcurrentMap();
                trainMap.put(train.getTeamId(), map);
            }

            map.put(train.getTrainId(), train);
        }

        List<TeamTrain> teamTrainList = trainAO.getAllTeamTrain();
        for (TeamTrain teamTrain : teamTrainList) {
            teamTrainMap.put(teamTrain.getTeamId(), teamTrain);
        }

//        initNpc = new int[] {};
//        initNpc = StringUtil.toIntArray(ConfigConsole.getGlobal().trainInitNpc, StringUtil.DEFAULT_ST);
//        for (int i = 0; i < initNpc.length; i++) {
//            initNPCTrain(initNpc[i]);
//        }
        
        // 初始化配置的NPc训练馆数据
        initNPCTrain(TrainConsole.getTrainNpcBeanMap());

        List<LeagueTrain> leagueTrainList = trainAO.getLeagueTrainList();
        for (LeagueTrain leagueTrain : leagueTrainList) {
            leagueTrainMap.put(leagueTrain.getBlId(), leagueTrain);
        }

        if (leagueTrainMap.isEmpty()) {
            for (int blId : TrainConsole.getLeagueTrainBeanMap().keySet()) {
                LeagueTrain leagueTrain = new LeagueTrain();
                leagueTrain.setBlId(blId);
                leagueTrain.setBtId(0);
                leagueTrain.save();
                leagueTrainMap.put(blId, leagueTrain);

            }
        }

        chioseTeamTask();
    }
    
    /**
     * 根据机器人Id(npcId)生成一个NPC在个人训练馆的数据.
     * @param npcId		机器人Id
     */
    private void initNPCTrain(Map<Integer, TrainNpcBean> map) {
    	if (map == null) {
			return;
		}
    	
    	map.values().forEach(trainNpcBean -> {
    		Train train = new Train();
    		train.setTrainId(1);
    		train.setPlayerCap(trainNpcBean.getDefence());
    		train.setPlayerId(trainNpcBean.getPlayerId());
    		train.setTeamId(trainNpcBean.getId());//设置成配置的NpcId
    		train.setPlayerRid(0);
    		train.setType(ETrain.TRAIN_TYPE_1.getId());//NPc默认1类型
    		train.setTrainLevel(trainNpcBean.getLevel());
    		train.setRewardState(ETrain.TRAIN_RSTATE_1.getId());
    		
    		Map<Integer, Train> innerMap = trainMap.get(train.getTeamId());
    		if (innerMap == null) {
    			innerMap = Maps.newConcurrentMap();
    			trainMap.put(train.getTeamId(), innerMap);
    		}
    		
    		innerMap.put(train.getTrainId(), train);
    		
    		// 存入Redis
    		redis.zadd(RedisKey.Train_Refrush_List, train.getPlayerCap(),
    				str(train.getTeamId()) + "_" + str(train.getTrainId()));
    	});
    }

    /**
     * 根据机器人Id(npcId)生成一个NPC在个人训练馆的数据.
     * @param npcId		机器人Id
     */
//    private void initNPCTrain(int npcId) {
//        NPCBean npcBean = NPCConsole.getNPC(npcId);
//        List<Player> players = PlayerConsole.getNPCPlayerList(npcBean.getNpcId(), npcBean.getPlayers());
//        if (players.size() > PlayerConsole.Team_Player_Num) {
//            players = players.subList(0, PlayerConsole.Team_Player_Num);
//        }
//
//        int totalAttack = players.stream().mapToInt(p -> (int) p.getPlayerBean().getAbility(EActionType.ocap)).sum();
//        int totalDefend = players.stream().mapToInt(p -> (int) p.getPlayerBean().getAbility(EActionType.dcap)).sum();
//
//        List<PlayerAbility> playerAbilities = Lists.newArrayList();
//        players.forEach(p -> {
//            PlayerAbility pa = new PlayerAbility(AbilityType.Npc_Buff, p.getPlayerRid());
//            float ocap = p.getPlayerBean().getAbility(EActionType.ocap);
//            float dcap = p.getPlayerBean().getAbility(EActionType.dcap);
//            pa.setAttr(EActionType.ocap, ocap / totalAttack * npcBean.getPlayerAttack());
//            pa.setAttr(EActionType.dcap, dcap / totalDefend * npcBean.getPlayerDefend());
//            p.updateAbility(pa);
//
//            playerAbilities.add(pa);
//        });
//
//        //根据球员的攻防cap值,从小打到排序
//        playerAbilities.sort(Comparator.comparing(PlayerAbility::getTotalCap));
//        PlayerAbility playerAbility = playerAbilities.get(playerAbilities.size() - 1);
//        Player player = players.stream().filter(p -> p.getPlayerRid() == playerAbility.getPlayerId()).findFirst().orElse(null);
//
//        Train train = new Train();
//        train.setTrainId(1);
//        train.setPlayerCap(playerAbility.getTotalCap());
//        train.setPlayerId(player.getId());
//        train.setTeamId(player.getTeamId());
//        train.setPlayerRid(player.getPlayerRid());
//        train.setType(ETrain.TRAIN_TYPE_1.getId());
//        train.setTrainLevel(1);
//        train.setRewardState(ETrain.TRAIN_RSTATE_1.getId());
//        // train.save();
//
//        Map<Integer, Train> map = this.getTrainMapByTeamId(player.getTeamId());
//        if (map == null) {
//            map = Maps.newConcurrentMap();
//            trainMap.put(player.getTeamId(), map);
//        }
//
//        map.put(train.getTrainId(), train);
//
//        // 存入Redis
//        redis.zadd(RedisKey.Train_Refrush_List, train.getPlayerCap(), str(player.getTeamId()) + "_" + str(train.getTrainId()));
//    }

    /** 获取球队训练馆数据(主界面) */
    @ClientMethod(code = ServiceCode.TrainManager_GetTeamTrainData)
    public void getTeamTrainData() {
        long teamId = getTeamId();

        Team team = teamManager.getTeam(teamId);
        if (isModuleDisable(team)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Level.code).build());
            return;
        }

        TeamTrain teamTrain = getTeamTrain(teamId);
        Map<Integer, Train> map = getTrainMapByTeamId(teamId);

        TeamTrainMainData.Builder builder = TeamTrainMainData.newBuilder();
        builder.setGrabCount(teamTrain.getRobbedCount());
        builder.setGrabTime(teamTrain.getRobbedTime());
        builder.setRefreshTime(teamTrain.getRefreshTime());
        builder.setRefreshListTime(teamTrain.getRefreshListTime());
        
        // 获取仓库cap 最大球员
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
        List<Player> players = teamPlayer.getStorages();
        int playerMaxCap = 0;//仓库球员最大的攻防值
        PlayerAbility playerCap = null;
        
        if (players != null && (playerCap = playerManager.getPlayerAbilityByMaxCap(players)) != null) {
        	playerMaxCap = playerCap.getTotalCap();
        }
        
        // 设置仓库球员中最大的攻防值
        builder.setPlayerMaxCap(playerMaxCap);

        for (Map.Entry<Integer, Train> entry : map.entrySet()) {
            builder.addTrain(builderTrainData(entry.getValue()));
        }

        sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_TeamTrainData);

        // 推送可抢夺数据
        this.refrushTrainList0(teamId, true);
    }

    /** 训练 */
    @ClientMethod(code = ServiceCode.TrainManager_Training)
    public void training(int trainId, int playerId, int trainType) {
        long teamId = getTeamId();

        Train train = this.getTrainMapByTeamIdAndTrainId(teamId, trainId);
        if (train == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Not_Exist.code).build());
            return;
        }

        synchronized (train.getLock()) {
            if (train.getPlayerId() > 0 || train.getPlayerRid() > 0) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Player_Exist.code).build());
                return;
            }

            TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
            if (!teamPlayer.existStoragePlayer(playerId)) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
                return;
            }

            Player player = teamPlayer.getStoragePlayer(playerId);
            if (player == null) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
                return;
            }

            player.setStorage(EPlayerStorage.训练馆.getType());
            player.save();

            PlayerAbility playerCap = teamCapManager.getPlayerAllCap(player.getTeamId(), player.getPlayerRid(), player.getPlayerTalent());
            train.setPlayerCap(playerCap.getTotalCap());
            train.setPlayerId(playerId);
            train.setPlayerRid(player.getPlayer().getPlayerRid());
            train.setStartTime(System.currentTimeMillis());
            train.setType(trainType);
            train.save();

            // 存入Redis
            redis.zadd(RedisKey.Train_Refrush_List, train.getPlayerCap(), str(train.getTeamId()) + "_" + str(train.getTrainId()));

            ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
            builder.addTrain(builderTrainData(train));

            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
            sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);

            // 任务
            taskManager.updateTask(teamId, ETaskCondition.训练馆训练xxx次, 1, "");
        }
    }

    /** 领取训练奖励 */
    @ClientMethod(code = ServiceCode.TrainManager_GetTrainReward)
    public void getTrainReward(int trainId) {
        long teamId = getTeamId();

        Train train = this.getTrainMapByTeamIdAndTrainId(teamId, trainId);
        if (train == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Not_Exist.code).build());
            return;
        }

        if (train.getPlayerId() < 1 || train.getPlayerRid() < 1) {
            log.error("train getTrainReward. teamId{}, train playerId {} is null", teamId, train.getPlayerId());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Reward_Not_Can_Get.code).build());
            return;
        }

        synchronized (train.getLock()) {
            // 获取球员数据
            TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
            Player player = teamPlayer.getStoragePlayer(train.getPlayerId());
            if (player == null) {
                log.error("train getTrainReward. tid {} playerId {} is null", train.getTrainId(), train.getPlayerId());
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Not_Have_Player.code).build());
                return;
            }

            TrainTypeBean trainTypeBean = TrainConsole.getTrainTypeBeanMap().get(train.getType());
            if (DateTimeUtil.difTimeMill(train.getStartTime()) < trainTypeBean.getTime() * DateTimeUtil.MINUTE && train.getTrainHour() < 1) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Reward_Not_Can_Get.code).build());
                return;
            }

            // 发奖励
            float rate = 1 + buffManager.getBuffSet(teamId, EBuffType.Train_Award).getValueSum() / 1000.0f;
            PropSimple propSimple = getTrainReward(train, trainTypeBean);
            // 总产量*（1-被抢次数*20%）
            propSimple.setNum((int) Math.ceil(propSimple.getNum() * rate) - (int) Math.ceil(1 - train.getRobbedNum() * ConfigConsole.getGlobal().trainGrabResourceSurplus * 0.01));
            int exp = (int) Math.ceil(propSimple.getNum() * 0.5);

            propManager.addProp(teamId, propSimple, true, ModuleLog.getModuleLog(EModuleCode.训练馆, "训练奖励"));

            // 获得训练经验，验证是否升级
            this.trainUpLevel(train, exp);
            log.info("getTrainReward|clearTrain()");
            // 清理训练位
            this.clearTrain(train, false);
            if (player.getStorage() == EPlayerStorage.训练馆.getType()) {
            	//强制球员状态改为在仓库中(普通训练位领取奖励球员训练状态没有改变的bug,没有找到bug是如何造成的所以先强制临时解决)
            	player.setStorage(EPlayerStorage.仓库.getType());
            	player.save();
            	log.info("TrainManager,清理训练位失败,现再次进行了手动处理球员的状态.teamId = {}|playerId = {}",teamId, player.getId());
			}
            
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

            ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
            builder.addTrain(builderTrainData(train));

            sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
        }
    }

    /** 
     * 训练馆清理.
     * Redis中缓存的玩家训练馆数据也删除.
     */
    private int clearTrain(Train train, boolean isClear) {
    	log.info("clearTrain|start|trainId|{}|teamId|{}|playerId|{}"
    		+ "|playerRid|{}|trainHour|{}|rewardState|{}|clear|{}"
    		, train.getTrainId(), train.getTeamId(), train.getPlayerId(), train.getPlayerRid()
    		, train.getTrainHour(), train.getRewardState(), train.getClear());
        int pid = train.getPlayerId();
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(train.getTeamId());
        if(train.getPlayerId() > 0) {
            Player player = teamPlayer.getPlayerPid(train.getPlayerId());
            player.setStorage(EPlayerStorage.仓库.getType());
            player.save();
        }
        
        // 删除Redis中的缓存的玩家训练数据
        redis.zrem(RedisKey.Train_Refrush_List, str(train.getTeamId()) + "_" + str(train.getTrainId()));

        train.setType(0);
        train.setRewardState(0);
        train.setRobbedNum(0);
        train.setPlayerId(0);
        train.setPlayerRid(0);
        train.setStartTime(0);
        train.setPlayerCap(0);
        train.setTrainHour(0);      

        if (isClear) {
            train.setClear(1);
            train.setBlId(0);
        } else if (train.getIsLeague() == 1) {
            int leagueId = leagueManager.getLeagueId(train.getTeamId());
            LeagueTrain leagueTrain = this.getLeagueTrainByLeagueId(leagueId);
            if (leagueTrain == null) {
                train.setClear(1);
                train.setBlId(0);
            }
        }

        train.save();
        
        log.info("clearTrain|end|trainId|{}|teamId|{}|playerId|{}"
        		+ "|playerRid|{}|trainHour|{}|rewardState|{}|clear|{}"
        		, train.getTrainId(), train.getTeamId(), train.getPlayerId(), train.getPlayerRid()
        		, train.getTrainHour(), train.getRewardState(), train.getClear());
        
        return pid;
    }

    /** 抢夺 */
    @ClientMethod(code = ServiceCode.TrainManager_TrainGrab)
    public void trainGrab(long targetTeamId, int targetTrainId) {
        long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        TeamTrain teamTrain = getTeamTrain(teamId);
        if (log.isDebugEnabled()) {
            log.debug("train grab. tid {} targetTeamId {} targetTrainId {} tt {}", teamId, targetTeamId, targetTrainId, teamTrain);
        }
        // 抢夺CD
        if (teamTrain.getRobbedTime() > System.currentTimeMillis()) {
            sendMsg(ErrorCode.Train_Grob_CD);
            return;
        }

        // 抢夺次数
        if (teamTrain.getRobbedCount() < 1) {
            sendMsg(ErrorCode.Train_Grob_Count_Not_Enough);
            return;
        }

        Train targetTrain = this.getTrainMapByTeamIdAndTrainId(targetTeamId, targetTrainId);
        if (targetTrain == null) {
            sendMsg(ErrorCode.Train_Not_Exist);
            return;
        }

        int state = 0;
        int num = 0;
        
        // 获取仓库cap 最大球员
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
        List<Player> players = teamPlayer.getStorages();
        if (players == null) {
            sendMsg(ErrorCode.Train_Storage_Player_Is_Null);
            return;
        }

        PlayerAbility playerCap = playerManager.getPlayerAbilityByMaxCap(players);
        if (playerCap == null) {
            sendMsg(ErrorCode.Train_Player_Not_Have_Cap);
            return;
        }
        
        int trainType = targetTrain.getType();
        TrainTypeBean trainTypeBean = TrainConsole.getTrainTypeBeanMap().get(trainType == 0 ? 1 : trainType);
        PropSimple propSimple = getTrainReward(targetTrain, trainTypeBean);
        //=========为true则是NPc,false为真实的玩家球员,NPC可以被任意抢夺============
        if(targetTrain.getTeamId() < 1000){
        	num = grabNpc(teamId, playerCap, targetTrain, propSimple);
        }else {
        	num = grabTruePlayer(teamId, playerCap.getTotalCap(), targetTrain, propSimple, trainTypeBean);
		}
        
        //抢夺胜利
        if (num > 0) {
			state = 1;
		}

        // 第一次抢夺刷新时间
        if (teamTrain.getRefreshTime() < 1) {
            teamTrain.setRefreshTime(System.currentTimeMillis() + ConfigConsole.getGlobal().trainGrabCountRefresh * DateTimeUtil.MINUTE);
        }

        teamTrain.setRobbedTime(System.currentTimeMillis() + ConfigConsole.getGlobal().trainGrabCDRefresh * DateTimeUtil.MINUTE);
        teamTrain.setRobbedCount(teamTrain.getRobbedCount() - 1);
        teamTrain.save();

        // 推送自己
        ChangeTeamTrainData.Builder builder = ChangeTeamTrainData.newBuilder();
        builder.setGrabCount(teamTrain.getRobbedCount());
        builder.setRefreshTime(teamTrain.getRefreshTime());
        builder.setGrabTime(teamTrain.getRobbedTime());
        sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTeamTrainData);

        // 目标添加抢夺记录
        if (targetTrain.getTeamId() > 1000) {
            addTrainGrobInfo(targetTeamId, team, state, num);
        }

        // 任务
        taskManager.updateTask(teamId, ETaskCondition.训练馆抢夺xxx次, 1, "");
    }
    
    /**
     * 抢夺NPc.
     * @param teamId		球员Id
     * @param playerCap		仓库球员最大的进攻防
     * @param targetTrain	被抢夺的NpC
     * @param reward		抢夺胜利获得资源
     * @return				抢夺成功的资源数量,失败为0
     */
    private int grabNpc(long teamId, BaseAbility playerCap, Train targetTrain, PropSimple reward){
    	int amount = 0;
    	//判断抢夺是否成功,最大攻防值大于被抢夺的攻防值,则抢夺成功,否则失败.
    	if(playerCap.getTotalCap() > targetTrain.getPlayerCap()){
    		float rate = 1;//机器人buffer加成为1
    		double grobNum = reward.getNum() * rate * ConfigConsole.getGlobal().trainGrabResourceSurplus * 0.01;
    		PropSimple grobProp = new PropSimple(reward.getPropId(), (int) Math.ceil(grobNum));
    		amount = grobProp.getNum();
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(1).build());
			// 抢夺成功,获得抢夺的资源
			propManager.addProp(teamId, grobProp, true, ModuleLog.getModuleLog(EModuleCode.训练馆, "抢夺资源"));
            ChangeTrainData.Builder builder2 = ChangeTrainData.newBuilder();
            builder2.addTrain(builderTrainData(targetTrain));
            sendMessage(teamId, builder2.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
    	}else {
			//抢夺失败
    		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(2).build());
		}
    	
    	return amount;
    }
    
    /**
     * 抢夺真是球员.
     * @param teamId		球员Id
     * @param playerCap		仓库球员最大的进攻防
     * @param targetTrain	被抢夺的NpC
     * @param reward		抢夺胜利获得资源
     * @param trainTypeBean 训练馆类型
     * @param playerMaxCap  抢夺玩家仓库球员最大的攻防值
     * @return				抢夺成功的资源数量,失败为0
     */
    private int grabTruePlayer(long teamId, int playerMaxCap, Train targetTrain,
    		PropSimple reward, TrainTypeBean trainTypeBean){
    	int amount = 0;
    	// 目标最大攻防值 
        int targetMaxCap = targetTrain.getPlayerCap();
        //判断抢夺是否成功,最大攻防值大于被抢夺的攻防值,则抢夺成功,否则失败.
    	if(playerMaxCap > targetMaxCap){
            synchronized (targetTrain.getLock()) {
            	// 训练馆是否可抢
                if (DateTimeUtil.difTimeMill(targetTrain.getStartTime()) < trainTypeBean.getTime() * DateTimeUtil.MINUTE 
                		|| targetTrain.getType() < 1) {
                    sendMsg(ErrorCode.Train_Resour_Not_Grob);
                    return 0;
                }

                //是否超过了最大抢夺次数
                if (targetTrain.getRobbedNum() >= ConfigConsole.getGlobal().trainResourceSurplus) {
                    sendMsg(ErrorCode.Train_Resour_Not_Grob);
                    return 0;
                }
                
                float rate = 1 + buffManager.getBuffSet(targetTrain.getTeamId(), EBuffType.Train_Award).getValueSum() / 1000.0f;
                double grobNum = reward.getNum() * rate * ConfigConsole.getGlobal().trainGrabResourceSurplus * 0.01;
                PropSimple grobProp = new PropSimple(reward.getPropId(), (int) Math.ceil(grobNum));
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(1).build());
    			// 抢夺成功,获得抢夺的资源
    			propManager.addProp(teamId, grobProp, true, ModuleLog.getModuleLog(EModuleCode.训练馆, "抢夺资源"));
    			amount = grobProp.getNum();
    			
                // 抢夺资源
                synchronized (targetTrain.getTrainId() + "") {
                    targetTrain.setRobbedNum(targetTrain.getRobbedNum() + 1);
                    targetTrain.save();
                }

                // 推送目标
                ChangeTrainData.Builder builder1 = ChangeTrainData.newBuilder();
                builder1.addTrain(builderTrainData(targetTrain));
                sendMessage(targetTrain.getTeamId(), builder1.build(), ServiceCode.TrainManager_Push_ChangeTrainData);

                ChangeTrainData.Builder builder2 = ChangeTrainData.newBuilder();
                builder2.addTrain(builderTrainData(targetTrain));
                sendMessage(teamId, builder2.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
            }
    	}else {
			//抢夺失败
    		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(2).build());
		}
    	
    	return amount;
    }

    /** 获取(刷新)训练馆数据(抢夺列表) */
    @ClientMethod(code = ServiceCode.TrainManager_RefrushTrainList)
    public void refrushTrainList() {
        long teamId = getTeamId();
        refrushTrainList0(teamId, false);
    }

    private void refrushTrainList0(long teamId, boolean isTime) {
        TeamTrain teamTrain = getTeamTrain(teamId);

        // 刷新CD(s)
        if (!isTime) {
            if (teamTrain.getRefreshListTime() > 0 && DateTimeUtil.difTimeMill(teamTrain.getRefreshListTime()) < 0) {
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Refresh_list_CD.code).build());
                return;
            }
        }

        List<Train> endList = Lists.newArrayList();
        ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
        if (getTrainList().size() <= ETrain.TRAIN_SHOW_LENGTH.getId()) {
            endList = getTrainList();
        } else {
            TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
            int cap = ConfigConsole.getGlobal().trainStorageNullInitCap;
            List<Player> players = teamPlayer.getStorages();
            if (players != null && players.size() > 0) {
                PlayerAbility playerAbility = playerManager.getPlayerAbilityByMaxCap(players);
                cap = playerAbility.getTotalCap();
            }

            endList = getTrainListInRedis(cap);
        }

        for (Train t : endList) {
            builder.addTrain(builderTrainData(t));
        }

        teamTrain.setRefreshListTime(System.currentTimeMillis() + ConfigConsole.getGlobal().trainRefreshListCD * DateTimeUtil.SECOND);
        teamTrain.save();

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
    }

    /** 获取训练馆枪夺记录信息 */
    @ClientMethod(code = ServiceCode.TrainManager_GetTrainGrabInfoList)
    public void getTrainGrabInfoList() {
        long teamId = getTeamId();

        TrainGrabInfoList.Builder builder = TrainGrabInfoList.newBuilder();

        List<TrainInfo> trainInfoList = getTrainInfoList(teamId);
        for (TrainInfo trainInfo : trainInfoList) {
            builder.addInfoList(builderTrainGrabInfoData(trainInfo));
        }

        sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_TrainGrabInfo);
    }

    private TrainGrabInfoData.Builder builderTrainGrabInfoData(TrainInfo trainInfo) {

        TrainGrabInfoData.Builder msg = TrainGrabInfoData.newBuilder();
        msg.setState(trainInfo.getState());
        msg.setCreateTime(trainInfo.getCreateTime());
        msg.setFlag(trainInfo.getFlag());
        msg.setTeamName(trainInfo.getTeamName());
        msg.setNum(trainInfo.getNum());

        return msg;
    }

    /** 获取联盟训练馆信息 */
    @ClientMethod(code = ServiceCode.TrainManager_getLeagueTrainData)
    public void getLeagueTrainData() {
        long teamId = getTeamId();

        List<Long> teamIds = Lists.newArrayList();
        teamIds.add(teamId);
        this.pushLeagueTrainData(teamIds);
    }

    private void pushLeagueTrainData(List<Long> teamIds) {
        LeagueTrainDataList.Builder builder = LeagueTrainDataList.newBuilder();

        long[] arr = getCurrBlIdAndEndTime();
        if (arr != null) {
            int rblId = (int) arr[0];
            long rendTime = arr[1];
            builder.setStopTime(rendTime);
            builder.setLeagueTrainId(rblId);
        }

        for (Map.Entry<Integer, LeagueTrain> entry : leagueTrainMap.entrySet()) {
            builder.addList(builderLeagueTrainData(entry.getValue()));
        }

        sendMessageTeamIds(teamIds, builder.build(), ServiceCode.TrainManager_push_getLeagueTrainData);
    }

    private long[] getCurrBlIdAndEndTime() {
        String str = redis.getStr(RedisKey.League_Train_Chiose_Ids_Time);
        if (str == null)
            return null;

        return StringUtil.toLongArray(str, StringUtil.UNDERLINE);
    }

    private LeagueTrainData.Builder builderLeagueTrainData(LeagueTrain leagueTrain) {
        LeagueTrainData.Builder builder = LeagueTrainData.newBuilder();
        builder.setLeagueTrainId(leagueTrain.getBlId());
        builder.setBtId(leagueTrain.getBtId());
        if (leagueTrain.getLeagueId() > 0) {
            builder.setLeagueId(leagueTrain.getLeagueId());
            League league = leagueManager.getLeague(leagueTrain.getLeagueId());
            if (league != null) {
                builder.setLeagueName(league.getLeagueName());
            }
        }
        return builder;
    }

    /** 联盟训练馆球队选择 */
    @ClientMethod(code = ServiceCode.TrainManager_choiseTeam)
    public void choiseTeam(int blId, int btid) {

        long tid = getTeamId();
        int leagueId = leagueManager.getLeagueId(tid);

        ErrorCode ret = choiseTeam0(tid, leagueId, blId, btid);
        sendMsg(ret);

        log.trace("TrainManager choiseTeam tid{}, ret{}", tid, ret);
    }

    private ErrorCode choiseTeam0(long teamId, int leagueId, int blId, int btid) {

        if (!LeagueArenaConsole.isChoiseTeam(new DateTime())) {        
            return ErrorCode.League_Arena_13;
        }

        long[] arr = getCurrBlIdAndEndTime();
        if (arr == null) {         
            return ErrorCode.League_Arena_13;
        }

        int rblId = (int) arr[0];
        long rendTime = arr[1];

        if (rblId != blId) {          
            return ErrorCode.League_Arena_15;
        }

        LeagueTrainBean leagueTrainBean = TrainConsole.getLeagueTrainBeanMap().get(blId);
        if (leagueTrainBean == null) {         
            return ErrorCode.Error;
        }

        TrainArenaBean trainArenaBean = TrainConsole.getTrainArenaBeanMap().get(btid);
        if (trainArenaBean == null) {         
            return ErrorCode.Error;
        }

        if (!getCanChioseBlIds().contains(blId)) {         
            return ErrorCode.League_Arena_12;
        }

        if (!this.getCanChioseBtIds().contains(btid)) {          
            return ErrorCode.League_Arena_14;
        }

        if (teamId > 0) {
            LeagueTeam leagueTeam = leagueManager.getLeagueTeam(teamId);
            if (leagueTeam == null || leagueTeam.getLeagueId() == 0 || (leagueTeam.getLevel() != ELeagueTeamLevel.盟主 && leagueTeam.getLevel() != ELeagueTeamLevel.副盟主)) {
                return ErrorCode.League_6;
            }

            if (System.currentTimeMillis() > rendTime) {            
                return ErrorCode.League_Arena_13;
            }
        }

        LeagueTrain leagueTrain = this.getLeagueTrainById(blId);
        if (leagueTrain.getLeagueId() != leagueId) {         
            return ErrorCode.League_Arena_12;
        }

        leagueTrain.setBtId(btid);
        leagueTrain.save();

        // 接下来可挑选球馆的联盟训练位
        int nextblId = blId + 1;
        long endTime = System.currentTimeMillis() + ConfigConsole.global().leagueChoiseTeamSustainTime * DateTimeUtil.SEC;
        log.debug("choiseTeam0 nextblId: " + nextblId + ", endTime :" + endTime);
        
        if (getCanChioseBlIds().contains(nextblId)) {
            redis.set(RedisKey.League_Train_Chiose_Ids_Time, nextblId + "_" + endTime);
        } else {
            nextblId = 0;
            endTime = 0;
            redis.del(RedisKey.League_Train_Chiose_Ids_Time);
        }

        LeagueTrainDataList.Builder builder = LeagueTrainDataList.newBuilder();
        builder.addList(builderLeagueTrainData(leagueTrain));
        builder.setLeagueTrainId(nextblId);
        builder.setStopTime(endTime);

        sendMessageTeamIds(getTeamIds(), builder.build(), ServiceCode.TrainManager_push_choiseTeam);

        return ErrorCode.Success;
    }

    /** 给联盟玩家增加训练馆*/
    public void addLeagueTrain(long tid, int blId) {
        Train train = getLeagueTrain(tid);
      
        if (train == null) {
            Map<Integer, Train> map = this.getTrainMapByTeamId(tid);
            int trainId = map.size() + 1;
            train = new Train(tid, trainId, 1, blId);
            map.put(trainId, train);
        } else {
            
//            System.out.println("addLeagueTrain train.getClear() : " + train.getClear() + ", train.getTeamId() :" + train.getTeamId());
            
            if (train.getClear() == 1) {
                train.setClear(0);
            }
           
            if(blId != train.getBlId()) {
            	log.info("addLeagueTrain|clearTrain()");
                this.clearTrain(train, false);
            }
            
            train.setBlId(blId);
            train.save();
        }

        ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
        builder.addTrain(builderTrainData(train));
        sendMessage(tid, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
    }

    /** 获取联盟训练馆攻防百分比*/
    public int getLeagueTrainCap(long tid, int prid) {

        Train train = getLeagueTrain(tid);
        if (train == null || train.getClear() == 1) {
            return 0;
        }

        int leagueId = leagueManager.getLeagueId(tid);
        LeagueTrain leagueTrain = this.getLeagueTrainByLeagueId(leagueId);
        if (leagueTrain == null) {           
            return 0;
        }

        TeamPlayer teamPlayer = playerManager.getTeamPlayer(tid);
        Player player = teamPlayer.getPlayer(prid);
        if (player == null) {           
            return 0;
        }

        LeagueTrainBean leagueTrainBean = TrainConsole.getLeagueTrainBeanMap().get(leagueTrain.getBlId());
        if (leagueTrainBean == null) {
            log.warn("getLeagueTrainCap leagueTrainBean is null ltid {}", leagueTrain.getBlId());
            return 0;
        }

        TrainArenaBean trainArenaBean = TrainConsole.getTrainArenaBeanMap().get(leagueTrain.getBtId());
        if (trainArenaBean == null) {

            return 0;
        }

        PlayerBean playerBean = PlayerConsole.getPlayerBean(player.getPlayerRid());
        if (playerBean == null) {
            log.warn("getLeagueTrainCap playerBean is null rid {}", player.getPlayerRid());
            return 0;
        }

        if (playerBean.getTeamId() != trainArenaBean.getTeamId())
            return 0;

        log.trace("getLeagueTrainCap cap {}", leagueTrainBean.getCap());

        return leagueTrainBean.getCap();
    }

    /** 联盟训练馆占位数据*/
    public LeagueTrainData.Builder getLeagueTrainData(long tid) {
        int leagueId = leagueManager.getLeagueId(tid);
        LeagueTrain leagueTrain = this.getLeagueTrainByLeagueId(leagueId);
        if (leagueTrain == null) {
            return LeagueTrainData.newBuilder();
        }

        return this.builderLeagueTrainData(leagueTrain);
    }

    /** 联盟占领的训练馆*/
    public LeagueTrain getLeagueTrainByLeagueId(int leagueId) {
        return leagueTrainMap.values().stream().filter(lt -> lt.getLeagueId() == leagueId).findFirst().orElse(null);
    }

    /** 联盟训练馆清理*/
    public void clearLeagueTrain(long teamId) {
        Train train = getLeagueTrain(teamId);
        if (train == null)
            return;
        this.calculateTrainHour(train);
        log.info("clearLeagueTrain|clearTrain()");
        int pid = this.clearTrain(train, true);
//        int pid = 0;
//        if(train.getTrainHour() < 1) {
//            pid = this.clearTrain(train, true);
//        }  
        
        //传递给前端
        train.setPlayerId(pid);
        ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
        builder.addTrain(builderTrainData(train));
        sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
        train.setPlayerId(0); //再次清除
    }

    /** 训练奖励(已根据训练类型加成) */
    private PropSimple getTrainReward(Train train, TrainTypeBean trainTypeBean) {
    	if (train == null || trainTypeBean == null) {
			return null;
		}
    	
        List<PropSimple> newList = null;
        if (train.getIsLeague() == 1) {
            LeagueTrainBean leagueTrainBean = TrainConsole.getLeagueTrainBeanMap().get(train.getBlId());
            newList = leagueTrainBean.getRewardList();

        } else {
            TrainBean trainBean = TrainConsole.getTrainBeanMap().get(train.getTrainLevel());
            newList = trainBean.getRewardList();
        }

        PropSimple propSimple = null;
        Collections.shuffle(newList);
        if (train.getTrainHour() > 0) {
            propSimple = new PropSimple(newList.get(0).getPropId(), newList.get(0).getNum() * train.getTrainHour());
        } else {
            propSimple = new PropSimple(newList.get(0).getPropId(), newList.get(0).getNum() * trainTypeBean.getTime() / 60);
        }

        return propSimple;
    }

    /** 训练馆系统在玩家达到固定等级后开启 */
    private boolean isModuleDisable(Team team) {
        return ModuleConsole.isDisabled(team.getLevel(), EModuleCode.训练馆);
    }

    /** 获取球队训练馆数据 */
    private Map<Integer, Train> getTrainMapByTeamId(long teamId) {

        Map<Integer, Train> map = trainMap.get(teamId);
        if (map == null) {
            map = Maps.newConcurrentMap();
            trainMap.put(teamId, map);

            List<Train> trainList = trainAO.getTrainByTeamId(teamId);
            if (trainList.isEmpty()) {
                int trainId = trainList.size() + 1;
                //new出对象,并进行了save to db.
                Train train = new Train(teamId, trainId, 0, 0);
                map.put(train.getTrainId(), train);
            } else {
                for (Train train : trainList) {
                    map.put(train.getTrainId(), train);
                }
            }
        }

        return map;
    }

    /** 获取球队某个训练馆数据 */
    public Train getTrainMapByTeamIdAndTrainId(long teamId, int trainId) {
        Map<Integer, Train> map = this.getTrainMapByTeamId(teamId);

        return map.get(trainId);
    }

    /** 根据类型,获取球队联盟训练馆数据 */
    public Train getLeagueTrain(long teamId) {
        Map<Integer, Train> map = this.getTrainMapByTeamId(teamId);
        for (Map.Entry<Integer, Train> entry : map.entrySet()) {
            if (entry.getValue().getIsLeague() == 1) {
                return entry.getValue();
            }
        }

        return null;
    }

    /** 获取所有训练馆数据 */
    private List<Train> getTrainList() {
        List<Train> list = Lists.newArrayList();
        for (Map.Entry<Long, Map<Integer, Train>> entry : trainMap.entrySet()) {
            Map<Integer, Train> map = entry.getValue();

            for (Map.Entry<Integer, Train> e : map.entrySet()) {
                list.add(e.getValue());
            }
        }

        return list;
    }

    /** 获取球队训练数据 */
    private TeamTrain getTeamTrainByTeamId(long teamId) {
        return teamTrainMap.get(teamId);
    }

    /** 两小时刷新抢夺次数 */
    public void refrushRobbedCount() {
        for (Map.Entry<Long, TeamTrain> entry : teamTrainMap.entrySet()) {
            TeamTrain teamTrain = entry.getValue();

            if (!GameSource.isOline(teamTrain.getTeamId()))
                continue;

            // 刷新抢夺次数
            if (teamTrain.getRobbedCount() >= ConfigConsole.getGlobal().trainGrabCountMax) {
                continue;
            }

            if (DateTimeUtil.difTimeMill(teamTrain.getRefreshTime()) < 0) {
                continue;
            }
            teamTrain.setRobbedCount(teamTrain.getRobbedCount() + 1);

            if (teamTrain.getRobbedCount() >= ConfigConsole.getGlobal().trainGrabCountMax) {
                teamTrain.setRefreshTime(0);
            } else {
                teamTrain.setRefreshTime(System.currentTimeMillis() + ConfigConsole.getGlobal().trainGrabCountRefresh * DateTimeUtil.MINUTE);
            }

            teamTrain.save();

            // 更新训练数据
            ChangeTeamTrainData.Builder builder = ChangeTeamTrainData.newBuilder();
            builder.setGrabCount(teamTrain.getRobbedCount());
            builder.setRefreshTime(teamTrain.getRefreshTime());
            builder.setGrabTime(teamTrain.getRobbedTime());
            sendMessage(teamTrain.getTeamId(), builder.build(), ServiceCode.TrainManager_Push_ChangeTeamTrainData);
        }
    }

    /**
     * 根据攻防cap值返回6个正在训练馆训练的数据.
     * @param playerCap
     * @return
     */
    public List<Train> getTrainListInRedis(int playerCap) {
        long teamId = getTeamId();

        double min = playerCap - ConfigConsole.getGlobal().trainPlayerCapRange;
        double max = playerCap + ConfigConsole.getGlobal().trainPlayerCapRange;

        int index = 10;
        Set<String> trainStrList = null;
        
        while ((trainStrList = redis.zrevrangeByScore(RedisKey.Train_Refrush_List, max, min, 0, 200)).size()
        		< (ETrain.TRAIN_SHOW_LENGTH.getId() + 1) && (index--) > 0 ) {
            min = min - 100;
            max = max + 100;
        }

        // 如果找了10次还没找到合适的数据，就随机取6个
        if (index < 1) {
            trainStrList = redis.zrevrangeByScore(RedisKey.Train_Refrush_List, 10000, 0, 0, 200);
            if (trainStrList == null || trainStrList.size() < 1) {
                log.error("train. trainStrList is not have 6, redis.zrevrangeByScore(RedisKey.Train_Refrush_List, 10000, 0, 0, 20)");
                return null;
            }

            // 过滤自己的训练馆
            List<String> list = new ArrayList<String>();
            for (String tid : trainStrList) {
                // 解析一下ID
                String[] ids = StringUtil.toStringArray(tid, StringUtil.UNDERLINE);
                if (ids[0].equals(str(teamId))) {
                    continue;
                }
                list.add(tid);
            }

            Collections.shuffle(list);

            List<Train> endList = getRefreshTrainList(list);
            if (endList == null) {
                log.error("index < 1, trainStrList is null");
                return null;
            } else if (endList.size() < 6) {
                log.error("index < 1, trainStrList is not have 6, endList.size() {}", endList.size());
                return endList.subList(0, endList.size());
            }

            return endList.subList(0, 6);
        }

//        trainStrList = redis.zrevrangeByScore(RedisKey.Train_Refrush_List, max, min, 0, 200);
        if (trainStrList == null || trainStrList.size() < 6) {
            log.error("train. trainStrList is not have 6, redis.zrevrangeByScore(RedisKey.Train_Refrush_List, max, min, 0, 20)");
            return null;
        }

        // 过滤自己的训练馆
        List<String> list = new ArrayList<String>();
        for (String tid : trainStrList) {
            // 解析一下ID
            String[] ids = StringUtil.toStringArray(tid, StringUtil.UNDERLINE);
            if (ids[0].equals(str(teamId))) {
                continue;
            }
            list.add(tid);
        }

        Collections.shuffle(list);

        List<Train> endList = getRefreshTrainList(list);
        if (endList == null) {
            log.error("index < 1, trainStrList is null");
            return null;
        } else if (endList.size() < 6) {
            log.error("index < 1, trainStrList is not have 6, endList.size() {}", endList.size());
            return endList.subList(0, endList.size());
        }

        return endList.subList(0, 6);
    }

    private List<Train> getRefreshTrainList(List<String> trainStrList) {
        List<Train> endList = Lists.newArrayList();
        for (String tidStr : trainStrList) {
            long[] ids = StringUtil.toLongArray(tidStr, StringUtil.UNDERLINE);

            long teamId = ids[0];
            int trainId = (int) ids[1];
            Train train = getTrainMapByTeamIdAndTrainId(teamId, trainId);
            if (train == null) {
                log.warn("train. trainId {} is null", trainId);
                continue;
            }
            endList.add(train);
        }
        return endList;
    }

    private TrainData.Builder builderTrainData(Train train) {
        if (train == null || train.getTeamId() < 0) {
            return null;
        }

        TrainData.Builder msg = TrainData.newBuilder();
        msg.setPlayerRid(train.getPlayerRid());
        msg.setTeamId(train.getTeamId());
        msg.setIsLeague(train.getIsLeague());
        msg.setPlayerCap(train.getPlayerCap());
        msg.setPlayerId(train.getPlayerId());
        msg.setTrainExp(train.getTrainExp());
        msg.setType(train.getType());
        msg.setTrainLevel(train.getTrainLevel());
        msg.setRobbedNum(train.getRobbedNum());
        msg.setTtid(train.getTrainId());
        msg.setHour(train.getTrainHour());
        msg.setClear(train.getClear());
        msg.setBlId(train.getBlId());

        TeamVip teamVip = vipManager.getVip(train.getTeamId());
        msg.setVipLv(teamVip.getLevel());

        if (train.getStartTime() > 0 && train.getType() > 0) {
            TrainTypeBean trainTypeBean = TrainConsole.getTrainTypeBeanMap().get(train.getType());
            msg.setEndTime(train.getStartTime() + trainTypeBean.getTime() * DateTimeUtil.MINUTE);

            if (DateTimeUtil.difTimeMill(train.getStartTime()) > trainTypeBean.getTime() * DateTimeUtil.MINUTE || train.getTrainHour() > 0) {
                train.setRewardState(ETrain.TRAIN_RSTATE_1.getId());
            }
        }
        
        //奖励是否可以领取:0不能,1可以
        msg.setRewardState(train.getRewardState());
        
        if (train.getPlayerId() > 0) {
            Team team = teamManager.getTeam(train.getTeamId());
            if (team != null) {
                msg.setTeamName(team.getName());
            }
        } else {
            NPCBean npcBean = NPCConsole.getNPC(train.getTeamId());
            
            if (npcBean != null) {
                msg.setTeamName(npcBean.getNpcName());
            }
        }

        // System.out.println("msg pid : " + msg.getPlayerId());
        return msg;
    }

    private TeamTrain getTeamTrain(long teamId) {
        TeamTrain teamTrain = this.getTeamTrainByTeamId(teamId);
        if (teamTrain == null) {
            teamTrain = trainAO.getTeamTrainByTeamId(teamId);

            if (teamTrain == null) {
                teamTrain = new TeamTrain(teamId);
                teamTrain.save();
            }

            teamTrainMap.put(teamId, teamTrain);
        }

        return teamTrain;
    }

    /** 获取抢夺记录列表 */
    public List<TrainInfo> getTrainInfoList(long teamId) {
        List<TrainInfo> trainInfoList = redis.getList(RedisKey.getTrainGrabInfoListKey(teamId));
        if (trainInfoList == null) {
            trainInfoList = Lists.newArrayList();
            redis.rpush(RedisKey.getTrainGrabInfoListKey(teamId), trainInfoList);
        }

        return trainInfoList;
    }

    /** 训练馆升级 */
    public void trainUpLevel(Train train, int exp) {
        if (train.getIsLeague() == 1)
            return;

        train.setTrainExp(train.getTrainExp() + exp);

        while (train.getTrainLevel() < TrainConsole.getTrainBeanMap().size() && train.getTrainExp() >= TrainConsole.getTrainBeanMap().get(train.getTrainLevel()).getTotalExp()) {

            train.setTrainLevel(train.getTrainLevel() + 1);
            train.save();

            // 任务
            taskManager.updateTask(train.getTeamId(), ETaskCondition.训练馆升至xxx级, train.getTrainLevel(), "");

            if (train.getTrainLevel() >= TrainConsole.getTrainBeanMap().size()) {
                break;
            }
        }
    }

    /**
     * @param 目标队伍ID
     * @param 发起队伍
     * @param 抢夺状态
     * @param 抢夺资源数量 添加抢夺记录
     */
    private void addTrainGrobInfo(long targetTeamId, Team team, int state, int num) {

        List<TrainInfo> changeList = Lists.newArrayList();
        List<TrainInfo> trainInfoList = getTrainInfoList(targetTeamId);
        TrainInfo trainInfo = null;
        if (trainInfoList.size() >= ConfigConsole.global().chatOfflineMsgLimit) {
            trainInfo = trainInfoList.remove(0);

            trainInfo.setFlag(2);
            changeList.add(trainInfo);
        } else {
            trainInfo = new TrainInfo();
        }

        trainInfo.setMsgId(IDUtil.geneteId(TrainInfo.class));
        trainInfo.setTeamName(team.getName());
        trainInfo.setNum(num);
        trainInfo.setState(state);
        trainInfo.setCreateTime(System.currentTimeMillis());
        trainInfo.setFlag(1);
        trainInfoList.add(trainInfo);
        changeList.add(trainInfo);

        redis.rpush(RedisKey.getTrainGrabInfoListKey(targetTeamId), trainInfoList, RedisKey.MONTH);

        // 推送给目标抢夺记录
        TrainGrabInfoList.Builder gibuilder = TrainGrabInfoList.newBuilder();
        for (TrainInfo ti : trainInfoList) {
            gibuilder.addInfoList(builderTrainGrabInfoData(ti));
        }

        // sendMessage(targetTeamId, gibuilder.build(),
        // ServiceCode.TrainManager_Push_TrainGrabInfo);
    }

    private static String str(Object obj) {
        return String.valueOf(obj);
    }

    private boolean isTrain(Train train) {
        if (train.getType() > 0) {
            return true;
        }
        return false;
    }

    /** 登录处理*/
    @Subscribe
    public void login(LoginParam param) {
        TeamTrain teamTrain = this.getTeamTrain(param.teamId);
        // 刷新抢夺次数
        if (teamTrain.getRobbedCount() >= ConfigConsole.getGlobal().trainGrabCountMax) {
            return;
        }
        if (DateTimeUtil.difTimeMill(teamTrain.getRefreshTime()) < 0) {
            return;
        }

        long dif = DateTimeUtil.difTimeMill(teamTrain.getRefreshTime());
        long bdif = ConfigConsole.getGlobal().trainGrabCountRefresh * DateTimeUtil.MINUTE;

        // 根据时间头次刷新重置
        int count = 1 + (int) (dif / bdif);
        long refresh = dif % bdif;
        teamTrain.setRobbedCount(Math.min(teamTrain.getRobbedCount() + count, ConfigConsole.getGlobal().trainGrabCountMax));
        log.trace("login set rebCount count{}, refresh{}", count, refresh);

        if (teamTrain.getRobbedCount() >= ConfigConsole.getGlobal().trainGrabCountMax) {
            teamTrain.setRefreshTime(0);
        } else {
            teamTrain.setRefreshTime(System.currentTimeMillis() + refresh);
        }

        teamTrain.save();

        // 更新训练数据
        ChangeTeamTrainData.Builder builder = ChangeTeamTrainData.newBuilder();
        builder.setGrabCount(teamTrain.getRobbedCount());
        builder.setRefreshTime(teamTrain.getRefreshTime());
        builder.setGrabTime(teamTrain.getRobbedTime());
        sendMessage(teamTrain.getTeamId(), builder.build(), ServiceCode.TrainManager_Push_ChangeTeamTrainData);
    }

    /** 获取联盟训练馆(基础配置ID) */
    public LeagueTrain getLeagueTrainById(int leagueTrainId) {
        
        return leagueTrainMap.get(leagueTrainId);
    }

    /** 清理所有联盟训练馆数据*/
    public void clearAllLeagueTrain() {
        if (DateTimeUtil.getCurrWeekDay() != ConfigConsole.getGlobal().leagueArenaOpenWeekday)
            return;

        for(Map.Entry<Integer, LeagueTrain> entry : leagueTrainMap.entrySet()) {
            LeagueTrain leagueTrain = entry.getValue();
            leagueTrain.setLeagueId(0);
            leagueTrain.setBtId(0);
        }
           
        trainAO.clearLeagueTrain();

        List<Train> list = this.getTrainList();
        if (list.isEmpty())
            return;
        for (Train train : list) {
            if (train.getIsLeague() != 1){ 
            	continue;
            }
            
            this.calculateTrainHour(train);
            
            if(train.getTrainHour() < 1) {
            	long teamId = train.getTeamId();
            	log.info("clearAllLeagueTrain|clearTrain()");
                int pid = this.clearTrain(train, true);
                // 给在线玩家发消息,告诉前端把球员的训练状态清除
                if (GameSource.isOline(teamId)) {
                	// 传递给前端
                	train.setPlayerId(pid);
                	ChangeTrainData.Builder builder = ChangeTrainData.newBuilder();
                	builder.addTrain(builderTrainData(train));
                	sendMessage(teamId, builder.build(), ServiceCode.TrainManager_Push_ChangeTrainData);
                	train.setPlayerId(0); //再次清除
				}
            }
        }
    }
    
    /** 结算训练时间*/
    private void calculateTrainHour(Train train) {
        if (train.getIsLeague() != 1 || train.getPlayerRid() < 1) return;
        
        TrainTypeBean trainTypeBean = TrainConsole.getTrainTypeBeanMap().get(train.getType());
        int hour = Math.min(DateTimeUtil.difTimeHour(train.getStartTime()), (int) Math.ceil(trainTypeBean.getTime() / 60));
        train.setTrainHour(hour);
        train.save();
        
        log.info("clearAllLeagueTrain train expire, train id is: {}, hour is :{}",train.getTrainId(), hour);        
    }

    /** 联盟训练馆开始挑选球队 */
    private void chioseTeamTask() {
        // 当前时间到开始挑选球队时间相差几天
        int day = Math.abs(LeagueArenaConsole.OpenDayOfWeek - DateTimeUtil.getCurrWeekDay());
        log.info("当前时间到开始挑选球队时间相差几天 day {}", day);

        long midnight = DateTimeUtil.midnight();
        long curr = System.currentTimeMillis();
        long choiseTime = ConfigConsole.global().leagueChoiseTeamStartTime;
        long delayChoise = day * DateTimeUtil.DAILY + midnight + choiseTime - curr;

        // 超过选球队时间就选球队
        if (delayChoise > 0) {
            ScheduledFuture<?> sf = QuartzServer.scheduleAtFixedRate(this::chioseTeamStart, delayChoise, DateTimeUtil.DAILY * 7, TimeUnit.MILLISECONDS);

            log.info("离下次选球队时间 {}", DateTimeUtil.duration(sf));
        }
    }

    // 开始选球队
    private void chioseTeamStart() {
        List<Integer> Ids = getCanChioseBlIds();
        long endTime = System.currentTimeMillis() + ConfigConsole.global().leagueChoiseTeamSustainTime * DateTimeUtil.SEC;
        redis.set(RedisKey.League_Train_Chiose_Ids_Time, Ids.get(0) + "_" + endTime);

        this.pushLeagueTrainData(getTeamIds());
    }

    private List<Integer> getCanChioseBlIds() {
        List<Integer> Ids = leagueTrainMap.keySet().stream().sorted().collect(Collectors.toList());
        return Ids;
    }

    /** 自动选择球队*/
    public void autoChioseTram() {
        if (!LeagueArenaConsole.isChoiseTeam(new DateTime())) {
            return;
        }

        long[] arr = getCurrBlIdAndEndTime();
        if (arr == null)
            return;

        int blId = (int) arr[0];
        long endTime = arr[1];

        if (System.currentTimeMillis() < endTime)
            return;

        LeagueTrain leagueTrain = this.getLeagueTrainById(blId);
        if (leagueTrain.getLeagueId() < 1 ||leagueTrain.getBtId() > 0)
            return;

        log.debug("autoChioseTram blId: {} , endTime : {}, getCanChioseBtIds().get(0) {} :" ,blId ,endTime, getCanChioseBtIds().get(0));
        // 自动选择球队
        this.choiseTeam0(0, leagueTrain.getLeagueId(), blId, getCanChioseBtIds().get(0));
    }

    /** 获取当前可选的球队ID(配置顺位)*/
    private List<Integer> getCanChioseBtIds() {

        // 所有可选的基础球馆配置ID
        List<Integer> ids = TrainConsole.getTrainArenaBeanMap().keySet().stream().sorted().collect(Collectors.toList());

        // 已被选择的球队
        leagueTrainMap.values().forEach(lt -> {
            if (lt.getBtId() > 0) {
                ids.remove(lt.getBtId());
            }
        });

        return ids;
    }

    /** 获取当前所有可进入联盟训练馆选择界面的所有球员*/
    private List<Long> getTeamIds() {

        // 所有可选的基础球馆配置ID
        List<Long> teamIds = Lists.newArrayList();

        // 已被选择的球队
        leagueTrainMap.values().forEach(lt -> {
            if (lt.getLeagueId() < 0)
                return;
            if (leagueManager.getLeagueTeamList(lt.getLeagueId()) == null)
                return;
            teamIds.addAll(leagueManager.getLeagueTeamList(lt.getLeagueId()));
        });

        return teamIds;
    }

    @Override
    public void offline(long teamId) {
//        trainMap.remove(teamId);
        teamTrainMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
//        trainMap.remove(teamId);
        teamTrainMap.remove(teamId);
    }
}
