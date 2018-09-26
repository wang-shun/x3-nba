package com.ftkj.manager.cross;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DraftConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EDraftStage;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.draft.DraftPlayer;
import com.ftkj.manager.draft.DraftRoom;
import com.ftkj.manager.draft.DraftRoomBean;
import com.ftkj.manager.draft.DraftRoomProduce;
import com.ftkj.manager.draft.DraftTeam;
import com.ftkj.manager.draft.RpcDraftRoom;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.proto.DraftPB;
import com.ftkj.proto.PlayerPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.RPCMessageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang
 * 2017年5月5日
 * 跨服选秀管理
 */
public class DraftManager extends BaseManager {

    private Map<Integer, DraftRoomProduce> mainRoomMap;

    private Map<Integer, DraftRoom> runningRoomMap;

    private AtomicInteger roomids;

    
    private DropBean _initDrop;
    /**
     * 显示当前选秀房间
     */
    @RPCMethod(code = CrossCode.DraftManager_showDraftMain, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void showDraftMain(int roomId) {
        ArrayList<DraftPB.DraftRoomMainData> roomsData = Lists.newArrayList();
        mainRoomMap.values().forEach(rooms -> roomsData.add(getDraftRoomMainData(rooms.getCurRoom())));
        DraftRoom room = getDraftRoom(roomId);
        //
        DraftPB.DraftRoomMainData myRoom = getDraftRoomMainData(room);
        RPCMessageManager.responseMessage(roomsData, myRoom);
    }
    
    @RPCMethod(code = CrossCode.DraftManager_getDraftRoomList, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void getDraftRoomList(String node) {
    	List<RpcDraftRoom> rooms = mainRoomMap.values().stream()
    		.filter(room-> room.getCurRoom().getStage() != EDraftStage.结束)
    		.filter(room-> room.getCurRoom().getNodes().contains(node))
    		.map(rp -> new RpcDraftRoom(rp.getCurRoom().getRoomId(), 
    				rp.getCurRoom().getTeamList().stream().map(t->t.getTeamId())
    				.collect(Collectors.toList()))).collect(Collectors.toList());
    	ArrayList<RpcDraftRoom> roomList = Lists.newArrayList(rooms);
    	RPCMessageManager.responseMessage(roomList);
    }
    
    /**
     * 加入选秀房间
     *
     * @param team
     * @param teamName
     * @param logo
     * @param roomLevel
     */
    @RPCMethod(code = CrossCode.DraftManager_joinDraft, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void joinDraft(TeamNode team, String teamName, String logo, int roomLevel) {
        DraftRoomProduce pro = mainRoomMap.get(roomLevel);
        if (pro == null) { return; }
        synchronized (pro) {
            DraftRoom room = pro.getCurRoom();
            DraftRoomBean bean = pro.getRoomBean();
            if(room.getStage() == EDraftStage.结束 
            			&& room.getEndTime() != null
            			&& room.getEndTime().plusSeconds(bean.getCdTime()).isBeforeNow()
            			&& bean.getTime().indexOf(DateTime.now().getHourOfDay()+"") != -1){
            		// 新秀房有限制时间段，只存在一个房间
            		pro.setCurRoom(createRoom(bean));
            }
            // 取最新房间
            room = pro.getCurRoom();
            // 房间有位置
            if(room.getTeamList().size() < pro.getRoomBean().getMaxPlayerCount()) {
            	DraftTeam dt = new DraftTeam(team.getTeamId(), teamName, logo, team.getNodeName());
            	room.putTeam(dt);
            	//将消息推送到所有的本地逻辑服务器。
                RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraft, room.getNodes(),
                        getDraftRoomMainData(room), getDraftRoomTeamData(dt));
                // 通知加入房间成功
                RPCMessageManager.responseMessage(room.getRoomId(), ErrorCode.Success);
            }else {
            	// 房间已满
            	RPCMessageManager.responseMessage(room.getRoomId(), ErrorCode.Draft_2);
            }
            // 是否开启选秀,参加选秀玩家已经足够，更新房间状态为:抽签阶段
            if (room.getTeamList().size() >= pro.getRoomBean().getMaxPlayerCount()
            		&& room.getStage() == EDraftStage.等待玩家) {
//              room.start();
                room.start(_initDrop);
                //将房间放入运行集合中
                runningRoomMap.put(room.getRoomId(), room);
                // 如果是没有CD的房间，提前创建房间
            	if(bean.getMaxRoom() < 0) {
            		pro.setCurRoom(createRoom(bean));
            	}
            }
        }
    }

    /**
     * 显示选秀房间内数据
     */
    @RPCMethod(code = CrossCode.DraftManager_showDraftRoomMain, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void showDraftRoomMain(int roomId) {
        DraftRoom room = getDraftRoom(roomId);
        if (room == null) {
            RPCMessageManager.responseMessage();
            return;
        }
        //
        EDraftStage stage = room.getStage();

        List<DraftPB.DraftRoomTeamData> teams = Lists.newArrayList();
        room.getTeamList().forEach(dt -> teams.add(getDraftRoomTeamData(dt)));

        if (stage == EDraftStage.等待玩家) {
            DraftPB.DraftRoomReadyMain data = DraftPB.DraftRoomReadyMain.newBuilder().addAllTeams(teams).build();
            RPCMessageManager.responseMessage(stage, data);
        } else if (stage == EDraftStage.抽签阶段) {
            List<DraftPB.DraftRoomOrderData> orders = Lists.newArrayList();
            for (int i = 0; i < room.getCardIndex().length; i++) {
                orders.add(DraftPB.DraftRoomOrderData.newBuilder().setIndex(i).setOrder(room.getCardIndex()[i]).build());
            }
            DraftPB.DraftRoomOrderMain data = DraftPB.DraftRoomOrderMain.newBuilder().addAllTeams(teams).addAllOrders(orders).setSecond(room.getNextSecond()).build();
            RPCMessageManager.responseMessage(stage, data);
        } else if (stage == EDraftStage.选人阶段) {
            List<DraftPB.DraftRoomPlayerData> players = Lists.newArrayList();
            room.getPlayers().forEach(player -> players.add(DraftPB.DraftRoomPlayerData.newBuilder()
            		.setPlayerId(player.getPlayerId())
            		.setSignTeamName(player.getSignTeamName())
            		.setTalent(getPlayerTalentData(player.getPt()))
            		.build()));
            DraftPB.DraftRoomPlayerMain data = DraftPB.DraftRoomPlayerMain.newBuilder()
                    .addAllTeams(teams)
                    .addAllPlayers(players)
                    .setStageInfo(getDraftRoomPlayerStageData(room.getNextSecond(), room.getCurOrder()))
                    .setRoomLevel(room.getRoomLevel())
                    .build();

            RPCMessageManager.responseMessage(stage, data);
        }
    }
    
    public PlayerPB.PlayerTalentData getPlayerTalentData(PlayerTalent pt){
    	return PlayerPB.PlayerTalentData.newBuilder()
    			.setDf(pt.getDf())
    			.setFqmz(pt.getFqmz())
    			.setGm(pt.getGm())
    			.setLb(pt.getLb())
    			.setQd(pt.getQd())
    			.setSfmz(pt.getSfmz())
    			.setTlmz(pt.getTlmz())
    			.setZg(pt.getZg())
    			.build();
    }

    @RPCMethod(code = CrossCode.DraftManager_openCard, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void opendCard(long teamId, int roomId, int index) {
        DraftRoom room = getDraftRoom(roomId);
        if (room == null || index < 0 || room.getStage() != EDraftStage.抽签阶段) {//房间不存在
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }
        DraftTeam team = room.getDraftTeam(teamId);
        if (team == null || index >= room.getCardIndex().length) {//玩家不存在
            RPCMessageManager.responseMessage(ErrorCode.Error);
            return;
        }

        synchronized (room) {
            if (room.getCardIndex()[index] > 0) {
                RPCMessageManager.responseMessage(ErrorCode.Draft_1);
                return;
            }
            room.getCardIndex()[index] = team.getOrder();
            //通知客户端更新数据
            RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftCard, room.getNodes(), roomId, index, team.getOrder());
            //判断是否全部选完，如果是的话修改当前状态，和倒计时
            boolean isAllOpend = true;
            for (int o : room.getCardIndex()) {
                if (o < 0) {
                    isAllOpend = false;
                    break;
                }
            }
            if (isAllOpend) {//所有签都已经翻开，修改当前阶段
                room.updateStage(EDraftStage.选人阶段);
            }
            RPCMessageManager.responseMessage(ErrorCode.Success);
        }
    }

    /**
     * 选秀签约
     * @param teamId
     * @param roomId
     * @param playerId
     */
    @RPCMethod(code = CrossCode.DraftManager_signPlayer, pool = EServerNode.Cross, type = ERPCType.MASTER)
    public void signPlayer(long teamId, int roomId, int playerId) {
        DraftRoom room = getDraftRoom(roomId);
        if (room == null || room.getStage() != EDraftStage.选人阶段) {//房间不存在
            RPCMessageManager.responseMessage(ErrorCode.Error, 0);
            return;
        }
        DraftTeam team = room.getDraftTeam(teamId);
        if (team == null) {//玩家不存在
            RPCMessageManager.responseMessage(ErrorCode.Error, 0);
            return;
        }
        DraftPlayer player = room.getPlayer(playerId);
        if (player == null || !"".equals(player.getSignTeamName())) {//球员不存在或者已经被签约
            RPCMessageManager.responseMessage(ErrorCode.Error, 0);
            return;
        }
        if (team.getOrder() != room.getCurOrder()) {
            RPCMessageManager.responseMessage(ErrorCode.Error, 0);
            return;
        }
        //
        room.signPlayer(playerId, team);
        room.startNext(team.getOrder());
        RPCMessageManager.responseMessage(ErrorCode.Success, player.getPrice(),player.getPt());
        //int roomId,int playerId,String signTeam
        //通知所有房间，球员被签
        RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftPlayer, room.getNodes(), roomId, playerId, team.getTeamName());
    }

    /**
     * 定时器，每秒执行一次，执行房间流程
     */
    public void execute() {
        log.debug("选秀流程线程执行");
        // 没有开始的新秀房
        mainRoomMap.values().stream()
        	.map(room-> room.getCurRoom())
        	.filter(room-> room != null)
        	.filter(room-> room.getStage() == EDraftStage.等待玩家)
        	.filter(room-> room.getEndTimeMillis() > 0 && DateTime.now().getMillis() >  room.getEndTimeMillis())
        	.forEach(room-> {
        		log.warn("draft room lv:{}, id:{} sign timeout, just end", room.getRoomLevel(), room.getRoomId());
        		List<DraftTeam> list = new ArrayList<DraftTeam>(room.getTeamList());
        		// 结束房间，返还道具
        		room.updateStage(EDraftStage.结束);
        		RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftEnd, room.getNodes(), room.getRoomId());
        		// 返还
        		list.forEach(team-> {
        			RPCMessageManager.sendMessage(CrossCode.LocalDraftManager_roomTimeoutEnd, team.getNodeName(), team.getTeamId(), room.getRoomLevel());
        		});
        	});
        //
        runningRoomMap.values().stream()//抽签时限结束，自动修改房间状态
                .filter(room -> room.getStage() == EDraftStage.选人阶段)
                .filter(room -> room.getCurOrder() == 0)
                .forEach(room -> {
                    room.setCurOrder(1);
                    RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftStage, room.getNodes(), room.getRoomId());
                    //log.warn("roomid {}, 选人阶段 调用 {}, 节点 {} " , room.getRoomId(), CrossCode.LocalDraftManager_tipDraftStage, room.getNodes());
                });

      runningRoomMap.values().stream()//抽签时限结束，自动修改房间状态
      .filter(room -> room.getStage() == EDraftStage.选人阶段)
      .filter(room -> room.getCurOrder() != 0 && room.getCurOrder() != 999)
      .filter(room -> room.getNextSecond() <= 0)
      .forEach(room -> {
          //DraftPlayer player = room.getPlayers().stream().filter(tmp -> "".equals(tmp.getSignTeamName())).findFirst().orElse(null);
          DraftTeam team = room.getCurTeam();
          if (team == null) {
              room.updateStage(EDraftStage.结束);
              RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftEnd, room.getNodes(), room.getRoomId());
              return;
          }
          // 设置自动抽取，然后让下个人开始选
          team.setAuto(true);
          room.startNext(team.getOrder());
          RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftPlayer, room.getNodes(), room.getRoomId(), 0, team.getTeamName());
      });
      
      // 自动选人的，发完就结束
      runningRoomMap.values().stream()
      .filter(room -> room.getStage() == EDraftStage.选人阶段)
      .filter(room -> room.getCurOrder() == 999)
      .filter(room -> room.getNextSecond() <= 0)
      .forEach(room -> {
    	  room.getAutoSignTeam().stream().sorted(Comparator.comparingInt(DraftTeam::getOrder)).forEach(team-> {
    		  DraftPlayer player = room.getPlayers().stream().filter(tmp -> "".equals(tmp.getSignTeamName())).findFirst().orElse(null);
    		  if (player != null) {
                  room.signPlayer(player.getPlayerId(), team);
                  RPCMessageManager.sendMessage(CrossCode.LocalDraftManager_autoSignPlayer, team.getNodeName(), team.getTeamId(), player.getPlayerId(), player.getPrice(),player.getPt());
    		  }
    	  });
          room.updateStage(EDraftStage.结束);
          RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftEnd, room.getNodes(), room.getRoomId());
      });
        // 老版的自动选人。
