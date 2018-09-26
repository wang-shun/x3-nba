package com.ftkj.manager.logic;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.TradeConsole;
import com.ftkj.db.ao.logic.ITradeAO;
import com.ftkj.db.domain.TradeP2PPO;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPlayerStorage;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.TeamProp;
import com.ftkj.manager.trade.TradeP2P;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.proto.TradeP2PPB.MyBuyingTradeData;
import com.ftkj.proto.TradeP2PPB.TradeP2PData;
import com.ftkj.proto.TradeP2PPB.TradeP2PMainData;
import com.ftkj.proto.TradePB;
import com.ftkj.proto.TradePB.HisTradeData;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 求购模块
 *
 * @author Jay
 * @time:2018年6月19日 下午2:53:52
 */
public class TradeP2PManager extends BaseManager {
    @IOC
    private ITradeAO tradeAO;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamMoneyManager moneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TradeManager tradeManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamEmailManager emailManager;
    @IOC
    private VipManager vipManager;
    /**
     * 交易记录playerId : 列表
     */
    private Map<Integer, LinkedList<TradeP2P>> historyQueryMap = Maps.newConcurrentMap();
    //
    private AtomicInteger seq;
    private Map<Integer, TradeP2P> teamBuyingMap = Maps.newConcurrentMap();

    private ModuleLog buyLog = ModuleLog.getModuleLog(EModuleCode.自由交易, "求购");
    private ModuleLog sellLog = ModuleLog.getModuleLog(EModuleCode.自由交易, "求购出售");

    @Override
    public void instanceAfter() {
        Integer maxId = tradeAO.queryP2PMaxId();
        seq = new AtomicInteger(maxId == null ? 0 : maxId);
        // 查询
        List<TradeP2PPO> tradeList = tradeAO.queryTradeP2PList();
        for (TradeP2PPO t : tradeList) {
            int stick = vipManager.getVipValue(t.getTeamId(), EBuffType.Trade_Sell_Stick);
            if (stick == 1) {
                t.setStickTop(true);
            }
            teamBuyingMap.put(t.getId(), new TradeP2P(t));
        }
    }

