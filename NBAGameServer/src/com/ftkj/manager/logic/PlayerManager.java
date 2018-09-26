package com.ftkj.manager.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.PlayerCutsBean;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.GradeConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PlayerCutsConsole;
import com.ftkj.console.PlayerExchangeConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.ao.common.INBADataAO;
import com.ftkj.db.ao.logic.IPlayerAO;
import com.ftkj.db.ao.logic.IPlayerLowerSalaryLogAO;
import com.ftkj.db.domain.NBAGameGuess;
import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.db.domain.NBAVS;
import com.ftkj.db.domain.PlayerAvgInfo;
import com.ftkj.db.domain.PlayerPO;
import com.ftkj.db.domain.bean.PlayerBeanVO;
import com.ftkj.db.domain.bean.PlayerMoneyBeanPO;
import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.logic.log.GamePlayerLogManager;
import com.ftkj.manager.logic.log.GamePriceLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerAvg;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerLowerSalaryLog;
import com.ftkj.manager.player.PlayerMinPrice;
import com.ftkj.manager.player.PlayerPriceBean;
import com.ftkj.manager.player.PlayerSimple;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillTree;
import com.ftkj.manager.skill.TeamSkill;
import com.ftkj.manager.system.NBAPlayerGuess;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.proto.PlayerCardPB.CollectData.Builder;
import com.ftkj.proto.PlayerPB;
import com.ftkj.proto.PlayerPB.NBAGuessData;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;
import com.ftkj.util.lambda.LBInt;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang 2017年3月2日 球员管理
 */
public class PlayerManager extends AbstractBaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(PlayerManager.class);
    @IOC
    private IPlayerAO playerAO;
    @IOC
    private TaskManager taskManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private PlayerCardManager playerCardManager;
    @IOC
    private GymManager localArenaManager;
    @IOC
    private TeamCapManager teamCapManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private GameManager gameManager;
    @IOC
    private PropManager propManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamEmailManager teamEmailManager;
    @IOC
    private SkillManager skillManager;
    @IOC
    private INBADataAO nbaDataAO;
    @IOC
    private ChatManager chatManager;
    @IOC
    private EquiManager equiManager;
    @IOC
    private TradeP2PManager tradeP2PManager;
    @IOC
    private StarletManager starletManager;
    @IOC
    private IPlayerLowerSalaryLogAO playerLowerSalaryLogAO;
    @IOC
    private JedisUtil redis;

    private int _maxPlayerCount;
    private Map<Long, TeamPlayer> teamPlayerMap;
    private Map<Integer, Integer> playerMinPriceMap;//底薪球员

    private Map<Integer, PlayerPB.PlayerMoneyDataMain> playerDetailMap;
    private Map<Integer, List<PlayerPB.NBAVSData>> nbaVSDetailMap;
