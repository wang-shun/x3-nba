package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.ICustomAO;
import com.ftkj.db.domain.CustomTeamPO;
import com.ftkj.enums.EEmailType;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.custom.CustomGuess;
import com.ftkj.manager.custom.CustomGuessResult;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.custom.CustomTeam;
import com.ftkj.manager.custom.CustomTeamGuess;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.proto.CustomPB;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.TeamPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年8月3日
 * 本地自定义擂台赛
 */
public class LocalCustomPVPManager extends BaseManager implements OfflineOperation {

    @IOC
    private TeamManager teamManager;
    @IOC
    private ChatManager chatManager;

    @IOC
    private NodeManager nodeManager;

    @IOC
    private TeamEmailManager teamEmailManager;

    @IOC
    private ICustomAO customAO;

    private Map<Integer, CustomGuess> guessMap;

    private Map<Long, CustomTeam> customTeamMap;

    private int _maxPageCount;

    //
    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_showCustomMain)
    public void showCustomMain() {
        long teamId = getTeamId();
        //		long teamId,int power,int level,int num
        TeamAbility ability = teamManager.getTeamAllAbility(teamId);
        Team team = teamManager.getTeam(teamId);
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_getCustomRoom,
                    null, tid, teamId, ability.getTotalCap(), team.getLevel(), _maxPageCount);
        }).appendTask((tid, maps, args) -> {
            List<CustomPVPRoom> resultList = (ArrayList<CustomPVPRoom>) args[0];
            CustomTeam ct = getCustomTeam(teamId);
            CustomPB.CustomMainData data = getCustomMainData(ct, resultList);
            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_callBackShowCustomMain);
        })
                .start();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_showCustomGuessMain)
    public void showCustomGuessMain() {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_getCustomGuessRoom,
                    null, tid, teamId, _maxPageCount);
        }).appendTask((tid, maps, args) -> {
            List<CustomPVPRoom> resultList = (ArrayList<CustomPVPRoom>) args[0];
            CustomTeam ct = getCustomTeam(teamId);
            CustomPB.CustomMainData data = getCustomMainData(ct, resultList);
            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_callBackShowCustomGuessMain);
        }).start();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_showGustomGuessMyMain)
    public void showGustomGuessMyMain() {
        long teamId = getTeamId();
        List<Integer> rooms = guessMap.values().stream().filter(g -> g.hasTeam(teamId)).map(g -> g.getRoomId()).collect(Collectors.toList());
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_getCustomMainByIds,
                    null, tid, (ArrayList) rooms);
        }).appendTask((tid, maps, args) -> {
            List<CustomPVPRoom> resultList = (ArrayList<CustomPVPRoom>) args[0];
            CustomTeam ct = getCustomTeam(teamId);
            CustomPB.CustomMainData data = getCustomMainData(ct, resultList);
            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_callBackShowGustomGuessMyMain);
        }).start();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_showGustomGuessPKMain)
    public void showGustomGuessPKMain(int roomId) {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_getCustomGuessInfo,
                    null, tid, roomId);
        }).appendTask((tid, maps, args) -> {
            float homeRate = (float) args[0];
            float awayRate = (float) args[1];
            CustomGuess cg = getCustonGuess(roomId);
            CustomTeamGuess ctg = cg.getTeamGuess(teamId);
            CustomPB.CustomGuessData data = null;
            data = CustomPB.CustomGuessData.newBuilder()
                    .setAway(ctg == null ? 0 : ctg.getMoneyB())
                    .setAwayRate(awayRate)
                    .setHome(ctg == null ? 0 : ctg.getMoneyA())
                    .setHomeRate(homeRate)
                    .setRoomId(roomId)
                    .build();
            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_callBackShowGustomGuessPKMain);
        }).start();

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    //    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_showGustomGuessPKMain2)
    //    public void showGustomGuessPKMain2(int roomId) {
    //        long teamId = getTeamId();
    //        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
    //            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_getCustomGuessInfo,
    //                    null, tid, roomId);
    //        }).appendTask((tid, maps, args) -> {
    //            float homeRate = (float) args[0];
    //            float awayRate = (float) args[1];
    //            CustomGuess cg = getCustonGuess(roomId);
    //            CustomTeamGuess ctg = cg.getTeamGuess(teamId);
    //            CustomPB.CustomGuessData data = null;
    //            int customMoney = getCustomTeam(teamId).getMoney();
    //            data = CustomPB.CustomGuessData.newBuilder()
    //                    .setAway(ctg == null ? 0 : ctg.getMoneyB())
    //                    .setAwayRate(awayRate)
    //                    .setHome(ctg == null ? 0 : ctg.getMoneyA())
    //                    .setHomeRate(homeRate)
    //                    .setRoomId(roomId)
    //                    .setMoney(customMoney)
    //                    .build();
    //            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_callBackShowGustomGuessPKMain2);
    //        }).start();
    //
    //        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    //    }

    public synchronized CustomGuess getCustonGuess(int roomId) {
        CustomGuess cg = guessMap.get(roomId);
        if (cg == null) {
            cg = new CustomGuess(roomId);
            guessMap.put(roomId, cg);
        }
        return cg;
    }

    public void closeCustomGuess(int roomId) {
        guessMap.remove(roomId);
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_guess)
    public void guess(int roomId, int money, int home) {
        //home 0 主队，1 客队
        if (money < 0 || money > 999999 || roomId < 0) { return; }
        long teamId = getTeamId();
        CustomTeam ct = getCustomTeam(teamId);
        if (ct.getMoney() < money) {//货币不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_5.code).build());
            return;
        }

        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_updateCustoGuessInfo,
                    null, tid, roomId, GameSource.serverName, home == 0 ? money : 0, home == 0 ? 0 : money);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            if (code == ErrorCode.Success) {
                ct.updateMoney(-money);
                CustomGuess cg = getCustonGuess(roomId);
                CustomTeamGuess ctg = cg.getTeamGuess(teamId);
                if (ctg == null) {
                    ctg = CustomTeamGuess.createCustomTeamGuess(teamId, roomId);
                    cg.getTeamGuess().put(teamId, ctg);
                }
                if (home == 0) {//压主队
                    ctg.updateMoneyA(money);
                } else {
                    ctg.updateMoneyB(money);
                }
            }
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code.code).build(), ServiceCode.LocalCustomPVPManager_guess);
        }).start();
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_createCustomRoom)
    public void createCustomRoom(int winType, String stepCondition, int positionCondition, int pkType
            , int powerCondition, int levelCondition, int roomScore, int roomMoney, String roomTip, int autoStart) {
        long teamId = getTeamId();
        CustomTeam ct = getCustomTeam(teamId);
        if (ct.getMoney() < roomMoney) {//货币不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
            return;
        }

        if (chatManager.shieldText(roomTip)) {//非法字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Common_2.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        TeamAbility ability = teamManager.getTeamAllAbility(teamId);
        TeamNodeInfo nodeInfo = new TeamNodeInfo(teamId, team.getName(), team.getLogo(), team.getLevel(), ability.getTotalCap());
        //因为是异步创建房间，所以先扣除所需货币，不管是否创建成功,验证交给前台
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_createCustomRoom,
                    null, tid, nodeInfo, winType, stepCondition, positionCondition, pkType,
                    powerCondition, levelCondition, roomScore, roomMoney, roomTip, autoStart == 1);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            if (code != ErrorCode.Success) { return; }
            CustomPVPRoom room = (CustomPVPRoom) args[2];
            sendMessage(teamId, getCustomRoomData(room)
                    , ServiceCode.LocalCustomPVPManager_callBackCreateCustomRoom);
            ct.updateMoney(-roomMoney);
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(-roomMoney).build()
                    , ServiceCode.Push_Custom_Money);
        })
                .start();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_getCustomMoney)
    public void getCustomMoney() {
        long teamId = getTeamId();
        int customMoney = getCustomTeam(teamId).getMoney();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(customMoney).build());
    }

    /**
     * 加入房间
     *
     * @param roomId
     */
    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_joinCustomRoom)
    public void joinCustomRoom(int roomId) {
        long teamId = getTeamId();
        //int roomId,TeamNodeInfo info,int money
        TeamNodeInfo info = teamManager.getLocalTeamNodeInfo(teamId);
        CustomTeam ct = getCustomTeam(teamId);
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_joinCustomRoom, null, roomId, info, ct.getMoney());
    }

    //	@RPCMethod(code=CrossCode.LocalCustomPVPManager_joinCustomRoomCallBack,pool=EServerNode.Logic,type=ERPCType.NONE)
    //	public void joinCustomRoomCallBack(long teamId,ErrorCode code,int roomId){
    //		sendMessage(teamId,DefaultPB.DefaultData.newBuilder()
    //				.setCode(ErrorCode.Success.code)
    //				.setMsg(""+roomId)
    //				.build()
    //				,ServiceCode.LocalCustomPVPManager_callBackJoinCustomRoom);
    //
    //	}

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_pushJoinCustomRoom, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void pushJoinCustomRoom(int roomId, TeamNodeInfo joinTeam, int money) {
        CustomPB.CustomRoomTeamData data = getCustomRoomTeamData(joinTeam);
        if (GameSource.serverName.equals(joinTeam.getNodeName())) {//
            CustomTeam ct = getCustomTeam(joinTeam.getTeamId());
            ct.updateMoney(-money);
            sendMessage(joinTeam.getTeamId(), DefaultPB.DefaultData.newBuilder().setCode(-money).build()
                    , ServiceCode.Push_Custom_Money);
        }
        sendMessage(ServiceConsole.getCustomRoomKey(roomId), data, ServiceCode.Push_Custom_Join_Room);
    }

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_pushCloseCustomGuess, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void pushCloseCustomGuess(int roomId) {

    }

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_pushExitCustomRoom, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void pushExitCustomRoom(int roomId, TeamNodeInfo joinTeam) {
        sendMessage(ServiceConsole.getCustomRoomKey(roomId), DefaultPB.DefaultData.newBuilder()
                        .setCode(ErrorCode.Success.code)
                        .setBigNum(roomId)
                        .build()
                , ServiceCode.Push_Custom_Exit_Room);
    }

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_pushCloseCustomRoom, pool = EServerNode.Logic, type = ERPCType.ALL)
    public void pushCloseCustomRoom(int roomId) {
        sendMessage(ServiceConsole.getCustomRoomKey(roomId), DefaultPB.DefaultData.newBuilder()
                        .setCode(ErrorCode.Success.code)
                        .setMsg("" + roomId).build()
                , ServiceCode.Push_Custom_Close_Room);
    }

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_pushConditionCustomRoom, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void _pushConditionCustomRoom(long teamId, int roomId, ErrorCode code) {
        sendMessage(ServiceConsole.getCustomRoomKey(roomId), DefaultPB.DefaultData.newBuilder()
                        .setCode(code.code)
                        .setBigNum(roomId)
                        .build()
                , ServiceCode.Push_Custom_Condition_Room);
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_openRoom)
    public void openRoom(int roomId) {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_seachRoom,
                    null, tid, roomId);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            CustomPVPRoom room = (CustomPVPRoom) args[1];
            CustomPB.CustomSeachRoomData data = null;
            if (code == ErrorCode.Success) {
                CustomPB.CustomRoomData roomData = getCustomRoomData(room);
                data = CustomPB.CustomSeachRoomData.newBuilder().setCode(code.code).setRoom(roomData).build();
            } else {
                data = CustomPB.CustomSeachRoomData.newBuilder().setCode(code.code).build();
            }
            sendMessage(teamId, data, ServiceCode.Push_Custom_Open_Room);
        }).start();
        ServiceManager.addService(ServiceConsole.getCustomRoomKey(roomId), teamId);
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_quitRoom)
    public void quitRoom(int roomId) {
        long teamId = getTeamId();
        ServiceManager.removeService(ServiceConsole.getCustomRoomKey(roomId), teamId);
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_exitRoom)
    public void exitRoom(int roomId) {
        long teamId = getTeamId();
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_exitRoom, null, roomId, teamId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_closeRoom)
    public void closeRoom(int roomId) {
        long teamId = getTeamId();
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_closeRoom, null, roomId, teamId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(roomId).build());
    }

    @RPCMethod(code = CrossCode.CrossCustomPVPManager_updateCustomMoneyCross, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void updateCustomMoneyCross(long teamId, int money) {
        CustomTeam ct = getCustomTeam(teamId);
        ct.updateMoney(money);

        sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(money).build()
                , ServiceCode.Push_Custom_Money);
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_autoStart)
    public void autoStart(int roomId, int o) {
        long teamId = getTeamId();
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_autoStart, null, roomId, teamId, o);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(roomId).build());
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_seachRoom)
    public void seachRoom(int roomId) {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_seachRoom,
                    null, tid, roomId);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            CustomPVPRoom room = (CustomPVPRoom) args[1];
            CustomPB.CustomSeachRoomData data = null;
            if (code == ErrorCode.Success) {
                CustomPB.CustomRoomData roomData = getCustomRoomData(room);
                data = CustomPB.CustomSeachRoomData.newBuilder().setCode(code.code).setRoom(roomData).build();
            } else {
                data = CustomPB.CustomSeachRoomData.newBuilder().setCode(code.code).build();
            }
            sendMessage(teamId, data, ServiceCode.Push_Custom_Room);
        }).start();
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_seachConditionRoom)
    public void seachConditionRoom(int winType, String stepCondition, int positionCondition, int pkType) {
        long teamId = getTeamId();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossCustomPVPManager_seachConditionRoom,
                    null, tid, winType, stepCondition, positionCondition, pkType, 5);
        }).appendTask((tid, maps, args) -> {
            List<CustomPVPRoom> resultList = (ArrayList<CustomPVPRoom>) args[0];
            CustomTeam ct = getCustomTeam(teamId);
            CustomPB.CustomMainData data = getCustomMainData(ct, resultList);
            sendMessage(teamId, data, ServiceCode.LocalCustomPVPManager_seachConditionRoomCallBack);
        }).start();
    }

    @ClientMethod(code = ServiceCode.LocalCustomPVPManager_startPK)
    public void startPK(int roomId) {
        long teamId = getTeamId();
        RPCMessageManager.sendMessage(CrossCode.CrossCustomPVPManager_startPK, null, teamId, roomId);
    }

    @RPCMethod(code = CrossCode.LocalCustomPVPManager_guessEnd, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void _guessEnd(CustomGuessResult result) {
        //进行竞猜结算
        CustomGuess cg = getCustonGuess(result.getRoomId());
        if (result.isA()) {
            cg.getTeamGuess().values().stream().filter(team -> team.getMoneyA() > 0).forEach(team -> {
                updateCustomMoneyToMail(team.getTeamId(), Math.round(team.getMoneyA() * result.getRate()));
                team.setStatus(EStatus.Close.getId());
            });
        } else {
            cg.getTeamGuess().values().stream().filter(team -> team.getMoneyB() > 0).forEach(team -> {
                updateCustomMoneyToMail(team.getTeamId(), Math.round(team.getMoneyB() * result.getRate()));
                team.setStatus(EStatus.Close.getId());
            });
        }
        closeCustomGuess(result.getRoomId());
    }

    public void updateCustomMoney(long teamId, int money) {
        CustomTeam ct = getCustomTeam(teamId);
        ct.updateMoney(money);
        sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(money).build()
                , ServiceCode.Push_Custom_Money);
    }

    public void updateCustomMoneyToMail(long teamId, int money) {
        teamEmailManager.sendEmailFinal(teamId, EEmailType.系统邮件.getType(), 0, "擂台赛竞猜", StringUtil.formatString("恭喜您，通过擂台赛竞猜获得{}擂台赛货币"
                , money), PropSimple.getPropStringNotConfig(new PropSimple(4011, money)));
    }

    public CustomTeam getCustomTeam(long teamId) {
        CustomTeam ct = customTeamMap.get(teamId);
        if (ct == null) {
            CustomTeamPO po = customAO.getCustomTeam(teamId);
            if (po == null) {
                ct = CustomTeam.createCustomTeam(teamId);
            } else {
                ct = new CustomTeam(po);
            }
            customTeamMap.put(teamId, ct);
        }
        return ct;
    }

    private CustomPB.CustomMainData getCustomMainData(CustomTeam team, List<CustomPVPRoom> roomList) {
        List<CustomPB.CustomRoomData> roomDataList = roomList.stream()
                .map(room -> getCustomRoomData(room))
                .collect(Collectors.toList());
        return CustomPB.CustomMainData.newBuilder().setMoney(team.getMoney())
                .addAllRooms(roomDataList).build();
    }

    private CustomPB.CustomRoomData getCustomRoomData(CustomPVPRoom room) {
        CustomPB.CustomRoomData.Builder builder = CustomPB.CustomRoomData.newBuilder();
        builder.setAutoStart(room.isAutoStart())
                .setGuessType(room.getGuessType().ordinal())
                .setHomeTeam(getCustomRoomTeamData(room.getHomeTeam()))
                .setLevelCodition(room.getLevelCondition())
                .setPkType(room.getPkType().ordinal())
                .setPositionCondition(room.getPositionCondition().getId())
                .setPowerCondition(room.getPowerCondition())
                .setRoomId(room.getRoomId())
                .setRoomMoney(room.getRoomMoney())
                .setRoomScore(room.getRoomScore())
                .setRoomStatus(room.getRoomStatus().ordinal())
                .setRoomTip(room.getRoomTip())
                .setBattleInfo(TeamPB.TeamBattleStatusData.newBuilder()
                        .setBattleId(room.getBattleId())
                        .setBattleType(EBattleType.擂台赛.getId())
                        .setNode(nodeManager.getServerIP(room.getBattleClientNode())).build())
                .setWinCondition(room.getWinCondition().getType())
                .addAllStepCondition(room.getStepConditions().stream().map(step -> step.ordinal()).collect(Collectors.toList()));

        if (room.getAwayTeam() != null) { builder.setAwayTeam(getCustomRoomTeamData(room.getAwayTeam())); }
        return builder.build();
    }

    private CustomPB.CustomRoomTeamData getCustomRoomTeamData(TeamNodeInfo info) {
        return CustomPB.CustomRoomTeamData.newBuilder().setLevel(info.getLevel())
                .setLogo(info.getLogo())
                .setPower(info.getPower())
                .setTeamId(info.getTeamId())
                .setTeamName(info.getTeamName())
                .setNodeName(info.getNodeName())
                .build();
    }

    @Override
    public void initConfig() {
        _maxPageCount = 10;
    }

    @Override
    public void offline(long teamId) {
        customTeamMap.remove(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        customTeamMap.remove(teamId);
    }

    @Override
    public void instanceAfter() {
        customTeamMap = Maps.newConcurrentMap();
        guessMap = Maps.newConcurrentMap();
    }

}