    /**
     * 发布求购
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_addBuying)
    public void addBuying(int playerId, int maxPrice, int minTalent, int buyMoney, int day) {
        long teamId = getTeamId();
        PlayerBean pb = PlayerConsole.getPlayerBean(playerId);
        if (pb == null) {
            sendErrorCode(ErrorCode.Player_Null);
            return;
        }
        if (day < 1 || day > 3) {
            sendErrorCode(ErrorCode.ParamError);
            return;
        }
        if (buyMoney < 1) {
            sendErrorCode(ErrorCode.ParamError);
            return;
        }
        if (maxPrice < 1) {
            sendErrorCode(ErrorCode.ParamError);
            return;
        }
        if (minTalent < 0) {
            sendErrorCode(ErrorCode.ParamError);
            return;
        }
        if (PlayerConsole.existCreateXPlayer(playerId)) {
            sendErrorCode(ErrorCode.ParamError);
            return;
        }
        if(tradeManager.getBuyMaxMoneyWeek(teamId) < buyMoney) {
          log.debug("本周剩余购买额度{}不足{}");
          sendErrorCode(ErrorCode.Prop_4);
          return;
        }
        TeamProp teamProp = propManager.getTeamProp(teamId);
        if (!teamProp.checkPropNum(TradeConsole.Trade_Prop_ID, day)) {
            log.debug("道具{}数量不足{}", TradeConsole.Trade_Prop_ID, day);
            sendErrorCode(ErrorCode.Prop_0);
            return;
        }
        TeamMoney tm = moneyManager.getTeamMoney(teamId);
        if (!tm.hasMoney(EMoneyType.Money, buyMoney)) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        // 占仓库位置
        if (playerManager.getStorageSize(teamId) < 1) {
            sendErrorCode(ErrorCode.Player_Storage_Full);
            return;
        }
        // 扣掉
        propManager.delProp(teamId, TradeConsole.Trade_Prop_ID, day, true, false);
        moneyManager.updateTeamMoneyUnCheck(tm, 0 - Math.abs(buyMoney), 0, 0, 0, true, buyLog);
        //
        TradeP2P trade = new TradeP2P(new TradeP2PPO(seq.incrementAndGet(), teamId, playerId, pb.getPosition()[0].name(), maxPrice, buyMoney, day, minTalent));
        trade.save();
        int stick = vipManager.getVipValue(teamId, EBuffType.Trade_Sell_Stick);
        if (stick == 1) {
            trade.setStickTop(true);
        }
        teamBuyingMap.put(trade.getId(), trade);
        //
        sendMessage(teamId, getTradeP2PData(trade), ServiceCode.TradeP2PManager_buying_push);
        sendErrorCode(ErrorCode.Success);
    }

    private TradeP2PData getTradeP2PData(TradeP2P p2p) {
        return TradeP2PData.newBuilder()
            .setTeamId(p2p.getTeamId())
            .setEndTime(p2p.getEndTimeMillis())
            .setId(p2p.getId())
            .setMaxPrice(p2p.getPrice())
            .setMinTalent(p2p.getTalent())
            .setMoney(p2p.getMoney())
            .setPlayerId(p2p.getPlayerId())
            .setPosition(EPlayerPosition.valueOf(p2p.getPosition()).getId())
            .setTeamName(teamManager.getTeamName(p2p.getTeamId()))
            .setStatus(p2p.getStatus())
            .setSticky(p2p.isStickTop())
            .build();
    }

    /**
     * 求购中的大小
     */
    int getTeamBuyingSize(long teamId) {
        return (int) teamBuyingMap.values().stream()
            .filter(t -> t.getTeamId() == teamId)
            .filter(t -> t.getStatus() == 0).count();
    }

    /**
     * 求购列表
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_trade_list)
    public void queryBuyingList(String playerName, String grade, int pos, int moneyOrder, int priceOrder, int page) {
        if (page < 0) { page = 0; }
        if (pos < 0 || pos > 5) { pos = 0; }
        // 过滤球员
        //        Collection<TradeP2P> list = Lists.newArrayList(teamBuyingMap.values());
        final int posId = pos;
        // 过滤
        long teamId = getTeamId();
        Collection<TradeP2P> list = teamBuyingMap.values().stream()
            .filter(p -> tradeManager.playerFilter(playerName, p.getPlayerId()))
            .filter(p -> tradeManager.gradeFilter(grade, p.getPlayerId()))
            .filter(p -> tradeManager.posFilter(posId, p.getPosition()))
            .filter(p -> p.getStatus() == 0)
            .filter(p -> p.getEndTime().isAfterNow())
            .sorted(tradeManager.getTradeComparator(teamId, moneyOrder, priceOrder))
            //            .skip(page * TradeConsole.Query_Page_Size)
            //            .limit(TradeConsole.Query_Page_Size)
            .collect(Collectors.toList());
        sendMessage(getTradeP2PListData(list));
    }

    /**
     * 交易协议列表
     */
    private TradeP2PMainData getTradeP2PListData(Collection<TradeP2P> list) {
        List<TradeP2PData> datalist = Lists.newArrayList();
        list.forEach(t -> datalist.add(getTradeP2PData(t)));
        return TradeP2PMainData.newBuilder()
            .addAllTradeList(datalist)
            .build();
    }

