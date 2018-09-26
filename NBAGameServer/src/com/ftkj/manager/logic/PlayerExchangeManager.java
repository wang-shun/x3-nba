package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.DropConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PlayerExchangeConsole;
import com.ftkj.console.PlayerExchangeConsole.ERefreshType;
import com.ftkj.console.PropConsole;
import com.ftkj.db.ao.logic.IPlayerAO;
import com.ftkj.db.domain.PlayerExchangePO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerExchange;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.system.CheckAPI;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.ScoutPB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 球员兑换
 *
 * @author Jay
 * @time:2018年3月3日 下午4:01:26
 */
public class PlayerExchangeManager extends BaseManager implements OfflineOperation {

    @IOC
    private IPlayerAO playerAO;
    @IOC
    private PropManager propManager;
    @IOC
    private ScoutManager scoutManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamMoneyManager moneyManager;
    /**
     * 掉落列表
     */
    private static List<DropBean> dropPlayerList;

    /**
     * 最近几日兑换统计数据
     */
    private List<PlayerExchange> beforeExchangePlayer;
    private Map<Integer, PlayerExchange> todayPlayerExchangeMap;

    /**
     * 刷新出来的球员
     * 球队ID：球员列表
     */
    private static Map<Long, List<TempPlayer>> teamRollPlayerMap;

    @Override
    public void instanceAfter() {
        // 前段时间兑换的数据，公式计算需要
        List<PlayerExchangePO> exchangePOList = playerAO.getPlayerExchangeList(5);
        beforeExchangePlayer = exchangePOList.stream().map(s -> new PlayerExchange(s)).collect(Collectors.toList());
        // 今日兑换数据
        List<PlayerExchangePO> todayExchangePOList = playerAO.getPlayerExchangeListByDate(new Date());
        todayPlayerExchangeMap = todayExchangePOList.stream().map(s -> new PlayerExchange(s)).collect(Collectors.toMap(PlayerExchange::getPlayerId, (s) -> s));
        // 刷新出来的球员列表
        teamRollPlayerMap = Maps.newConcurrentMap();
        //
        EventBusManager.register(EEventType.任务调度每分钟, this);
    }

    @Override
    public void initConfig() {
        initDropPlayerBean();
    }

    private void initDropPlayerBean() {
        dropPlayerList = Arrays.stream(PlayerExchangeConsole.getDropsConfig()).map(d -> DropConsole.getDrop(Integer.valueOf(d))).collect(Collectors.toList());
    }

    /**
     * 每日定时刷新
     *
     * @param dateTime
     */
    @Subscribe
    private void everyDayRefreshJob(Date dateTime) {
        DateTime now = new DateTime(dateTime);
        if (!(now.getHourOfDay() == 16 && now.getMinuteOfHour() == 5)) {
            return;
        }
        // 16:05刷新
        log.debug("刷新所有球员兑换球员={}", DateTimeUtil.getStringSql(now));
        Set<Long> teamKeySet = Sets.newHashSet(teamRollPlayerMap.keySet());
        teamRollPlayerMap.clear();
        for (long teamKey : teamKeySet) {
            long teamId = teamKey - getRefreshTimeCfg();
            List<TempPlayer> playerList = getTeamRollPlayer(teamId);
            teamRollPlayerMap.put(teamKey, playerList);
            sendMessage(teamKey, getExchangePlayerMainData(playerList), ServiceCode.ExchangePlayerManager_pushlist);
        }
    }

    /**
     * 每天小时刷新
     *
     * @return
     */
    private long getRefreshTimeCfg() {
        DateTime hitTime = DateTime.now().withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        return hitTime.getMillis();
    }
    //	private long getRefreshTimeCfg() {
    //		DateTime hitTime = DateTime.now().withHourOfDay(16).withMinuteOfHour(4).withSecondOfMinute(59).withMillisOfSecond(59);
    //		DateTime now = DateTime.now();
    //		if(now.isAfter(hitTime)) {
    //			return hitTime.plusDays(1).getMillis();
    //		}
    //		return hitTime.getMillis();
    //	}

