package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DraftConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.enums.EBuffType;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EDraftStage;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.draft.DraftRoomBean;
import com.ftkj.manager.draft.RpcDraftRoom;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.money.TeamMoney;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.proto.DraftPB;
import com.ftkj.proto.DraftPB.DraftRoomPlayerData;
import com.ftkj.proto.DraftPB.DraftRoomPlayerStageData;
import com.ftkj.proto.DraftPB.DraftRoomTeamData;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.server.rpc.task.RPCLinkedTask;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年5月4日
 * 选秀管理
 */
public class LocalDraftManager extends BaseManager implements OfflineOperation {
    private static final Logger log = LoggerFactory.getLogger(LocalDraftManager.class);
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private BeSignManager beSignManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamEmailManager teamEmailManager;

    private Set<Long> reqRoom;

    //选秀最大次数
    private int _maxDayCount;
    private int _draftMoney;

    @Override
    public void offline(long teamId) {
        clean(teamId);
    }

    @Override
    public void dataGC(long teamId) {
        clean(teamId);
    }

    private void clean(long teamId) {
        ServiceManager.removeService(ServiceConsole.getDraftMainKey(), teamId);
    }

    @SuppressWarnings("unchecked")
    @ClientMethod(code = ServiceCode.LocalDraftManager_showDraftMain)
    public void showDraftMain() {
        long teamId = getTeamId();
        int rid = getRid();
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_showDraftMain, null, tid, roomId);
        }).appendTask((tid, maps, args) -> {
            List<DraftPB.DraftRoomMainData> data = (List<DraftPB.DraftRoomMainData>) args[0];
            DraftPB.DraftRoomMainData my = (DraftPB.DraftRoomMainData) args[1];
            if (my == null || my.getRoomId() == 0 || my.getStatus() == EDraftStage.结束.getId()) {
                teamStatusManager.draftRoomEnd(roomId);
            }
            DraftPB.DraftMainData resultData = DraftPB.DraftMainData.newBuilder()
                .addAllRooms(data)
                .setDraftCount(getDayCount(teamId))
                .setMyRoom(my)
                .build();
            sendMessage(teamId, resultData, ServiceCode.LocalDraftManager_showDraftMain, rid);
            //添加监听
            ServiceManager.addService(ServiceConsole.getDraftMainKey(), teamId);
        }).start();
    }

    private Map<Long, Integer> helpPlayerIdMap;
    private Map<Long, PlayerTalent> helpPlayerTalentMap;

    @ClientMethod(code = ServiceCode.LocalDraftManager_showHelpPlayers)
    public void showHelpPlayers() {
        long teamId = getTeamId();
        taskManager.updateTask(teamId, ETaskCondition.选秀, 1, EModuleCode.选秀.getName());
        DropBean db = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Draft_Frist_Drop));

        List<PlayerBean> playerBeanList = db.roll().stream()
            .map(ps -> PropConsole.getPlayerProp(ps.getPropId()))
            .filter(player -> player != null)
            .map(bean -> PlayerConsole.getPlayerBean(bean.getHeroId()))
            .filter(player -> player != null)
            .collect(Collectors.toList());
        PlayerBean pb = playerBeanList.stream()
            .min((playerA, playerB) -> Integer.compare(playerB.getPrice(), playerA.getPrice()))
            .orElse(null);
        playerBeanList.remove(pb);
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        if (!helpPlayerIdMap.containsKey(teamId)) {
            Player p = tp.getPlayers().stream()
                .filter(player -> player.getPlayerBean().getGrade() == EPlayerGrade.X)
                .findFirst().orElse(null);
            pb = getPlayerBean(p);
            helpPlayerIdMap.put(teamId, pb.getPlayerRid());
        } else {
            pb = PlayerConsole.getPlayerBean(helpPlayerIdMap.get(teamId));
        }
        
        PlayerTalent talent = PlayerTalent.createPlayerTalent(0, pb.getPlayerRid(), 0, PlayerManager._initDrop, false);
        helpPlayerTalentMap.put(teamId, talent);
        playerBeanList.add(0, pb);
        boolean flag = true;
        List<DraftPB.DraftRoomPlayerData> pLists = Lists.newArrayList();
        for (PlayerBean player : playerBeanList) {
        	PlayerTalent tmpTalent = null;
        	if (flag) {
        		tmpTalent = helpPlayerTalentMap.get(teamId);
				flag = false;
			}else {
				tmpTalent = PlayerTalent.createPlayerTalent(0, player.getPlayerRid(), 0, PlayerManager._initDrop, false);
			}
        	DraftRoomPlayerData build = DraftPB.DraftRoomPlayerData.newBuilder()
            .setPlayerId(player.getPlayerRid())
            .setSignTeamName("")
            .setTalent(PlayerManager.getPlayerTalentData(tmpTalent))
            .build();
        	pLists.add(build);
		}
        
        DraftPB.HelpDraftPlayerMain _HelpDraftData = DraftPB.HelpDraftPlayerMain.newBuilder()
            .addAllPlayers(pLists)
            .build();
        sendMessage(_HelpDraftData);
    }

    @ClientMethod(code = ServiceCode.LocalDraftManager_signHelpPlayer)
    public void signHelpPlayer() {
        long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        if (!team.getHelp().contains("g=150")) {
            return;
        }
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        //			PlayerBean pb = tp.getPlayerList().stream()
        ////			.filter(p->p.getPlayerBean().getGrade()!=EPlayerGrade.X)
        //			.sorted((playerA,playerB)->playerB.getPrice()-playerA.getPrice())
        //			.findFirst().orElse(null).getPlayerBean();
        PlayerBean pb;
        if (!helpPlayerIdMap.containsKey(teamId)) {
            Player p = tp.getPlayers().stream()
                .filter(player -> player.getPlayerBean().getGrade() == EPlayerGrade.X).findFirst().orElse(null);
            pb = getPlayerBean(p);
        } else {
            int playerId = helpPlayerIdMap.remove(teamId);
            pb = PlayerConsole.getPlayerBean(playerId);
        }
        //
        PlayerTalent pt = helpPlayerTalentMap.get(teamId);
//        PlayerTalent.createPlayerTalent(teamId, pb.getPlayerRid(), tp.getNewTalentId(), PlayerManager._initDrop, true);
        tp.putPlayerTalent(pt);
        Player player = tp.createPlayer(pb, pb.getPrice(), EPlayerPosition.NULL.name(), pt, true);
        playerManager.addPlayer(teamId, tp, player, pb, true, ModuleLog.getModuleLog(EModuleCode.新手引导, ""));
        //			beSignManager.addBeSignPlayer(teamId, pb.getPlayerId(), pb.getPrice(),pt.getId());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    private PlayerBean getPlayerBean(Player p) {
        PlayerBean pb;
        if (p == null) {
            pb = PlayerConsole.getRanPlayer(EPlayerGrade.S1, EPlayerGrade.S1, EPlayerPosition.C, null);
            if (pb == null) {
                pb = PlayerConsole.getRanPlayer(EPlayerGrade.S1, EPlayerGrade.S1, EPlayerPosition.PF, null);
            }
        } else {
            pb = PlayerConsole.getRanPlayerOr(EPlayerGrade.S1, EPlayerGrade.S1, p.getPlayerPosition());
        }
        return pb;
    }

    private int getFreeDraftCount(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Draft_Free);
        int count = redis.getIntNullIsZero(key);
        return count;
    }

    private void saveFreeDraftCount(long teamId, int count) {
        String key = RedisKey.getDayKey(teamId, RedisKey.Draft_Free);
        redis.set(key, count + "", RedisKey.DAY);
    }

    /**
     * 签约球员到代签列表
     *
     * @param playerId
     */
    @ClientMethod(code = ServiceCode.LocalDraftManager_signPlayer)
    public void signPlayer(int playerId) {
        long teamId = getTeamId();
        //long teamId,int roomId,int playerId
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_signPlayer, null, tid, teamId, roomId, playerId);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            if (code == ErrorCode.Success) {
                int price = (int) args[1];
                PlayerTalent pt = (PlayerTalent) args[2];
                log.debug("签约球员[{}]到待签列表, 状态：{}", playerId, ErrorCode.Success);

                log.debug("选秀签约代签球员{},{}", teamId, playerId);
                pt.setTeamId(teamId);
                TeamPlayer tp = playerManager.getTeamPlayer(teamId);
                pt.setId(tp.getNewTalentId());
                pt.save();
                tp.putPlayerTalent(pt);

                beSignManager.addBeSignPlayer(teamId, playerId, price, pt.getId(), ModuleLog.getModuleLog(EModuleCode.选秀, "签约"));
                PlayerBean player = PlayerConsole.getPlayerBean(playerId);
                taskManager.updateTask(teamId, ETaskCondition.选秀选人, 1, "" + player.getGrade().ordinal());
            }
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code.code).build(),
                ServiceCode.LocalDraftManager_signPlayer, rid);
        }).start();
    }

    /**
     * 选秀流程结束自动签约
     *
     * @param teamId
     * @param playerId
     * @param price
     * @param pt
     */
    @RPCMethod(code = CrossCode.LocalDraftManager_autoSignPlayer, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void autoSignPlayer(long teamId, int playerId, int price, PlayerTalent pt) {
        log.debug("选秀自动签约 球队{}, 球员={}, 身价={}", teamId, playerId, price);
        pt.setTeamId(teamId);
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        pt.setId(tp.getNewTalentId());
        pt.save();
        tp.putPlayerTalent(pt);
        beSignManager.addBeSignPlayer(teamId, playerId, price, pt.getId(), ModuleLog.getModuleLog(EModuleCode.选秀, "自动签约"));
        PlayerBean player = PlayerConsole.getPlayerBean(playerId);
        taskManager.updateTask(teamId, ETaskCondition.选秀选人, 1, "" + player.getGrade().ordinal());
    }

    @ClientMethod(code = ServiceCode.LocalDraftManager_opendCard)
    public void opendCard(int index) {
        //		long teamId,int roomId,int index
        long teamId = getTeamId();
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            //			long teamId,int roomId,int index
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_openCard, null, tid, teamId, roomId, index);
        }).appendTask((tid, maps, args) -> {
            ErrorCode code = (ErrorCode) args[0];
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(code.code).build(), ServiceCode.LocalDraftManager_opendCard, rid);
        }).start();
    }

    /**
     * 显示选秀房间初始数据
     * 根据不同阶段，推送不同数据
     */
    @ClientMethod(code = ServiceCode.LocalDraftManager_showDraftRoomMain)
    public void showDraftRoomMain() {
        long teamId = getTeamId();
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_showDraftRoomMain, null, tid, roomId);
        }).appendTask((tid, maps, args) -> {
            if (args == null || args.length == 0) {
                sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Fail.code).build(), ServiceCode.LocalDraftManager_showDraftRoomMain, rid);
                return;
            }

            EDraftStage stage = (EDraftStage) args[0];
            if (stage == EDraftStage.等待玩家) {
                DraftPB.DraftRoomReadyMain data = (DraftPB.DraftRoomReadyMain) args[1];
                sendMessage(teamId, data, ServiceCode.LocalDraftManager_showDraftRoomReadyMain);
            } else if (stage == EDraftStage.抽签阶段) {
                DraftPB.DraftRoomOrderMain data = (DraftPB.DraftRoomOrderMain) args[1];
                sendMessage(teamId, data, ServiceCode.LocalDraftManager_showDraftRoomOrderMain);
            } else if (stage == EDraftStage.选人阶段) {
                DraftPB.DraftRoomPlayerMain data = (DraftPB.DraftRoomPlayerMain) args[1];
                sendMessage(teamId, data, ServiceCode.LocalDraftManager_showDraftRoomPlayerMain);
                tipCurrTeamDraft(data);
            }
            ServiceManager.addService(ServiceConsole.getDraftRoomKey(roomId), teamId);
            //默认回包
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build()
                , ServiceCode.LocalDraftManager_showDraftRoomMain, rid);
        }).start();
    }
    
    /**
     * 选秀,选人阶段,给轮到当前选秀玩家一个提示消息.
     * @param data
     */
    private void tipCurrTeamDraft(DraftPB.DraftRoomPlayerMain data) {
		DraftRoomPlayerStageData stageInfo = data.getStageInfo();
		int curOrder = stageInfo.getCurOrder();
		DraftRoomTeamData teamData = data.getTeamsList().stream().filter( obj -> obj.getOrder() == curOrder).findFirst().orElse(null);
		if (teamData == null) {
			return;
		}
		
		sendMessage(teamData.getTeamId(), DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(data.getRoomLevel()).build(),
				ServiceCode.LocalDraftManager_currPlayerDraftTip);
	}

    /**
     * 接收选秀房间数据推送
     */
    @RPCMethod(code = CrossCode.LocalDraftManager_tipDraft, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void tipDraftMain(DraftPB.DraftRoomMainData mainData, DraftPB.DraftRoomTeamData teamData) {
        //告诉最外层界面玩家，数据变动
        sendMessage(ServiceConsole.getDraftMainKey(), mainData, ServiceCode.Push_Draft_Main);
        //通知房间内玩家，信息变动.该信息为人数变动
        sendMessage(ServiceConsole.getDraftRoomKey(mainData.getRoomId()), teamData, ServiceCode.Push_Draft_Room_Read);
        //log.debug("~~~~~~~~~~有人加入房间咯~~人数={}~~~~~~~~~", mainData.getTeamCount());
    }

    @RPCMethod(code = CrossCode.LocalDraftManager_tipDraftCard, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void tipDraftCard(int roomId, int index, int order) {
        //通知正在房间内的玩家，卡牌被翻
        sendMessage(ServiceConsole.getDraftRoomKey(roomId), DefaultPB.DefaultData.newBuilder().setMsg(index + "," + order).build(), ServiceCode.Push_Draft_Room_Order);
    }

    @RPCMethod(code = CrossCode.LocalDraftManager_tipDraftStage, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void tipDraftStage(int roomId) {
        //通知选秀阶段变更,客户端自己调用显示选秀房列表进行
        //log.debug("选秀阶段变动推送100084 ： roomid {}, user {}", roomId, ServiceManager.getUsers(ServiceConsole.getDraftStageKey(roomId)));
        sendMessage(ServiceConsole.getDraftStageKey(roomId), DefaultPB.DefaultData.newBuilder().setCode(0).build(), ServiceCode.Push_Draft_Room_Stage);
    }

    @RPCMethod(code = CrossCode.LocalDraftManager_tipDraftPlayer, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void tipDraftPlayer(int roomId, int playerId, String signTeam) {
        //通知正在房间内的玩家，球员被签。客户端自行更换下一个顺位玩家
        sendMessage(ServiceConsole.getDraftRoomKey(roomId), DefaultPB.DefaultData.newBuilder().setMsg(playerId + "," + signTeam).build(), ServiceCode.Push_Draft_Room_Player);
    }

    @RPCMethod(code = CrossCode.LocalDraftManager_tipDraftEnd, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void tipDraftEnd(int roomId) {
        teamStatusManager.draftRoomEnd(roomId);
        //选秀结束，推送消息到客户端
        DefaultData endResp = DefaultData.newBuilder().setCode(0).setBigNum(roomId).build();
        sendMessage(ServiceConsole.getDraftStageKey(roomId), endResp, ServiceCode.Push_Draft_Room_End);
        sendMessage(ServiceConsole.getDraftMainKey(), endResp, ServiceCode.Push_Draft_Room_End);
        ServiceManager.removeService(ServiceConsole.getDraftStageKey(roomId));
    }

    /**
     * 参加选秀
     * 最后一个人自动弹出
     *
     * @param roomLevel
     */
    @ClientMethod(code = ServiceCode.LocalDraftManager_joinDraft)
    public synchronized void joinDraft(int roomLevel) {
        long teamId = getTeamId();
        //判断消耗
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        if (roomId != 0 || reqRoom.contains(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Draft_0.code).build());
            return;
        }
        //
        DraftRoomBean bean = DraftConsole.getDraftRoomBean(roomLevel);
        Team team = teamManager.getTeam(teamId);
        if (team.getLevel() < bean.getLimitMinLevel() || team.getLevel() > bean.getLimitMaxLevel()) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_0.code).build());
            return;
        }
        //
        int count = getDayCount(teamId) + 1;
        if (roomLevel != 4 && count > _maxDayCount) {
            log.debug("超过当天最大选秀上限");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_1.code).build());
            return;
        }
        int freeCount = getFreeDraftCount(teamId);
        int vipCount = buffManager.getBuffSet(teamId, EBuffType.每日选秀高级选秀免费次数).getValueSum();
        if (roomLevel != 2 && freeCount >= vipCount) {
            if (roomLevel == 1) {
                TeamMoney tm = teamMoneyManager.getTeamMoney(teamId);
                boolean ok = teamMoneyManager.checkTeamMoney(tm, _draftMoney, 0, 0, 0);
                if (!ok) {
                    sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
                    return;
                }
            } else if (bean.getConsumeProp() != null && bean.getConsumeProp().size() > 0) {
                if (!propManager.getTeamProp(teamId).checkPropListNum(bean.getConsumeProp())) {
                    sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Prop_0.code).build());
                    return;
                }
            }
        }
        //等待跨服返回确切房间ID，先修改不为0，避免客户端重复调用导致的数据异常.只保存在内存中。
        reqRoom.add(teamId);
        //发送加入房间请求
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_joinDraft, null, tid
                , new TeamNode(team.getTeamId(), GameSource.serverName)
                , team.getName()
                , team.getLogo()
                , roomLevel);

        }).appendTask((tid, maps, args) -> {
            reqRoom.remove(teamId);
            //
            int newRoomId = (int) args[0];
            ErrorCode status = (ErrorCode) args[1];
            if (status == ErrorCode.Success) {
                // 扣东西要放这里
                draftPay(teamId, roomLevel);
                log.debug("球队{}进入选秀房间{}", teamId, newRoomId);
                //获得房间号，加入成功。将数据放入缓存，保证离线后仍然能拉取数据
                teamStatusManager.addTeamToDraftRoom(newRoomId, teamId);
                ServiceManager.addService(ServiceConsole.getDraftStageKey(newRoomId), team.getTeamId());
                if (roomLevel != 4) {
                    saveDayCount(team.getTeamId(), count);
                }
                //返回响应消息
                sendMessage(team.getTeamId(), DefaultPB.DefaultData.newBuilder().setCode(0).setMsg(newRoomId + "," + roomLevel).build()
                    , ServiceCode.LocalDraftManager_joinDraft, rid);
                taskManager.updateTask(teamId, ETaskCondition.选秀, 1, EModuleCode.选秀.getName());
            }
            //
            sendMessage(teamId, DefaultPB.DefaultData.newBuilder().setCode(status.code).build(), ServiceCode.LocalDraftManager_joinDraft);
        }).start();
    }

    /**
     * 超时关闭房间
     *
     * @param teamId
     * @param roomLevel
     */
    @RPCMethod(code = CrossCode.LocalDraftManager_roomTimeoutEnd, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void roomTimeoutEnd(long teamId, int roomLevel) {
        log.warn("新秀选秀房 {} 退回报名道具 , team : {} ", roomLevel, teamId);
        if (roomLevel != 4 && roomLevel != 2) {
            return;
        }
        
        String title = (roomLevel == 4 ? "新秀选秀报名返还" : "高级选秀报名返还");
        DraftRoomBean roomBean = DraftConsole.getDraftRoomBean(roomLevel);
        String awardConfig = PropSimple.getPropStringByList(roomBean.getConsumeProp());
        teamEmailManager.sendEmailFinal(teamId, 0, 40011, title, "", awardConfig);
    }

    /**
     * GM命令加入选秀房.
     *
     * @param roomLevel
     * @param teamId
     */
    public void gmJoinRoom(int roomLevel, long teamId) {
        Team team = teamManager.getTeam(teamId);
        if (team == null) {
            return;
        }
        reqRoom.add(teamId);
        //发送加入房间请求
        int rid = getRid();
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_joinDraft, null, tid
                , new TeamNode(team.getTeamId(), GameSource.serverName)
                , team.getName()
                , team.getLogo()
                , roomLevel);

        }).appendTask((tid, maps, args) -> {
            reqRoom.remove(teamId);
            //
            int newRoomId = (int) args[0];
            ErrorCode status = (ErrorCode) args[1];
            if (status == ErrorCode.Success) {
                // 扣东西要放这里
                log.debug("球队{}进入选秀房间{}", teamId, newRoomId);
                //获得房间号，加入成功。将数据放入缓存，保证离线后仍然能拉取数据
                teamStatusManager.addTeamToDraftRoom(newRoomId, teamId);
                ServiceManager.addService(ServiceConsole.getDraftStageKey(newRoomId), team.getTeamId());
                //返回响应消息
                sendMessage(team.getTeamId(), DefaultPB.DefaultData.newBuilder().setCode(0).setMsg(newRoomId + "," + roomLevel).build()
                    , ServiceCode.LocalDraftManager_joinDraft, rid);
                taskManager.updateTask(teamId, ETaskCondition.选秀, 1, EModuleCode.选秀.getName());
                log.warn("tid {} join draft room {} success", teamId, roomLevel);
            } else {
                log.warn("tid {} join draft room {} fail code {}", teamId, roomLevel, status.code);
            }
            //
        }).start();
    }

    private void draftPay(long teamId, int roomLevel) {
        DraftRoomBean bean = DraftConsole.getDraftRoomBean(roomLevel);
        int freeCount = getFreeDraftCount(teamId);
        int vipCount = buffManager.getBuffSet(teamId, EBuffType.每日选秀高级选秀免费次数).getValueSum();
        if (roomLevel == 2 && freeCount < vipCount) {
            saveFreeDraftCount(teamId, freeCount + 1);
        } else {
            if (roomLevel == 1) {
                teamMoneyManager.updateTeamMoney(teamId, 0, -_draftMoney, 0, 0, true, ModuleLog.getModuleLog(EModuleCode.选秀, ""));
            } else if (bean.getConsumeProp() != null && bean.getConsumeProp().size() > 0) {
                propManager.delProp(teamId, bean.getConsumeProp(), true, true);
            }
        }
    }

    @ClientMethod(code = ServiceCode.LocalDraftManager_quitDraftMain)
    public void quitDraftMain() {
        long teamId = getTeamId();
        ServiceManager.removeService(ServiceConsole.getDraftMainKey(), teamId);
    }

    @ClientMethod(code = ServiceCode.LocalDraftManager_quitDraftRoom)
    public void quitDraftRoom() {
        long teamId = getTeamId();
        int roomId = teamStatusManager.getDraftRoomId(teamId);
        if (roomId > 0) {
            ServiceManager.removeService(ServiceConsole.getDraftRoomKey(roomId), teamId);
        }
    }

    @Override
    public void initConfig() {
        _maxDayCount = ConfigConsole.getIntVal(EConfigKey.Draft_Day_Count);
        _draftMoney = ConfigConsole.getIntVal(EConfigKey.Draft_Need_1);
    }

    @Override
    public void instanceAfter() {
        helpPlayerIdMap = Maps.newConcurrentMap();
        helpPlayerTalentMap = Maps.newConcurrentMap();
        reqRoom = Sets.newConcurrentHashSet();
        EventBusManager.register(EEventType.服务器节点注册, this);
        // 同步房间
        syncCrossRoomIds();
    }

    //	/**
    //	 * 等级房间的免费次数
    //	 * @return
    //	 */
    //	private int getFreeCount(long teamId, int level) {
    //		int count = buffManager.getBuffSet(teamId, EBuffType.每日选秀高级选秀免费次数).getValueSum();
    //		String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Draft_Free_Count + level + "_");
    //		int use = redis.getIntNullIsZero(key);
    //		return count - use;
    //	}
    //	/**
    //	 * 保存使用次数
    //	 * @param teamId
    //	 * @param count
    //	 */
    //	private void saveUseCountAdd(long teamId, int level, int count) {
    //		String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Draft_Free_Count + level + "_");
    //		redis.set(key, redis.getIntNullIsZero(key) + count,RedisKey.DAY);
    //	}

    /**
     * 等级房间的免费次数
     *
     * @return
     */
    public int getDayCount(long teamId) {
        String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Draft_Day_Count);
        int use = redis.getIntNullIsZero(key);
        return use;
    }

    /**
     * 保存使用次数
     *
     * @param teamId
     * @param count
     */
    private void saveDayCount(long teamId, int count) {
        String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Draft_Day_Count);
        redis.set(key, "" + count, RedisKey.DAY);
    }

    /**
     * 每天使用的选秀次数
     */
    public int getUsedDraftCount(long teamId) {
        return getDayCount(teamId) + getFreeDraftCount(teamId);
    }

    /**
     * 节点注册回调
     *
     * @param server
     */
    @Subscribe
    private void nodeRegisterCall(RPCServer server) {
        // 若果是逻辑节点收到PK节点的注册信息，则检查是否拉取BattleId
        if (EServerNode.Cross.equals(server.getPool()) && GameSource.pool.equals(EServerNode.Logic)) {
            log.warn("游戏服{}收到Corss节点注册, 同步选秀房状态", GameSource.serverName);
            syncCrossRoomIds();
        }
    }

    private void syncCrossRoomIds() {
        // 房间不存在，则清空
        log.info("~~~~~~~~~~~~~~~~同步跨服选秀房间~~~~~~~~~~~~~~~");
        RPCLinkedTask.build().appendTask((tid, maps, args) -> {
            RPCMessageManager.sendLinkedTaskMessage(CrossCode.DraftManager_getDraftRoomList, null, tid, GameSource.serverName);
        })
            //
            .appendTask((tid, maps, args) -> {
                @SuppressWarnings("unchecked")
                List<RpcDraftRoom> rooms = (ArrayList<RpcDraftRoom>) args[0];
                teamStatusManager.syncRoomMap(rooms);
            }).start();
    }

}