    /**
     * 我的求购列表
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_myBuying)
    public void getMyBuyingListData() {
        long teamId = getTeamId();
        List<TradeP2P> list = teamBuyingMap.values().stream()
            .filter(t -> t.getTeamId() == teamId)
            .filter(t -> t.getStatus() == 0)
            .collect(Collectors.toList());
        List<TradeP2PData> datalist = Lists.newArrayList();
        list.forEach(t -> datalist.add(getTradeP2PData(t)));
        sendMessage(MyBuyingTradeData.newBuilder()
            .addAllTradeList(datalist)
            .build());
    }

    /**
     * 每5分钟清理过期
     */
    public void clearTimeOut(DateTime now) {
        // 移除map，改变状态，邮件返回
        List<TradeP2P> timeOutList = Lists.newArrayList();
        timeOutList.addAll(teamBuyingMap.values().stream()
            .filter(t -> t.getStatus() == 0 && t.getEndTime().isBeforeNow())
            .collect(Collectors.toList()));
        //
        for (TradeP2P trade : timeOutList) {
            synchronized (trade) {
                teamBuyingMap.remove(trade.getId());
                trade.setStatus(1);
                trade.save();
                //
                sendMessage(trade.getTeamId(), getTradeP2PData(trade), ServiceCode.TradeP2PManager_buying_push);
                // 邮件
                send40009(trade.getTeamId(), trade);
            }
        }
    }

    /**
     * 出售
     *
     * @param id  交易id
     * @param pid 球员唯一id
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_sellPlayer)
    public void sellPlayer(int id, int pid) {
        TradeP2P trade = teamBuyingMap.get(id);
        if (trade == null || trade.getEndTime().isBeforeNow()) {
            // 交易不存在或者已过期
            sendErrorCode(ErrorCode.trade_1);
            return;
        }
        long teamId = getTeamId();
        // 球员是否满足
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        Player player = tp.getStoragePlayer(pid);
        if (player == null) {
            sendErrorCode(ErrorCode.Player_Null);
            return;
        }
        // X 不能出售
        if (PlayerConsole.existCreateXPlayer(player.getPlayerRid())) {
            sendErrorCode(ErrorCode.Player_Null);
            return;
        }
        if (player.getPlayerRid() != trade.getPlayerId()) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        if (player.getPrice() > trade.getPrice()) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        if (player.isBind()) {
            sendErrorCode(ErrorCode.Player_Bind);
            return;
        }
        // 天赋算法：攻防*天赋  /  攻防*满天赋
        if (player.getPlayerTalent().getTalentValue() < trade.getTalent()) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        if (trade.getTeamId() == teamId) {
            sendErrorCode(ErrorCode.Error);
            return;
        }

        if (player.getStorage() == EPlayerStorage.训练馆.getType()) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Train_Player_Training.code).build());
            return;
        }
        
        long qiugouTeamId = trade.getTeamId();
        synchronized (trade) {
            if (trade.getStatus() != 0) {
                sendErrorCode(ErrorCode.Error);
                return;
            }
            trade.setStatus(2);
            trade.setBuyTeam(teamId);
            trade.setDealTime(DateTime.now());
            trade.setMarketPrice(PlayerConsole.getPlayerBean(trade.getPlayerId()).getPrice());
            trade.save();
            tradeManager.setBuyUseMoney(qiugouTeamId, tradeManager.getBuyUseMoney(qiugouTeamId) + trade.getMoney());
            // 得到球券
            int sellMoney = (int) Math.floor(trade.getMoney() * (1 - tradeManager.getBuyRate(trade.getTeamId())));
           
            // 删除球员
            Player p = playerManager.removePlayerFromStorage(teamId, pid, sellLog);
            if(p == null) {
                log.debug("求购 delPlayer fail teamId{}, pid{}", teamId, pid);
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_10.code).build());
            }
         
            TeamPlayer buyTeamPlayer = playerManager.getTeamPlayer(trade.getTeamId());
            PlayerTalent talent = new PlayerTalent();
            talent.setTeamId(trade.getTeamId());
            talent.setPlayerId(p.getPlayerRid());
            talent.setId(buyTeamPlayer.getNewTalentId());
            talent.refreshTalent(p.getPlayerTalent());
            talent.save();
            buyTeamPlayer.putPlayerTalent(talent);
            //
            PlayerBean playerBean = PlayerConsole.getPlayerBean(p.getPlayerRid());
            Player buyPlayer = buyTeamPlayer.createPlayer(playerBean, p.getPrice(), EPlayerPosition.NULL.name(), talent);
            playerManager.addPlayerToStore(trade.getTeamId(), buyTeamPlayer, buyPlayer, playerBean, ModuleLog.getModuleLog(EModuleCode.自由交易, "求购球员"));
            p.getPlayerTalent().del();
            //
            sendMessage(trade.getTeamId(), getTradeP2PData(trade), ServiceCode.TradeP2PManager_buying_push);
            sendMessage(teamId, getTradeP2PData(trade), ServiceCode.TradeP2PManager_buying_push);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(pid).build());
            // 求购成功
            emailManager.sendEmail(trade.getTeamId(), 40008, playerBean.getName() + "," + teamManager.getTeamNameById(teamId) + "," + trade.getMoney(), "");
            // 求购出售成功
            emailManager.sendEmail(teamId, 40010, playerBean.getName() + "," + teamManager.getTeamNameById(trade.getTeamId()) + "," + trade.getMoney() + "," + (trade.getMoney() - sellMoney), "4004:" + sellMoney);
            //
            addToHistory(trade);
        }
    }

    /**
     * 下架，取消求购
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_delBuying)
    public void delBuying(int id) {
        //
        TradeP2P trade = teamBuyingMap.get(id);
        if (trade == null) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        long teamId = getTeamId();
        if (teamId != trade.getTeamId()) {
            sendErrorCode(ErrorCode.Error);
            return;
        }
        //
        synchronized (trade) {
            if (trade.getStatus() != 0) {
                sendErrorCode(ErrorCode.Error);
                return;
            }
            teamBuyingMap.remove(id);
            trade.setStatus(1);
            trade.save();
            //
            sendMessage(teamId, getTradeP2PData(trade), ServiceCode.TradeP2PManager_buying_push);
            send40009(teamId, trade);
        }
        sendErrorCode(ErrorCode.Success);
    }

    /**
     * 下架邮件
     *
     * @param teamId
     * @param trade
     */
    private void send40009(long teamId, TradeP2P trade) {
        emailManager.sendEmail(teamId, 40009, trade.getPrice() + "," + trade.getTalent() + "," + PlayerConsole.getPlayerBean(trade.getPlayerId()).getName() + "," + trade.getMoney(), "4004:" + trade.getMoney());
    }