//    private Map<Integer, NBAGuess> nbaVSDetailGameIdMap;
    /**key=竞猜比赛的Id,value=竞猜比赛活动数据*/
    private Map<Integer, NBAGameGuess> nbaGameGuessMap;
    /**key=竞猜比赛的Id,value=所有玩家竞猜的数据*/
    private Map<Integer, NBAPlayerGuess> nbaPlayerGuessMap;

    private Map<Long, PlayerAvg> playerAvgMap;
    private int _refreshCard;
    private int _refreshSupperCard;
    private int _lockCard;
    private int _lockSupperCard;
    private PropSimple _refreshCardCallBack;
    private PropSimple _refreshSupperCardCallBack;
    private PropSimple _lockCardCallBack;
    private PropSimple _lockSupperCardCallBack;
    private static DropBean _commonDrop;
    private static DropBean _superDrop;
    public static DropBean _initDrop;
    static DropBean _buyHelpPlayerDeop;
    // 随机身价的赛程列表
    private List<String> randSecheduleDateList;
    // 随机赛程开始时间
    private final DateTime startRand = DateTimeUtil.parseToLdtDateTime("2018-06-12");
    /**
     * 是否显示随机赛程最新5场比赛，随机身价期间启用
     */
    private boolean isRandSechedule = true;
    
    //新秀球员降薪表的Id
    private AtomicLong plsId;

    @Override
    public void instanceAfter() {
        teamPlayerMap = Maps.newConcurrentMap();
        playerDetailMap = Maps.newConcurrentMap();
        playerAvgMap = Maps.newConcurrentMap();
        //nbaVSDetailGameIdMap = Maps.newConcurrentMap();
        playerMinPriceMap = Maps.newConcurrentMap();
        nbaGameGuessMap = Maps.newConcurrentMap();
        nbaPlayerGuessMap = Maps.newConcurrentMap();
        
//        List<PlayerMinPrice> playerMinPriceList = playerAO.getPlayerMinPriceList();
//        playerMinPriceList.forEach(pmp -> playerMinPriceMap.put(pmp.getPlayerId(), pmp.getMinPrice()));
        initPlayerMinPrice(false);
        // 随机身价的赛程, 注意，同身价系统配置
        randSecheduleDateList = nbaDataAO.getRandSecheduleDateList(2017, "2017-10-18", "2018-04-12", 2);
        //查询出新秀球员降薪表的最大Id值
        plsId = new AtomicLong(playerLowerSalaryLogAO.queryMaxKeyId());
        
        //加载竞猜活动比赛的数据(数据通过运行后台配置的),过期的数据(奖励已经发送或者比赛取消)不加载.
        List<NBAGameGuess> nbaGameGuesslList = nbaDataAO.getNBAGameGuessDetailList();
        nbaGameGuesslList.forEach(obj -> {
        	nbaGameGuessMap.put(obj.getId(), obj);
        	initNBAPlayerGuessData(obj);
        });
    }
    
    /**
     * 初始化球员的底薪工资.每天16点系统更新一次,其他情况修改了球员的底薪工资都不
     * 更新球员的底薪.计算出nba_data库中的player_info表中的现役球员的底薪,
     * 如果nba库中的表t_u_player不存在指定球员,则最低工资以player_info中的
     * price字段为准.最底的底薪数据保存到Redis中,重启服务器看Redis中是否有数据,
     * 如果有则取Redis中的数据,否则重新再初始化.
     * @param isSwap 是否更新Redis中的数据,true替换,否则false.
     */
    private void initPlayerMinPrice(Boolean isSwap){
    	if (isSwap) {
    		//清除旧的数据
    		playerMinPriceMap.clear();
    		List<PlayerMinPrice> playerMinPriceList = playerAO.getPlayerMinPriceList();
            playerMinPriceList.forEach(pmp -> {
            	playerMinPriceMap.put(pmp.getPlayerId(), pmp.getMinPrice());
            });
            List<PlayerBeanVO> playerInfoList = nbaDataAO.getPlayerInfoList();
            playerInfoList.forEach(obj -> {
            	if(!playerMinPriceMap.containsKey(obj.getPlayerId())){
            		playerMinPriceMap.put(obj.getPlayerId(), obj.getPrice());
            	}
            });
            redis.hmset("PLAYER_MIN_PRICE", playerMinPriceMap);
		}else {
			Map<Integer, Integer> map = redis.hgetallIKIV2("PLAYER_MIN_PRICE");
			if (map == null || map.size() == 0) {
				List<PlayerMinPrice> playerMinPriceList = playerAO.getPlayerMinPriceList();
	            playerMinPriceList.forEach(pmp -> playerMinPriceMap.put(pmp.getPlayerId(), pmp.getMinPrice()));
	            List<PlayerBeanVO> playerInfoList = nbaDataAO.getPlayerInfoList();
	            playerInfoList.forEach(obj -> {
	            	if(!playerMinPriceMap.containsKey(obj.getPlayerId())){
	            		playerMinPriceMap.put(obj.getPlayerId(), obj.getPrice());
	            	}
	            });
	            redis.hmset("PLAYER_MIN_PRICE", playerMinPriceMap);
			}else {
				playerMinPriceMap = map;
			}
			
		}
    	
    }
    
    /**
     * 初始化竞猜比赛数据,如果有玩家竞猜了比赛则数据也从Redis加载.
     * @param nbaGameGuess
     */
    private void initNBAPlayerGuessData(NBAGameGuess nbaGameGuess){
    	NBAPlayerGuess nbaPlayerGuess = new NBAPlayerGuess(nbaGameGuess.getId(), this.redis);
    	this.nbaPlayerGuessMap.put(nbaGameGuess.getId(), nbaPlayerGuess);
    }

    public void reloadPlayerMinPrice() {
        initPlayerMinPrice(true);
    }

    /** 更新球员统计数据 */
    void updatePlayerSource(long teamId, List<BattlePlayer> playerList) {
        PlayerAvg avg = getPlayerAvg(teamId);
        playerList.forEach(player -> avg.updatePlayerAvg(player.getPlayer().getPlayerRid(),
            player.getRealTimeActionStats().getActionStatistics()));
    }

    private PlayerAvg getPlayerAvg(long teamId) {
        PlayerAvg avg = playerAvgMap.get(teamId);
        if (avg == null) {
            List<PlayerAvgInfo> poList = playerAO.getPlayerAvgList(teamId);
            avg = new PlayerAvg(teamId, poList);
            playerAvgMap.put(teamId, avg);
        }
        return avg;
    }

    @ClientMethod(code = ServiceCode.PlayerManager_showPlayerAvg)
    public void showPlayerAvg() {
        long teamId = getTeamId();
        PlayerAvg avg = getPlayerAvg(teamId);
        TeamPlayer tp = getTeamPlayer(teamId);
        List<PlayerPB.NBAPlayerAvgData> datas = Lists.newArrayList();
        for (Player p : tp.getPlayers()) {
            PlayerAvgInfo avgp = avg.getPlayerAvgInfo(p.getPlayerRid());
            if (avgp == null || avgp.getTotal() == 0) {
                continue;
            }
            datas.add(getNBAPlayerAvgData(avgp));
        }
        sendMessage(PlayerPB.TeamPlayerAvgMain.newBuilder().addAllPlayerAvgs(datas).build());
    }

    /**
     * 双位置转换
     */
    @ClientMethod(code = ServiceCode.PlayerManager_updatePlayerPosition)
    public void updatePlayerPosition(int playerId) {
        long teamId = getTeamId();
        TeamPlayer tp = getTeamPlayer(teamId);
        List<Player> playerList = tp.getAllPlayerByPlayerId(playerId);
        if (playerList == null || playerList.size() == 0) {
            log.debug("球员不存在无法转换球员");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        if (pb.getPosition().length < 2) {
            log.debug("球员不是双位置，无法转换位置");
            sendMessage(DefaultPB.DefaultData.newBuilder().setMsg(Arrays.toString(pb.getPosition()))
                .setCode(ErrorCode.Error.code).build());
            return;
        }
        EPlayerPosition oldPosition = EPlayerPosition.valueOf(playerList.get(0).getPosition());
        EPlayerPosition newPosition;
        if (oldPosition == pb.getPosition()[0]) {
            newPosition = pb.getPosition()[1];
        } else {
            newPosition = pb.getPosition()[0];
        }
        for (Player player : playerList) {
            player.updatePlayerPosition(newPosition.name());
            player.save();
        }
        // 变化位置后的技能转换
        PlayerSkill ps = skillManager.getTeamSkill(teamId).getPlayerSkill(playerId);
        if (ps != null) {
            ps.resetPosSkill(ps.getPlayerId(), ps.getPlayerId(), oldPosition, newPosition, 0, 0);
        }
        sendMessage(
            DefaultPB.DefaultData.newBuilder().setMsg(newPosition.name()).setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.PlayerManager_delPlayerAvg)
    public void delPlayerAvg(int playerId) {
        long teamId = getTeamId();
        PlayerAvg avg = getPlayerAvg(teamId);
        avg.delPlayer(playerId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    private PlayerPB.NBAPlayerAvgData getNBAPlayerAvgData(PlayerAvgInfo player) {
        List<PlayerPB.NBAPlayerAvgAbilityData> abilitys = Lists.newArrayList();
        for (EActionType type : EActionType.values()) {
            abilitys.add(PlayerPB.NBAPlayerAvgAbilityData.newBuilder().setType(type.getType())
                .setValue(player.getAbility(type)).build());
        }
        abilitys.add(PlayerPB.NBAPlayerAvgAbilityData.newBuilder().setType(-1).setValue(player.getTotal()).build());
        return PlayerPB.NBAPlayerAvgData.newBuilder().setPlayerId(player.getPlayerId()).addAllAbilitys(abilitys)
            .build();
    }

    @Override
    public void initConfig() {
        _maxPlayerCount = ConfigConsole.getIntVal(EConfigKey.Max_Player_Count);
        NPCConsole.getNpcs().values().forEach(npc -> {
            List<Player> players = PlayerConsole.getNPCPlayerList(npc.getNpcId(), npc.getPlayers());
            if (players.size() > PlayerConsole.Team_Player_Num) {
                players = players.subList(0, PlayerConsole.Team_Player_Num);
            }
            int totalAttack = players.stream().mapToInt(p -> (int) p.getPlayerBean().getAbility(EActionType.ocap))
                .sum();
            int totalDefend = players.stream().mapToInt(p -> (int) p.getPlayerBean().getAbility(EActionType.dcap))
                .sum();
            // if(npc.getNpcId()==10003){
            // log.error("tttt");
            // }
            players.forEach(p -> {
                PlayerAbility pa = new PlayerAbility(AbilityType.Npc_Buff, p.getPlayerRid());
                float ocap = p.getPlayerBean().getAbility(EActionType.ocap);
                float dcap = p.getPlayerBean().getAbility(EActionType.dcap);
                pa.setAttr(EActionType.ocap, ocap / totalAttack * npc.getPlayerAttack());
                pa.setAttr(EActionType.dcap, dcap / totalDefend * npc.getPlayerDefend());
                p.updateAbility(pa);
                // log.debug("NPC攻防振幅{},{},{}",npc.getNpcId(),p.getPlayerId(),pa.getCap());
            });

            TeamPlayer tp = new TeamPlayer(npc.getNpcId(), players, 0, null);
            createTeamPlayer(npc.getNpcId(), tp);
        });

        _commonDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Normal_Refresh_Talent_Drop));
        _superDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Advanced_Refresh_Talent_Drop));
        _initDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Build_Refresh_Talent_Drop));
        _buyHelpPlayerDeop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Zero_Talent));

        _refreshCard = ConfigConsole.getIntVal(EConfigKey.Normal_Refresh_Talent_Need);
        _refreshSupperCard = ConfigConsole.getIntVal(EConfigKey.Advanced_Refresh_Talent_Need);
        _lockCard = ConfigConsole.getIntVal(EConfigKey.Normal_Lock_Talent_Need);
        _lockSupperCard = ConfigConsole.getIntVal(EConfigKey.Advanced_Lock_Talent_Need);

        _refreshCardCallBack = PropSimple
            .getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Normal_Refresh_Talent_Recycling));
        _refreshSupperCardCallBack = PropSimple
            .getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Advanced_Refresh_Talent_Recycling));
        _lockCardCallBack = PropSimple
            .getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Normal_Lock_Talent_Recycling));
        _lockSupperCardCallBack = PropSimple
            .getPropSimpleByString(ConfigConsole.getVal(EConfigKey.Advanced_Lock_Talent_Recycling));
        _replacePropId = ConfigConsole.getIntVal(EConfigKey.Player_Replace_Price_Prop);
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Player.getOrder();
    }

    public TeamPlayer getTeamPlayer(long teamId) {
        TeamPlayer teamPlayer = teamPlayerMap.get(teamId);
        if (teamPlayer == null) {
            List<PlayerPO> tp = playerAO.getPlayerList(teamId);
            if (tp != null) {
                List<PlayerTalent> ptList = playerAO.getPlayerTalentList(teamId);
                Map<Integer, PlayerTalent> tmpMap = ptList.stream()
                    .collect(Collectors.toMap(PlayerTalent::getId, val -> val));
                int max = ptList.size() <= 0 ? 0 : ptList.stream().mapToInt(PlayerTalent::getId).max().orElse(0);
                LBInt tid = new LBInt(max);

                List<Player> plist = tp.stream().map(p -> new Player(p, tmpMap.computeIfAbsent(
                    p.getTalentId() <= 0 ? tid.increaseAndGet() : p.getTalentId(),
                    key -> PlayerTalent.createPlayerTalent(p.getTeamId(), p.getPlayerRid(), key, _initDrop, true))))
                    .collect(Collectors.toList());
                teamPlayer = new TeamPlayer(teamId, plist, tid.getVal(), tmpMap);
                teamPlayerMap.put(teamId, teamPlayer);
            }
        }
        return teamPlayer;
    }

    void createTeamPlayer(long teamId, TeamPlayer tp) {
        this.teamPlayerMap.put(teamId, tp);
    }

    PlayerPB.PlayerData getPlayerData(Player player) {
        PlayerAbility playerCap = teamCapManager.getPlayerAllCap(player.getTeamId(), player.getPlayerRid(),
            player.getPlayerTalent());
        //
        return PlayerPB.PlayerData.newBuilder()
            .setPid(player.getId())
            .setPlayerId(player.getPlayerRid())
            .setPlayerPosition(player.getPlayerPosition().getId())
            .setAttack((int) playerCap.getAttrData(EActionType.ocap))
            .setDefend((int) playerCap.getAttrData(EActionType.dcap))
            .setPosition(player.getLineupPosition().getId())
            .setPrice(player.getPrice())
            .setStatus(player.getStorage())
            .setBind(player.isBind())
            .setPlayerTalent(getPlayerTalentData(player.getPlayerTalent()))
            .build();
    }

    static PlayerPB.PlayerTalentData getPlayerTalentData(PlayerTalent pt) {
        return PlayerPB.PlayerTalentData.newBuilder().setDf(pt == null ? 0 : pt.getDf())
            .setFqmz(pt == null ? 0 : pt.getFqmz()).setGm(pt == null ? 0 : pt.getGm())
            .setLb(pt == null ? 0 : pt.getLb()).setQd(pt == null ? 0 : pt.getQd())
            .setSfmz(pt == null ? 0 : pt.getSfmz()).setTlmz(pt == null ? 0 : pt.getTlmz())
            .setZg(pt == null ? 0 : pt.getZg()).build();
    }

    protected static PlayerPB.PlayerSimpleData getPlayerSimpleData(Player player) {
        return PlayerPB.PlayerSimpleData.newBuilder()
            .setPid(player.getId())
            .setPlayerId(player.getPlayerRid())
            .setPlayerPosition(player.getPlayerPosition().getId())
            .setPosition(player.getLineupPosition().getId())
            .setPrice(player.getPrice())
            .setBind(player.isBind())
            .setTalent(getPlayerTalentData(player.getPlayerTalent()))
            .build();
    }

    // /***
    // * 位置变化推包
    // */
    // private PlayerPB.PlayerListPosData getPlayerPosChangeData(Player...
    // playerList) {
    // List<PlayerPB.PlayerPositionData> posData = Lists.newArrayList();
    // for (Player p : playerList) {
    // posData.add(
    // PlayerPB.PlayerPositionData.newBuilder().
    // setPid(p.getId())
    // .setPlayerPosition(p.getLineupPosition().getId())
    // .build());
    // }
    // return
    // PlayerPB.PlayerListPosData.newBuilder().addAllPlayerPosList(posData).build();
    // }

    /**
     * 最近5场比赛数据
     *
     * @param playerId
     */
    @ClientMethod(code = ServiceCode.PlayerManager_showPlayerMoneyList)
    public void showPlayerMoneyList(int playerId) {
        PlayerPB.PlayerMoneyDataMain data = playerDetailMap.get(playerId);
        if (data == null) {
            List<PlayerMoneyBeanPO> moneyList = PlayerConsole.getPlayerMoneyList(playerId);//获得球员历史身价
            // 最近5场比赛
            List<NBAPKScoreBoardDetail> tmpList = null;
            if (isRandSechedule) {
                // 随机身价最近5场
                int dayIndex = DateTimeUtil.getDaysBetweenNum(startRand, DateTime.now(), 0);
                int index = Math.abs(dayIndex) % randSecheduleDateList.size();
                List<String> dateList = randSecheduleDateList.stream().limit(index + 1).collect(Collectors.toList());
                //data_game_schedule a,data_score_board_detail
                tmpList = nbaDataAO.getNBAScoreBoardDetailLimit(playerId, dateList);
            } else {
                tmpList = nbaDataAO.getNBAScoreBoardDetailLimit(playerId);
            }
            List<PlayerPB.PlayerBoardDetailData> detailList = tmpList.stream()
                .map(detail -> getPlayerBoardDetailData(detail)).collect(Collectors.toList());
            List<PlayerPB.PlayerMoneyData> dataList = Lists.newArrayList();
            if (moneyList != null) {
                moneyList.forEach(po -> dataList.add(getPlayerMoneyData(po)));
            }
            PlayerBean playerBean = PlayerConsole.getPlayerBean(playerId);
            List<PlayerPB.NBAVSData> nbaVsList = nbaVSDetailMap.get(playerBean.getTeamId());
            PlayerPB.NBAPlayerAvgData avgData = gameManager.getPlayerAvgData(playerId);
            //
            data = PlayerPB.PlayerMoneyDataMain.newBuilder().setPlayerId(playerId).addAllMoneys(dataList)
                .addAllNbaData(detailList).addAllVsData(nbaVsList == null ? Lists.newArrayList() : nbaVsList)
                .setPlayerAvg(avgData == null ? PlayerPB.NBAPlayerAvgData.newBuilder().setPlayerId(playerId).build()
                    : avgData)
                .build();
            playerDetailMap.put(playerId, data);
        }
        sendMessage(data);
    }

    @ClientMethod(code = ServiceCode.PlayerManager_lockAbility)
    public void lockAbility(int playerId, int type, String lock) {
        long teamId = getTeamId();
        int lockInt[] = StringUtil.toIntArray(lock, StringUtil.DEFAULT_ST);
        int refreshCount = Arrays.stream(lockInt).sum();
        int commonProp = refreshCount > 3 ? 3 : refreshCount;
        int superProp = refreshCount > 3 ? refreshCount - 3 : 0;
        TeamPlayer tp = getTeamPlayer(teamId);
        Player player = tp.getPlayer(playerId);
        DropBean db1;
        List<PropSimple> callBackList = Lists.newArrayList();
        if (player == null || lockInt.length < 8) {
            log.debug("lockAbility warn : player is null");
            sendMessage(getPlayerTalentData(null));
            return;
        }
        List<PropSimple> delList = Lists.newArrayList();
        if (commonProp > 0) {
            delList.add(new PropSimple(_lockCard, commonProp));
            if (_lockCardCallBack.getNum() > 0) {
                callBackList
                    .add(new PropSimple(_lockCardCallBack.getPropId(), _lockCardCallBack.getNum() * commonProp));
            }
        }
        if (superProp > 0) {
            delList.add(new PropSimple(_lockSupperCard, superProp));
            if (_lockSupperCardCallBack.getNum() > 0) {
                callBackList.add(new PropSimple(_lockSupperCardCallBack.getPropId(),
                    _lockSupperCardCallBack.getNum() * superProp));
            }
        }

        if (type == 0) {
            delList.add(new PropSimple(_refreshCard, 1));
            db1 = _commonDrop;
            if (_refreshCardCallBack.getNum() > 0) {
                callBackList.add(new PropSimple(_refreshCardCallBack.getPropId(), _refreshCardCallBack.getNum()));
            }
        } else {
            delList.add(new PropSimple(_refreshSupperCard, 1));
            db1 = _superDrop;
            if (_refreshSupperCardCallBack.getNum() > 0) {
                callBackList.add(
                    new PropSimple(_refreshSupperCardCallBack.getPropId(), _refreshSupperCardCallBack.getNum()));
            }
        }
        //
        if (propManager.delProp(teamId, delList, true, true).size() <= 0) {
            log.debug("lockAbility warn : not prop");
            sendMessage(getPlayerTalentData(player.getPlayerTalent()));
            return;
        }
        //
        PlayerTalent ptTemp = null;
        try {
            // private int df;
            // private int zg;
            // private int lb;
            // private int qd;
            // private int gm;
            // private int tlmz;
            // private int fqmz;
            // private int sfmz;
            ptTemp = player.getPlayerTalent().clone();
            // if(lockInt[0]==0){
            // ptTemp.setDf(RandomUtil.randInt(100));
            // }
            // if(lockInt[1]==0){
            // ptTemp.setZg(RandomUtil.randInt(100));
            // }
            // if(lockInt[2]==0){
            // ptTemp.setLb(RandomUtil.randInt(100));
            // }
            // if(lockInt[3]==0){
            // ptTemp.setQd(RandomUtil.randInt(100));
            // }
            // if(lockInt[4]==0){
            // ptTemp.setGm(RandomUtil.randInt(100));
            // }
            // if(lockInt[5]==0){
            // ptTemp.setTlmz(RandomUtil.randInt(100));
            // }
            // if(lockInt[6]==0){
            // ptTemp.setFqmz(RandomUtil.randInt(100));
            // }
            // if(lockInt[7]==0){
            // ptTemp.setSfmz(RandomUtil.randInt(100));
            // }
            if (lockInt[0] == 0) {
                ptTemp.setDf(db1.roll().get(0).getNum());
            }
            if (lockInt[1] == 0) {
                ptTemp.setZg(db1.roll().get(0).getNum());
            }
            if (lockInt[2] == 0) {
                ptTemp.setLb(db1.roll().get(0).getNum());
            }
            if (lockInt[3] == 0) {
                ptTemp.setQd(db1.roll().get(0).getNum());
            }
            if (lockInt[4] == 0) {
                ptTemp.setGm(db1.roll().get(0).getNum());
            }
            if (lockInt[5] == 0) {
                ptTemp.setTlmz(db1.roll().get(0).getNum());
            }
            if (lockInt[6] == 0) {
                ptTemp.setFqmz(db1.roll().get(0).getNum());
            }
            if (lockInt[7] == 0) {
                ptTemp.setSfmz(db1.roll().get(0).getNum());
            }
            if (callBackList.size() > 0) {
                ptTemp.setPropList(callBackList);
                // propManager.addPropList(teamId, callBackList, true, EModuleCode.球员天赋);
            }
            savePlayerTalentTmp(ptTemp);
        } catch (CloneNotSupportedException e) {
            log.error(e.getMessage(), e);
        }
        sendMessage(getPlayerTalentData(ptTemp));
        taskManager.updateTask(teamId, ETaskCondition.刷新球队天赋, 1, "" + type);
    }

    // public static void refreshTalent(DropBean db1,PlayerTalent talent){
    // talent.setDf(db1.roll().get(0).getNum());
    // talent.setZg(db1.roll().get(0).getNum());
    // talent.setLb(db1.roll().get(0).getNum());
    // talent.setQd(db1.roll().get(0).getNum());
    // talent.setGm(db1.roll().get(0).getNum());
    // talent.setTlmz(db1.roll().get(0).getNum());
    // talent.setFqmz(db1.roll().get(0).getNum());
    // talent.setSfmz(db1.roll().get(0).getNum());
    // }

    @ClientMethod(code = ServiceCode.PlayerManager_confirmPlayerTalent)
    public void confirmPlayerTalent() {
        long teamId = getTeamId();
        PlayerTalent pt = getPlayerTalentTmp(teamId);
        if (pt == null) {
            log.debug("prm talent confirm. tid {} pt is null", teamId);
            return;
        }
        TeamPlayer tp = getTeamPlayer(teamId);
        Player p = tp.getPlayer(pt.getPlayerId());
        if (p == null) {
            log.debug("prm talent confirm. tid {} pt {} pr is null", teamId, pt.getPlayerId());
            return;
        }
        p.getPlayerTalent().refreshTalent(pt);
        redis.del(RedisKey.getKey(teamId, RedisKey.PlayerTalent_Temp));
        sendMessage(getPlayerTalentData(p.getPlayerTalent()));
    }

    @ClientMethod(code = ServiceCode.PlayerManager_cancelPlayerTalent)
    public void cancelPlayerTalent() {
        long teamId = getTeamId();
        PlayerTalent pt = getPlayerTalentTmp(teamId);
        if (pt == null) {
            log.debug("PlayerId is old id");
            return;
        }
        loginCheck(teamId, pt);
        redis.del(RedisKey.getKey(teamId, RedisKey.PlayerTalent_Temp));
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code)
            .setMsg(PropSimple.getPropStringByListNotConfig(pt.getPropList())).build());
    }

    private int _replacePropId;

    @ClientMethod(code = ServiceCode.PlayerManager_replacePlayerPrice)
    public void replacePlayerPrice(int pid, int replacePid, int type) {
        long teamId = getTeamId();
        TeamPlayer tp = getTeamPlayer(teamId);
        Player player = tp.getPlayerPid(pid);
        Player replacePlayer = tp.getStoragePlayer(replacePid);

        if (pid == replacePid || player == null || replacePlayer == null) {
            log.debug("replacePlayerPrice->player is null");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ParamError.code).build());
            return;
        }
        
        if(replacePlayer.getStorage() != EPlayerStorage.仓库.getType()) {
            log.info("pm removePlayerFromStorage replacePid{} is not in Storage", replacePid);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
        }

        if (type == 1 && propManager.delProp(teamId, _replacePropId, 1, true, true) == null) {
            log.debug("replacePlayerPrice->prop is null");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        
        ModuleLog moduleLog = ModuleLog.getModuleLog(EModuleCode.身价变动, "身价变动");
        Player delPlayer = this.removePlayerFromStorage(teamId, replacePid, moduleLog);
        if(delPlayer == null) {
            log.debug("身价变动 delPlayer fail teamId{}, replacePid{}", teamId, replacePid);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
        }
        
        int replacePrice = type == 1 ? delPlayer.getPrice() : getReplacePrice(player.getPrice(), delPlayer.getPrice());
        GamePriceLogManager.Log(teamId, player.getId(), player.getPlayerRid(), player.getPrice(), replacePrice,
            moduleLog);
    
        player.updatePrice(replacePrice);
        if(replacePlayer.isBind() || player.isBind()) {
            player.getPlayer().setBind(true);
        }
        
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg((player.getPlayer().isBind() ? 1 : 0)+"").setBigNum(replacePrice).build());
    }

    private static int getReplacePrice(int cur, int focus) {
        int rnd = RandomUtil.randInt(9999);
        float num = 5f;
        return Math.round((int) (rnd / (10000 / num)) * (cur - focus) / num + focus);
    }

    void loginCheck(long teamId) {
        PlayerTalent pt = getPlayerTalentTmp(teamId);
        loginCheck(teamId, pt);
    }

    private void loginCheck(long teamId, PlayerTalent pt) {
        if (pt == null || pt.getPropList() == null || pt.getPropList().size() <= 0) {
            return;
        }
        propManager.addPropList(teamId, pt.getPropList(), true, ModuleLog.getModuleLog(EModuleCode.球员天赋, ""));
    }

    private PlayerTalent getPlayerTalentTmp(long teamId) {
        return redis.getObj(RedisKey.getKey(teamId, RedisKey.PlayerTalent_Temp));
    }

    private void savePlayerTalentTmp(PlayerTalent pt) {
        redis.set(RedisKey.getKey(pt.getTeamId(), RedisKey.PlayerTalent_Temp), pt);
    }

    void reloadPlayerMoney() {
        playerDetailMap = Maps.newConcurrentMap();
        Map<Integer, List<PlayerPB.NBAVSData>> tmp = Maps.newConcurrentMap();
        List<NBAVS> vsList = nbaDataAO.getNBAVSDetailList();
        for (NBAVS vs : vsList) {
            if (vs == null) {
                continue;
            }
            log.debug("reloadPlayerMoney home {} away {} gameId {} startTime{}", vs.getHome(), vs.getAway(),
                vs.getGameId(), vs.getDateTime());
            ENBAPlayerTeam home = ENBAPlayerTeam.getENBAPlayerTeam(vs.getHome());
            ENBAPlayerTeam away = ENBAPlayerTeam.getENBAPlayerTeam(vs.getAway());
            if (home == null || away == null) {
                log.warn("reloadPlayerMoney home {} away {}", vs.getHome(), vs.getAway());
                continue;
            }
            PlayerPB.NBAVSData data = getNBAVSData(vs, home, away);
            tmp.computeIfAbsent(home.getTid(), (key) -> Lists.newArrayList()).add(data);
            tmp.computeIfAbsent(away.getTid(), (key) -> Lists.newArrayList()).add(data);
            // 比赛信息是隔天的信息，并且在配置中放入竞猜数据中
//            if (GameConsole.getGuessBean(vs.getGameId()) != null && DateTimeUtil.getString(vs.getDateTime())
//                .equals(DateTimeUtil.getString(DateTime.now().plusDays(1)))) {
//                if (!nbaVSDetailGameIdMap.containsKey(vs.getGameId())) {
//                    nbaVSDetailGameIdMap.put(vs.getGameId(),
//                        new NBAGuess(vs.getGameId(), home.getTid(), away.getTid(), vs.getDateTime(), 0));
//                }
//            }
        }
        nbaVSDetailMap = tmp;
    }

    @ClientMethod(code = ServiceCode.PlayerManager_showNBABattleGuessListMain)
    public void showNBABattleGuessListMain() {
        long teamId = getTeamId();
        List<PlayerPB.NBAGuessData> guessDataList = Lists.newArrayList();
        for (NBAPlayerGuess guess : nbaPlayerGuessMap.values()) {
        	NBAGuessData nbaGuessData = getNBAGuessData(teamId, guess);
        	if (nbaGuessData != null) {
				guessDataList.add(nbaGuessData);
			}
		}
        
        sendMessage(PlayerPB.NBAGuessMainData.newBuilder().addAllVsData(guessDataList).build());
    }

    /**
     * 玩家竞猜,竞猜的数据保存在Redis中.
     * @param gameId		比赛的Id
     * @param winTeamId		1主队赢,2客队赢
     */
    @ClientMethod(code = ServiceCode.PlayerManager_guessNBABattle)
    public void guessNBABattle(int gameId, int winTeamId) {
        long teamId = getTeamId();
      
        NBAPlayerGuess nbaPlayerGuess = this.nbaPlayerGuessMap.get(gameId);
        NBAGameGuess nbaGameGuess = this.nbaGameGuessMap.get(gameId);
        if (nbaPlayerGuess == null || nbaGameGuess == null) {
            log.debug("竞猜的比赛不存在:{}", gameId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        int guessId = nbaPlayerGuess.getTeamGuessId(teamId);
        if (guessId != 0) {
            log.debug("已经竞猜过了:{},{}", gameId, guessId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        if (DateTimeUtil.isBefore(nbaGameGuess.getEndDateTime()) || DateTimeUtil.isAfter(nbaGameGuess.getStartDateTime())) {
            log.debug("时间还没到:{},{}", gameId, guessId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_0.code).build());
            return;
        }
        
        nbaPlayerGuess.addTeam(winTeamId, teamId, this.redis);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code)
        		.setMsg(gameId + "," + winTeamId).build());
    }

//    void sendBattleGuessGifts() {
//        log.info("开始发放比赛竞猜奖励");
//        nbaVSDetailGameIdMap.values().forEach(vs -> {
//            // if(vs.getWinId()==0){
//            // log.error("发奖异常，有未结束比赛需发奖:{},{}",vs.getGameId(),vs.getWinId());
//            // return;
//            // }
//            String par = ENBAPlayerTeam.getENBAPlayerTeam(vs.getHomeId()).name() + ","
//                + ENBAPlayerTeam.getENBAPlayerTeam(vs.getAwayId()).name();
//            NBABattleGuessBean bean = GameConsole.getGuessBean(vs.getGameId());
//            String gift = bean == null ? "4003:5" : PropSimple.getPropStringByListNotConfig(bean.getGiftList());
//            if (vs.getWinId() == vs.getHomeId()) {
//                vs.getHomeTeamIds().forEach(teamId -> teamEmailManager.sendEmail(teamId, 40000, par, gift));
//            } else {
//                vs.getAwayTeamIds().forEach(teamId -> teamEmailManager.sendEmail(teamId, 40000, par, gift));
//            }
//        });
//        log.info("结束发放比赛竞猜奖励");
//        saveGuessGiftStatus();
//        nbaVSDetailGameIdMap = Maps.newConcurrentMap();
//    }
    
    /**
     * 运营后台新增竞猜比赛触发.
     * @param gameId	比赛的Id
     */
    @RPCMethod(code = CrossCode.WebManager_updateGameGuessData, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void gmUpdateGameGuessDate(int gameId) {
    	NBAGameGuess nbaGameGuess = this.nbaDataAO.getNBAGameGuessById(gameId);
    	if (nbaGameGuess == null) {
    		//移除失效的竞猜活动数据
    		this.nbaGameGuessMap.remove(gameId);
			return;
		}
    	
    	if (DateTimeUtil.isAfter(nbaGameGuess.getEndDateTime())) {
    		this.nbaGameGuessMap.put(gameId, nbaGameGuess);
    		if (!this.nbaPlayerGuessMap.containsKey(gameId)) {
				this.nbaPlayerGuessMap.put(gameId, new NBAPlayerGuess(gameId));
			}
		}
    }
    
    /**
     * 0点定时调用更新竞猜的所有比赛活动数据.
     */
    public void zeroUpdateGameGuessData(){
    	List<NBAGameGuess> list = this.nbaDataAO.getNBAGameGuessDetailList();
    	// 清除掉缓存
    	this.nbaGameGuessMap.clear();
    	
    	if (list.size() == 0) {
			return;
		}
    	
    	for (NBAGameGuess nbaGameGuess : list) {
    		if (DateTimeUtil.isAfter(nbaGameGuess.getEndDateTime())) {
    			int gameId = nbaGameGuess.getId();
        		this.nbaGameGuessMap.put(gameId, nbaGameGuess);
        		if (!this.nbaPlayerGuessMap.containsKey(gameId)) {
    				this.nbaPlayerGuessMap.put(gameId, new NBAPlayerGuess(gameId));
    			}
    		}
		}
    }
    
    /**
     * 发送竞猜活动的奖励邮件.运行后台手工触发的.
     * @param gameId	比赛的Id
     * @param winId		1主队赢,2客队赢
     */
    @RPCMethod(code = CrossCode.WebManager_sendGameGuessEMail, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void gmSendGameGuessTAll(int gameId, int winId) {
    	sendBattleGuessGifts(gameId, winId);
    }
    
    /**
     * 竞猜活动结束,根据竞猜的比赛Id发放邮件奖励,
     * 邮件发放是由运营后台手工触发的.比赛的结果以数据库为准.
     * @param gameId 比赛的Id
     * @param winId 1主队赢,2客队赢
     */
    private int sendBattleGuessGifts(int gameId, int winId){
    	NBAGameGuess nbaGameGuess = this.nbaDataAO.selectNBAGameGuessById(gameId);
    	if (nbaGameGuess == null || nbaGameGuess.getRewardConfig() == null 
    			|| nbaGameGuess.getRewardConfig() == "") {
			log.warn("竞猜活动数据不存在:id={},发放竞猜邮件奖励失败!", gameId);
			return -1;
		}
    	
//    	if (nbaGameGuess.getSendReward() == 1 && nbaGameGuessCache.getSendReward() == 1) {
//    		log.warn("竞猜活动:id={},已发放竞猜邮件奖励!", gameId);
//    		return -1;
//		}
    	
    	NBAPlayerGuess nbaPlayerGuess = this.nbaPlayerGuessMap.get(gameId);
    	if (nbaPlayerGuess == null) {
    		nbaPlayerGuess = new NBAPlayerGuess(gameId, redis);
    		if (nbaPlayerGuess.getAwayTeamIds().size() == 0 
    				&& nbaPlayerGuess.getHomeTeamIds().size() == 0) {
    			log.warn("竞猜活动:id={}, 所有玩家竞猜的数据不存在!", gameId);
    			return -1;
			}
		}
    	
    	Set<Long> iteamIdSet = null;
    	String winNameStr = "主队赢";
    	//主队赢
    	if (winId == 1) {
    		iteamIdSet = nbaPlayerGuess.getHomeTeamIds();
		}else {
			iteamIdSet = nbaPlayerGuess.getAwayTeamIds();
			winNameStr = "客队赢";
		}
    	
    	log.info("本次竞猜的比赛Id={},赢的队伍是:{},猜中的人数是:{}", 
    			gameId,winNameStr, iteamIdSet.size());
    	iteamIdSet.forEach(obj ->{
    		String paramStr = nbaGameGuess.getHomeName() +"," + nbaGameGuess.getRoadName();
    		teamEmailManager.sendEmail(obj.longValue(), 40000, paramStr, nbaGameGuess.getRewardConfig());
    	});
		
		log.info("结束发放比赛竞猜奖励");
//		nbaGameGuessCache.setSendReward(1);
		// 移除发送完奖励的竞猜数据
		this.nbaGameGuessMap.remove(gameId);
		nbaPlayerGuess.delNBAPlayerGuessData(this.redis);
    	this.nbaPlayerGuessMap.remove(gameId);
		
    	return 0;
    }

//    private void saveGuessGiftStatus() {
//        redis.set(RedisKey.getDayKey(0, RedisKey.Battle_Guess_Gift), 1);
//    }

//    void updateNBAGuessWinTeam(int gameId, int winTeamId) {
//        NBAGuess guess = nbaVSDetailGameIdMap.get(gameId);
//        if (guess == null) {
//            return;
//        }
//        if (guess.getWinId() == 0 && winTeamId != 0) {
//            guess.setWinId(winTeamId);
//        }
//    }

//    private PlayerPB.NBAGuessData getNBAGuessData(long teamId, NBAGuess guess) {
//        NBABattleGuessBean guessBean = GameConsole.getGuessBean(guess.getGameId());
//        return PlayerPB.NBAGuessData.newBuilder().setAwayId(guess.getAwayId()).setGameId(guess.getGameId())
//            .setGuessId(guess.getTeamGuessId(teamId)).setHomeId(guess.getHomeId())
//            .setTime(guessBean == null ? DateTimeUtil.getString(guess.getTime(), DateTimeUtil.ymdhms)
//                : DateTimeUtil.getString(guessBean.getBattleStartTime(), DateTimeUtil.ymdhms))
//            .setWinId(guess.getWinId()).build();
//
//    }
    
    private PlayerPB.NBAGuessData getNBAGuessData(long teamId, NBAPlayerGuess guess) {
        NBAGameGuess nbaGameGuess = this.nbaGameGuessMap.get(guess.getGameId());
        if (nbaGameGuess == null || DateTimeUtil.isBefore(nbaGameGuess.getEndDateTime())
        		|| nbaGameGuess.getCancel() == 1 || nbaGameGuess.getSendReward() == 1) {
			return null;
		}
        
        return PlayerPB.NBAGuessData.newBuilder()
        		.setHomeId(1)
        		.setAwayId(2)
        		.setGameId(guess.getGameId())
        		.setWinId(nbaGameGuess.getGameResult())
        		.setGuessId(guess.getTeamGuessId(teamId))
        		.setHomeName(nbaGameGuess.getHomeName())
        		.setAwayName(nbaGameGuess.getRoadName())
        		.setReward(nbaGameGuess.getRewardConfig())
        		.setTime(DateTimeUtil.getString(nbaGameGuess.getStartDateTime(), DateTimeUtil.ymdhms))
        		.setStartTimeStr(DateTimeUtil.getString(nbaGameGuess.getStartDateTime(), DateTimeUtil.ymdhms))
        		.setEndTimeStr(DateTimeUtil.getString(nbaGameGuess.getEndDateTime(), DateTimeUtil.ymdhms))
        		.build();
    }

    private PlayerPB.NBAVSData getNBAVSData(NBAVS vs, ENBAPlayerTeam home, ENBAPlayerTeam away) {
        return PlayerPB.NBAVSData.newBuilder().setAwayId(home.getTid()).setHomeId(away.getTid())
            .setTime(vs.getDateTime().getMillis()).build();
    }

    private PlayerPB.PlayerBoardDetailData getPlayerBoardDetailData(NBAPKScoreBoardDetail detail) {
        return PlayerPB.PlayerBoardDetailData.newBuilder().setAst(detail.getAst()).setBlk(detail.getBlk())
            .setDreb(detail.getDreb()).setEffectPoint(detail.getEffectPoint()).setFga(detail.getFga())
            .setFgm(detail.getFgm()).setFta(detail.getFta()).setFtm(detail.getFtm())
            .setIsStarter(detail.getIsStarter()).setMin(detail.getMin()).setOreb(detail.getOreb())
            .setPf(detail.getPf()).setPts(detail.getPts()).setReb(detail.getReb()).setStl(detail.getStl())
            .setThreePa(detail.getThreePa()).setThreePM(detail.getThreePm()).setTo(detail.getTo())
            .setTime(DateTimeUtil.getString(detail.getTime(), DateTimeUtil.ymd)).build();
    }

    private PlayerPB.PlayerMoneyData getPlayerMoneyData(PlayerMoneyBeanPO po) {
        return PlayerPB.PlayerMoneyData.newBuilder().setPrice(po.getMoney()).setTime(po.getMoneyTime().getMillis())
            .build();

    }

    /**
     * Player请在外部调用Player.createPlayer后传进来 添加球员到仓库，一般是从交易市场购买球员调用； 触发事件：得到球员
     */
    public void addPlayerToStore(long teamId, TeamPlayer tp, Player player, PlayerBean playerBean, ModuleLog module) {
        synchronized (tp) {
            tp.addPlayerToStorage(player);
            player.save();
            //            updatePlayerMinPrice(player.getPlayerRid(), player.getPrice());
            sendMessage(teamId, getPlayerSimpleData(player), ServiceCode.Player_Storage_Change_Push);
            GamePlayerLogManager.Log(teamId, player.getId(), player.getPlayerRid(), player.getPrice(), 1, module);
            EventBusManager.post(EEventType.得到球员, player);

            // 获得球星推送
            Team team = teamManager.getTeam(teamId);
            // 是不是s+ 球员
            if (playerBean.getGrade().ordinal() != EPlayerGrade.S2.ordinal()) { return; }
            if (module.getId() == EModuleCode.充值福利.getId()) {
                chatManager.pushGameTip(EGameTip.领取首充奖励, 0, team.getName());
            } else if (module.getId() != EModuleCode.自由交易.getId()) {
                chatManager.pushGameTip(EGameTip.获得S加球员, 0, team.getName(), playerBean.getName());
            }
        }
    }

    /**
     * 创建球队使用
     */
    ErrorCode addCreatePlayer(long teamId, TeamPlayer tp, PlayerSimple player, ModuleLog module) {
        PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, player.getPlayerId(), tp.getNewTalentId(), _initDrop,
            true);
        Player tplayer = tp.createPlayer(PlayerConsole.getPlayerBean(player.getPlayerId()), player.getPrice(),
            player.getPosition().name(), talent);
        tp.addPlayer(tplayer);
        tp.putPlayerTalent(talent);
        //        updatePlayerMinPrice(player.getPlayerId(), player.getPrice());
        GamePlayerLogManager.Log(teamId, tplayer.getId(), tplayer.getPlayerRid(), tplayer.getPrice(), 1, module);
        return ErrorCode.Success;
    }

    @ClientMethod(code = ServiceCode.PlayerManager_makeArenaPlayer)
    public void makeArenaPlayer(int pid) {
        long teamId = getTeamId();
        TeamPlayer tp = getTeamPlayer(teamId);
        Player player = tp.getStoragePlayer(pid);
        if (player == null) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_1.code).build());
            return;
        }
        
        if (player.getStorage() == EPlayerStorage.训练馆.getType()) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Train_Player_Training.code).build());
            return;
        }
        
        this.removePlayerFromStorage(teamId, pid,  ModuleLog.getModuleLog(EModuleCode.球员回收, "球馆makeArenaPlayer"));
       
        int p = localArenaManager.addPlayerLineup(teamId, player.getPlayerRid());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg("" + p).build());
    }

    /**
     * 得到球员到阵容 触发事件：球队现有工资 , 得到球员
     */
    public void addPlayerUnCheck(long teamId, TeamPlayer tp, Player player, PlayerBean playerBean, boolean send,
                                 ModuleLog module) {
        int playerId = player.getPlayerRid();
        int price = player.getPrice();
        tp.addPlayer(player);
        player.save();
        //        updatePlayerMinPrice(player.getPlayerRid(), player.getPrice());
        if (send) {
            sendMessage(teamId, getPlayerSimpleData(player), ServiceCode.Push_Player);
        }
        GamePlayerLogManager.Log(teamId, player.getId(), playerId, price, 1, module);
        //
        EventBusManager.post(EEventType.球队现有工资, teamId);
        EventBusManager.post(EEventType.得到球员, player);

        // 获得球星推送
        Team team = teamManager.getTeam(teamId);
        // 是不是詹姆斯
        if (playerBean.getGrade().ordinal() != EPlayerGrade.S2.ordinal()) { return; }
        if (module.getId() == EModuleCode.充值福利.getId()) {
            chatManager.pushGameTip(EGameTip.领取首充奖励, 0, team.getName());
        } else if (module.getId() != EModuleCode.自由交易.getId()) {
            chatManager.pushGameTip(EGameTip.获得S加球员, 0, team.getName(), playerBean.getName());
        }
    }

    ErrorCode addPlayer(long teamId, TeamPlayer tp, Player player, PlayerBean payerBean, boolean send,
                        ModuleLog module) {
        ErrorCode code = checkAddPlayer(teamId, tp, player.getPlayerRid(), player.getPrice());
        if (code != ErrorCode.Success) {
            return code;
        }
        //
        addPlayerUnCheck(teamId, tp, player, payerBean, send, module);
        return code;
    }

    private ErrorCode checkAddPlayer(long teamId, TeamPlayer tp, int playerId, int price) {
        Player tmp = tp.getPlayer(playerId);
        if (tmp != null) {
            return ErrorCode.Player_0;// 场上存在相同球员，无法获得
        }
        if (tp.getPlayers().size() >= teamManager.getTeamMaxPlayerCount(teamId)) {
            return ErrorCode.Player_1;// 球员达到上限
        }
        return ErrorCode.Success;
    }

    //    /**
    //     * 更新球员底薪
    //     */
    //    private void updatePlayerMinPrice(int playerId, int price) {
    //        if (this.getPlayerMinPrice(playerId) > price) {
    //            playerMinPriceMap.put(playerId, price);
    //        }
    //    }

    /**
     * 获取球员底薪
     */
    public int getPlayerMinPrice(int playerId) {
        Integer price = playerMinPriceMap.get(playerId);
        if (price == null || price <= 0) {
            price = PlayerConsole.getPlayerBean(playerId).getPrice();
            playerMinPriceMap.put(playerId, price);
        }
        return price;
    }

    /**
     * 判断该球员是否底薪
     */
    public boolean isMinPricePlayer(int playerId, int price) {
        return this.getPlayerMinPrice(playerId) >= price;
    }

    /**
     * 取玩家仓库球员底薪数量
     *
     * @param tp
     * @param playerId
     * @return
     */
    public int getMinPriceCount(TeamPlayer tp, int playerId) {
        return (int) tp.getStoragePlayerList(playerId).stream().filter(p -> isMinPricePlayer(playerId, p.getPrice())).count();
    }

    /**
     * 交换球员位置
     *
     * @param player1 唯一ＩＤ
     * @param player2 唯一ＩＤ
     */
    @ClientMethod(code = ServiceCode.Player_Tran_Position)
    public void tranPlayerPosition(int player1, int player2) {
        long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);
        if (!teamPlayer.existPlayerPid(player1) || !teamPlayer.existPlayerPid(player2)) {
            log.debug("球员不存在");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        teamPlayer.tranPlayerPosition(player1, player2);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        //
        // 推包
        // sendMessage(getPlayerPosChangeData(teamPlayer.getPlayer(player1),
        // teamPlayer.getPlayer(player2)));
    }

    /**
     * 仓库球员制卡
     *
     * @param pid 唯一ID
     */
    @ClientMethod(code = ServiceCode.Player_Storage_MakeCard)
    public void makeCard(int pid) {
        long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);
        if (!teamPlayer.existStoragePlayer(pid)) {
            log.debug("球员不存在{}", pid);
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        Player player = teamPlayer.getStoragePlayer(pid);
        // X 不能解
        if (isXPlayer(player.getPlayerRid())) {
            log.debug("X球员不不能制卡{}", player.getPlayerRid());
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Player_3.code).build());
            return;
        }
        if (player.getStorage() == EPlayerStorage.训练馆.getType()) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Train_Player_Training.code).build());
            return;
        }
        
        // 制卡
        Player delPlayer = this.removePlayerFromStorage(teamId, pid, ModuleLog.getModuleLog(EModuleCode.球星卡, "制卡"));
        if(delPlayer == null) {
            log.debug("制卡 delPlayer fail teamId{}, pid{}", teamId, pid);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
        }
        
        Builder data = playerCardManager.markCard(teamId, new int[]{delPlayer.getPlayerRid()});
        sendMessage(data.setCode(ErrorCode.Success.code).build());
    }

    /** 自动添加球员, 添加到阵容或者仓库 */
    public ErrorCode addPlayerAuto(long teamId, int playerId, int price, boolean bind, ModuleLog module) {
        TeamPlayer tp = getTeamPlayer(teamId);
        PlayerTalent pt = PlayerTalent.createPlayerTalent(teamId, playerId, tp.getNewTalentId(), _initDrop, true);
        return addPlayerAuto(teamId, tp, playerId, price, pt, bind, module);
    }

    /** 自动添加球员, 添加到阵容或者仓库 */
    public ErrorCode addPlayerAuto(long teamId, int playerId, int price, PlayerTalent pt, boolean bind, ModuleLog module) {
        TeamPlayer tp = getTeamPlayer(teamId);
        return addPlayerAuto(teamId, tp, playerId, price, pt, bind, module);
    }

    /** 自动添加球员, 添加到阵容或者仓库 */
    public ErrorCode addPlayerAuto(long teamId, TeamPlayer tp, int playerId, int price, PlayerTalent pt, boolean bind, ModuleLog module) {
        Player tmp = tp.getPlayer(playerId);
        boolean inStorage = false;
        if (tmp != null) {
            // 场上存在相同球员，只进仓库，判断仓库容量
            if (getStorageSize(teamId) < 1) {
                return ErrorCode.Player_Storage_Full;
            }
            inStorage = true;
        }
        // 判断是否有签约位置：
        if (!inStorage && tp.getPlayers().size() >= teamManager.getTeamMaxPlayerCount(teamId)) {
            if (getStorageSize(teamId) < 1) {
                return ErrorCode.Player_Storage_Full;
            }
            inStorage = true;
        }
        if (pt == null) {
            pt = PlayerTalent.createPlayerTalent(teamId, playerId, tp.getNewTalentId(), _initDrop, true);
        }
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        int fprice = price;
        if (price == 9999) {  // 取当前市价
            fprice = pb.getPrice();
        } else if (price == 8888) { // 取本服低薪
            fprice = getPlayerMinPrice(playerId);
        }
        if (fprice <= 0) {
            fprice = 2051;
        }

        Player player = tp.createPlayer(pb, fprice, EPlayerPosition.NULL.name(), pt, bind);
        if (inStorage) {
            addPlayerToStore(teamId, tp, player, pb, module);
        } else {
            addPlayerUnCheck(teamId, tp, player, pb, true, module);
        }
        tp.putPlayerTalent(pt);
        return ErrorCode.Success;
    }

    /**
     * 球员回收, 只能解雇阵容,仓库球员
     */
    @ClientMethod(code = ServiceCode.PlayerManager_Fire)
    public void firePlayer(int pid) {
        long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);
        if (!teamPlayer.existStoragePlayer(pid)) {
            log.debug("球员不存在{}", pid);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        Player player = teamPlayer.getStoragePlayer(pid);
        // X 不能解
        if (isXPlayer(player.getPlayerRid())) {
            log.debug("X球员不不能解雇{}", player.getPlayerRid());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_3.code).build());
            return;
        }
               
        // 解雇
        ModuleLog moduleLog = ModuleLog.getModuleLog(EModuleCode.球员回收, "球员回收");
        Player delPlayer = playerManager.removePlayerFromStorage(teamId, pid, moduleLog);
        if(delPlayer == null) {
            log.debug("球员回收 delPlayer fail teamId{}, pid{}", teamId, pid);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
        }
        
        int propNum = PlayerExchangeConsole
                .getRecoveryNum(PlayerConsole.getPlayerBean(delPlayer.getPlayerRid()).getGrade().getGrade());
        propManager.addProp(teamId, new PropSimple(PlayerExchangeConsole.Recycling_PROP_ID, propNum), true, moduleLog);
       
        taskManager.updateTask(teamId, ETaskCondition.解雇球员, 1, EModuleCode.球员回收.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(delPlayer.getId())
            .setMsg(propNum + "").build());
    }

    /**
     * 更换X球星
     *
     * @param xPleyerId 新X球星
     */
    @ClientMethod(code = ServiceCode.PlayerManager_changeXplayer)
    public void changeXPlayer(int xPlayerId) {
        long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);

        // 相同球星不做更改
        Player oldXplayer = teamPlayer.getXPlayer();
        if (oldXplayer == null) {
            log.debug("队伍X球星不存在{}", teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }

        if (oldXplayer.getPlayerRid() == xPlayerId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_5.code).build());
            return;
        }
        
        // X球员在训练不能更换新的X球员
        if (oldXplayer.getStorage() == EPlayerStorage.训练馆.getType()) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Train_Player_Training.code).build());
            return;
		}

        // X球员不存在或者是非法X球员
        PlayerBean X = PlayerConsole.getPlayerBean(xPlayerId);
        if (X == null || !PlayerConsole.existCreateXPlayer(xPlayerId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Prop_Bean_Null.code).build());
            return;
        }

        // 消耗道具
        TeamProp teamProp = propManager.getTeamProp(teamId);
        PropSimple needProp = new PropSimple(ConfigConsole.global().teamChangeXplayerPropId, 1);
        if (!teamProp.checkPropNum(needProp)) {
            log.debug("更换X球星道具不足{}", needProp.toString());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }

        // 删除道具
        propManager.delProp(teamId, needProp, true, true);

        // 天赋设置
        oldXplayer.getPlayerTalent().setPlayerId(xPlayerId);
        oldXplayer.getPlayerTalent().save();

        // 更新装备
        equiManager.transEqui(teamId, oldXplayer.getPlayerRid(), xPlayerId);
        // sendMessage(teamId,
        // equiManager.getTeamEquiData(equiManager.getTeamEqui(getTeamId())),
        // ServiceCode.EquiManager_showEquiList);

        // 更新技能
        PlayerBean oldX = PlayerConsole.getPlayerBean(oldXplayer.getPlayerRid());
        skillManager.transSkill(teamId, oldXplayer.getPlayerRid(), xPlayerId,
            this.getTeamPlayer(teamId).getplayerPosition(oldX), this.getTeamPlayer(teamId).getplayerPosition(X));
        TeamSkill ts = skillManager.getTeamSkill(teamId);
        sendMessage(teamId, skillManager.getSkillMain(teamId, ts), ServiceCode.SkillManager_showSkillMain);

        // 更新X球星数据
        oldXplayer.setPlayerRid(X.getPlayerRid());
        oldXplayer.setPrice(X.getPrice());
        oldXplayer.setPosition(X.getPosition()[0].name());
        oldXplayer.save();

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());

        // 更新球星数据推送
        sendMessage(teamId, getPlayerSimpleData(oldXplayer), ServiceCode.Push_Player);
    }

    /**
     * 降低球员工资
     *
     * @param pleyerId 球星唯一ID
     */
    @ClientMethod(code = ServiceCode.PlayerManager_lowerPlayerPrice)
    public void lowerPlayerPrice(int playerId) {
        long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);

        // 球星
        Player player = teamPlayer.getPlayerPid(playerId);
        if (player == null) {
            log.debug("球星不存在{}", teamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }

        // x球星不能更换
        if (isXPlayer(player.getPlayerRid())) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_3.code).build());
            return;
        }

        // 球员不存在或者是非法球员
        //        PlayerBean X = PlayerConsole.getPlayerBean(player.getPlayerRid());
        //        if (X == null || !PlayerConsole.existCreateXPlayer(player.getPlayerRid())) {
        //            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Bean_Null.code).build());
        //            return;
        //        }

        // 获取玩家30天内的最低工资
        int minPrice = this.getMinPrice(PlayerConsole.getPlayerMoneyList(player.getPlayerRid()));

        // 计算玩家当前工资最大降幅
        int percent = this.getMaxPercent(PlayerConsole.getPlayerPriceBeanList());
        //如果30天内最低市价*工资最大降幅<玩家当前工资
        if (minPrice - (int) (minPrice * percent * 0.001) > player.getPrice()) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_7.code).build());
            return;
        }

        // 消耗道具
        TeamProp teamProp = propManager.getTeamProp(teamId);
        PropSimple needProp = new PropSimple(ConfigConsole.global().teamLowerPriceItem, ConfigConsole.global().teamLowerPriceItemNum);
        if (!teamProp.checkPropNum(needProp)) {
            log.debug("降薪道具不足{}", needProp.toString());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }

        // 删除道具
        propManager.delProp(teamId, needProp, true, true);

        // 计算降薪
        int per = this.globalRandom(PlayerConsole.getPlayerPriceBeanList()).getPercent();
        int endPrice = minPrice - (int) (minPrice * per * 0.001);
        if (player.getPrice() > endPrice) {
            player.setPrice(endPrice);
            player.save();
        }

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(endPrice).build());

        // 更新球星数据推送
        sendMessage(teamId, getPlayerSimpleData(player), ServiceCode.Push_Player);
    }
    
    /**
     * 玩家使用降薪卡降低新秀球员工资.要求新秀球员的等级为N,且最大降薪为300W.
     * @param pleyerId 球星唯一ID
     * @param pid	       降薪卡道具配置Id
     * @param costCount使用降薪卡的数量
     */
    @ClientMethod(code = ServiceCode.PlayerManager_startletLowerSalary)
    public void startletLowerSalary(int playerId, int pid, int costCount) {
    	ErrorCode errorCode = startletLowerSalary0(playerId, pid, costCount);
    	if (errorCode != null) {
    		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(errorCode.code).build());
		}
    }
    
    private ErrorCode startletLowerSalary0(int playerId, int pid, int num){
    	long teamId = getTeamId();
        TeamPlayer teamPlayer = getTeamPlayer(teamId);

        // 球星
        Player player = teamPlayer.getPlayerPid(playerId);
        if (player == null) {
            log.debug("球星不存在{}", teamId);
            return ErrorCode.Player_Null;
        }
        
        // 判断是否是可降薪的新秀球员
        if(!PlayerCutsConsole.getPlayerCutsBeanMap().containsKey(player.getPlayerRid())){
        	return ErrorCode.Player_Cant_Not_Lower_Salary;
        }
        
        // 新秀球员里状态为训练中, 交易中, 求购中都不能降薪
        if (player.getStorage() == EPlayerStorage.交易.getType()
        		|| player.getStorage() == EPlayerStorage.训练馆.getType()) {
        	return ErrorCode.Player_Cant_Not_Lower_Salary;
		}
        
        PlayerCutsBean playerCutsBean = PlayerCutsConsole.getPlayerCutsBeanMap().get(player.getPlayerRid());
        //新秀球员要满足某个特定的等级(N)才能被降薪
        if (!player.getPlayerBean().getGrade().getGrade().equals(playerCutsBean.getGrade())) {
			return ErrorCode.Player_Startlet_Grade_Not_N;
		}
        
        // 新秀球员已经降薪到最低薪资不能再继续降薪
        if (player.getPrice() <= playerCutsBean.getMinPrice()) {
			return ErrorCode.Player_Price_Is_Lowest_Salary;
		}
        
        //获取降薪卡道具的配置数据
        PropBean lowerSalaryCard = PropConsole.getProp(pid);
        if (lowerSalaryCard == null) {
			return ErrorCode.ParamError;
		}
        
        int amount = 0;
        try {
        	amount = Integer.valueOf(lowerSalaryCard.getConfig());
		} catch (NumberFormatException e) {
			log.warn("字符串转换成整型异常:{}", lowerSalaryCard.getConfig());
			return ErrorCode.ParamError;
		}
        
        TeamProp teamProp = propManager.getTeamProp(teamId);
        // 消耗的降薪卡道具
        PropSimple needProp = new PropSimple(pid, num);
        if (!teamProp.checkPropNum(needProp)) {
            log.debug("降薪道具不足{}", needProp.toString());
            return ErrorCode.Prop_0;
        }

        int beforeSalary = player.getPrice();
        int lowerAmount = amount;
        // 计算降低多少薪资
        amount = amount * num;
        int afterSalary = player.getPrice() - amount;
        // 降到最低薪资以下,则取最低薪资,降薪卡根据实际消耗数量计算
        if ( afterSalary < playerCutsBean.getMinPrice()) {
        	int sub = player.getPrice() - playerCutsBean.getMinPrice();
        	if (sub > 0) {
        		// 最终消耗的降薪卡数量
				needProp.setNum((int) Math.ceil(sub * 1.0 / lowerAmount));
			}
        	afterSalary = playerCutsBean.getMinPrice();
        }
        
        // 删除道具
        propManager.delProp(teamId, needProp, true, true);
        
        player.setPrice(afterSalary);
        player.save();
        // 保存新秀球员降薪记录
        PlayerLowerSalaryLog.createPlayerLowerSalaryLog(getMaxPlsIdValue(), teamId, playerId,
        		beforeSalary, afterSalary, amount, new DateTime());
        
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(playerId+"").setBigNum(afterSalary).build());
        // 更新球星数据推送
