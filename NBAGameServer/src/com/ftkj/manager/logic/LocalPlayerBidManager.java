package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.bid.PlayerBidEndSource;
import com.ftkj.manager.bid.PlayerBidGuessMain;
import com.ftkj.manager.bid.PlayerBidGuessSimple;
import com.ftkj.manager.bid.TeamGuess;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerBidPB;
import com.ftkj.proto.PlayerBidPB.PlayerBidBaseResp;
import com.ftkj.proto.PlayerBidPB.PlayerBidBeforeDetailMainData;
import com.ftkj.proto.PlayerBidPB.PlayerBidInfoResp;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.ftkj.util.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

/**
 * 球员竞价本地
 *
 * @author tim.huang 2018年3月20日
 */
public class LocalPlayerBidManager extends AbstractBaseManager {
    private static final Logger log = LoggerFactory.getLogger(LocalPlayerBidManager.class);
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamEmailManager teamEmailManager;   

    private Map<Long, TeamGuess> teamGuessMaps;  
    private PlayerBidPB.PlayerBidBeforeMainData beforeMainData;
    private List<PlayerBidPB.PlayerBidMainDetailData> mainDetailDatas;

    private PlayerBidGuessMain guessMain;

    private int _levelupMoney1;
    private int _levelupMoney2;
    private int _levelupMoney3;

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_Info)
    public void info() {
        PlayerBidInfoResp.Builder resp = PlayerBidInfoResp.newBuilder();
        resp.setBase(baseResp());
        sendMessage(resp.build());
    }

    private PlayerBidBaseResp baseResp() {
        long mid = DateTimeUtil.midnight();
        return PlayerBidBaseResp.newBuilder()
                .setStartTime(mid + ConfigConsole.global().newStartBidStartTime)
                .setEndTime(mid + ConfigConsole.global().newStartBidEndTime)
                .build();
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_showPlayerBidGuessBeforeMain)
    public void showPlayerBidGuessBeforeMain() {
        long teamId = getTeamId();
        if (beforeMainData == null) {
            reloadBeforeMain();
        }
        List<PlayerBidPB.PlayerBidBeforeDetailClockData> dataList = Lists.newArrayList();
        TeamGuess guess = getTeamGuess(teamId);
        guess.getSbMap().forEach((key, val) -> dataList.add(getPlayerBidBeforeDetailClockData(key, val)));
        sendMessage(PlayerBidBeforeDetailMainData.newBuilder().setMainData(beforeMainData)
                .addAllClockList(dataList).build());
    }

    private PlayerBidPB.PlayerBidBeforeDetailClockData getPlayerBidBeforeDetailClockData(int id, String val) {
        return PlayerBidPB.PlayerBidBeforeDetailClockData.newBuilder().setClock(val).setId(id).build();
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_showPlayerBidGuessMain)
    public void showPlayerBidGuessMain() {
        long teamId = getTeamId();
        TeamGuess tg = getTeamGuess(teamId);

        sendMessage(PlayerBidPB.PlayerBidMainData.newBuilder()
                .setEndSecond(getEndSecond())
                .setMyId(tg.getId())
                .addAllDetailList(mainDetailDatas)
                .build());
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_showPlayerBidGuessPlayerMain)
    public void showPlayerBidGuessPlayerMain(int id) {
        long teamId = getTeamId();
        TeamGuess tg = getTeamGuess(teamId);
        PlayerBidGuessSimple guess = this.guessMain.getGuessMaps().get(id);
        List<PlayerBidPB.PlayerBidTeamInfoData> logList = Lists.newArrayList();      
        guess.getTopGuess().forEach(team -> logList.add(getPlayerBidTeamInfoData(team)));
        sendMessage(PlayerBidPB.PlayerBigGuessMainData.newBuilder()
                .setEndSecond(getEndSecond())
                .setId(id)
                .setMaxPrice(guess.getMaxPrice())
                .setMyPrice(tg.getPrice())
                .setTotalPeople(guess.getPeople())
                .addAllTeamInfoList(logList)
                .setStatus(((tg.getPrice() > 0 && tg.getId() == id) || tg.getPrice() <= 0) ? 0 : 1)
                .build());
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_bidPlayer)
    public void bidPlayer(int id, int price) {
        ErrorCode ret = bidPlayer0(id, price);
        if (ret.isError()) {
            sendMsg(ret);
        }
    }

    private ErrorCode bidPlayer0(int id, int price) {
        long teamId = getTeamId();
        TeamGuess tg = getTeamGuess(teamId);
        if (tg.getPrice() >= price) { //出价不能比上一轮出价低
            return ErrorCode.PrBid_Price;
        }
        if (tg.getId() != id && tg.getPrice() > 0) {//每轮只能选一个，不可修改
            return ErrorCode.PrBid_Priced;
        }
        int startTime = ConfigConsole.global().newStartBidStartTime;
        if (System.currentTimeMillis() < DateTimeUtil.midnight() + startTime) {//还未开启，无法竞价
            return ErrorCode.PrBid_Start;
        }
        int oldPrice = tg.getPrice();
        TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
        int needPrice = price - oldPrice;
        if (needPrice <= 0) {//出价异常，比上一轮价格低
            return ErrorCode.PrBid_Price_Lower;
        }
        if (!teamMoneyManager.checkTeamMoney(tm, 0, needPrice, 0, 0)) {
            return ErrorCode.Money_Common;
        }
        tg.updatePrice(id, price);
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.PlayerBidManager_bidPlayer, null, tid, tg);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            if (code == ErrorCode.Success) {
                teamMoneyManager.updateTeamMoneyUnCheck(tm, 0, -needPrice, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.高中新星, "竞价"));
                saveTeamGuess(teamId, tg);
            } else {
                tg.updatePrice(id, oldPrice);
            }
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code.code).build(), ServiceCode.LocalPlayerBidManager_bidPlayer);
        }).start();
        return ErrorCode.Success;
    }

    /**
     * 竞价训练馆界面
     */
    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_showPlayerBidTrainMain)
    public void showPlayerBidTrainMain() {
//        long teamId = getTeamId();
//        PlayerTrainInfo info = getPlayerTrainInfo(teamId);
//        PlayerBidPB.PlayerBidTrainMainData trainData = PlayerBidPB.PlayerBidTrainMainData.newBuilder()
//                .setEndSecond(info.getSecond())
//                .setGrade(info.getGrade().ordinal())
//                .setMaxGrade(info.getMaxGrade().ordinal())
//                .setStatus(info.getStatus())
//                .setGroup(info.getGroup())
//                .setPosition(info.getPosition())
//                .build();
//        sendMessage(trainData);
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_levelUpPlayer)
    public void levelUpPlayer() {
        long teamId = getTeamId();
//        PlayerTrainInfo info = getPlayerTrainInfo(teamId);
//        if (info.getSecond() > 0) {
//            log.debug("不在可训练的时间段");
//            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).setMsg("" + info.getGrade().ordinal()).build());
//            return;
//        }
//
//        if (info.isMax()) {
//            log.debug("已经达到最等级");
//            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).setMsg("" + info.getGrade().ordinal()).build());
//            return;
//        }
//
//        int needMoney = getLevelUpMoney(info.getGrade());
//        if (!teamMoneyManager.updateTeamMoney(teamId, -needMoney, 0, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.高中新星, "训练"))) {
//            log.debug("球卷不足");
//            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).setMsg("" + info.getGrade().ordinal()).build());
//            return;
//        }
//        info.levelUpGrade();
//        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg("" + info.getGrade().ordinal()).build());
    }

    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_getBidPlayer)
    public void getBidPlayer() {
//        long teamId = getTeamId();
//        PlayerTrainInfo info = getPlayerTrainInfo(teamId);
//        if (info.getSecond() > 0) {
//            log.debug("训练还未结束");
//            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
//            return;
//        }
//        PlayerBean pb = PlayerConsole.getRanPlayerByPosition(EPlayerPosition.getEPlayerPosition(info.getPosition()), info.getGrade(), info.getMaxGrade());
//        if (pb == null) {//范围内没有，再roll一次
//            pb = PlayerConsole.getRanPlayerByPosition(EPlayerPosition.getEPlayerPosition(info.getPosition())
//                    , info.getGrade().ordinal() - 1 < 0 ? info.getGrade() : EPlayerGrade.values()[info.getGrade().ordinal() - 1],
//                    info.getMaxGrade().ordinal() + 1 >= EPlayerGrade.values().length ? info.getMaxGrade() : EPlayerGrade.values()[info.getMaxGrade().ordinal() + 1]);
//        }
//        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
//        PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, pb.getPlayerRid(), tp.getNewTid(), PlayerManager._initDrop, false);
//        ErrorCode code = playerManager.addPlayeAuto(teamId, pb.getPlayerRid(), pb.getPrice(), talent, ModuleLog.getModuleLog(EModuleCode.高中新星, ""));
//        if (code == ErrorCode.Success) {
//            info.endBid();
//            talent.save();
//            tp.putPlayerTalent(talent);
//        }
//        //		playerManager.addPlayer(teamId, tp, pb.getPlayerId(), pb.getPrice(),talent , true, EModuleCode.高中新星);
//        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(code.code).setMsg("" + pb.getPlayerRid()).build());
    }

    @Deprecated
    @ClientMethod(code = ServiceCode.LocalPlayerBidManager_startTrainPlayer)
    public void startTrainPlayer() {
//        long teamId = getTeamId();
//        PlayerTrainInfo info = getPlayerTrainInfo(teamId);
//        info.open();
//        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(info.getSecond()).build());
    }

    private int getLevelUpMoney(EPlayerGrade cur) {
        if (cur == EPlayerGrade.D1) {//D-
            return this._levelupMoney1;
        } else if (cur == EPlayerGrade.B1) {//B-
            return this._levelupMoney2;
        } else if (cur == EPlayerGrade.A1) {//A-
            return this._levelupMoney3;
        } else {//S-
            return Integer.MAX_VALUE;
        }
    }
    
    private PlayerBidPB.PlayerBidTeamInfoData getPlayerBidTeamInfoData(TeamGuess guess) {
        return PlayerBidPB.PlayerBidTeamInfoData.newBuilder().setNode(guess.getNode())
                .setTeamId(guess.getTeamId())
                .setTeamName(guess.getTeamName())
                .build();

    }

    /**
     * 取结束剩余秒数
     */
    private int getEndSecond() {
        int endTime = ConfigConsole.global().newStartBidEndTime;
        long millis = (DateTimeUtil.midnight() + endTime) - System.currentTimeMillis();
        if (millis < 0) {
            return 99999;
        }
        return (int) millis / 1000;
    }

    private TeamGuess getTeamGuess(long teamId) {
        TeamGuess team = this.teamGuessMaps.get(teamId);
        if (team == null) {
            team = redis.getObj(RedisKey.getDayKey(teamId, RedisKey.Player_Bid_Guess));
            if (team == null) {
                String name = teamManager.getTeamName(teamId);
                team = new TeamGuess(teamId, name, getRanSBMap());
                saveTeamGuess(teamId, team);
            }
            this.teamGuessMaps.put(teamId, team);
        }
        return team;
    }

    private Map<Integer, String> getRanSBMap() {
        Map<Integer, String> result = Maps.newHashMap();
        Map<Integer, PlayerBidGuessSimple> guessMaps = guessMain.getGuessMaps();
        if (guessMaps == null) {
            return result;
        }
        guessMaps.values().forEach(detail -> result.put(detail.getId(), getRanSBString()));
        return result;
    }

    private String getRanSBString() {
        int[] ss = new int[]{1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0};
        List<Integer> aa = Arrays.stream(ss).boxed().collect(Collectors.toList());
        Collections.shuffle(aa);
        return aa.stream().map(b -> "" + b).collect(Collectors.joining(","));
    }

    private void saveTeamGuess(long teamId, TeamGuess guess) {
        redis.set(RedisKey.getDayKey(teamId, RedisKey.Player_Bid_Guess), guess, RedisKey.DAY);
    }

    @RPCMethod(code = CrossCode.LocalPlayerBidManagger_endBid, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void endBid(PlayerBidEndSource endSource) {
        log.error("球员竞价开始发奖");
        guessMain.getGuessMaps().values().forEach(simple -> sendGift(simple, endSource));
        Set<Long> result = endSource.getTeams().values().stream().reduce(Sets.newHashSet(), (init, val) -> {
            init.addAll(val);
            return init;
        });
        teamGuessMaps.values().stream()
                .filter(team -> team.getId() != -1 && team.getPrice() > 0)
                .peek(team -> log.error("默认球队有:{}", team.getTeamId()))
                .filter(team -> !result.contains(team.getTeamId()))
                .peek(team -> log.error("失败返回球队：{}->{}", team.getTeamId(), team.getPrice()))
                .forEach(team -> teamEmailManager.sendEmail(team.getTeamId(), 40004, "", "4001:" + team.getPrice()));
        log.error("球员竞价结束发奖");
    }

    private void sendGift(PlayerBidGuessSimple simple, PlayerBidEndSource endSource) {
//        List<PlayerTrainInfo> teams = endSource.getTeams(simple.getId()).stream().map(teamId -> getPlayerTrainInfo(teamId)).collect(Collectors.toList());
//        PlayerBean bean = PlayerConsole.getPlayerBean(simple.getPlayerId());
//
//        teams.forEach(info -> {
//            info.updatePlayer(bean.getGrade(), bean.getPosition()[0].getId(), simple.getGroup());
//            teamEmailManager.sendEmail(info.getTeamId(), 40003, "", "");
//            log.error("成功返回球队:{}={}", info.getTeamId(), simple.getId());
//        });
    }

    @RPCMethod(code = CrossCode.localplayerbidmanagger_updateguessmain, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void updateGuessMain(PlayerBidGuessMain data) {
        log.debug("刷新竞价界面数据");
        this.guessMain = data;
    }

    /**
     * 高中新星的招募信息加载
     */
    @RPCMethod(code = CrossCode.LocalPlayerBidManagger_reloadBeforeMain, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void reloadBeforeMain() {
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.PlayerBidManager_showBidBeforeMain, null, tid);
        }).appendTask((tid, maps, args) -> {
            this.beforeMainData = (PlayerBidPB.PlayerBidBeforeMainData) args[0];
            //							log.debug("加载最新的竞价联赛数据{}",this.beforeMainData);
            mainDetailDatas = Lists.newArrayList();
            this.beforeMainData.getBidPlayerListList().forEach(player -> mainDetailDatas.add(PlayerBidPB.PlayerBidMainDetailData
                    .newBuilder()
                    .setId(player.getId())
                    .setPlayerGroup(player.getPlayerGroup())
                    .setPlayerPosition(player.getPlayerPosition())
                    .build()
            ));
            this.teamGuessMaps = Maps.newConcurrentMap();
        }).start();
    }

    @Override
    public void initConfig() {
        _levelupMoney1 = ConfigConsole.getIntVal(EConfigKey.Training_1);
        _levelupMoney2 = ConfigConsole.getIntVal(EConfigKey.Training_2);
        _levelupMoney3 = ConfigConsole.getIntVal(EConfigKey.Training_3);
    }

    @Override
    public void instanceAfter() {
        EventBusManager.register(EEventType.服务器节点注册, this);      
        teamGuessMaps = Maps.newConcurrentMap();
        reloadBeforeMain();
        reloadPlayerBidBeforeData();
    }

    /**
     * 节点注册回调
     * EventBusManager.register(EEventType.服务器节点注册, this);
     */
    @Subscribe
    private void nodeRegisterCall(RPCServer server) {
        // 若果是逻辑节点收到PK节点的注册信息，则检查是否拉取BattleId
        if (EServerNode.Cross.equals(server.getPool()) && GameSource.pool.equals(EServerNode.Logic)) {
            log.warn("游戏服{}收到Corss节点注册, 初始高中新星和竞价数据", GameSource.serverName);
            if (beforeMainData == null) {
                reloadBeforeMain();
            }
            if (guessMain == null) {
                reloadPlayerBidBeforeData();
            }
        }
    }

    /**
     * 加载最新的竞价联赛数据
     */
    private void reloadPlayerBidBeforeData() {
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.PlayerBidManager_initPlayerBidBeforeData, null, tid);
        }).appendTask((tid, maps, args) -> {
            this.guessMain = (PlayerBidGuessMain) args[0];
            // FIXME 有时候重启完没有数据，先跟进一下，稳定了再改成info打印
            log.warn("加载最新的竞价联赛数据{}", this.guessMain);
        }).start();
    }

}