//        runningRoomMap.values().stream()//抽签时限结束，自动修改房间状态
//                .filter(room -> room.getStage() == EDraftStage.选人阶段)
//                .filter(room -> room.getCurOrder() != 0)
//                .filter(room -> room.getNextSecond() <= 0)
//                .forEach(room -> {
//                    DraftPlayer player = room.getPlayers().stream().filter(tmp -> "".equals(tmp.getSignTeamName())).findFirst().orElse(null);
//                    DraftTeam team = room.getCurTeam();
//                    if (team == null || player == null) {
//                        room.updateStage(EDraftStage.结束);
//                        RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftEnd, room.getNodes(), room.getRoomId());
//                        //log.warn("roomid {}, 结束 调用 {}, 节点 {} " , room.getRoomId(), CrossCode.LocalDraftManager_tipDraftEnd, room.getNodes());
//                        return;
//                    }
//                    room.signPlayer(player.getPlayerId(), team);
//                    //发送回本地，自动签约
//                    RPCMessageManager.sendMessage(CrossCode.LocalDraftManager_autoSignPlayer, team.getNodeName(), team.getTeamId(), player.getPlayerId(), player.getPrice(),player.getPt());
//                    //log.warn("roomid {}, 选人 自动签约 调用 {}, 节点 {} " , room.getRoomId(), CrossCode.LocalDraftManager_autoSignPlayer, team.getNodeName());
//                    //发送到监听的节点,发现球员变动
//                    RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftPlayer, room.getNodes(), room.getRoomId(), player.getPlayerId(), team.getTeamName());
//                    //log.warn("roomid {}, 选人 球员被签约通知 调用 {} , 节点 {}" , room.getRoomId(), CrossCode.LocalDraftManager_tipDraftPlayer, room.getNodes());
//                });

        runningRoomMap.values().stream()//刚进入抽签阶段的选秀房间
                .filter(room -> room.getStage() == EDraftStage.抽签阶段)
                .filter(room -> room.getCurOrder() == 0)
                .forEach(room -> {
                    room.setCurOrder(1);
                    RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftStage, room.getNodes(), room.getRoomId());
                    //log.warn("roomid {}, 抽签阶段 调用 {} , 节点 {}" , room.getRoomId(), CrossCode.LocalDraftManager_tipDraftStage, room.getNodes());
                });

        runningRoomMap.values().stream()//抽签时限结束，自动修改房间状态
                .filter(room -> room.getStage() == EDraftStage.抽签阶段)
                .filter(room -> room.getCurOrder() == 1)
                .filter(room -> room.getNextSecond() <= 0)
                .forEach(room -> {
                    room.updateStage(EDraftStage.选人阶段);
                    RPCMessageManager.sendMessageNodes(CrossCode.LocalDraftManager_tipDraftStage, room.getNodes(), room.getRoomId());
                    //log.warn("roomid {}, 选人阶段 调用 {} , 节点 {}" , room.getRoomId(), CrossCode.LocalDraftManager_tipDraftStage, room.getNodes());
                });

    }

    private DraftPB.DraftRoomPlayerStageData getDraftRoomPlayerStageData(int second, int order) {
        return DraftPB.DraftRoomPlayerStageData.newBuilder().setCurOrder(order).setSecond(second).build();
    }

    private DraftRoom getDraftRoom(int roomId) {
        return mainRoomMap.values()
                .stream()
                .map(r -> r.getCurRoom())
                .filter(r -> r.getRoomId() == roomId)
                .findFirst()
                .orElseGet(() -> runningRoomMap.get(roomId));
    }

    @Override
	public void initConfig() {
    	 _initDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Build_Refresh_Talent_Drop));
	}

	private DraftPB.DraftRoomTeamData getDraftRoomTeamData(DraftTeam dt) {

        return DraftPB.DraftRoomTeamData.newBuilder()
                .setLogo(dt.getLogo())
                .setOrder(dt.getOrder())
                .setShardName(dt.getNodeName())
                .setTeamId(dt.getTeamId())
                .setTeamName(dt.getTeamName())
                .build();
    }

	/**
	 * 创建选秀房间
	 * @param bean
	 * @return
	 */
    private DraftRoom createRoom(DraftRoomBean bean) {
        List<PropSimple> ps = bean.getDrop().roll();
//        List<DraftPlayer> players = null;
//        players = ps.stream().map(p -> (PropPlayerBean) (PropConsole.getProp(p.getPropId())))
//                .distinct()
//                .map(p -> PlayerConsole.getPlayerBean(p.getHeroId()))
//                .map(p -> new DraftPlayer(p.getPlayerRid(), p.getPrice(),PlayerTalent.createPlayerTalent(0, p.getPlayerRid(), 0,_initDrop, false)))
//                .collect(Collectors.toList());
//        players = players.stream().sorted((a, b) -> b.getPrice() - a.getPrice()).collect(Collectors.toList());//返回按身价降序排序的集合
//        log.info("开始创建选秀房，选秀房等级是:{}", bean.getRoomLevel());
//        players.forEach(player -> {log.info("生成的球员Id:{},球员的工资:{}", player.getPlayerId(),player.getPrice());});
//        log.info("创建选秀房结束，选秀房等级是:{}", bean.getRoomLevel());
//        return new DraftRoom(getRoomId(), bean.getRoomLevel(), players);
        return new DraftRoom(ps, getRoomId(), bean.getRoomLevel());
    }

    private int getRoomId() {
        return roomids.incrementAndGet();
    }

    private DraftPB.DraftRoomMainData getDraftRoomMainData(DraftRoom room) {
        if (room == null) {
            return DraftPB.DraftRoomMainData.newBuilder()
                    .setRoomId(0)
                    .setRoomLevel(0)
                    .setTeamCount(0)
                    .setStatus(EDraftStage.结束.getId())
                    .setLastEndTime(0)
                    .build();
        }
        return DraftPB.DraftRoomMainData.newBuilder()
                .setRoomLevel(room.getRoomLevel())
                .setTeamCount(room.getTeamList().size())
                .setRoomId(room.getRoomId())
                .setStatus(room.getStage().getId())
                .setLastEndTime(room.getEndTimeMillis())
                .build();
    }

    @Override
    public void instanceAfter() {
        roomids = new AtomicInteger();
        mainRoomMap = Maps.newConcurrentMap();
        runningRoomMap = Maps.newConcurrentMap();
        _initDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Build_Refresh_Talent_Drop));
        //
        DraftConsole.getDraftList().forEach(room -> mainRoomMap.put(room.getRoomLevel(), new DraftRoomProduce(room, createRoom(room))));

    }

	public DropBean get_initDrop() {
		return _initDrop;
	}
    
}
