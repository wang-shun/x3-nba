package com.ftkj.manager.pk;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.battle.BattleBean;
import com.ftkj.console.BattleConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.manager.battle.BattleHandle;
import com.ftkj.manager.battle.BattleManager;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.BattleRoundReport;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.handle.BattleTeamCustom;
import com.ftkj.manager.battle.model.BattleAttribute;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.custom.CustomGuessResult;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.custom.guess.ICustomGuessBattle;
import com.ftkj.manager.pvp.common.BattleRoom;
import com.ftkj.manager.pvp.common.BattleRoomTeam;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.GameLogPB;
import com.ftkj.proto.PVPBattlePB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.IZKMaster;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.ftkj.util.RandomUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tim.huang
 * 2017年4月25日
 * 跨服即时PK赛管理
 */
public class CrossPVPManager extends BaseManager implements IZKMaster {
    private static final Logger log = LoggerFactory.getLogger(CrossPVPManager.class);
    @IOC
    private CrossBattleManager crossBattleManager;
    @IOC
    private NodeManager nodeManager;
    @IOC
    private BattleManager battleManager;

    private Map<Integer, BattleRoom> rooms;

    private Map<Long, Integer> teamRoomMap;

    private Map<Integer, Set<TeamNode>> matchMaps;

    private AtomicInteger roomIds;

    private List<TeamNode> npcList;

