package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.TradeConsole;
import com.ftkj.db.ao.logic.ITradeAO;
import com.ftkj.db.domain.TradePO;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.chat.ChatOfflineInfo;
import com.ftkj.manager.logic.log.GamePlayerLogManager;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamDaily;
import com.ftkj.manager.trade.OrderTrade;
import com.ftkj.manager.trade.Trade;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.proto.TradePB;
import com.ftkj.proto.TradePB.HisTradeData;
import com.ftkj.proto.TradePB.TradeChatListData;
import com.ftkj.proto.TradePB.TradeChatMsg;
import com.ftkj.proto.TradePB.TradeData;
import com.ftkj.proto.TradePB.TradeHelpData;
import com.ftkj.proto.TradePB.TradeListData;
import com.ftkj.proto.TradePB.TranData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 自由买卖
 *
 * @author Jay
 * @time:2017年7月5日 下午2:52:47
 */
public class TradeManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(TradeManager.class);
    @IOC
    private ITradeAO tradeAO;
    @IOC
    private TeamManager teamManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private BeSignManager beSignManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private TeamEmailManager teamEmailManager;
    @IOC
    private ChatManager chatManager;
    @IOC
    private VipManager vipManager;
    @IOC
    private StarletManager starletManager;

    // 上架球员：tradeID:list
    private Map<Integer, Trade> marketPlayers;
    // 球队正在交易的球员 teamid ，跟marketPlayers同步走
    private Map<Long, List<Trade>> teamTradeListMap;
    //近30天成就记录 playerId:list
    private Map<Integer, LinkedList<Trade>> historyQueryMap;
    // 玩家近30天的交易记录 teamId:list
    private Map<Long, LinkedList<Trade>> teamTranHistoryMap;
    // 本服交易编号序列
    private AtomicInteger seq;

    private PlayerBean helpPlayer;
    //private PlayerTalent helpTalent;

    // 即将下架的交易ID列表，每30分钟更新一次即可;
    private Set<Integer> pastList;

    // 交易留言信息
    private static List<ChatOfflineInfo> tradeChatList;

    @Override
    public void instanceAfter() {
        // map实例化
        marketPlayers = Maps.newConcurrentMap();
        historyQueryMap = Maps.newConcurrentMap();
        teamTranHistoryMap = Maps.newConcurrentMap();
        teamTradeListMap = Maps.newConcurrentMap();
        tradeChatList = this.getTradeChatInfos();

        // 初始id
        Integer maxId = tradeAO.queryMaxId();
        seq = new AtomicInteger(maxId == null ? 0 : maxId);
        // 查询
        List<TradePO> tradeList = tradeAO.queryTradeList();
        marketPlayers = tradeList.stream().collect(Collectors.toMap(TradePO::getId, t -> new Trade(t)));
        initTeamTradeMap(tradeList);

        // 自动清理下架
        loadClear();
        PropSimple ps = DropConsole.getAndRoll(ConfigConsole.getIntVal(EConfigKey.Trade_Frist_Drop)).get(0);
        PropExtPlayerBean prop = PropConsole.getPlayerProp(ps.getPropId());
        helpPlayer = PlayerConsole.getPlayerBean(prop.getHeroId());
    }

    /**
     * 下线清理
     */
    @Override
    public void offline(long teamId) {
        teamTranHistoryMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        teamTranHistoryMap.remove(teamId);
    }

    private void initTeamTradeMap(List<TradePO> tradeList) {
        for (TradePO t : tradeList) {
            if (!teamTradeListMap.containsKey(t.getTeamId())) {
                teamTradeListMap.put(t.getTeamId(), Lists.newArrayList());
            }
            teamTradeListMap.get(t.getTeamId()).add(marketPlayers.get(t.getId()));
        }

        teamTradeListMap.forEach((tid, trades) -> {
            int stick = vipManager.getVipValue(tid, EBuffType.Trade_Sell_Stick);
            if (stick == 1) {
                trades.forEach(trade -> trade.setStickTop(true));
            }
        });
    }

    public List<Trade> getTeamTradeList(long teamId) {
        if (!teamTradeListMap.containsKey(teamId)) {
            return Lists.newArrayList();
        }
        return teamTradeListMap.get(teamId);
    }

    /**
     * 球队的上架交易列表
     *
     * @param teamId
     * @return
     */
    public List<TradeData> getTeamTradeDatas(long teamId) {
        List<TradeData> datalist = Lists.newArrayList();
        for (Trade t : getTeamTradeList(teamId)) {
            datalist.add(getTradeData(t));
        }
        return datalist;
    }

    /** 获取交易留言信息列表 */
    public List<ChatOfflineInfo> getTradeChatInfos() {
        tradeChatList = redis.getList(RedisKey.Trade_Chat_Info);
        if (tradeChatList == null) {
            tradeChatList = Lists.newArrayList();
            redis.rpush(RedisKey.Trade_Chat_Info, tradeChatList);
        }

        return tradeChatList;
    }

    /**
     * 上架调用
     *
     * @param trade
     */
    private void addTradeToMarket(Trade trade) {
        marketPlayers.put(trade.getId(), trade);
        if (!teamTradeListMap.containsKey(trade.getTeamId())) {
            teamTradeListMap.put(trade.getTeamId(), Lists.newArrayList());
        }
        teamTradeListMap.get(trade.getTeamId()).add(trade);
    }

    /**
     * 找到本球员对应的交易
     *
     * @param teamId
     * @param pid
     * @return
     */
    public Trade getTeamPlayerTrade(long teamId, int pid) {
        if (!teamTradeListMap.containsKey(teamId)) {
            return null;
        }
        return teamTradeListMap.get(teamId).stream().filter(t -> t.getTeamId() == teamId && t.getPid() == pid).findFirst().orElse(null);
    }

    public long getPlayerTradeTime(Player player) {
        // 交易时间处理
        long endTime = 0L;
        if (player.getStorage() == EPlayerStorage.交易.getType()) {
            Trade t = getTeamPlayerTrade(player.getTeamId(), player.getId());
            endTime = t == null ? 0L : t.getEndTime().getMillis();
        }
        return endTime;
    }

    /**
     * 重启时，清理下架球员
     */
    private void loadClear() {
        refrushPast();
        clearOffTrade();
    }

    /**
     * 刷新即将清理的过期交易
     * 只保存5分钟以内
     */
    public void refrushPast() {
        try {
            Set<Integer> list = Sets.newHashSet();
            for (Trade t : marketPlayers.values()) {
                // 5分钟后过期
                //log.debug("已过期：{}, 即将过期：{}", t.getEndTime().isBeforeNow(), t.getEndTime().minusMinutes(TradeConsole.Check_Past_Min).isBeforeNow());
                if (t.getEndTime().isBeforeNow() || t.getEndTime().minusMinutes(TradeConsole.Check_Past_Min).isBeforeNow()) {
                    // 小于5分钟的列表
                    //log.debug("交易[{}]即将过期时间为：{}", t.getId(), t.getEndTime().toString("yyyy-MM-dd HH:mm:ss"));
                    list.add(t.getId());
                }
            }
            pastList = list;
            // debug 打印
            if (pastList.size() > 0) {
                log.debug("即将过期交易：{}", pastList.size());
            }
            for (int id : pastList) {
                log.debug("即将过期交易[{}]时间为：{}", id, DateTimeUtil.getString(marketPlayers.get(id).getEndTime()));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 定时清理过期下架交易
     * 每1分钟处理会合适点
     */
    public void clearOffTrade() {
        try {
            if (pastList == null || pastList.size() == 0) { return; }
            Set<Integer> delSet = Sets.newHashSet();
            for (int tradeId : pastList) {
                if (!marketPlayers.containsKey(tradeId)) {
                    log.debug("清理过期球员失败，没找到交易[{}]", tradeId);
                    delSet.add(tradeId);
                    continue;
                }
                Trade trade = marketPlayers.get(tradeId);
                // 跳过还没过期
                if (trade.getEndTime().isAfterNow()) {
                    continue;
                }
                log.info("清理过期球员[{}], 状态[{}], 时间过期[{}]", tradeId, trade.getStatus(), trade.getEndTime().isBeforeNow());
                delSet.add(tradeId);
                //
                trade.setStatus(1);
                trade.save();
                // 邮件通知
                teamEmailManager.sendEmail(trade.getTeamId(), 40001, PlayerConsole.getPlayerBean(trade.getPlayerId()).getName(), "");
                playerManager.getTeamPlayer(trade.getTeamId()).marketToStorage(trade.getPid());
                removeFromMarket(trade.getId());
                sendMessage(trade.getTeamId(), getTradeData(trade), ServiceCode.TradeManager_push_Trade);
            }
            pastList.removeAll(delSet);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 过滤底薪球员
     *
     * @param p
     * @param isLowFilter
     * @return
     */
    public boolean lowPricePlayer(int playerId, int price, boolean isLowFilter) {
        if (isLowFilter) {
            // 过滤底薪球员：
            return playerManager.isMinPricePlayer(playerId, price);
        }
        return true;
    }

    public boolean playerFilter(String playerName, int playerId) {
        if (playerName.equals("")) {
            return true;
        }
        return PlayerConsole.getPlayerBean(playerId).getName().contains(playerName);
    }

    public boolean posFilter(int posId, String pos) {
        if (posId == 0) { return true; }
        return EPlayerPosition.valueOf(pos).getId() == posId;
    }

    public boolean gradeFilter(String grade, int playerId) {
        if (grade.equals("")) { return true; }
        return PlayerConsole.getPlayerBean(playerId).getGrade().getGrade().contains(grade);
    }

    /**
     * 球员关键字
     *
     * @param playerName 球员名字，模糊查询
     * @param grade      球员等级，0 是所有
     * @param pos        球员位置， 0是所有
     * @param moneyOrder 球券排序 同上
     * @param priceOrder 工资排序，0是默认， 1，升序 2，降
     * @param page       显示分页
     *                   位置：1~5分别是PG,SG,SF,PF,C
     *                   注意：1页最大能显示16个球员
     */
    @ClientMethod(code = ServiceCode.TradeManager_QueryTradeList)
    public void queryTradeList(String playerName, String grade, int pos, int moneyOrder, int priceOrder, int page) {
        if (page < 0) {
            page = 0;
        }
        if (pos < 0 || pos > 5) {
            pos = 0;
        }
        // 过滤球员
        Collection<Trade> list = Lists.newArrayList();
        list.addAll(marketPlayers.values());
        final int posId = pos;
        final long teamId = getTeamId();
        // 过滤
        list = list.stream()
            .parallel()
            .filter(p -> playerFilter(playerName, p.getPlayerId()))
            .filter(p -> gradeFilter(grade, p.getPlayerId()))
            .filter(p -> posFilter(posId, p.getPosition()))
            .filter(p -> p.getStatus() == 0)
            .filter(p -> p.getEndTime().isAfterNow())
            .filter(p -> lowPricePlayer(p.getPlayerId(), p.getPrice(), priceOrder == 3))
            .collect(Collectors.toList());
        final int size = list.size();
        // 排序
        list = list.stream()
            .sorted(getTradeComparator(teamId, moneyOrder, priceOrder))
            .skip(page * TradeConsole.Query_Page_Size)
            .limit(TradeConsole.Query_Page_Size)
            .collect(Collectors.toList());
        // 返回列表
        taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.自由交易.getName());
        sendMessage(getTradeListData(getBuyMaxMoneyWeek(teamId), size, list));
    }

    Comparator<OrderTrade> getTradeComparator(long teamId, int moneyOrder, int priceOrder) {
        return (o1, o2) -> {
            long order1 = o1.getTeamId() == teamId ? 0 : o1.getTeamId();
            long order2 = o2.getTeamId() == teamId ? 0 : o2.getTeamId();
            int ret = 0;
            if (order1 == 0 || order2 == 0) {
                ret = Long.compare(order1, order2);
            }

            if (ret == 0 && (o1.isStickTop() || o2.isStickTop())) {// vip 置顶功能
                int stick1 = o1.isStickTop() ? 0 : 1;
                int stick2 = o2.isStickTop() ? 0 : 1;
                ret = Integer.compare(stick1, stick2);
            }

            if (ret == 0 && moneyOrder > 0) {
                if (moneyOrder == 1) {
                    ret = o1.getMoney() - o2.getMoney();
                } else if (moneyOrder == 2) {
                    ret = o2.getMoney() - o1.getMoney();
                }
            }
            if (ret == 0 && priceOrder > 0) {
                if (priceOrder == 1) {
                    ret = o1.getPrice() - o2.getPrice();
                } else if (priceOrder == 2) {
                    ret = o2.getPrice() - o1.getPrice();
                }
            }
            if (ret == 0) {
                ret = Long.compare(o2.getEndTimeMillis(), o1.getEndTimeMillis());
            }
            return ret;
        };
    }

    private TradeListData getTradeListData(int buyMaxMoney, int size, Collection<Trade> list) {
        List<TradeData> datalist = Lists.newArrayList();
        for (Trade t : list) {
            datalist.add(getTradeData(t));
        }
        return TradeListData.newBuilder()
            .setBuyMaxMoney(buyMaxMoney)
            .setSize(size)
            .addAllTradeList(datalist)
            .build();
    }

    private TradeData getTradeData(Trade trade) {
        return TradeData.newBuilder()
            .setId(trade.getId())
            .setTeamId(trade.getTeamId())
            .setTeamName(teamManager.getTeamNameById(trade.getTeamId()))
            .setPid(trade.getPid())
            .setPlayerId(trade.getPlayerId())
            .setPrice(trade.getPrice())
            .setMoney(trade.getMoney())
            .setStatus(trade.getStatus())
            .setEndTime(trade.getEndTime().getMillis())
            .setPosition(EPlayerPosition.valueOf(trade.getPosition()).getId())
            .setTalent(PlayerManager.getPlayerTalentData(PlayerTalent.createPlayerTalent(trade.getTalentScore())))
            .setSticky(trade.isStickTop())
            .build();
    }

    private TradePB.HisTradeData getHistTradeData(Trade trade) {
        return HisTradeData.newBuilder()
            .setPlayerId(trade.getPlayerId())
            .setPrice(trade.getPrice())
            .setMoney(trade.getMoney())
            .setMarketPrice(trade.getMarketPrice())
            .setDealTime(trade.getDealTime().getMillis())
            .setPosition(EPlayerPosition.valueOf(trade.getPosition()).getId())
            .build();
    }

    /**
     * 查询我的买卖记录，最大30
     */
    @ClientMethod(code = ServiceCode.TradeManager_MyTranList)
    public void myTranList() {
        long teamId = getTeamId();
        if (!teamTranHistoryMap.containsKey(teamId)) {
            List<TradePO> tradeList = tradeAO.queryTeamHistoryList(teamId);
            List<Trade> list = tradeList.stream().map(t -> new Trade(t)).collect(Collectors.toList());
            teamTranHistoryMap.put(teamId, Lists.newLinkedList(list));
        }
        TradePB.TranListData data = getTranListData(teamId, teamTranHistoryMap.get(teamId));
        sendMessage(data);
    }

    private TradePB.TranListData getTranListData(long teamId, List<Trade> list) {
        List<TradePB.TranData> datalist = Lists.newArrayList();
        if (list != null && list.size() > 0) {
            for (Trade t : list) {
                //  1购买 2，出售
                int type = t.getTeamId() == teamId ? 2 : 1;
                long dealTeam = type == 2 ? t.getBuyTeam() : t.getTeamId();
                datalist.add(TranData.newBuilder()
                    .setDealTime(t.getDealTime().getMillis())
                    .setMoney(t.getMoney())
                    .setPlayerId(t.getPlayerId())
                    .setPrice(t.getPrice())
                    .setType(type)
                    .setTeamId(dealTeam)
                    .setPosition(EPlayerPosition.valueOf(t.getPosition()).getId())
                    .setTeamName(teamManager.getTeamNameById(dealTeam))
                    .build());
            }
        }
        return TradePB.TranListData.newBuilder().addAllTranList(datalist).build();
    }

    /**
     * 查询指定球员的交易记录
     *
     * @param playerId
     */
    @ClientMethod(code = ServiceCode.TradeManager_PlayerTradeHist)
    public void playerTranQuery(int playerId) {
        if (!historyQueryMap.containsKey(playerId)) {
            List<TradePO> tradeList = tradeAO.queryPlayerHistoryList(playerId);
            List<Trade> list = tradeList.stream().map(t -> new Trade(t)).collect(Collectors.toList());
            historyQueryMap.put(playerId, Lists.newLinkedList(list));
        }
        List<Trade> list = historyQueryMap.get(playerId);
        List<TradePB.HisTradeData> datalist = Lists.newArrayList();
        if (list != null) {
            list = list.stream().limit(30).collect(Collectors.toList());
            for (Trade t : list) {
                datalist.add(getHistTradeData(t));
            }
        }
        sendMessage(TradePB.HisTradeListData.newBuilder().addAllHistoryList(datalist).build());
    }

    /**
     * 上架
     *
     * @param pid   球员唯一ID
     * @param money 出售价格
     * @param day   上架天数 1~3
     */
    @ClientMethod(code = ServiceCode.TradeManager_SellPlayer)
    public void sellPlayer(int pid, int money, int day) {
        ErrorCode ret = sellPlayer0(pid, money, day);
        sendMsg(ret);
    }

    private ErrorCode sellPlayer0(int pid, int money, int day) {
        long teamId = getTeamId();
        if (getTodaySellAndTradeNum(teamId) >= TradeConsole.Trade_Max_EveryDay) {
            log.debug("每天最多可出售{}个球员", TradeConsole.Trade_Max_EveryDay);
            return ErrorCode.trade_0;
        }
        // 校验参数
        //		if(!playerManager.getTeamPlayer(teamId).existStoragePlayer(pid)) {
        //			log.debug("球队{}球员ID不存在：{}", teamId, pid);
        //			sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
        //			return;
        //		}
        if (money < 50 || money > 999999) {
            log.debug("出售球券{}参数异常", money);
            return ErrorCode.ParamError;
        }
        if (day < 1 || day > 7) {
            log.debug("上架时间{}参数异常", day);
            return ErrorCode.ParamError;
        }
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(TradeConsole.Trade_Prop_ID, day)) {
            log.debug("道具{}数量不足{}", TradeConsole.Trade_Prop_ID, day);
            return ErrorCode.Prop_0;
        }
        // 创建交易
        TeamPlayer teamPlayer = playerManager.getTeamPlayer(teamId);
        Player player = teamPlayer.getStoragePlayer(pid);
        if (player == null || player.getStorage() != EPlayerStorage.仓库.getType()) {
            log.warn("球队{}仓库中不存在球员ID：{}", teamId, pid);
            return ErrorCode.Player_Null;
        }
        if (player.isBind()) {
            return ErrorCode.Player_Bind;
        }
        // X球员，不能交易
        if (PlayerConsole.getPlayerBean(player.getPlayerRid()).getGrade() == EPlayerGrade.X) {
            log.debug("X球员[]不能交易", player.getPlayerRid());
            return ErrorCode.Player_3;
        }
        
        if (player.getStorage() == EPlayerStorage.训练馆.getType()) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Train_Player_Training.code).build());
            return ErrorCode.Train_Player_Training;
        }
        
        // 扣道具
        propManager.delProp(teamId, TradeConsole.Trade_Prop_ID, day, true, false);
        synchronized (player) {
            teamPlayer.storageToMarket(player.getId());
            Trade tp = new Trade(new TradePO(seq.incrementAndGet(), teamId, pid, player.getPlayerRid(),
                player.getPosition(), player.getPrice(), money, day, player.getPlayerTalent().getTalentScore()));
            tp.save();
            addTradeToMarket(tp);
            sendMessage(tp.getTeamId(), getTradeData(tp), ServiceCode.TradeManager_push_Trade);
            taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.自由交易.getName() + "," + 2);
            int stick = vipManager.getVipValue(teamId, EBuffType.Trade_Sell_Stick);
            if (stick == 1) {
                tp.setStickTop(true);
            }
        }
        return ErrorCode.Success;
    }

    @ClientMethod(code = ServiceCode.TradeManager_getHelpPlayer)
    public void getHelpPlayer() {
        long teamId = getTeamId();
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        PlayerTalent pt = PlayerTalent.createPlayerTalent(teamId, helpPlayer.getPlayerRid(), tp.getNewTalentId(), PlayerManager._initDrop, true);
        tp.putPlayerTalent(pt);
        redis.set(RedisKey.getKey(teamId, RedisKey.Help_Trade_Talent), pt, RedisKey.DAY7);
        taskManager.updateTask(teamId, ETaskCondition.点击界面, 1, EModuleCode.自由交易.getName());
        sendMessage(TradeHelpData.newBuilder().setPlayerId(helpPlayer.getPlayerRid())
            .setTalent(PlayerManager.getPlayerTalentData(pt)).build());
        log.debug("trade gethelppr. tid {} prid {} pt {}", teamId, helpPlayer.getPlayerRid(), pt);
    }

    /**
     * 新手购买交易球员
     */
    @ClientMethod(code = ServiceCode.TradeManager_buyHelpPlayer)
    public void buyHelpPlayer() {
        int prid = helpPlayer.getPlayerRid();
        long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        if (team.getHelp().indexOf("l=12_230000") < 0) {
            log.error("新手引导不对,{}", team.getHelp());
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        PlayerBean pb = PlayerConsole.getPlayerBean(prid);
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        PlayerTalent talent = redis.getObj(RedisKey.getKey(teamId, RedisKey.Help_Trade_Talent));
        if (talent == null || talent.getPlayerId() != prid) {
            talent = PlayerTalent.createPlayerTalent(teamId, helpPlayer.getPlayerRid(), tp.getNewTalentId(), PlayerManager._buyHelpPlayerDeop, true);
        }
        talent.save();
        tp.putPlayerTalent(talent);
        int storageSize = playerManager.getStorageSize(teamId);
        log.debug("trade buyhelppr. tid {} prid {} ptid {} ssize {}", teamId, pb.getPlayerRid(), talent.getId(), storageSize);
        if (storageSize > 1) {
            Player player = tp.createPlayer(pb, helpPlayer.getPrice(), EPlayerPosition.NULL.name(), talent, true);
            playerManager.addPlayerToStore(teamId, tp, player, pb, ModuleLog.getModuleLog(EModuleCode.新手引导, "新手引导"));
            log.debug("trade buyhelppr. tid {} prid {} pid {} pt {}", teamId, player.getPlayerRid(), player.getId(), talent);
        } else {
            beSignManager.addBeSignPlayer(teamId, prid, helpPlayer.getPrice(), talent.getId(), true, ModuleLog.getModuleLog(EModuleCode.新手引导, "新手引导"));
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 购买/下架
     *
     * @param tradeId 交易ID
     */
    @ClientMethod(code = ServiceCode.TradeManager_BuyPlayer)
    public synchronized void buyPlayer(int tradeId) {
        //购买人
        long buyTeamId = getTeamId();
        //
        if (!marketPlayers.containsKey(tradeId)) {
            // 前台自行变 灰色
            log.debug("{}该球员已下架", tradeId);
            sendMessage(DefaultData.newBuilder().setCode(ErrorCode.trade_1.code).setBigNum(1).build());
            return;
        }
        // 仓库位置
        Trade trade = marketPlayers.get(tradeId);
        if (buyTeamId != trade.getTeamId()) {
            if (playerManager.getStorageSize(buyTeamId) < 1) {
                log.debug("{}仓库位置已满", tradeId);
                sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Player_Storage_Full.code).build());
                return;
            }
            TeamMoney buyTeamMoney = moneyManager.getTeamMoney(buyTeamId);
            if (buyTeamMoney.getMoney() < trade.getMoney()) {
                log.debug("{}球券{}不足{}", buyTeamId, buyTeamMoney.getMoney(), trade.getMoney());
                sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Money_1.code).build());
                return;
            }
            if (getBuyMaxMoneyWeek(buyTeamId) < trade.getMoney()) {
                log.debug("本周剩余购买额度{}不足{}");
                sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Prop_4.code).build());
                return;
            }
        }
        synchronized (trade) {
            if (trade == null || trade.getEndTime().isBeforeNow() || trade.getStatus() > 0) {
                log.debug("{}该球员已下架", tradeId);
                sendMessage(DefaultData.newBuilder().setCode(ErrorCode.trade_1.code).setBigNum(1).build());
                sendMessage(buyTeamId, getTradeData(trade), ServiceCode.TradeManager_push_Trade);
                return;
            }
            //
            trade.setDealTime(DateTime.now());
            // 当前市场价格
            trade.setMarketPrice(PlayerConsole.getPlayerBean(trade.getPlayerId()).getPrice());
            //
            if (trade.getTeamId() == buyTeamId) {
                // 自行购买，赎回到仓库
                trade.setStatus(1);
                playerManager.getTeamPlayer(trade.getTeamId()).marketToStorage(trade.getPid());
            } else {
                setTodaySell(trade.getTeamId(), getTodaySell(trade.getTeamId()) + 1);
                setBuyUseMoney(buyTeamId, getBuyUseMoney(buyTeamId) + trade.getMoney());
                //
                trade.setStatus(2);
                trade.setBuyTeam(buyTeamId);
                //
                String playerName = PlayerConsole.getPlayerBean(trade.getPlayerId()).getName();
                moneyManager.updateTeamMoney(buyTeamId, 0 - trade.getMoney(), 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.自由交易, "购买-" + playerName));
                int sellMoney = (int) Math.floor(trade.getMoney() * (1 - getBuyRate(trade.getTeamId())));
                moneyManager.updateTeamMoney(trade.getTeamId(), sellMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.自由交易, "出售-" + playerName));
                //
                TeamPlayer sellTeamPlayer = playerManager.getTeamPlayer(trade.getTeamId());
                TeamPlayer buyTeamPlayer = playerManager.getTeamPlayer(buyTeamId);
                //
                Player player = sellTeamPlayer.delFromMarket(trade.getPid());
                
                //从交易市场删除球员清理新秀相关数据
                starletManager.deleteStarletTeamPlayer(sellTeamPlayer, player.getTeamId(), player.getPlayerRid());
                playerManager.switchPFAndSFPlayerExtendsSkillLevel(trade.getTeamId(), player);
                
                GamePlayerLogManager.Log(trade.getTeamId(), player.getId(), player.getPlayerRid(), player.getPrice(), 1, ModuleLog.getModuleLog(EModuleCode.自由交易, "出售球员"));
                PlayerTalent talent = new PlayerTalent();
                talent.setTeamId(buyTeamId);
                talent.setPlayerId(player.getPlayerRid());
                talent.setId(buyTeamPlayer.getNewTalentId());
                talent.refreshTalent(player.getPlayerTalent());
                talent.save();
                buyTeamPlayer.putPlayerTalent(talent);
                //
                PlayerBean playerBean = PlayerConsole.getPlayerBean(player.getPlayerRid());
                Player buyPlayer = buyTeamPlayer.createPlayer(playerBean, player.getPrice(), player.getLineupPosition().name(), talent);
                playerManager.addPlayerToStore(buyTeamId, buyTeamPlayer, buyPlayer, playerBean, ModuleLog.getModuleLog(EModuleCode.自由交易, "购买球员"));
                player.getPlayerTalent().del();
            }
            //
            trade.save();
            // 下架交易，推送下架
            removeFromMarket(trade.getId());
            TradeData tpData = getTradeData(trade);
            sendMessage(trade.getTeamId(), tpData, ServiceCode.TradeManager_push_Trade);
            // 交易变动通知，非下架
            if (buyTeamId != trade.getTeamId()) {
                taskManager.updateTask(buyTeamId, ETaskCondition.点击界面, 1, EModuleCode.自由交易.getName() + "," + 1);
            }
        }
        sendMessage(DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(trade.getStatus()).build());
    }

    @ClientMethod(code = ServiceCode.TradeManager_getTradeMsgList)
    public void getTradeChatList() {
        long teamId = getTeamId();
        TeamDaily teamDaily = teamManager.getTeamDaily(teamId);

        TradeChatListData.Builder builder = TradeChatListData.newBuilder();
        builder.setTradeChatState(teamDaily.getTradeChatLMState());

        if (tradeChatList == null) {
            sendMessage(builder.build());
            return;
        }

        tradeChatList.forEach(chatOfflineInfo -> builder.addTradeChat(builderTradeChatMsg(chatOfflineInfo)));
        sendMessage(builder.build());
    }

    private TradeChatMsg.Builder builderTradeChatMsg(ChatOfflineInfo chatOfflineInfo) {
        Team team = teamManager.getTeam(chatOfflineInfo.getTeamId());
        TradeChatMsg.Builder builder = TradeChatMsg.newBuilder();
        builder.setSendTeamId(team.getTeamId());
        builder.setSendTeamName(team.getName());
        builder.setSendTeamLevel(team.getLevel());
        builder.setSendTeamLogo(team.getLogo());
        builder.setContent(chatOfflineInfo.getContent());
        builder.setCerateTime(chatOfflineInfo.getCreateTime());
        return builder;
    }

    @ClientMethod(code = ServiceCode.TradeManager_levelMessage)
    public void tradeLevelMessage(String msg) {
        long teamId = getTeamId();

        if (msg.length() >= ConfigConsole.getGlobal().chatMsgWroldCountLimit) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_1.code).build());
            return;
        }

        msg = chatManager.repShieldText(msg);

        // 交易留言状态
        TeamDaily teamDaily = teamManager.getTeamDaily(teamId);
        if (teamDaily.getTradeChatLMState() == 1) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.trade_2.code).build());
            return;
        }
        teamDaily.setTradeChatLMState(1);
        teamDaily.save();

        ChatOfflineInfo chatOfflineInfo = null;
        if (tradeChatList.size() >= ConfigConsole.global().tradeMaxLevelMessage) {
            chatOfflineInfo = tradeChatList.remove(0);
        } else {
            chatOfflineInfo = new ChatOfflineInfo();
        }

        chatOfflineInfo.setTeamId(teamId);
        chatOfflineInfo.setContent(msg);
        chatOfflineInfo.setCreateTime(System.currentTimeMillis());
        tradeChatList.add(chatOfflineInfo);

        redis.rpush(RedisKey.Trade_Chat_Info, tradeChatList);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 购买费率
     *
     * @param teamId
     * @return 不可以是负数， 不可以大于基础费率
     */
    public float getBuyRate(long teamId) {
        float rate = TradeConsole.Trade_Money_Rate - buffManager.getBuffSet(teamId, EBuffType.交易手续费降低X).getValueSum() / 1000.0f;
        if (rate < 0) { rate = 0; } else if (rate > TradeConsole.Trade_Money_Rate) {
            rate = TradeConsole.Trade_Money_Rate;
        }
        return rate;
    }

    /**
     * 下架调动
     *
     * @param trade
     */
    private void removeFromMarket(int tradeId) {
        Trade trade = marketPlayers.remove(tradeId);
        //
        if (trade == null) {
            log.debug("下架球员时，球员为空吗{}", tradeId);
            return;
        }
        if (teamTradeListMap.containsKey(trade.getTeamId())) {
            List<Trade> list = teamTradeListMap.get(trade.getTeamId()).stream().filter(t -> t.getId() != trade.getId()).collect(Collectors.toList());
            teamTradeListMap.put(trade.getTeamId(), list);
        }
        // 历史
        addToHistory(trade);
    }

    /**
     * 添加历史交易记录
     *
     * @param trade
     */
    private void addToHistory(Trade trade) {
        // 球员相关的，交易才算，下架不算
        if (trade.getStatus() == 2 && historyQueryMap.containsKey(trade.getPlayerId())) {
            LinkedList<Trade> list = historyQueryMap.get(trade.getPlayerId());
            list.addFirst(trade);
            if (list.size() > 30) {
                list.removeLast();
            }
        }
        // 个人相关的
        addTeamTranHis(trade.getTeamId(), trade);
        addTeamTranHis(trade.getBuyTeam(), trade);
    }

    /*
     * 添加球队的交易历史
     */
    private void addTeamTranHis(long teamId, Trade trade) {
        if (!teamTranHistoryMap.containsKey(teamId)) {
            return;
        }
        LinkedList<Trade> list = teamTranHistoryMap.get(teamId);
        list.addFirst(trade);
        if (list.size() > 30) {
            list.removeLast();
        }
    }

    /**
     * 取今天出售数量 + 已上架数量
     *
     * @param teamId
     * @return
     */
    public int getTodaySellAndTradeNum(long teamId) {
        return getTodaySell(teamId) + getTeamTradeList(teamId).size();
    }

    /**
     * 今天出售数量
     *
     * @return
     */
    public int getTodaySell(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Trade_Max_Day_);
        int value = redis.getIntNullIsZero(key);
        return value;
    }

    /**
     * 保存今天出售数量
     *
     * @param teamId
     * @param num
     */
    public void setTodaySell(long teamId, int num) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Trade_Max_Day_);
        redis.set(key, "" + num, RedisKey.DAY);
    }

    /**
     * 本周剩余最大可购买额度
     * = 最大购买额度 - 已购买额度
     *
     * @return
     */
    public int getBuyMaxMoneyWeek(long teamId) {
        return TradeConsole.Week_Max_Buy_Money - getBuyUseMoney(teamId);
    }

    /**
     * 本周已购买额度
     *
     * @return
     */
    public int getBuyUseMoney(long teamId) {
        String key = RedisKey.getWeekKey(teamId, RedisKey.Trade_Week_Max_Buy_Money);
        return redis.getIntNullIsZero(key);
    }

    /**
     * 更新本周已购买额度
     *
     * @param money
     */
    public void setBuyUseMoney(long teamId, int money) {
      if(money > TradeConsole.Week_Max_Buy_Money) {
        money = TradeConsole.Week_Max_Buy_Money;
      }
      String key = RedisKey.getWeekKey(teamId, RedisKey.Trade_Week_Max_Buy_Money);
      redis.set(key, money + "", RedisKey.DAY7);
    }

    public static void main(String[] args) {
        // 过滤球员
        List<Long> list = Arrays.asList(31021000061450L,
            31021000058432L,
            31021000061008L,
            31021000061008L,
            31021000060080L,
            31021000060327L,
            31021000056881L,
            31021000060858L,
            31021000042196L);
        long teamId = 31021000056881L;
        List<Long> list2 = list.stream().sorted((o1, o2) -> {
            //            if (o1.equals(teamId)) {
            //                return -1;
            //            }
            //            return 0;
            long order1 = o1.equals(teamId) ? 0 : o1;
            long order2 = o2.equals(teamId) ? 0 : o2;
            return Long.compare(order1, order2);
        }).collect(Collectors.toList());
        System.out.println("list2 " + list2);
    }
}
