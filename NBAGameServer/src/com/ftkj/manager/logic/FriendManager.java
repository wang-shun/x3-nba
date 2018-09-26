package com.ftkj.manager.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.FriendConsole;
import com.ftkj.db.ao.logic.IFriendAO;
import com.ftkj.db.domain.FriendPO;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERank;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.battle.BaseBattleHandle;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.manager.battle.BattleEnd;
import com.ftkj.manager.battle.BattleHandle;
import com.ftkj.manager.battle.handle.BattleCommon;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.friend.TeamFriends;
import com.ftkj.manager.friend.bean.Friend;
import com.ftkj.manager.logic.LocalBattleManager.BattleContxt;
import com.ftkj.manager.rank.TeamRank;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.FriendPB;
import com.ftkj.proto.FriendPB.CompareNotesDeal;
import com.ftkj.proto.FriendPB.CompareNotesInfo;
import com.ftkj.proto.FriendPB.InitiateCompareNotes;
import com.ftkj.server.GameSource;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FriendManager extends BaseManager implements OfflineOperation {

	@IOC
	private TeamManager teamManager;

	@IOC
	private IFriendAO friendAO;

	@IOC
	private TaskManager taskManager;

	@IOC
	private RankManager rankManger;

	@IOC
	private LeagueManager leagueManager;

	@IOC
	private LocalBattleManager localBattleManager;

	@IOC
	private TeamStatusManager teamStatusManager;
	

	private Map<Long, TeamFriends> teamFriendsMap;

	/**
	 * 等级：在线球队列表
	 */
	private Map<Integer, List<Long>> teamRankList;
	/**
	 * 只保证刷新频率分钟前在线 等级排名在线前50;
	 */
	private List<Long> onlineRankList;
	/** 比赛结束处理 */
	private BattleEnd battleEnd = this::endMatch0;

	@Override
	public void instanceAfter() {
		teamFriendsMap = Maps.newConcurrentMap();
		teamRankList = Maps.newHashMap();
		onlineRankList = Lists.newArrayList();
		// 刷新推荐一次
		refrushReconmend();
	}

	/**
	 * 只有在线才会调用到
	 * 
	 * @param teamId
	 * @return
	 */
	public TeamFriends getTeamFriends(long teamId) {
		TeamFriends tf = teamFriendsMap.get(teamId);
		if (tf == null) {
			List<FriendPO> list = friendAO.getFriendByTeam(teamId);
			tf = new TeamFriends(teamId, list);
			teamFriendsMap.put(teamId, tf);
		}
		return tf;
	}

	@Override
	public void offline(long teamId) {
		teamFriendsMap.remove(teamId);
	}

	@Override
	public void dataGC(long teamId) {
		teamFriendsMap.remove(teamId);
	}

	/**
	 * 刷新推荐列表
	 */
	public void refrushReconmend() {
		// 按等级分类在线列表，每5分钟刷一次
		List<Long> teamList = GameSource.getUsers().stream().mapToLong(u -> u.getTeamId()).boxed()
				.collect(Collectors.toList());
		Map<Integer, List<Long>> map = Maps.newHashMap();
		for (long teamId : teamList) {
			Team t = teamManager.getTeam(teamId);
			if (t == null)
				continue;
			int level = t.getLevel() / 5;
			if (!map.containsKey(level)) {
				map.put(level, Lists.newArrayList());
			}
			map.get(level).add(t.getTeamId());
		}
		teamRankList.clear();
		teamRankList = map;
		// 排名在线列表,高级玩家往前面插
		List<Long> rankList = Lists.newArrayList();
		List<TeamRank> lvRank = rankManger.getRankList(ERank.Team_Lev, 0, 100);
		for (TeamRank t : lvRank) {
			// if(!GameSource.isOline(t.getTeamId())) continue;
			rankList.add(t.getTeamId());
		}
		onlineRankList = rankList;
	}

	/**
	 * 推荐好友
	 * 
	 * @param teamId
	 */
	@ClientMethod(code = ServiceCode.Friend_recommend)
	public void recommend() {
		// refrushReconmend(); //调试
		Team team = teamManager.getTeam(getTeamId());
		List<Long> tList = getMyRecommendTeam(team.getTeamId(), team.getLevel());
		sendMessage(getFindFriendData(tList));
	}

	/**
	 * 打印推荐列表
	 * 
	 * @param teamId
	 * @return
	 */
	private List<Long> getMyRecommendTeam(long teamId, int level) {
		List<Long> myFriends = getTeamFriends(teamId).getFriendList().stream().mapToLong(f -> f.getFriendTeamId())
				.boxed().collect(Collectors.toList());
		int showSize = 10;
		/*
		 * 规则： 1,随机5名在线等级相同 2，排行版随机5名在线 3,根据玩家坐标位置推荐附近玩家
		 */
		List<Long> tempTeam = teamRankList.get(level / 5);
		List<Long> sameLv = null;
		if (tempTeam != null) {
			sameLv = Lists.newArrayList(
					tempTeam.stream().filter(tid -> !myFriends.contains(tid)).collect(Collectors.toList()));
		} else {
			sameLv = Lists.newArrayList();
		}
		if (sameLv == null)
			sameLv = Lists.newArrayList();
		int llevel = level / 5 - 1;
		int hlevel = level / 5 + 1;
		if (sameLv.size() < 20 && teamRankList.containsKey(hlevel))
			sameLv.addAll(teamRankList.get(hlevel));
		if (sameLv.size() < 20 && teamRankList.containsKey(llevel))
			sameLv.addAll(teamRankList.get(llevel));
		// 过滤自己，取20,随机5；
		sameLv = sameLv.stream().filter(tid -> tid != teamId && !myFriends.contains(tid)).limit(20)
				.collect(Collectors.toList());
		if (sameLv.size() > 3) {
			Collections.shuffle(sameLv);
		}
		// 高级玩家往前面插
		int addSize = sameLv.size() >= 5 ? 5 : showSize - sameLv.size();
		List<Long> myNewFriendList = onlineRankList.stream().filter(tid -> !myFriends.contains(tid))
				.collect(Collectors.toList());
		addSize = myNewFriendList.size() >= addSize ? addSize : myNewFriendList.size();
		List<Long> onlineList = myNewFriendList.stream().filter(tid -> GameSource.isOline(tid))
				.collect(Collectors.toList());
		if (onlineList.size() < addSize) {
			int offlineCount = addSize - onlineList.size();
			onlineList.addAll(myNewFriendList.stream().filter(tid -> !GameSource.isOline(tid)).limit(offlineCount)
					.collect(Collectors.toList()));
		}
		Collections.shuffle(onlineList);
		//
		onlineList = Lists.newArrayList(onlineList.subList(0, addSize));
		sameLv.addAll(onlineList);
		sameLv = sameLv.stream().filter(tid -> !myFriends.contains(tid)).distinct().limit(showSize)
				.collect(Collectors.toList());
		return sameLv;
	}

	/**
	 * 添加好友
	 * 
	 * @param friendId
	 */
	@ClientMethod(code = ServiceCode.Friend_nice_add)
	public void addFriend(long friendId) {
		long teamId = getTeamId();
		//
		if (friendId == 0L) {
			log.debug("没找到球队{}", friendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return; // 没有找到球队
		}
		Team friend = teamManager.getTeam(friendId);
		if (friend == null) {
			log.debug("没找到球队{}", friendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return;
		}
		if (teamId == friendId) {
			log.debug("没找到球队{}", friendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return;
		}
		//
		TeamFriends tf = getTeamFriends(teamId);
		if (tf.isFriend(friendId)) {
			log.debug("已经是好友{}", friendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_1.code).build());
			return;
		}
		//
		if (tf.getFriendSize() + 1 > FriendConsole.MAX_FRIEND_COUTN) {
			log.debug("好友上限{}", tf.getFriendSize());
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_3.code).build());
			return;
		}
		tf.addFriend(friendId);
        
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		Team t = teamManager.getTeam(friendId);
		taskManager.updateTask(teamId, ETaskCondition.好友, 1, EModuleCode.好友.getName());

		sendMessage(
				teamId, FriendPB.FriendPushData.newBuilder().setFriend(getFriendData(t, 0))
						.setFriendType(TeamFriends.Friend).setOperation(EStatus.FriendAdd.getId()).build(),
				ServiceCode.Push_Friend);
	}

	/**
	 * 删除好友
	 * 
	 * @param friendId
	 */
	@ClientMethod(code = ServiceCode.Friend_nice_del)
	public void delFriend(long friendId) {
		long teamId = getTeamId();
		//
		if (friendId == 0L) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return; // 没有找到球队
		}
		TeamFriends tf = getTeamFriends(teamId);
		if (!tf.isFriend(friendId)) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_5.code).build());
			return; // 不是好友
		}
		tf.delFriend(friendId);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		Team t = teamManager.getTeam(friendId);
		sendMessage(
				teamId, FriendPB.FriendPushData.newBuilder().setFriend(getFriendData(t, 0))
						.setFriendType(TeamFriends.Friend).setOperation(EStatus.FriendDel.getId()).build(),
				ServiceCode.Push_Friend);
	}

	/**
	 * 添加到黑名单
	 * 
	 * @param friendId
	 */
	@ClientMethod(code = ServiceCode.Friend_balck_add)
	public void addBlackTeam(long friendId) {
		long teamId = getTeamId();
		//
		if (friendId == 0L) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return; // 没有找到球队
		}
		Team friend = teamManager.getTeam(friendId);
		if (friend == null) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return; // 没找到球队
		}
		//
		TeamFriends tf = getTeamFriends(teamId);
		if (tf.isBlackName(friendId)) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_1.code).build());
			return; // 已经是好友
		}
		// 上限
		if (tf.getBlackSize() + 1 > FriendConsole.MAX_FRIEND_COUTN) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_3.code).build());
			return;
		}
		tf.addBlack(friendId);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		Team t = teamManager.getTeam(friendId);
		sendMessage(
				teamId, FriendPB.FriendPushData.newBuilder().setFriend(getFriendData(t, 0))
						.setFriendType(TeamFriends.Black).setOperation(EStatus.FriendAdd.getId()).build(),
				ServiceCode.Push_Friend);
	}

	/**
	 * @param friendTeamId
	 *            删除黑名单
	 * @param friendId
	 */
	@ClientMethod(code = ServiceCode.Friend_balck_del)
	public void delBlackTeam(long friendId) {
		//
		long teamId = getTeamId();
		if (friendId == 0L) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
			return; // 没有找到球队
		}
		TeamFriends tf = getTeamFriends(teamId);
		if (!tf.isBlackName(friendId)) {
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_4.code).build());
			return; // 不是黑名单
		}
		tf.delBlack(friendId);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		Team t = teamManager.getTeam(friendId);
		sendMessage(teamId, FriendPB.FriendPushData.newBuilder().setFriend(getFriendData(t, 0))	.setFriendType(TeamFriends.Black).setOperation(EStatus.FriendDel.getId()).build(),
				ServiceCode.Push_Friend);
	}

	@ClientMethod(code = ServiceCode.FriendManager_showOnlineFriend)
	public void showOnlineFriend() {
		long teamId = getTeamId();
		TeamFriends tf = getTeamFriends(teamId);
		List<FriendPB.FriendOnlineData> onlineList = tf.getFriendList().stream().map(f -> f.getFriendTeamId())
				.map(tid -> getFriendOnlineData(tid, GameSource.isOline(tid))).collect(Collectors.toList());

		List<FriendPB.FriendOnlineData> backOnlineList = tf.getBlackList().stream().map(f -> f.getFriendTeamId())
				.map(tid -> getFriendOnlineData(tid, GameSource.isOline(tid))).collect(Collectors.toList());

		sendMessage(FriendPB.FriendOnlineMain.newBuilder().addAllOnlineList(onlineList)
				.addAllBlackOnlineList(backOnlineList).build());
	}

	private FriendPB.FriendOnlineData getFriendOnlineData(long teamId, boolean online) {
		return FriendPB.FriendOnlineData.newBuilder().setOnline(online).setTeamId(teamId).build();
	}
	
	public FriendPB.TeamFriendData getTeamFriendData(long teamId) {
		TeamFriends tf = getTeamFriends(teamId);
		return FriendPB.TeamFriendData.newBuilder().addAllFriendList(getFriendListData(tf.getFriendList()))
				.addAllBlackList(getFriendListData(tf.getBlackList())).build();
	}

	/**
	 * 推荐好友列表
	 * 
	 * @param list
	 * @return
	 */
	public FriendPB.FindFriendData getFindFriendData(List<Long> list) {
		return FriendPB.FindFriendData.newBuilder().addAllFriendList(getFriendListData(list)).build();
	}

	public List<FriendPB.FriendData> getFriendListData(List<Long> list) {
		List<FriendPB.FriendData> datalist = Lists.newArrayList();
		for (Long taemId : list) {
			Team t = teamManager.getTeam(taemId);
			
			datalist.add(getFriendData(t, 0));
		}
		return datalist;
	}

	public List<FriendPB.FriendData> getFriendListData(Collection<Friend> list) {
		List<FriendPB.FriendData> datalist = Lists.newArrayList();
		for (Friend f : list) {
			datalist.add(getFriendData(teamManager.getTeam(f.getFriendTeamId()),f.getFriendPO().getRefusedTime()));
		}
		return datalist;
	}

	public FriendPB.FriendData getFriendData(Team team, long refusedTime) {
		if (team == null)
			return FriendPB.FriendData.newBuilder().setTeamId(0).setName("").setLogo("").setLv(0).setAddress("")
			        .setRefusedTime(0).build();		
	
		    return FriendPB.FriendData.newBuilder().setTeamId(team.getTeamId()).setName(team.getName())
				.setLogo(team.getLogo()).setLv(team.getLevel()).setAddress("")
				.setOnline(GameSource.isOline(team.getTeamId()))
				.setLeagueId(leagueManager.getLeagueId(team.getTeamId()))
				.setRefusedTime(refusedTime).build();
	}

	/*
	 * 好友查询
	 */
	@ClientMethod(code = ServiceCode.Friend_nice_query)
	public void queryTeamByName(String teamName) {
		long teamId = getTeamId();
		if (teamName == null || teamName.equals("")) {
			sendMessage(getFriendData(null, 0));
			return;
		}
		if (!teamManager.existTeamName(teamName)) {
			sendMessage(getFriendData(null, 0));
			return;
		}
		Team team = teamManager.getTeamByName(teamName);

		if (team != null && team.getTeamId() == teamId) {
			sendMessage(getFriendData(null, 0));
			return;
		}
		//
		sendMessage(getFriendData(team, 0));
	}	
	
	/**
     * 切磋信息获取    
     */
    @ClientMethod(code = ServiceCode.FriendManager_CompareNotesInfo)
    public void CompareNotesInfo() {     
        long teamId = getTeamId();          
        TeamStatus ts = teamStatusManager.getTeamStatus(teamId);
        TeamBattleStatus tbs = ts.getBattle(EBattleType.Friend_Match);
        if(tbs == null) {
            sendMessage(builderCompareNotesInfoMsg(0).build());
            return;
        }
        
        BattleHandle bh = BattleAPI.getInstance().getBattleHandle(tbs.getBattleId());
        if (bh == null) {
            sendMessage(builderCompareNotesInfoMsg(0).build());
            return;
        }
        BattleTeam bt = bh.getBattleSource().getOtherTeam(teamId);
        if (bt == null) {
            sendMessage(builderCompareNotesInfoMsg(0).build());
            return;
        }
    
        sendMessage(builderCompareNotesInfoMsg(bt.getTeamId()).build());
    }

	/**
	 * 发起好友切磋请求
	 * @param targetTeamId 目标球队ID
	 */
	@ClientMethod(code = ServiceCode.FriendManager_InitiateCompareNotes)
	public void initiateCompareNotes(long targetTeamId) {
		long teamId = getTeamId();
		Team team = teamManager.getTeam(teamId);
        if (team != null && team.getTeamId() != teamId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
            return;
        }
		
	    Team targetTeam = teamManager.getTeam(targetTeamId);
        if (targetTeam != null && targetTeam.getTeamId() != targetTeamId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
            return;
        }        
        
        if(isInBattle(teamId) || isInBattle(targetTeamId)) {
            log.debug("已在比赛中");
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_2.code).build());
            return;
        }
	        
		// 目标玩家不在线
		if (!GameSource.isOline(targetTeamId)) {
			log.debug("好友不在线,不能发起切磋", targetTeamId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_Offline.code).build());
			return;
		}

		TeamFriends tf = getTeamFriends(teamId);	
		TeamFriends tff = getTeamFriends(targetTeamId);  
		if (!tf.isFriend(targetTeamId) || !tff.isFriend(teamId)) {
			log.debug("不是好友{},不能发起切磋", targetTeamId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_5.code).build());
			return;
		}	

		// 推送之前判断一下有没有设置5分钟拒绝接受信息
		if(tf.getFriend(targetTeamId) != null && tf.getFriend(targetTeamId).getFriendPO().getRefusedTime() > System.currentTimeMillis()) {
		    log.debug("五分钟内不接收请求{},不能发起切磋", targetTeamId);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Friend_6.code).build());
            return;
		}
		
		// 推送消息
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		sendMessage(targetTeamId, builderInitiateCompareNotesMsg(team).build(), ServiceCode.FriendManager_CompareNotesPush);
	}
	
	/**
	 * @param initiateTeamId(发起切磋的球队ID)
	 * @param state(切磋请求 0:拒绝,1:同意)，
	 * @param receiveState(5分钟内不再接收好友切磋邀请 0:否, 1:是)	
	 */
	@ClientMethod(code = ServiceCode.FriendManager_CompareNotesDeal)
    public void compareNotesDeal(long initiateTeamId, int state, int receiveState) {
	    long teamId = getTeamId();
        Team team = teamManager.getTeam(teamId);
        if (team != null && team.getTeamId() != teamId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
            return;
        }
        
        Team initiateTeam = teamManager.getTeam(initiateTeamId);
        if (initiateTeam != null && initiateTeam.getTeamId() != initiateTeamId) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Team_1.code).build());
            return;
        }    
        
        // 根据状态处理
        if(state == 0) {
            // 拒绝
            if(receiveState == 1) { // 设置五分钟内不再接收好友切磋邀请
                TeamFriends friends = this.getTeamFriends(initiateTeamId);
                if(friends.getFriend(teamId) != null) {                    
                    friends.getFriend(teamId).getFriendPO().setRefusedTime(System.currentTimeMillis() + ConfigConsole.getGlobal().friendCompareNotesRefreshTime*1000);
                    friends.getFriend(teamId).getFriendPO().save();
                }               
            }
            
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
            sendMessage(initiateTeamId, builderCompareNotesDealMsg(teamId, state).build(), ServiceCode.FriendManager_CompareNotesDealPush);
            return;
        }else {
            sendMessage(initiateTeamId, builderCompareNotesDealMsg(teamId, state).build(), ServiceCode.FriendManager_CompareNotesDealPush); 
            
            if(isInBattle(teamId) || isInBattle(initiateTeamId)) {
                log.debug("已在比赛中");
                sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Battle_2.code).build());
                return;
            }
            
            // 开始比赛          
            EBattleType bt = EBattleType.Friend_Match;
            BattleSource bs = localBattleManager.buildBattle(bt, teamId, initiateTeamId, null, null, teamId);
            BattleContxt bc = localBattleManager.defaultContext(battleEnd);
            BaseBattleHandle bb = new BattleCommon(bs);
            localBattleManager.initBattleWithContext(bb, bs, bc);
            localBattleManager.start(bs, bc, bb);
        } 	    
	}

	/** 是否在比赛中 */
	private boolean isInBattle(long teamId) {
		return TeamStatus.inBattle(teamStatusManager.getTeamStatus(teamId), EBattleType.Friend_Match);
	}

	/** 比赛结束 */
	private void endMatch0(BattleSource bs) {
		try {
			endMatch1(bs);
		} catch (Exception e) {
			log.error("mmatch " + e.getMessage(), e);
		}
	}

	/** 比赛结束 */
	private void endMatch1(BattleSource bs) {		
		localBattleManager.sendEndMain(bs, true);
	}
	
	private InitiateCompareNotes.Builder builderInitiateCompareNotesMsg(Team team){
	    InitiateCompareNotes.Builder msg = InitiateCompareNotes.newBuilder();
	    msg.setTeamId(team.getTeamId());
	    msg.setTeamName(team.getName());
	    
	    return msg;
	}
	
	private CompareNotesDeal.Builder builderCompareNotesDealMsg(long targetTeamId, int state){
	    CompareNotesDeal.Builder msg = CompareNotesDeal.newBuilder();
	    msg.setTargetTeamId(targetTeamId);
	    msg.setState(state);
        return msg;
    }
	
	private CompareNotesInfo.Builder builderCompareNotesInfoMsg(long teamId){
	    CompareNotesInfo.Builder msg = CompareNotesInfo.newBuilder();
        msg.setTeamId(teamId);          
        return msg;
    }
}