    /**
     * teamId加上刷新秒数，作为每天刷新的KEY
     *
     * @param teamId
     * @return
     */
    private long getTodayTeamKey(long teamId) {
        return teamId + getRefreshTimeCfg();
    }

    /**
     * 取今天球员的兑换数
     *
     * @param playerId
     * @return
     */
    private PlayerExchange getTodayPlayerExchange(int playerId) {
        if (!todayPlayerExchangeMap.containsKey(playerId)) {
            PlayerExchange exchange = new PlayerExchange(new PlayerExchangePO(playerId, DateTime.now()));
            todayPlayerExchangeMap.put(playerId, exchange);
            return exchange;
        }
        return todayPlayerExchangeMap.get(playerId);
    }

    /**
     * 刷新，给所有在线的人推送
     */
    private List<TempPlayer> refresh(long teamId, ERefreshType type) {
        List<TempPlayer> playerList = Lists.newArrayList();
        for (DropBean rollBean : dropPlayerList) {
            PropSimple ps = rollBean.roll().get(0);
            PropExtPlayerBean prop = PropConsole.getPlayerProp(ps.getPropId());
            if (prop == null) {
                continue;
            }
            PlayerBean pb = PlayerConsole.getPlayerBean(prop.getHeroId());
            // 有特殊约束，请看球探模块
            TeamPlayer tp = playerManager.getTeamPlayer(teamId);
            TempPlayer t = new TempPlayer(pb.getPlayerRid(), pb.getPrice(), type);
            t.setBind(prop.isBind());
            PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, pb.getPlayerRid(), tp.getNewTalentId(), PlayerManager._initDrop, false);
            t.setTalent(talent);
            // 计算需要的数量
            int dropRate = PlayerExchangeConsole.advancedLowPriceDrop.roll().get(0).getNum();
            t.setNeedNum(getPlayerTodayNeedNum(type, pb.getPlayerRid(), pb.getPrice(), dropRate));
            if (type == ERefreshType.高级刷新) {
                // 球员身价下降0%~3%
                t.setNowPrice((int) Math.ceil(t.getBasePrice() * (1 - dropRate / 1000.0)));
            } else {
                t.setNowPrice(t.getBasePrice());
            }
            playerList.add(t);
        }
        return playerList;
    }

    /**
     * 高级刷新身价下降
     *
     * @return
     */
    private int getAdvancedPrice(int basePrice) {
        //		t.setNowPrice((int)Math.floor(t.getBasePrice() * (1-RandomUtil.randInt(4)/100.0)));

        return basePrice;
    }

    /**
     * 取玩家刷新出来的球员列表
     *
     * @param teamId
     * @return
     */
    private List<TempPlayer> getTeamRollPlayer(long teamId) {
        long teamKey = getTodayTeamKey(teamId);
        String key = RedisKey.getDayKey(teamKey, RedisKey.PlayerExchange_RollList + teamId + "_");
        if (!teamRollPlayerMap.containsKey(key)) {
            // 看看是否redis有数据
            List<TempPlayer> list = redis.getList(key + "");
            if (list == null || list.size() == 0) {
                list = refresh(teamId, ERefreshType.普通刷新);
                redis.rpush(key + "", list, RedisKey.DAY);
            } else {
                TeamPlayer tp = playerManager.getTeamPlayer(teamId);
                for (TempPlayer t : list) {
                    t.getTalent().setId(tp.getNewTalentId());
                }
            }
            teamRollPlayerMap.put(teamKey, list);
            return list;
        }
        return teamRollPlayerMap.get(key);
    }
    
    @ClientMethod(code = ServiceCode.ExchangePlayerManager_testRefresh)
    public void testRefresh() {
    	Map<Integer, AtomicInteger> map = Maps.newHashMap();
    	 for (DropBean rollBean : dropPlayerList) {
             PropSimple ps = rollBean.roll().get(0);
             PropExtPlayerBean prop = PropConsole.getPlayerProp(ps.getPropId());
             if (prop == null) {
                 continue;
             }
             PlayerBean pb = PlayerConsole.getPlayerBean(prop.getHeroId());
             log.warn("商店刷新球员: roll {}, {}", rollBean.getDropId(), pb.getPlayerRid());
             if(map.containsKey(pb.getPlayerRid())) {
            	 map.get(pb.getPlayerRid()).incrementAndGet();
             }else {
            	 map.put(pb.getPlayerRid(), new AtomicInteger(1));
             }
    	 }
    	 for(int playerId : map.keySet()) {
    		 log.warn("playerId {}, num {}", playerId, map.get(playerId).get());
    	 }
    }

    /**
     * 主动刷新
     */
    @ClientMethod(code = ServiceCode.ExchangePlayerManager_refresh)
    public void teamRefresh(int type) {
        long teamId = getTeamId();
        if (type < 1 || type > 2) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        TeamProp teamProp = propManager.getTeamProp(teamId);
        TeamMoney teamMoney = moneyManager.getTeamMoney(teamId);
        // 消耗 //checkTeamPropNum
        List<PropSimple> needProp = Lists.newArrayList(PlayerExchangeConsole.getRefreshPropNeed(type));
        if (!CheckAPI.checkTeamPropNum(needProp, teamProp, teamMoney)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        //
        propManager.usePropOrMoney(teamId, needProp, true, ModuleLog.getModuleLog(EModuleCode.球员兑换, "刷新"));
        // 刷新
        List<TempPlayer> playerList = refresh(teamId, ERefreshType.values()[type - 1]);
        long teamKey = getTodayTeamKey(teamId);
        String key = RedisKey.getDayKey(teamKey, RedisKey.PlayerExchange_RollList + teamId + "_");
        redis.rpush(key, playerList, RedisKey.DAY);
        //
        teamRollPlayerMap.put(teamKey, playerList);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        sendMessage(teamId, getExchangePlayerMainData(playerList), ServiceCode.ExchangePlayerManager_pushlist);
    }

    /**
     * 显示可兑换球员
     */
    @ClientMethod(code = ServiceCode.ExchangePlayerManager_showView)
    public void showView() {
        long teamId = getTeamId();
        List<TempPlayer> list = getTeamRollPlayer(teamId);
        sendMessage(getExchangePlayerMainData(list));
    }

    private ScoutPB.ExchangePlayerMain getExchangePlayerMainData(List<TempPlayer> list) {
        List<ScoutPB.ExchangePlayerData> dataList = list.stream().map(s -> getExchangePlayerData(s)).collect(Collectors.toList());
        return ScoutPB.ExchangePlayerMain.newBuilder().addAllPlayerList(dataList).build();
    }

    private ScoutPB.ExchangePlayerData getExchangePlayerData(TempPlayer player) {
        return ScoutPB.ExchangePlayerData.newBuilder()
            .setPlayerId(player.playerId)
            .setBasePrice(player.basePrice)
            .setNowPrice(player.nowPrice)
            .setType(player.type.getType())
            .setNeedNum(player.needNum)
            .setSign(player.sign)
            .setTalent(PlayerManager.getPlayerTalentData(player.talent))
            .build();
    }

    /**
     * 签约
     *
     * @param index
     * @param playerId 为了验证ID已经发生改变
     */
    @ClientMethod(code = ServiceCode.ExchangePlayerManager_signPlayer)
    public void signPlayer(int index, int playerId) {
        if (index < 0 || index >= PlayerExchangeConsole.VIEW_PLAYER_NUM) { return; }
        long teamId = getTeamId();
        List<TempPlayer> teamRollPlayerList = getTeamRollPlayer(teamId);
        TempPlayer player = teamRollPlayerList.get(index);
        if (player.getPlayerId() != playerId) {
            // 不能签约，球员不存在
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        if (player.isSign()) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;
        }
        int needPropId = PlayerExchangeConsole.getPropIdByType(player.getType().getType());
        TeamProp teamProp = propManager.getTeamProp(teamId);
        //
        if (!teamProp.checkPropNum(needPropId, player.getNeedNum())) {
            // 道具不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
            return;
        }
        // 位置是否足够
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        tp.putPlayerTalent(player.getTalent());
        ErrorCode StatusCode = scoutManager.signPlayer(teamId, player.getPlayerId(), player.getNowPrice(),
            player.getTalent().getId(), player.isBind(), ModuleLog.getModuleLog(EModuleCode.球员兑换, ""));
        if (StatusCode == ErrorCode.Success) {
            propManager.delProp(teamId, needPropId, player.getNeedNum(), true, true);
            player.setSign(true);
            //
            player.getTalent().save();
            long teamKey = getTodayTeamKey(teamId);
            String key = RedisKey.getDayKey(teamKey, RedisKey.PlayerExchange_RollList + teamId + "_");
            redis.rpush(key, teamRollPlayerList, RedisKey.DAY);
            // 统计签约数量
            PlayerExchange pe = getTodayPlayerExchange(player.getPlayerId());
            pe.addExchangeNum(1);
            pe.save();
        } else {
            tp.removePlayerTalent(player.getTalent().getId());
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(StatusCode.code).build());
    }

    /**
     * 计算当天需要的兑换数量
     *
     * @param type
     * @param basePrice
     * @param playerId
     * @return
     */
    private int getPlayerTodayNeedNum(ERefreshType type, int playerId, int basePrice, int dropRate) {
        if (type == ERefreshType.高级刷新) {
            int base = PlayerExchangeConsole.getCashNum(PlayerConsole.getPlayerBean(playerId).getGrade().getGrade());
            return (int) Math.ceil(base * (1 + (100.0 / 30.0) * dropRate / 100.0));
        }
        return PlayerExchangeConsole.getCashNum(PlayerConsole.getPlayerBean(playerId).getGrade().getGrade());
    }

    /**
     * 刷新出来的球员列表
     *
     * @author Jay
     * @time:2018年3月5日 上午10:37:07
     */
    public static class TempPlayer implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private int playerId;
        private int basePrice;
        private int nowPrice;
        private PlayerTalent talent;
        private ERefreshType type;
        // 签约消耗数量
        private int needNum;
        private boolean sign;
        /** 是否绑定 */
        private boolean bind;

        public TempPlayer(int playerId, int basePrice, ERefreshType type) {
            super();
            this.playerId = playerId;
            this.basePrice = basePrice;
            this.type = type;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public int getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(int basePrice) {
            this.basePrice = basePrice;
        }

        public int getNowPrice() {
            return nowPrice;
        }

        public void setNowPrice(int nowPrice) {
            this.nowPrice = nowPrice;
        }

        public ERefreshType getType() {
            return type;
        }

        public void setType(ERefreshType type) {
            this.type = type;
        }

        public int getNeedNum() {
            return needNum;
        }

        public void setNeedNum(int needNum) {
            this.needNum = needNum;
        }

        public boolean isSign() {
            return sign;
        }

        public void setSign(boolean sign) {
            this.sign = sign;
        }

        public PlayerTalent getTalent() {
            return talent;
        }

        public void setTalent(PlayerTalent talent) {
            this.talent = talent;
        }

        public boolean isBind() {
            return bind;
        }

        public void setBind(boolean bind) {
            this.bind = bind;
        }
    }

    @Override
    public void offline(long teamId) {
        teamRollPlayerMap.remove(getTodayTeamKey(teamId));
    }

    @Override
    public void dataGC(long teamId) {
        teamRollPlayerMap.remove(getTodayTeamKey(teamId));
    }
}
