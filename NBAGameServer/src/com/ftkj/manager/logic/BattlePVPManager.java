package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.battle.BattlePb;
import com.ftkj.manager.battle.handle.BattleCommonAPI;
import com.ftkj.manager.battle.model.BattleInfo;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PVPBattlePB;
import com.ftkj.proto.PVPBattlePB.BattleRoomMain;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.task.RPCLinkedTask;

import java.util.List;

/**
 * @author tim.huang
 * 2017年4月25日
 * 即时PK赛，本地方法
 */
public class BattlePVPManager extends BaseManager {

    @IOC
    private LocalBattleManager localBattleManager;

    @IOC
    private TeamManager teamManager;

    @IOC
    private TeamStatusManager teamStatusManager;

    @IOC
    private TaskManager taskManager;

    @IOC
    private NodeManager nodeManager;

    @IOC
    private BuffManager buffManager;

    private int _maxBattleDayCount;
    //	private Map<Long,LocalBattleTeam> teams;

    /**
     * 匹配比赛
     */
    @ClientMethod(code = ServiceCode.BattlePVPManager_match)
    public void match() {
        long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        //		if(this.teams.containsKey(teamId)){//正在匹配或比赛中
        //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_3.code).build());
        //			return;
        //		}
        int rid = getRid();
        int group = BattleCommonAPI.getBattleRoomGroup(team.getLevel());
        TeamStatus status = teamStatusManager.get(teamId);
        //
        TeamBattleStatus tbs = status.getBattle(EBattleType.即时比赛跨服);
        if (tbs != null && (tbs.getStatus() == EBattleRoomStatus.比赛中 || tbs.getStatus() == EBattleRoomStatus.等待开启)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_In.code).build());
            return;
        }

        if (getBattleDayCount(teamId) >= getMaxDayCount(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_5.code).build());
            return;
        }

        RPCLinkedTask.build().appendTask((tid, maps, args) -> {//申请跨服匹配
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossPVPManager_math, null, tid, new TeamNode(teamId, GameSource.serverName), group);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            if (code == ErrorCode.Success) {//
                //放入一场比赛信息，等待比赛开启
                status.putBattle(EBattleType.即时比赛跨服, 0, "", GameSource.serverName, EBattleRoomStatus.等待开启);
                //				teams.putIfAbsent(teamId, new LocalBattleTeam(teamId, EBattleType.即时比赛跨服));
            }
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code.code).build(), ServiceCode.BattlePVPManager_match, rid);
        }).start();
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.BattlePVPManager_showMain)
    public void showRoomMain(int group) {
        long teamId = getTeamId();
        //		Team team = teamManager.getTeam(teamId);
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {//long teamId,int group,int num
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.CrossPVPManager_showRoom, null, tid, teamId, group, 10);
        }).appendTask((tid, maps, args) -> {
            List<PVPBattlePB.BattleRoomData> roomMainData = (List<PVPBattlePB.BattleRoomData>) args[0];
            TeamStatus ts = teamStatusManager.get(teamId);
            TeamBattleStatus status = ts.getBattle(EBattleType.即时比赛跨服);
            //			LocalBattleTeam bt = teams.get(teamId);
            log.debug("收到跨服服务器的回调----------->", roomMainData.size());
            sendMessage(teamId, BattleRoomMain.newBuilder().addAllRooms(roomMainData)
                    .setDayCount(getBattleDayCount(teamId))
                    .setRoomGroup(group)
                    .setStatus(status == null ? EBattleRoomStatus.默认未匹配.ordinal()
                        : status.getStatus().ordinal()).build()
                , ServiceCode.BattlePVPManager_showMain, rid);
        }).start();
    }

    /**
     * 更新本地比赛信息
     */
    @RPCMethod(code = CrossCode.BattlePVPManager_updateLocalBattleTeam, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void updateLocalBattleTeam(long teamId, long battleId, EBattleType bt, long home, long away, String nodeName) {
        String nodeIp = nodeManager.getServerIP(nodeName);
        log.debug("btpvp uploct. bid {} tid {} btype {} nname {} nip {} net {}",
            battleId, teamId, bt, nodeName, nodeIp, GameSource.net);
        if (GameSource.isNPC(teamId)) {
            return;
        }
        TeamStatus ts = teamStatusManager.get(teamId);
        //		LocalBattleTeam lbt = teams.get(teamId);
        //		if(lbt == null) return;
        //		lbt.setBattleId(battleId);
        //		lbt.setStatus(EBattleRoomStatus.比赛中);
        //		lbt.setNodeName(nodeName);
        //		TeamBattleStatus status = ts.getBattle(EBattleType.即时比赛跨服);
        //		status.setBattleId(battleId);
        //		status.updateStatus(EBattleRoomStatus.比赛中);
        //		status.setNode(nodeIp);
        ts.putBattle(bt, battleId, nodeIp, nodeName, EBattleRoomStatus.比赛中);
        sendMessage(teamId, BattlePb.battleStartResp(bt, battleId, home, away, nodeIp), ServiceCode.Battle_Start_Push);
        //避免影响比赛
        int count = getBattleDayCount(teamId) + 1;
        saveBattleDayCount(teamId, count);
    }

    /**
     * 即时PK，比赛回调
     *
     * @param battleSource
     */
    @RPCMethod(code = CrossCode.BattlePVPManager_battleEnd, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void battleEnd(BattleInfo info, BattleTeam home, BattleTeam away, EndReport endReport) {
        localBattleManager.sendEndMain(info, home, away, endReport, false);
        //字符串中为，自己等级，对方等级，自己分数，对方分数

        //        if (home.getNodeName().equals(GameSource.serverName)) {
        //            taskManager.updateTask(home.getTeamId(), ETaskCondition.即时PK, 1, home.getLevel() + "," + away.getLevel() + "," + home.getScore() + "," + away.getScore());
        //            //			teams.remove(home.getTeamId());
        //        }
        //        if (away.getNodeName().equals(GameSource.serverName)) {
        //            taskManager.updateTask(away.getTeamId(), ETaskCondition.即时PK, 1, away.getLevel() + "," + home.getLevel() + "," + away.getScore() + "," + home.getScore());
        //            //			teams.remove(away.getTeamId());
        //        }
    }

    //	@ClientMethod(code = ServiceCode.GameManager_debugStartBattle)
    //	@UnCheck
    //	public void debugStartBattle(long teamId,long otherTeamId){
    //		for(int i = 0 ; i < 100;i++){
    //			RPCLinkedTask.build()
    //					.appendTask((tid,maps,args)->{
    //						EBattleType type = (EBattleType)args[0];
    //						log.debug("从跨服中取battleId，比赛类型为[{}]",type.name());
    //						RPCMessageManager.sendLinkedTaskMessage(CrossCode.BattleManager_getBattleId, null, tid,type);
    //					})
    //					.appendTask((tid,maps,args)->{
    //						long battleId = (long)args[0];
    //						EBattleType battleType = (EBattleType)args[1];
    //						log.debug("从跨服中取battleId，比赛ID为[{}]",battleId);
    //						BattleSource bs = localBattleManager.buildBattleSource(battleId, battleType, teamId, otherTeamId);
    //						localBattleManager.start(bs);
    //					}).start(EBattleType.普通比赛);
    //		}
    //	}

    public int getBattleDayCount(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Battle_Day_Count);
        int count = redis.getIntNullIsZero(key);
        return count;
    }

    private void saveBattleDayCount(long teamId, int count) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Battle_Day_Count);
        redis.set(key, count + "", RedisKey.DAY);
    }

    public int getMaxDayCount(long teamId) {
        int result = _maxBattleDayCount;
        int vipCount = buffManager.getBuffSet(teamId, EBuffType.即使PK赛次数).getValueSum();
        return result + vipCount;
    }

    @Override
    public void initConfig() {
        _maxBattleDayCount = ConfigConsole.getIntVal(EConfigKey.Battle_Day_Count);

    }

    @Override
    public void instanceAfter() {
        //		teams = Maps.newConcurrentMap();
    }

    @Override
    public int getOrder() {
        return ManagerOrder.PVPBattle.getOrder();
    }
}
