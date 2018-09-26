package com.ftkj.manager.pk;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.db.ao.pk.IPKCustomAO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.ECustomPVPType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.custom.CustomGuessResult;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.server.CrossCode;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年8月1日
 * 自定义傻逼比赛
 */
public class CrossCustomPVPManager extends BaseManager {
    private Map<Integer, CustomPVPRoom> roomMap;

    @IOC
    private IPKCustomAO customAO;

    @IOC
    private CrossBattleManager crossBattleManager;

    @IOC
    private NodeManager nodeManager;

    private int _maxRoomCount;
    private int _totalRoomCount;
    private int _clearCD;

    private AtomicInteger roomIds;

    private AtomicInteger clearAtomic;

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_getCustomRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getCustomRoom(long teamId, int power, int level, int num) {

        List<CustomPVPRoom> myRoomList = getMyRooms(teamId);
        //匹配符合规则的房间
        List<CustomPVPRoom> tmpList = roomMap.values().stream()
                .filter(room -> room.getHomeTeam() != null)
                .filter(room -> !room.hasTeam(teamId))
                //				.filter(room->!room.checkPowerLimit(power))
                //				.filter(room->!room.checkLevelLimit(level))
                .collect(Collectors.toList());
        int size = num - myRoomList.size();
        //打乱排序,将数据放入集合中
        if (tmpList.size() >= size) {
            Collections.shuffle(tmpList);
        } else {
            size = tmpList.size();
        }
        if (size > 0) { myRoomList.addAll(tmpList.subList(0, size)); }
        //
        RPCMessageManager.responseMessage((ArrayList) myRoomList);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_getCustomGuessRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getCustomGuessRoom(long teamId, int num) {
        List<CustomPVPRoom> tmpList = roomMap.values().stream()
                .filter(room -> room.getHomeTeam() != null)
                .filter(room -> !room.hasTeam(teamId))
                .filter(room -> room.getRoomStatus() == EBattleRoomStatus.比赛中)
                .limit(num)
                //				.filter(room->!room.checkPowerLimit(power))
                //				.filter(room->!room.checkLevelLimit(level))
                .collect(Collectors.toList());

        if (tmpList.size() >= num) {
            Collections.shuffle(tmpList);
        } else {
            num = tmpList.size();
        }
        List<CustomPVPRoom> roomList = Lists.newArrayList();
        if (num > 0) { roomList.addAll(tmpList.subList(0, num)); }
        RPCMessageManager.responseMessage((ArrayList) roomList);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_getCustomMainByIds, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getCustomMainByIds(List<Integer> roomIds) {
        List<CustomPVPRoom> roomList = roomIds.stream()
                .map(id -> roomMap.get(id))
                .filter(room -> room != null)
                .collect(Collectors.toList());
        RPCMessageManager.responseMessage((ArrayList) roomList);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_getCustomGuessInfo, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void getCustomGuessInfo(int roomId) {
        CustomPVPRoom room = roomMap.get(roomId);
        if (room == null) {
            RPCMessageManager.responseMessage(0, 0);
            return;
        }
        RPCMessageManager.responseMessage(room.getHomeMoneyRate(), room.getAwayMoneyRate());
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_updateCustoGuessInfo, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void updateCustoGuessInfo(int roomId, String node, int homeMoney, int awayMoney) {
        CustomPVPRoom room = roomMap.get(roomId);
        room.addNode(node);
        room.updateAwayMoney(awayMoney);
        room.updateHomeMoney(homeMoney);
        RPCMessageManager.responseMessage(ErrorCode.Success);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_createCustomRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void createCustomRoom(TeamNodeInfo team, int winType, String stepCondition, int positionCondition, int pkType
            , int powerCondition, int levelCondition, int roomScore, int roomMoney, String roomTip, boolean autoStart) {
        List<CustomPVPRoom> myRoomList = getMyRooms(team.getTeamId());
        if (myRoomList.size() >= _maxRoomCount) {//超出可创建房间上限
            RPCMessageManager.responseMessage(ErrorCode.Error, -1, null);
            return;
        }
        //

        CustomPVPRoom room = getEmptyRoom(team);
        room.createCustomPVPRoom(team, winType, stepCondition, positionCondition,
                pkType, powerCondition, levelCondition, roomScore, roomMoney, roomTip, autoStart);
        roomMap.put(room.getRoomId(), room);
        RPCMessageManager.responseMessage(ErrorCode.Success, room.getRoomId(), room);
    }

    private synchronized CustomPVPRoom getEmptyRoom(TeamNodeInfo team) {
        CustomPVPRoom result = roomMap.values().stream()
                .filter(room -> room.getRoomTeamId() == 0)
                .findFirst().orElse(new CustomPVPRoom(roomIds.incrementAndGet()));
        //
        result.setHomeTeam(team);
        return result;
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_seachRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void seachRoom(int roomId) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null || room.getHomeTeam() == null) {//房间不存在
            RPCMessageManager.responseMessage(ErrorCode.Error, room);
            return;
        }
        RPCMessageManager.responseMessage(ErrorCode.Success, room);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_seachConditionRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void seachConditionRoom(int winType, String stepCondition, int positionCondition, int pkType, int num) {
        List<CustomPVPRoom> tmpList = roomMap.values().stream()
                //						,room.getPositionCondition()==EPlayerPosition.getEPlayerPosition(positionCondition)
                //						,room.getPkType()==ECustomPVPType.values()[pkType]
                //						,room.getStepConditions().stream().map(step->step.ordinal()+"").collect(Collectors.joining(","))==stepCondition))
                .filter(room -> winType == -1 ? true : room.getWinCondition() == EActionType.convertByType(winType))
                .filter(room -> positionCondition == -1 ? true : room.getPositionCondition() == EPlayerPosition.getEPlayerPosition(positionCondition))
                .filter(room -> pkType == -1 ? true : room.getPkType() == ECustomPVPType.values()[pkType])
                .filter(room -> "".equals(stepCondition) ? true : room.getStepConditions().stream().map(step -> step.ordinal() + "").collect(Collectors.joining(",")) == stepCondition)
                .collect(Collectors.toList());
        Collections.shuffle(tmpList);
        int size = Ints.min(num, tmpList.size());
        List<CustomPVPRoom> resultList = tmpList.stream().limit(size).collect(Collectors.toList());
        RPCMessageManager.responseMessage((ArrayList) resultList);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_topCloseGuess, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void topCloseGuess(int roomId) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        room.close();
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_joinCustomRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void joinCustomRoom(int roomId, TeamNodeInfo info, int money) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null || room.getHomeTeam() == null) {//房间不存在
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }

        List<CustomPVPRoom> myRoomList = getMyRooms(info.getTeamId());
        if (myRoomList.size() >= _maxRoomCount) {//超出可创建房间上限
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }

        if (room.getAwayTeam() != null) {//房间已经有人了
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }

        if (money < room.getRoomMoney()) {//玩家没钱
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }

        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.TeamManager_getTeamNodeInfo, room.getHomeTeam().getNodeName()
                    , tid, room.getHomeTeam().getTeamId());
        }).appendTask((tid, maps, args) -> {
            TeamNodeInfo newInfo = (TeamNodeInfo) args[0];
            synchronized (room) {
                room.updateHomeInfo(newInfo);
                if (room.checkLevelLimit(info.getLevel()) || room.checkPowerLimit(info.getPower())) {//符合房间条件
                    RPCMessageManager.sendMessage(CrossCode.LocalCustomPVPManager_pushConditionCustomRoom,
                            info.getNodeName(), info.getTeamId(), roomId, ErrorCode.Battle_1);
                    return;
                }
                room.ready(info);
                //int roomId,TeamNodeInfo joinTeam,int money
                RPCMessageManager.sendMessage(CrossCode.LocalCustomPVPManager_pushJoinCustomRoom, null, roomId, info, room.getRoomMoney());
            }
        }).start();
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_exitRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void exitRoom(int roomId, long teamId) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        if (room.getAwayTeam() != null && room.getAwayTeam().getTeamId() == teamId) {
            room.setAwayTeam(null);
        }
        //通知所有节点，玩家退出房间
        RPCMessageManager.sendMessage(CrossCode.LocalCustomPVPManager_pushExitCustomRoom
                , null, room.getRoomId(), room.getAwayTeam());
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_closeRoom, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void closeRoom(int roomId, long teamId) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        if (room.getHomeTeam() == null || room.getHomeTeam().getTeamId() != teamId) { return; }
        if (room.getRoomStatus() != EBattleRoomStatus.比赛中) {
            RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_updateCustomMoneyCross
                    , room.getHomeTeam().getNodeName(), room.getHomeTeam().getTeamId(), room.getRoomMoney());
            //
            if (room.getAwayTeam() != null) {
                RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_updateCustomMoneyCross
                        , room.getAwayTeam().getNodeName(), room.getAwayTeam().getTeamId(), room.getRoomMoney());
            }
        }
        room.close();
        RPCMessageManager.sendMessage(CrossCode.LocalCustomPVPManager_pushCloseCustomRoom
                , null, room.getRoomId());
    }

    private void close(CustomPVPRoom room) {
        if (room == null) { return; }
        if (room.getRoomStatus() == EBattleRoomStatus.比赛中) {
            return;
        }
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_updateCustomMoneyCross
                , room.getHomeTeam().getNodeName(), room.getHomeTeam().getTeamId(), room.getRoomMoney());
        //
        if (room.getAwayTeam() != null) {
            RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_updateCustomMoneyCross
                    , room.getAwayTeam().getNodeName(), room.getAwayTeam().getTeamId(), room.getRoomMoney());
        }
        room.close();
        RPCMessageManager.sendMessage(CrossCode.LocalCustomPVPManager_pushCloseCustomRoom
                , null, room.getRoomId());
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_pkend, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void pkEnd(CustomGuessResult result) {
        CustomPVPRoom room = getRoom(result.getRoomId());
        if (result.isA()) {
            result.setRate(room.getHomeMoneyRate());
        } else {
            result.setRate(room.getAwayMoneyRate());
        }
        //推送竞猜结算
        RPCMessageManager.sendMessageNodes(CrossCode.LocalCustomPVPManager_guessEnd
                , room.getNodes(), result);
        //关闭房间
        room.close();
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_topCloseGuess
                , null, room.getRoomId());
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_autoStart, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void autoStart(int roomId, long teamId, int o) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        if (room.getHomeTeam() == null || room.getHomeTeam().getTeamId() != teamId) { return; }
        room.setAutoStart(o == 1);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_startPK, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void startPK(long teamId, int roomId) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        if (room.getHomeTeam() == null || room.getHomeTeam().getTeamId() != teamId
                || room.getAwayTeam() == null) { return; }
        startPK(room);
    }

    /**
     * 房间自动启动检查
     */
    public void roomAutoStart() {
        DateTime now = DateTime.now();
        roomMap.values().stream()
                .filter(room -> room.getRoomStatus() != EBattleRoomStatus.比赛结束)
                .filter(room -> room.getRoomStatus() != EBattleRoomStatus.比赛中)
                .filter(room -> room.isAutoStart())
                .filter(room -> room.isStart(now))
                .filter(room -> room.getHomeTeam() != null)
                .filter(room -> room.getAwayTeam() != null)
                .forEach(room -> startPK(room));

        if (clearAtomic.incrementAndGet() >= _clearCD) {
            DateTime end = now.plusHours(5);
            clearAtomic.set(0);
            //清除无效房间
            roomMap.values().stream()
                    .filter(room -> room.autoClose(end))
                    .forEach(room -> close(room));
        }
    }

    /**
     * 开启比赛，将比赛分配到比赛节点中开启
     *
     * @param room
     */
    private void startPK(CustomPVPRoom room) {
        long battleId = crossBattleManager.getBattleId();
        room.setBattleId(battleId);
        RPCMessageManager.sendMessage(CrossCode.CrossPVPManager_loadCustomTeamSource, null, battleId, room);
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_updatePKRoomInfo, pool = EServerNode.Battle, type = ERPCType.MASTER)
    public void updatePKRoomInfo(int roomId, String nodeName) {
        CustomPVPRoom room = getRoom(roomId);
        if (room == null) { return; }
        room.setRoomStatus(EBattleRoomStatus.比赛中);
        room.setBattleClientNode(nodeName);
    }

    private CustomPVPRoom getRoom(int roomId) {
        return roomMap.get(roomId);

    }

    private List<CustomPVPRoom> getMyRooms(long teamId) {
        List<CustomPVPRoom> myRoomList = roomMap.values().stream()
                .filter(room -> room.hasTeam(teamId))
                .collect(Collectors.toList());
        return myRoomList;
    }

    @Override
    public void initConfig() {
        _maxRoomCount = ConfigConsole.getIntVal(EConfigKey.Custom_Max_Room);
        _totalRoomCount = ConfigConsole.getIntVal(EConfigKey.Custom_Total_Room);
        _clearCD = 60 * 60;
    }

    @Override
    public void instanceAfter() {
        List<CustomPVPRoom> rooms = customAO.getCustomPVPRoomList();
        int maxRoomId = rooms.stream().mapToInt(room -> room.getRoomId()).max().orElse(0);
        if (rooms.size() < _totalRoomCount) {//房间不足
            rooms.add(new CustomPVPRoom(++maxRoomId));
        }
        roomMap = rooms.stream().collect(Collectors.toConcurrentMap(CustomPVPRoom::getRoomId, val -> val));
        roomIds = new AtomicInteger(maxRoomId);
        clearAtomic = new AtomicInteger(0);
    }

}