    /**
     * 查询指定球员的交易记录
     *
     * @param playerId
     */
    @ClientMethod(code = ServiceCode.TradeP2PManager_sellHistory)
    public void playerTranQuery(int playerId) {
        if (!historyQueryMap.containsKey(playerId)) {
            List<TradeP2PPO> tradeList = tradeAO.queryPlayerP2PHistoryList(playerId);
            List<TradeP2P> list = tradeList.stream().map(t -> new TradeP2P(t)).collect(Collectors.toList());
            historyQueryMap.put(playerId, Lists.newLinkedList(list));
        }
        List<TradeP2P> list = historyQueryMap.get(playerId);
        List<TradePB.HisTradeData> datalist = Lists.newArrayList();
        if (list != null) {
            list = list.stream().limit(30).collect(Collectors.toList());
            for (TradeP2P t : list) {
                datalist.add(getHistTradeData(t));
            }
        }
        sendMessage(TradePB.HisTradeListData.newBuilder().addAllHistoryList(datalist).build());
    }

    private TradePB.HisTradeData getHistTradeData(TradeP2P trade) {
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
     * 添加历史交易记录
     *
     * @param trade
     */
    private void addToHistory(TradeP2P trade) {
        // 球员相关的，交易才算，下架不算
        if (trade.getStatus() == 2 && historyQueryMap.containsKey(trade.getPlayerId())) {
            LinkedList<TradeP2P> list = historyQueryMap.get(trade.getPlayerId());
            list.addFirst(trade);
            if (list.size() > 30) {
                list.removeLast();
            }
        }
    }

}