//        sendMessage(teamId, getPlayerSimpleData(player), ServiceCode.Push_Player);
        return null;
    }
    
    /**
     * 生成球员降薪表的Id
     * @return
     */
    private long getMaxPlsIdValue(){
    	if (plsId.longValue() == 0) {
			plsId.set(GameSource.shardId * 10000 + plsId.incrementAndGet());
		}else {
			plsId.incrementAndGet();
		}
    	
    	return plsId.longValue();
    }

    /**
     * 判断一个球员是否是Ｘ
     */
    private boolean isXPlayer(int playerId) {
        return PlayerConsole.getPlayerBean(playerId).getGrade() == EPlayerGrade.X;
    }

    /** 全局随机降工资概率 */
    public PlayerPriceBean globalRandom(List<PlayerPriceBean> objectList) {
        if (objectList.size() == 1) {
            return objectList.get(0);
        }

        int randomSum = 0;
        for (PlayerPriceBean model : objectList) {
            randomSum += model.getRate();
        }

        int random = RandomUtil.randInt(randomSum);
        int rate = 0;
        for (PlayerPriceBean model : objectList) {
            if (random <= model.getRate() + rate) {
                return model;
            }
            rate += model.getRate();
        }

        return null;
    }

    /** 取列表中的最大概率 */
    private int getMaxPercent(List<PlayerPriceBean> objectList) {
        List<Integer> primes = new ArrayList<>();

        objectList.forEach(ppb -> {
            primes.add(ppb.getPercent());
        });

        return primes.stream().mapToInt((x) -> x).summaryStatistics().getMax();
    }

    /** 取30天内玩家最低市价 */
    private int getMinPrice(List<PlayerMoneyBeanPO> objectList) {
        int index = RandomUtil.randInt(objectList.size());
        return objectList.get(index).getMoney();
    }

    // ------------------------仓库模块----------------------------------
    // 仓库模块
    // 就是仓库去除到阵容，阵容保存到仓库； 就是更新一下标志位，不过注意阵容不能出现重复球员，仓库可以。
    // ---------只有一个方法，替换------------

    /**
     * 取仓库剩余大小，包括在交易市场的，求购也占位置
     */
    public int getStorageSize(long teamId) {
        TeamPlayer tp = getTeamPlayer(teamId);
        synchronized (tp) {
            // vip大小
            int lvSize = GradeConsole.getTeamExpBean(teamManager.getTeam(teamId).getLevel()).getStorage();
            int vipSize = buffManager.getBuffSet(teamId, EBuffType.球员仓库上限增加).getValueSum();

            int buySize = tradeP2PManager.getTeamBuyingSize(teamId);
            return PlayerConsole.MAX_STORAGE_SIZE + lvSize + vipSize - tp.getStorages().size() - buySize;
        }
    }

    /**
     * 仓库和阵容位置交替 当p1_id为-1时，是从仓库取出到阵容 当p2_id为-1时，是从阵容放入到仓库 当他两都不为-1时，是仓库和阵容球员的替换
     *
     * @param p1_id 阵容的球员ID，唯一ID
     * @param p2_id 仓库的球员ID，唯一ID
     */
    @ClientMethod(code = ServiceCode.Player_Tran_Store)
    public void changeStore(int p1_id, int p2_id) {
        ErrorCode ret = changeStore0(p1_id, p2_id);
        sendMsg(ret);
    }

    private ErrorCode changeStore0(int p1_id, int p2_id) {
        long teamId = getTeamId();
        if (p1_id < 0 && p2_id < 0) {
            return ErrorCode.Player_Null;
        }
        TeamPlayer tp = getTeamPlayer(teamId);
        synchronized (tp) {
            boolean hasPlayer = p1_id >= 0 && tp.existPlayerPid(p1_id); // 阵容是否选中球员
            boolean hasStorage = p2_id >= 0 && tp.existStoragePlayer(p2_id); // 仓库是否选中球员
            if ((!hasPlayer && !hasStorage) || (p2_id > 0 && !hasStorage)) { // 只允许仓库和替补球员交换
                return ErrorCode.Player_Null;
            }
            Player p1 = tp.getPlayerPid(p1_id);
            if (p1 != null && p1.getLineupPosition() != EPlayerPosition.NULL) { // 首发阵容不可以放入仓库
                return ErrorCode.Player_4;
            }
            if (hasStorage && !hasPlayer && tp.existPlayer(tp.getStoragePlayer(p2_id).getPlayerRid())) {// 从仓库取出不能和阵容重复
                return ErrorCode.Player_5;
            }
            if (hasPlayer && !hasStorage && getStorageSize(teamId) < 1) {// 只放入判断仓库大小
                return ErrorCode.Player_Storage_Full;
            }
            if (hasStorage && tp.getStoragePlayer(p2_id).getStorage() == EPlayerStorage.训练馆.getType()) {
                return ErrorCode.Train_Player_Training;
            }

            if (hasPlayer && hasStorage) {// 交换位置
                EPlayerPosition tranPos = p1.getLineupPosition();
                tp.teamToStorage(p1_id);
                tp.storageToTeam(p2_id, tranPos);
            } else if (hasPlayer) { // 放入仓库
                tp.teamToStorage(p1_id);
            } else { // 放入阵容 必须有足够的替补位
                if (tp.getPlayers().size() + 1 > teamManager.getTeamMaxPlayerCount(teamId)) {
                    return ErrorCode.Error;
                }
                tp.storageToTeam(p2_id, EPlayerPosition.NULL);
            }
            if (log.isDebugEnabled()) {
                log.debug("prm changestore. tid {} pid1 {} pid2 {}. size lp {} storage {} ", teamId, p1_id, p2_id,
                    tp.getPlayers().size(), tp.getStoragesAndMarket().size());
            }
            return ErrorCode.Success;
        }
    }

    int getMaxPlayerCount() {
        return _maxPlayerCount;
    }

    /** 获取最大攻防玩家数据 */
    public PlayerAbility getPlayerAbilityByMaxCap(List<Player> players) {
        List<PlayerAbility> playerAbilities = Lists.newArrayList();
        players.forEach(player -> {
            playerAbilities.add(teamCapManager.getPlayerAllCap(player.getTeamId(), player.getPlayerRid(), player.getPlayerTalent()));
        });

        playerAbilities.sort(Comparator.comparing(PlayerAbility::getTotalCap));

        if (playerAbilities.size() < 1) { return null; }

        return playerAbilities.get(playerAbilities.size() - 1);
    }
    
    /** 从仓库中彻底删除球员(删数据)*/
    public Player removePlayerFromStorage(long tid, int pid, ModuleLog moduleLog) {
        TeamPlayer teamPlayer = getTeamPlayer(tid);
        Player delPlayer = teamPlayer.getPlayerPid(pid);
        if(delPlayer.getStorage() != EPlayerStorage.仓库.getType() 
        		&& delPlayer.getStorage() != EPlayerStorage.交易.getType()) {
            log.info("pm removePlayerFromStorage pid{} is not in Storage", pid);
            return null;
        }
        
        Integer prid = delPlayer.getPlayerRid();
        teamPlayer.removeFromStorage(pid);
        delPlayer.del();

        // 清理新秀阵容玩家
        starletManager.deleteStarletTeamPlayer(teamPlayer, tid, prid);
        switchPFAndSFPlayerExtendsSkillLevel(tid, delPlayer);
        
        GamePlayerLogManager.Log(tid, pid, delPlayer.getPlayerRid(), delPlayer.getPrice(), -1, moduleLog);
        return delPlayer;
    }
    
    /**
     * 当相同球员最后一个被清除了之后,判断该球员是否是PF/SF的双位置球员,
     * 然后去判断球员技能是否是默认的技能,如果不是则技能等级转成默认PF或者SF的对应技能等级.
     * 为了解决bug,双位置球员PF/SF,转换了球员位置最后该球员都被删除,然后再次获得该球员
     * 导致技能等级不能继承下来的bug.
     * @param tid			球队teamId
     * @param delPlayer		被消耗掉(删除)的球员
     */
    public void switchPFAndSFPlayerExtendsSkillLevel(long tid, Player delPlayer){
    	TeamPlayer teamPlayer = getTeamPlayer(tid);
    	List<Player> list = teamPlayer.getAllPlayerByPlayerId(delPlayer.getPlayerRid());
    	//---被删除的球员是否是最后一个相同球员---
    	if (list.size() != 0) {
			return;
		}
    	
    	PlayerBean pb = PlayerConsole.getPlayerBean(delPlayer.getPlayerRid());
    	//该球员不存在双位置
    	if (pb.getPosition().length < 2) {
			return;
		}
    	
    	//要求球员的双位置是PF和SF
    	if (pb.getPosition()[0] != EPlayerPosition.PF && pb.getPosition()[0] != EPlayerPosition.SF) {
			return;
		}
    	
    	if (pb.getPosition()[1] != EPlayerPosition.PF && pb.getPosition()[1] != EPlayerPosition.SF) {
			return;
		}
    	
    	// 被删除球员的位置是默认的初始位置则返回,不用处理,新获得的球员不会问题
    	if (delPlayer.getPlayerPosition() == pb.getPosition()[0]) {
			return;
		}
    	
    	//判断技能数据是否存在
    	PlayerSkill ps = skillManager.getTeamSkill(tid).getPlayerSkill(delPlayer.getPlayerRid());
    	if (ps == null) {
			return;
		}
    	
    	//step=1的技能
    	SkillTree skillTree = ps.getSkillTree(1);
    	int maxLevel = skillTree.getMaxSkillLevel();
    	//最大等级 == 0不用处理
    	if (maxLevel == 0) {
			return;
		}
    	
    	// step=1的技能等级交换位置PF[x1,x2,0,0] -> SF[0,0,x1,x2]
    	int s1 = skillTree.getS1(), s2 = skillTree.getS2(), s3 = skillTree.getS3(), s4 = skillTree.getS4();
    	skillTree.updateLevel(0, s3);
    	skillTree.updateLevel(1, s4);
    	skillTree.updateLevel(2, s1);
    	skillTree.updateLevel(3, s2);
    	
    	// step=2的技能
    	skillTree = ps.getSkillTree(2);
    	maxLevel = skillTree.getMaxSkillLevel();
    	//最大等级 == 0不用处理
    	if (maxLevel == 0) {
    		ps.setAttack(0);
    		ps.setDefend(0);
    		ps.save();
    		return;
		}
    	
    	// step=2的技能等级交换位置PF[x1,x2,0,x3] -> SF[x1,0,x2,x3]
    	if (skillTree.getS2() == 0 && skillTree.getS3() == 0) {
    		ps.setAttack(0);
    		ps.setDefend(0);
    		ps.save();
			return;
		}
    	
    	int s = 0;
    	if (delPlayer.getPlayerPosition() == EPlayerPosition.PF) {
    		s = skillTree.getS2();
			skillTree.updateLevel(1, 0);
			skillTree.updateLevel(2, s);
		}else {
			s = skillTree.getS3();
			skillTree.updateLevel(1, s);
			skillTree.updateLevel(2, 0);
		}
    	
    	ps.setAttack(0);
		ps.setDefend(0);
    	ps.save();
    	
    }

    // -------------------------------------------------------------------------------

    @Override
    public void offline(long teamId) {
        teamPlayerMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamPlayerMap.remove(teamId);
    }
}