    @RPCMethod(code = CrossCode.CrossPVPManager_math, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public synchronized void match(TeamNode team, int group) {
        Set<TeamNode> matchList = matchMaps.computeIfAbsent(group, (k) -> Sets.newConcurrentHashSet());
        //		matchList.clear();
        if (matchList.contains(team)) {//已经在匹配队列
            RPCMessageManager.responseMessage(ErrorCode.Battle_In);
            return;
        }
        //将玩家放入匹配队列
        matchList.add(team);
        //将玩家放入匹配队列
        //		matchList.add(new TeamNode(NPCConsole.debug_getNPCId(),team.getNodeName()));
        RPCMessageManager.responseMessage(ErrorCode.Success);
    }

    @RPCMethod(code = CrossCode.CrossPVPManager_createRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void createRoom(BattleRoom room) {
        rooms.put(room.getRoomId(), room);
        teamRoomMap.put(room.getHome().getTeamId(), room.getRoomId());
        teamRoomMap.put(room.getAway().getTeamId(), room.getRoomId());
    }

    @RPCMethod(code = CrossCode.CrossPVPManager_againMatch, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void againMatch(TeamNode home, TeamNode away, int group) {
        Set<TeamNode> matchList = matchMaps.computeIfAbsent(group, (k) -> Sets.newConcurrentHashSet());
        if (home != null && !matchList.contains(home)) {//已经在匹配队列
            matchList.add(home);
        }
        if (away != null && !matchList.contains(away)) {//已经在匹配队列
            matchList.add(away);
        }
    }

    /**
     * 拉取房间列表数据
     *
     * @param teamId
     * @param group
     * @param num
     */
    @RPCMethod(code = CrossCode.CrossPVPManager_showRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void showRoom(long teamId, int group, int num) {
        int mroom = teamRoomMap.getOrDefault(teamId, -1);
        List<BattleRoom> rs = rooms.values().stream()
            .filter(rm -> rm.getStatus() == EBattleRoomStatus.比赛中)
            .filter(rm -> rm.getLevel() == group)
            .filter(rm -> rm.getRoomId() != mroom)
            .collect(Collectors.toList());
        PVPBattlePB.BattleRoomData myRoom = null;
        if (mroom != -1) {
            BattleRoom tmr = rooms.get(mroom);
            if (tmr != null && tmr.getLevel() == group && tmr.getStatus() == EBattleRoomStatus.比赛中) {
                myRoom = getBattleRoomData(tmr);
                num--;//数量减1，把自己房间放在第一位
            }
        }
        num = rs.size() < num ? rs.size() : num;

        ArrayList<PVPBattlePB.BattleRoomData> roomList = Lists.newArrayList();
        if (num > 0) {
            roomList.addAll(Stream.generate(() -> rs.get(RandomUtil.randInt(rs.size()))).distinct().limit(num).map((room) -> getBattleRoomData(room)).collect(Collectors.toList()));
        }

        if (myRoom != null) { roomList.add(0, myRoom); }
        //
        RPCMessageManager.responseMessage(roomList);
    }

    private PVPBattlePB.BattleRoomData getBattleRoomData(BattleRoom room) {

        return PVPBattlePB.BattleRoomData.newBuilder()
            .setAway(getBattleRoomTeamData(room.getAway()))
            .setHome(getBattleRoomTeamData(room.getHome()))
            .setBattleId(room.getBattleId())
            .setNode(nodeManager.getServerIP(room.getNodeName()))
            .setRoomId(room.getRoomId())
            .build();

    }

    private PVPBattlePB.BattleRoomTeamData getBattleRoomTeamData(BattleRoomTeam team) {

        return PVPBattlePB.BattleRoomTeamData.newBuilder()
            .setLeagueName(team.getLeagueName())
            .setLogo(team.getLogo())
            .setShardName(team.getTeamNodeName())
            .setTeamId(team.getTeamId())
            .setTeamName(team.getName())
            .setLevel(team.getLevel())
            .build();
    }

    //-----------------------------------------------------------------------------

    /**
     * 异步加载玩家数据，并且开启比赛。
     * 将最终比赛状态推送到本地逻辑服和比赛主服
     *
     * @param home
     * @param away
     */
    @RPCMethod(code = CrossCode.CrossPVPManager_loadTeamSource, pool = EServerNode.Battle, type = ERPCType.NONE)
    public void loadTeamSource(TeamNode home, TeamNode away, long battleId, int roomId, int group) {
        log.debug("准备拉取各个大区玩家数据,home-{}-{},away-{}-{}"
            , home.getNodeName(), home.getTeamId(), away.getNodeName(), away.getTeamId());
        if (GameSource.isNPC(home.getTeamId())) {
            home.setNodeName(away.getNodeName());
        } else if (GameSource.isNPC(away.getTeamId())) {
            away.setNodeName(home.getNodeName());
        }
        if (GameSource.isNPC(home.getTeamId()) && GameSource.isNPC(away.getTeamId())) {
            return;
        }
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {//拉取home球队比赛信息
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalBattleManager_getBattleTeam, home.getNodeName(), tid, home.getTeamId());
        }).appendTask((tid, maps, args) -> {//拉取away球队比赛信息
            if (args == null) {//标记出出局球队，调用下一步链式任务
                return;
            }

            BattleTeam btHome = args[0] == null ? null : (BattleTeam) args[0];
            maps.put("homeTeam", btHome);
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalBattleManager_getBattleTeam, away.getNodeName(), tid, away.getTeamId());
        }).appendTask((tid, maps, args) -> {//双方比赛球队信息验证，是否20:0
            if (args == null) {//标记出出局球队，调用下一步链式任务
                return;
            }
            BattleTeam bthome = maps.get("homeTeam");
            BattleTeam btAway = args[0] == null ? null : (BattleTeam) args[0];

            //开启比赛
            BattleHandle battle = getBattle(battleId, bthome, btAway);
            if (battle == null || battle.getBattleSource().getStage() == EBattleStage.Close) {//比赛开启失败，通知比赛主服务器，将这两名玩家重新放入队列
                RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_againMatch, null, home, away, group);
                return;
            }
            BattleAttribute ba = new BattleAttribute(0);
            ba.addVal(EBattleAttribute.Room_Id, roomId);
            battle.getBattleSource().addBattleAttribute(ba);
            //放入队列并开始运行比赛
            BattleAPI.getInstance().putBattle(battle);

            BattleRoom room = new BattleRoom(roomId, battleId, new BattleRoomTeam(bthome.getTeamId(), bthome.getName(), "",
                bthome.getLevel(), home.getNodeName(), bthome.getLogo()),
                new BattleRoomTeam(btAway.getTeamId(), btAway.getName(), "",
                    btAway.getLevel(), away.getNodeName(), btAway.getLogo()),
                EBattleRoomStatus.比赛中, group, GameSource.serverName);

            //告诉比赛主服务器，该玩家比赛开启。初始化房间信息,不需要等待返回
            RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_createRoom, null, room);
            //告诉逻辑服务器，该玩家匹配成功比赛开启，不需要等待返回
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_updateLocalBattleTeam,
                home.getNodeName(), home.getTeamId(), battleId, EBattleType.即时比赛跨服,
                home.getTeamId(), away.getTeamId(), GameSource.serverName);
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_updateLocalBattleTeam,
                away.getNodeName(), away.getTeamId(), battleId, EBattleType.即时比赛跨服,
                home.getTeamId(), away.getTeamId(), GameSource.serverName);
        }).start();

    }

    //----------------------------------------------------------------------------

    /**
     * 异步加载玩家数据，并且开启比赛。
     * 将最终比赛状态推送到本地逻辑服和比赛主服
     *
     * @param home
     * @param away
     */
    @RPCMethod(code = CrossCode.CrossPVPManager_loadCustomTeamSource, pool = EServerNode.Battle, type = ERPCType.NONE)
    public void loadCustomTeamSource(long battleId, CustomPVPRoom room) {

        RPCLinkedTask.build().appendTask((tid, maps, args) -> {//拉取home球队比赛信息
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalBattleManager_getBattleTeam, room.getHomeTeam().getNodeName(), tid, room.getHomeTeam().getTeamId());
        }).appendTask((tid, maps, args) -> {//拉取away球队比赛信息
            if (args == null) {//标记出出局球队，调用下一步链式任务
                return;
            }

            BattleTeam btHome = args[0] == null ? null : (BattleTeam) args[0];
            maps.put("homeTeam", btHome);
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.LocalBattleManager_getBattleTeam, room.getAwayTeam().getNodeName(), tid, room.getAwayTeam().getTeamId());
        }).appendTask((tid, maps, args) -> {//双方比赛球队信息验证，是否20:0
            if (args == null) {//标记出出局球队，调用下一步链式任务
                return;
            }
            BattleTeam bthome = maps.get("homeTeam");
            BattleTeam btAway = args[0] == null ? null : (BattleTeam) args[0];
            room.getHomeTeam().setPower(bthome.getAbility().getTotalCap());
            room.getHomeTeam().setLevel(bthome.getLevel());

            //开启比赛com/ftkj/manager/pk/CrossPVPManager.java:299
            BattleHandle battle = getCustomBattle(battleId, bthome, btAway, room);
            if (battle == null || battle.getBattleSource().getStage() == EBattleStage.Close
                || !(room.checkLevelLimit(btAway.getLevel()) || room.checkPowerLimit(btAway.getAbility().getTotalCap()))) {//比赛开启失败，通知比赛主服务器，将这两名玩家重新放入队列
                RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_closeRoom, null, room.getRoomId(), 0);
                return;
            }

            BattleAPI.getInstance().putBattle(battle);
            //告诉逻辑服务器，该玩家匹配成功比赛开启，不需要等待返回
            RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_updatePKRoomInfo, null, room.getRoomId(), GameSource.serverName);
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_updateLocalBattleTeam,
                room.getHomeTeam().getNodeName(), room.getHomeTeam().getTeamId(), battleId, EBattleType.擂台赛,
                bthome.getTeamId(), btAway.getTeamId(), GameSource.serverName);
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_updateLocalBattleTeam,
                room.getAwayTeam().getNodeName(), room.getAwayTeam().getTeamId(), battleId, EBattleType.擂台赛,
                bthome.getTeamId(), btAway.getTeamId(), GameSource.serverName);
        }).start();
    }

    public BattleHandle getBattle(long battleId, BattleTeam home, BattleTeam away) {
        if (home == null || away == null) {
            return null;
        }
        BattleSource bs = createBattleSource(battleId, home, away, EBattleType.即时比赛跨服);
        // 参数
        BattleHandle battle = new BattleCommon(bs, this::freePvpMatchEnd, getRoundReport());
        return battle;
    }

    public BattleSource createBattleSource(long battleId, BattleTeam home, BattleTeam away, EBattleType bt) {
        BattleBean dbb = BattleConsole.getDefault();
        BattleBean bb = BattleConsole.getBattle(bt);
        BattleSource bs = new BattleSource(battleId, bt,
            dbb.getSteps(bb.getBaseBean()),
            dbb.getSpeed(bb.getBaseBean()),
            null, null, true, 0);
        bs.addTeam(home, away);
        return bs;
    }

    private BattleHandle getCustomBattle(long battleId, BattleTeam home, BattleTeam away, CustomPVPRoom room) {
        if (home == null || away == null) {
            return null;
        }
        BattleSource bs = createBattleSource(battleId, home, away, EBattleType.擂台赛);
        // 参数
        bs.addBattleAttribute(new BattleAttribute(0));
        bs.getAttributeMap(0).addVal(EBattleAttribute.擂台赛房间, room);
        BattleHandle battle = new BattleTeamCustom(bs, this::customPvpMatchEnd, getRoundReport());
        return battle;
    }

    private int getRoomId() {
        return roomIds.incrementAndGet();
    }

    @RPCMethod(code = CrossCode.CrossPVPManager_updateRoomStatus, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void updateRoomStatus(int roomId, EBattleRoomStatus status) {
        BattleRoom room = getRoom(roomId);
        if (room == null) { return; }
        room.setStatus(status);
    }

    private AtomicLong _N;

    /**
     * 匹配正在等待的玩家
     */
    public void matchTeams() {
        long num = _N.getAndIncrement();
        Set<Integer> keys = matchMaps.keySet();
        log.trace("-开始匹配玩家-");
        List<TeamNode> tmp = null;
        for (int group : keys) {
            if (group == 1 && num % 10 != 0) {//1-10
                continue;
            }
            if (group == 2 && num % 20 != 0) {//11-20
                continue;
            }
            if (group >= 3 && num % 60 != 0) {//20+
                continue;
            }
            tmp = Lists.newArrayList(matchMaps.remove(group));
            //			matchMaps.computeIfAbsent(group, (k)->Sets.newConcurrentHashSet())
            if (tmp == null || tmp.size() == 0) {
                continue;//不够2人，无法匹配,等待下轮
            }
            Collections.shuffle(tmp);//打乱匹配玩家顺序
            //			int end = tmp.size()-1;
            if (tmp.size() % 2 != 0) {//说明是基数，有一人轮空。添加NPC
                TeamNode npcNode = npcList.get(RandomUtil.randInt(npcList.size()));
                tmp.add(npcNode);
                //				matchMaps.computeIfAbsent(group, (k)->Sets.newConcurrentHashSet()).add(tmp.get(end));
            }
            int end = tmp.size() - 1;
            for (int i = 0; i < end; ) {
                //将匹配双方基础数据发送到执行比赛逻辑的跨服服务器上
                RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_loadTeamSource, null
                    , tmp.get(i++), tmp.get(i++), crossBattleManager.getBattleId(), getRoomId(), group);
            }
        }
    }

    private BattleRoom getRoom(int roomId) {
        return this.rooms.get(roomId);
    }

    @Override
    public void initConfig() {
    }

    @Override
    public void instanceAfter() {
        rooms = Maps.newConcurrentMap();
        matchMaps = Maps.newConcurrentMap();
        roomIds = new AtomicInteger();
        teamRoomMap = Maps.newConcurrentMap();
        npcList = Lists.newArrayList();
        npcList.add(new TeamNode(10001));
        npcList.add(new TeamNode(10002));
        npcList.add(new TeamNode(10003));
        npcList.add(new TeamNode(10004));
        npcList.add(new TeamNode(10005));
        npcList.add(new TeamNode(10006));
        npcList.add(new TeamNode(10007));
        npcList.add(new TeamNode(10008));
        npcList.add(new TeamNode(10009));
        npcList.add(new TeamNode(10010));
        _N = new AtomicLong();
    }

    private void freePvpMatchEnd(BattleSource bs) {
        BattleTeam home = bs.getHome();
        BattleTeam away = bs.getAway();
        String node = home.getNodeName();
        //不是NPC，讲数据回传到玩家本地逻辑服务器，进行玩家数据处理
        //			BattleInfo info,BattleTeam home,BattleTeam away,BattleEndReport endReport
        GameLogPB.BattleEndLogData endLog = BattlePb.getBattleEndLogData(home, away);
        redis.set(RedisKey.Battle_End_Source + bs.getBattleId(), endLog, RedisKey.DAY2);
        log.debug("xpvp end. bid {} htid {} {} atid {} {}", bs.getId(), bs.getHome().getTeamId(), bs.getHome().getNodeName(),
            bs.getAway().getTeamId(), bs.getAway().getNodeName());
        RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_battleEnd, home.getNodeName(), bs.getInfo(), bs.getHome(), bs.getAway(), bs.getEndReport());
        BattlePB.BattleEndMainData data = BattlePb.battleEndMainData(bs.getInfo().getBattleId(), bs.getEndReport());
        sendMessage(ServiceConsole.getBattleKey(bs.getInfo().getBattleId())
            , data, ServiceCode.Battle_PK_Stage_Round_Main_End);
        //同一个本地服务器只发送一次结算推送
        if (!node.equals(away.getNodeName())) {//不是NPC，讲数据回传到玩家本地逻辑服务器，进行玩家数据处理
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_battleEnd, away.getNodeName()
                , bs.getInfo(), bs.getHome(), bs.getAway(), bs.getEndReport());
        }
        //修改服务器房间状态
        int roomId = bs.getAttributeMap(0).getVal(EBattleAttribute.Room_Id);
        RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_updateRoomStatus, null, roomId, EBattleRoomStatus.比赛结束);
    }

    private void customPvpMatchEnd(BattleSource bs) {
        BattleTeam home = bs.getHome();
        String node = home.getNodeName();
        //不是NPC，讲数据回传到玩家本地逻辑服务器，进行玩家数据处理
        //			BattleInfo info,BattleTeam home,BattleTeam away,BattleEndReport endReport
        log.debug("xcpvp end. bid {} htid {} {} atid {} {}", bs.getId(), bs.getHome().getTeamId(), bs.getHome().getNodeName(),
            bs.getAway().getTeamId(), bs.getAway().getNodeName());
        RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_battleEnd, home.getNodeName(), bs.getInfo(), bs.getHome(), bs.getAway(), bs.getEndReport());
        BattlePB.BattleEndMainData data = BattlePb.battleEndMainData(bs.getInfo().getBattleId(), bs.getEndReport());
        sendMessage(ServiceConsole.getBattleKey(bs.getInfo().getBattleId())
            , data, ServiceCode.Battle_PK_Stage_Round_Main_End);
        BattleTeam away = bs.getAway();//同一个本地服务器只发送一次结算推送
        if (!node.equals(away.getNodeName())) {//不是NPC，讲数据回传到玩家本地逻辑服务器，进行玩家数据处理
            RPCMessageManager.sendMessage(CrossCode.BattlePVPManager_battleEnd, away.getNodeName()
                , bs.getInfo(), bs.getHome(), bs.getAway(), bs.getEndReport());
        }
        //修改服务器房间状态
        CustomPVPRoom room = bs.getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
        ICustomGuessBattle guessBattle = room.getGuessType().getBattle();
        CustomGuessResult result = guessBattle.getCustomGuessResult(bs);

        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_pkend, null, result);
        //			int roomId = bs.getAttributeMap(0).getVal(EBattleAttribute.房间号);
        //			RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_updateRoomStatus, null,roomId,EBattleRoomStatus.比赛结束);
    }

    public BattleRoundReport getRoundReport() {
        return (bs, report) -> {
            sendMessage(ServiceConsole.getBattleKey(bs.getInfo().getBattleId())
                , BattlePb.battleRoundMainData(bs, report),
                ServiceCode.Battle_Round_Push);
        };
    }

    @Override
    public void masterInit(String nodeName) {

    }

}
