package com.ftkj.manager.logic;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.GroupWarTierBean;
import com.ftkj.console.LeagueGroupWarConsole;
import com.ftkj.console.NPCConsole;
import com.ftkj.db.ao.logic.ILeagueAO;
import com.ftkj.db.domain.group.LeagueGroupPO;
import com.ftkj.db.domain.group.LeagueGroupTeamPO;
import com.ftkj.enums.ELeagueTeamLevel;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.league.League;
import com.ftkj.manager.league.LeagueTeam;
import com.ftkj.manager.league.groupwar.GroupTeam;
import com.ftkj.manager.league.groupwar.LGroupApply;
import com.ftkj.manager.league.groupwar.LeagueGroup;
import com.ftkj.manager.league.groupwar.LeagueGroupSeason;
import com.ftkj.manager.logic.LeagueGroupSeasonManager.GruopWarVS;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.rank.GroupTierRank;
import com.ftkj.manager.rank.LeagueGroupRank;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.LeagueGroupPB;
import com.ftkj.proto.LeagueGroupPB.LgGroupDetailData;
import com.ftkj.proto.LeagueGroupPB.LgGroupRankMainData.Builder;
import com.ftkj.proto.LeagueGroupPB.LgLeagueGroupMainData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.IDUtil;
import com.ftkj.util.lambda.LBInt;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 联盟战队王(联盟组队天梯)
 * @author lin.lin
 */
public class LeagueGroupWarManager extends BaseManager {
	
	@IOC
	private ILeagueAO leagueAO;
	@IOC
	private ChatManager chatManager;
	@IOC
	private LeagueManager leagueManager;
	@IOC
	private TeamManager teamManager;
	@IOC
	private LeagueGroupSeasonManager groupSeasonManager;
	@IOC
	private PropManager propManager;
	
	/**
	 * 联盟ID ：战队列表
	 */
	private Map<Integer, List<LeagueGroup>> leagueGroupMap;
	/**
	 * 联盟ID ：战队编号序列
	 */
	private Map<Integer, AtomicInteger> leagueGroupSeqMap;
	/**
	 * 战队名字：战队ID (联盟ID + 战队编号)
	 */
	private BiMap<String, Long> groupMap;
	
	private LgLeagueGroupMainData lgGrouopMainEmpty;
	
	private LgGroupDetailData lgGrouopDetailEmpty;

	@Override
	public void instanceAfter() {
		lgGrouopMainEmpty = LeagueGroupPB.LgLeagueGroupMainData.newBuilder().build();
		lgGrouopDetailEmpty = LeagueGroupPB.LgGroupDetailData.newBuilder().setMyGroupTeir(1).build();
		List<LeagueGroupPO> groupPOList = leagueAO.getLeagueGroupList();
		List<LeagueGroupTeamPO> groupTeamPOList = leagueAO.getLeagueGroupTeamList();
		Map<Long, List<GroupTeam>> groupTeamList = groupTeamPOList.stream().map(po-> new GroupTeam(po)).collect(Collectors.groupingBy(g->g.getLeagueGroupId(),Collectors.toList()));
		// 
		Map<Integer, List<LeagueGroup>> lgMap = groupPOList.stream()
				.map(po-> {
					// 申请列表
					Map<Long, LGroupApply> applyListMap = redis.getMapAllKeyValues(RedisKey.getKey(po.getLeagueGroupId(), RedisKey.League_Group_Apply));
					List<LGroupApply> applyList = applyListMap == null ? Lists.newArrayList() : applyListMap.values().stream().collect(Collectors.toList());
					List<GroupTeam> teamList = !groupTeamList.containsKey(po.getLeagueGroupId()) ? Lists.newArrayList() : groupTeamList.get(po.getLeagueGroupId());
					return new LeagueGroup(po, teamList, applyList);
				}).collect(Collectors.groupingBy(g->g.getLeagueId(),Collectors.toList()));
		leagueGroupMap = Maps.newConcurrentMap();
		leagueGroupMap.putAll(lgMap);
		//
		leagueGroupSeqMap = Maps.newConcurrentMap();
		for(Integer leagueId : leagueGroupMap.keySet()) {
			AtomicInteger seq = new AtomicInteger(leagueGroupMap.get(leagueId).stream().mapToInt(g->g.getGroupId()).max().orElse(0));
			leagueGroupSeqMap.put(leagueId, seq);
		}
		//
		groupMap = HashBiMap.create();
		groupPOList.stream().forEach(po-> {
			groupMap.put(po.getName(), po.getLeagueGroupId());
		});
	}
	
	/**
	 * 查询战队
	 * @param leagueId
	 * @param groupId
	 * @return
	 */
	public LeagueGroup getLeagueGroup(int leagueId, int groupId) {
		 List<LeagueGroup> list = leagueGroupMap.get(leagueId);
	        if(list == null) {
	            return null;
	        }
	        return list.stream().filter(g-> g.getGroupId() == groupId).findFirst().orElse(null);
	}
	
	private List<LeagueGroup> getLeagueGroupList(int leagueId) {
		return leagueGroupMap.get(leagueId);
	}
	
	private int getGroupSeq(int leagueId) {
		if(!leagueGroupSeqMap.containsKey(leagueId)) {
			leagueGroupSeqMap.put(leagueId, new AtomicInteger(0));
		}
		return leagueGroupSeqMap.get(leagueId).incrementAndGet();
	}
	
	/**
	 * 创建战队
	 * @param leagueId
	 * @param teamId
	 * @param name 
	 * @return
	 */
	private synchronized LeagueGroup createGroup(int leagueId, long teamId, String name) {
		LeagueGroup lg = new LeagueGroup(leagueId, getGroupSeq(leagueId), name); 
        lg.addBattleTeam(teamId, 1);
        if(!leagueGroupMap.containsKey(leagueId)) {
        	leagueGroupMap.put(leagueId, Lists.newArrayList());
        }
        leagueGroupMap.get(leagueId).add(lg);
        groupMap.put(lg.getName(), lg.getLeagueGroupId());
		return lg;
	}
	
	/**
	 * 解散战队
	 */
	private synchronized void dissolveGroup(LeagueGroup group) {
		leagueGroupMap.get(group.getLeagueId()).remove(group);
		groupMap.remove(group.getName());
		group.dissolve();
	}
	
	/**
	 * 取本盟已有战队数
	 * @param leagueId
	 * @return
	 */
	
	@SuppressWarnings("unused")
	private synchronized int getLeagueGroupSize(int leagueId) {
		if(!leagueGroupMap.containsKey(leagueId)) {
			return 0;
		}
		return leagueGroupMap.get(leagueId).size();
	}
	
	/**
	 * 创建战队
	 * @param name
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_createGroup)
	public synchronized void createGroup(String name) {
		long teamId = getTeamId();
        if (name == null || name.trim().equals("") || name.trim().length() > 10) {//长度有误
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        name = name.trim();
        if (chatManager.shieldText(name)) {//名字含有敏感字符
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_14.code).build());
            return;
        }
        // 是否重名
        if(groupMap.containsKey(name)) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Name_Repeat.code).build());
            return;
        }
        LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        League league = leagueManager.getLeague(lt.getLeagueId());
        if (league == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_8.code).build());
            return;
        }
        // 人数上限的限制
//        int max = league.getPeopleCount() / 3;
//        if(getLeagueGroupSize(lt.getLeagueId()) + 1 > max) {
//        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Max.code).build());
//        	return;
//        }
        LeagueGroup lg = createGroup(league.getLeagueId(), teamId, name);
        // 推送创建包
        sendMessage(teamId, getLgGroupDetailData(lg), ServiceCode.LeagueGroupWarManager_push_createGroup);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * 解散
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_dissolveGroup)
	public void dissolveGroup(int groupId) {
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        // 是队长或者盟主
        if(group.getLeaderTeamId() != teamId 
        		&& (lt.getLevel() != ELeagueTeamLevel.盟主 && lt.getLevel() != ELeagueTeamLevel.副盟主)) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Limit.code).build());
            return;
        }
        synchronized (group) {
        	if(group.getStatus() != 0) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
                return;
        	}
        	//  解散
        	List<Long> teamIds = Lists.newArrayList(group.getTeamIds());
        	dissolveGroup(group);
        	// 推包
        	for(long t : teamIds) {
        		sendMessageTeamIds(teamIds, 
        				DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(t).build(), 
        				ServiceCode.LeagueGroupWarManager_kickout);
        	}
		}
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	
	/**
	 * 申请
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_joinApply)
	public void joinApply(int groupId) {
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        LGroupApply apply =  new LGroupApply(IDUtil.geneteId(getClass()), group.getLeagueId(), group.getGroupId(), teamId, teamManager.getTeamCap(teamId));
        group.addApplyTeam(apply);
        // 
        String key = RedisKey.getKey(group.getLeagueGroupId(), RedisKey.League_Group_Apply);
        redis.putMapValue(key, apply.getId(), apply);
        //
//        long leader = group.getLeaderTeamId();
//        if(leader > 0) {
//        	sendMessage(leader, getLgApplyMainData(group), ServiceCode.LeagueGroupWarManager_push_applyList);
//        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        // 做个上限? 联盟人数也不多
	}
	
	
	/**
	 * 同意/拒绝
	 * @param groupId
	 * @param tid
	 * @param op 0 拒绝， 1通过
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_accept)
	public void accept(int groupId, long tid, int op) {
		// 队长才有权限
		long teamId = getTeamId();
		if(op < 0 || op > 1) {
			return;
		}
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        // 是队长或者盟主
        if(group.getLeaderTeamId() != teamId && lt.getLevel() != ELeagueTeamLevel.盟主) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Limit.code).build());
            return;
        }
        LGroupApply apply = group.getApplyTeamById(tid);
    	if(apply == null) {
    		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Apply.code).build());
    		return;
    	}
        synchronized (lt) {
        	if(op == 1 && inGroup(lt.getLeagueId(), tid)) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Joined.code).build());
        		return;
        	}
        	// 只移除记录
        	group.removeApply(tid);
        	//
        	if(group.inTeam(tid)) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_In_Team.code).build());
        		return;
        	}
        	if(op == 1) {
        		// 同意加入
        		group.addBattleTeam(tid, 0);
        		// 推送人数变动
        		sendMessageTeamIds(group.getTeamIds(), 
        				getLgGroupDetailData(group), 
        				ServiceCode.LeagueGroupWarManager_push_groupDetail);
        	}
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		}
        String key = RedisKey.getKey(group.getLeagueGroupId(), RedisKey.League_Group_Apply);
        redis.removeMapValue(key, apply.getId());
	}
	
	/**
	 * 退出
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_exit)
	public void exit(int groupId) {
		long teamId = getTeamId();
		exitGruop(teamId, groupId);
	}
	
	private void exitGruop(long teamId, int groupId) {
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        if(group.getStatus() != 0) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
        	return;
        }
        // 不管在不在都会退出
        if(!group.inTeam(teamId)) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Team.code).build());
        	return;
        }
        List<Long> teamIds = Lists.newArrayList(group.getTeamIds());
        group.exit(teamId);
        // 推送退出
    	sendMessageTeamIds(teamIds, 
    			DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(teamId).build(), 
    			ServiceCode.LeagueGroupWarManager_exit);
    	// 如果是队长，则任命最大战力的人为新队长
    	checkAutoSetupLeader(group);
	}
	
	/**
	 * 踢出
	 * @param tid
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_kickout)
	public void kickout(long tid) {
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = findTeamGruop(lt.getLeagueId(), teamId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        if(group.getStatus() != 0) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
        	return;
        }
        synchronized (group) {
        	if(group.getLeaderTeamId() != teamId) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Limit.code).build());
        		return;
        	}
        	if(!group.inTeam(tid)) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Team.code).build());
        		return;
            }
        	//
        	List<Long> teamIds = Lists.newArrayList(group.getTeamIds());
        	group.exit(tid);
        	//
        	sendMessageTeamIds(teamIds, 
        			DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(tid).build(), 
        			ServiceCode.LeagueGroupWarManager_kickout);
		}
	}
	
	/**
	 * 查看战队明细
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_getGruopTeamDetail)
	public void getGruopTeamDetail(int leagueId, int groupId) {
		LeagueGroup group = getLeagueGroup(leagueId, groupId);
		if(group == null) {
			sendMessage(lgGrouopDetailEmpty);
			return;
		}
		sendMessage(getLgGroupDetailData(group));
	}
	
	
	/**
	 * 退出联盟调用，如果在战队战
	 * @param group 
	 * @param teamId
	 */
	public void exitLeague(LeagueGroup group, long teamId) {
		if(group == null || !group.inTeam(teamId)) {
			return;
		}
		List<Long> teamIds = Lists.newArrayList(group.getTeamIds());
		group.exit(teamId);
		sendMessageTeamIds(teamIds, 
    			DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(teamId).build(), 
    			ServiceCode.LeagueGroupWarManager_exit);
		// 如果是队长，则任命最大战力的人为新队长
		checkAutoSetupLeader(group);
	}
	
	/**
	 * 自动检测任命新的队长,或者解散
	 * @param group
	 */
	private void checkAutoSetupLeader(LeagueGroup group) {
		long leader = group.getLeaderTeamId();
		if(group.getTeamIds().size() == 0) {
			dissolveGroup(group);
		}
		else if(leader == 0) {
			long newLeader = group.getTeamIds().stream().max(Comparator.comparing(o -> teamManager.getTeamCap(o))).get();;
			group.changeLeader(newLeader);
			// 队员推包
	        sendMessageTeamIds(group.getTeamIds(), 
	        		DefaultPB.DefaultData.newBuilder()
	        		.setCode(0)
	        		.setBigNum(newLeader).build(), 
	        		ServiceCode.LeagueGroupWarManager_push_leaderTeam);
		}
	}
	
	/**
	 * 替换位置，两个位置替换
	 * 
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_exchangePos)
	public void exchangePos(int groupId, int pos1, int pos2) {
		if(pos1 < 0 || pos1 > 3 || pos2 < 0 || pos2 > 3) {
			return;
		}
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        synchronized (group) {
        	if(group.getStatus() != 0) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
        		return;
        	}
        	group.exchangePos(pos1, pos2);
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		}
        // 推送人数变动
		sendMessageTeamIds(group.getTeamIds(), 
				getLgGroupDetailData(group), 
				ServiceCode.LeagueGroupWarManager_push_groupDetail);
	}
	
	/**
	 * 任命队长，盟主操作
	 * @param groupId
	 * @param tid
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_appointLeader)
	public void appointLeader(int groupId, long tid) {
		// 队长才有权限
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        // 副盟主或者盟主
        if(lt.getLevel() != ELeagueTeamLevel.盟主 && lt.getLevel() != ELeagueTeamLevel.副盟主) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Limit.code).build());
            return;
        }
        if(!group.inTeam(tid)) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Team.code).build());
            return;
        }
        group.changeLeader(tid);
        // 盟主回包
        sendMessage(DefaultPB.DefaultData.newBuilder()
        		.setCode(ErrorCode.Success.code)
        		.setBigNum(tid)
        		.build());
        // 队员推包
        sendMessageTeamIds(group.getTeamIds(), 
        		DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(tid).build(), 
        		ServiceCode.LeagueGroupWarManager_push_leaderTeam);
	}
	
	/**
	 * 队员准备 </BR>
	 * 1,准备 0取消准备
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_ready)
	public void ready(int op) {
		long teamId = getTeamId();
		if(op < 0 || op > 1) {
			return;
		}
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
            return;
        }
        LeagueGroup group = findTeamGruop(lt.getLeagueId(), lt.getTeamId());
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
		synchronized (group) {
			group.ready(teamId, op == 1);
		}
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		// 准备
		sendMessageTeamIds(group.getTeamIds(), 
				DefaultPB.DefaultData.newBuilder().setCode(op).setBigNum(teamId).build(), 
				ServiceCode.LeagueGroupWarManager_push_ready);
	}
	
	/**
	 * 开始比赛 </BR>
	 * 队长操作, 
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_startMatch)
	public void startMatch(int groupId) {
		// 队长才有权限
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
        if(group == null) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Find_Null.code).build());
        	return;
        }
        // 第3段位后是队长才可以开始
        GroupWarTierBean bean = LeagueGroupWarConsole.getTierByScore(group.getScore());
        if(bean.getId() > LeagueGroupWarConsole.NoReaderTier && group.getLeaderTeamId() != teamId) { 
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Not_Limit.code).build());
            return;
        }
        // 是否在开赛时间
        if(!groupSeasonManager.isMatchTime()) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle_Start.code).build());
        	return;
        }
        // 已经在参战中
        if(group.getStatus() != 0) {
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
        	return;
        }
        synchronized (group) {
        	//是否都准备, 前3段位都不用准备
        	if(!group.canJoinPK(LeagueGroupWarConsole.getTierById(3).getNextRating())) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle_Ready.code).build());
        		return;
        	}
        	// 是否在匹配中
        	if(group.getStatus() != 0) {
        		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_Group_Battle.code).build());
        		return;
        	}
        	group.setStatus(1);
        	groupSeasonManager.addGroupToBattle(group);
        	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
		}
        //
        sendMessageTeamIds(group.getTeamIds(), 
        		DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(group.getStatus()).build(), 
        		ServiceCode.LeagueGroupWarManager_push_groupStatus);
   	}
	
	/**
	 * 状态变更强制推包
	 * @param group
	 */
	public void groupStatusChange(LeagueGroup group) {
		sendMessageTeamIds(group.getTeamIds(), 
        		DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(group.getStatus()).build(), 
        		ServiceCode.LeagueGroupWarManager_push_groupStatus);
	}
	
	/**
	 * 是否在联盟战队中
	 * @param leagueId
	 * @param teamId
	 * @return
	 */
	public boolean inGroup(int leagueId, long teamId) {
		LeagueGroup group = findTeamGruop(leagueId, teamId);
		return group != null;
	}
	
	/**
	 * 查找战队
	 * @param leagueId
	 * @param teamId
	 * @return
	 */
	public LeagueGroup findTeamGruop(int leagueId, long teamId) {
		if(!leagueGroupMap.containsKey(leagueId)) {
			return null;
		}
		return this.leagueGroupMap.get(leagueId).stream()
				.filter(g-> g.inTeam(teamId)).findFirst().orElse(null);
	}	
	
	/**
	 * 本盟战队
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_getLeagueGroupMain)
	public void getLeagueGroupMain() {
		long teamId = getTeamId();
		LeagueTeam lt = leagueManager.getLeagueTeam(teamId);
        if (lt == null) {
        	sendMessage(lgGrouopMainEmpty);
            return;
        }
        // 有盟，则按联盟信息返回
        List<LeagueGroup> groupList = getLeagueGroupList(lt.getLeagueId());
        List<LeagueGroupPB.LgGroupData> dataList = Lists.newArrayList();
        //我的申请
        List<Integer> myApplyList = Lists.newArrayList();
        if(groupList != null) {
        	groupList.stream().forEach(g-> dataList.add(getLgGroupData(g)));
        	myApplyList = groupList.stream().filter(g-> g.inApply(teamId)).mapToInt(g-> g.getGroupId()).boxed().collect(Collectors.toList());
        }
        // 我的战队
        LeagueGroup group = findTeamGruop(lt.getLeagueId(), lt.getTeamId());
        TierInfo tierInfo = getTierInfo(group);
        //
        sendMessage(LeagueGroupPB.LgLeagueGroupMainData.newBuilder()
        		.setMyGruop(getLgGroupDetailData(group))
        		.addAllGroupList(dataList)
        		.addAllApplyGroupList(myApplyList)
        		.setCurrRank(tierInfo.getRank())
        		.setNextScore(tierInfo.getNextScore())
        		.build());
	}
	
	/**
	 * 入队申请列表
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_getGroupApplyList)
	public void getGroupApplyList(int groupId) {
		LeagueTeam lt = leagueManager.getLeagueTeam(getTeamId());
        if (lt == null) {
            return;
        }
		LeagueGroup group = getLeagueGroup(lt.getLeagueId(), groupId);
		sendMessage(getLgApplyMainData(group));
	}
	
	//------------------------------------------------
	private LeagueGroupPB.LgApplyMainData getLgApplyMainData(LeagueGroup group) {
		List<LeagueGroupPB.LgApplyData> dataList = Lists.newArrayList();
		if(group != null) {
			group.getApplyList().stream().forEach(a-> dataList.add(getLgApplyData(a)));
		}
		return LeagueGroupPB.LgApplyMainData.newBuilder()
				.addAllApplyList(dataList)
				.build();
	} 
	
	private LeagueGroupPB.LgApplyData getLgApplyData(LGroupApply a) {
		return LeagueGroupPB.LgApplyData.newBuilder()
				.setTeamId(a.getTeamId())
				.setName(teamManager.getTeamNameById(a.getTeamId()))
				.setCap((int)a.getCap())
				.build();
	}
	
	
	private LeagueGroupPB.LgGroupData getLgGroupData(LeagueGroup group) {
		return LeagueGroupPB.LgGroupData.newBuilder()
				.setGruopId(group.getGroupId())
				.setName(group.getName())
				.setScore(group.getScore())
				.setWin(group.getWinNum())
				.setLoss(group.getLossNum())
				.setStatus(group.getStatus())
				.setLeaderTeam(group.getLeaderTeamId())
				.setTeamCount(group.getTeamSise())
				.setLeagueId(group.getLeagueId())
				.setLeagueName(leagueManager.getLeagueName(group.getLeagueId()))
				.setTotalPrivity(group.getTotalPrivity())
				.build();
	}
	
	private LeagueGroupPB.LgGroupDetailData getLgGroupDetailData(LeagueGroup group) {
		if(group == null) {
			return LeagueGroupPB.LgGroupDetailData.newBuilder().setMyGroupTeir(1).build();
		}
		TierInfo tierInfo = getTierInfo(group);
        //
		List<LeagueGroupPB.LgGroupTeamData> teamListData = Lists.newArrayList();
		group.getTeamList().forEach(t-> teamListData.add(getLgGroupTeamData(t)));
		return LeagueGroupPB.LgGroupDetailData.newBuilder()
				.setInfo(getLgGroupData(group))
				.addAllTeamList(teamListData)
				.setMyGroupTeir(tierInfo.getTierId())
				.build();
	}
	
	private LeagueGroupPB.LgGroupTeamData getLgGroupTeamData(GroupTeam t) {
		Team team = teamManager.getTeam(t.getTeamId());
		int cap = (int) teamManager.getTeamCap(t.getTeamId());
		return LeagueGroupPB.LgGroupTeamData.newBuilder()
				.setTeamId(team.getTeamId())
				.setName(team.getName())
				.setLogo(team.getLogo())
				.setLevel(t.getLevel())
				.setPrivity(t.getPrivity())
				.setIsReady(t.isReady())
				.setPosition(t.getPosition())
				.setCap(cap)
				.build();
	}
	
	/**
	 * 比赛结束
	 * @param vs
	 */
	public void GroupBattleEnd(GruopWarVS vs) {
		// 计算胜负比分
		long winGroupId = vs.getWinGroupId();
		//
		final int dh = vs.getScoreGap();
		final double P = 1 / (1 + Math.pow(10.0, (-dh / 400.0)));
		LeagueGroup winGroup = null;
		LeagueGroup lossGroup = null;
		if(vs.getHomeGroup().getLeagueGroupId() == winGroupId) {
			winGroup = vs.getHomeGroup();
			lossGroup = vs.getAwayGroup();
		}else {
			winGroup = vs.getAwayGroup();
			lossGroup = vs.getHomeGroup();
		}
		GroupWarTierBean winTier = LeagueGroupWarConsole.getTierByScore(winGroup.getScore());
		GroupWarTierBean lossTier = LeagueGroupWarConsole.getTierByScore(lossGroup.getScore());
		// 胜场
		winGroup.addWinNum(1);
		lossGroup.addLossNum(1);
		// 胜方每场奖励
		winGroup.getTeamIds().stream().filter(tid-> !NPCConsole.isNPC(tid)).forEach(teamId-> {
			propManager.addPropList(teamId, winTier.getEachAwardList(), true, ModuleLog.getModuleLog(EModuleCode.联盟组队赛, ""));
		});
		//
		int honor = winTier.getEachHonor();
		League league = leagueManager.getLeague(winGroup.getLeagueId());
		if(winGroup.getLeagueId() != 0 && league != null) {
			league.updateLeagueHonor(honor);
		}
		// 比分 
		final int K = winTier.getRatingFactor();
		final int winScore = (int) (K * (1 - P) * 1);
		final int lossScore = (int) (K * (1 - P) * lossTier.getFailCorrectionFactor());
		//
		winGroup.addScore(winScore);
		winGroup.checkPrivityAdd(10);
		winGroup.clearReady();
		winGroup.setStatus(0);
		winGroup.save();
		//
		lossGroup.addScore(lossScore);
		lossGroup.checkPrivityAdd(10);
		lossGroup.clearReady();
		lossGroup.setStatus(0);
		lossGroup.save();
		// 推送一个额外的包，要显示奖励
		LeagueGroupPB.LgPKRoundData reportData = vs.getScoreDetailData(0, false);
		List<PropSimple> winAwardList = Lists.newArrayList(winTier.getEachAwardList());
		List<PropSimple> leagueAward = Lists.newArrayList(new PropSimple(4009, honor));
		//
		sendMessageTeamIds(winGroup.getTeamIds(), 
				LeagueGroupPB.LgPKEndReportData.newBuilder().setWin(true)
				.setScoreDetail(reportData)
				.addAllAwardList(PropManager.getPropSimpleListData(winAwardList))
				.addAllLeagueAward(PropManager.getPropSimpleListData(leagueAward))
				.setAddScore(winScore)
				.build(), 
				ServiceCode.LeagueGroupWarManager_push_match_report);
		//
		sendMessageTeamIds(lossGroup.getTeamIds(), 
				LeagueGroupPB.LgPKEndReportData.newBuilder()
				.setWin(false)
				.setAddScore(lossScore)
				.setScoreDetail(reportData)
				.build(), 
				ServiceCode.LeagueGroupWarManager_push_match_report);
		
	}
	
	/**
	 * 联盟排名
	 * @param page
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_showLeagueRank)
	public void showLeagueGroupWarRank(int page) {
		final int pageSize = 10;
		if(page < 1) {
			page = 1;
		}
		List<LeagueGroupRank> leagueRanks = Lists.newArrayList();
		LeagueGroupRank myLeagueRank = null;
		LeagueTeam lt = leagueManager.getLeagueTeam(getTeamId());
		LeagueGroup group = null;
		if (lt != null) {
        	group = findTeamGruop(lt.getLeagueId(), lt.getTeamId());
		}
		for(int leagueId : leagueGroupMap.keySet()) {
			List<LeagueGroup> list = leagueGroupMap.get(leagueId);
			int jf = 0;
			int win = 0;
			int loss = 0;
			for(LeagueGroup g : list) {
				// 过滤没有积分和羁绊值不满300的进入联盟排名计算
				if(g.getScore() < 1 || g.getTotalPrivity() != 300) {
					continue;
				}
				jf += g.getScore();
				win += g.getWinNum();
				loss += g.getLossNum();
				//
			}
			//
			if(jf < 1) {
				continue;
			}
			LeagueGroupRank temp = new LeagueGroupRank(leagueId, 
					leagueManager.getLeagueName(leagueId), 
					jf, win, loss);
			leagueRanks.add(temp);
			if(group != null && group.getLeagueId() == temp.getLeagueId()) {
				myLeagueRank = temp;
			}
		}
		leagueRanks = leagueRanks.stream()
		.sorted(Comparator.comparing(LeagueGroupRank::getJf).reversed())
		.collect(Collectors.toList());
		//
        LeagueGroupPB.LgRankMainData.Builder builder = LeagueGroupPB.LgRankMainData.newBuilder();
        // 本盟
    	if(group != null) {
    		int index = leagueRanks.indexOf(myLeagueRank); 
    		if(index != -1) {
    			int myRank = index + 1;
    			LeagueGroupRank lr = new LeagueGroupRank(
    					group.getLeagueId(), 
    					leagueManager.getLeagueName(group.getLeagueId()), 
    					group.getScore(), 
    					group.getWinNum(), 
    					group.getLossNum());
    			builder.setMy(getLgRankData(lr, myRank));
    		}
    	}
        //
        LBInt i = new LBInt((page-1) * pageSize);
		List<LeagueGroupPB.LgRankData> ranks = Lists.newArrayList();
		leagueRanks = leagueRanks.stream().skip((page-1) * pageSize).limit(pageSize).collect(Collectors.toList());
		leagueRanks.forEach(r -> ranks.add(getLgRankData(r, i.increaseAndGet())));
		builder.addAllRankList(ranks);
        sendMessage(builder.build());
	}
	
	/**
	 * 取联盟排行
	 * @param size
	 * @return
	 */
	public List<Integer> getLeagueRankIds(int size) {
		List<LeagueGroupRank> leagueRanks = Lists.newArrayList();
		for(int leagueId : leagueGroupMap.keySet()) {
			List<LeagueGroup> list = leagueGroupMap.get(leagueId);
			int jf = 0;
			int win = 0;
			int loss = 0;
			for(LeagueGroup g : list) {
				// 过滤没有积分和羁绊值不满300的进入联盟排名计算
				if(g.getScore() < 1 || g.getTotalPrivity() != 300) {
					continue;
				}
				jf += g.getScore();
				win += g.getWinNum();
				loss += g.getLossNum();
				//
			}
			//
			if(jf < 1) {
				continue;
			}
			LeagueGroupRank temp = new LeagueGroupRank(leagueId, 
					leagueManager.getLeagueName(leagueId), 
					jf, win, loss);
			leagueRanks.add(temp);
		}
		return leagueRanks.stream()
				.sorted(Comparator.comparing(LeagueGroupRank::getJf).reversed())
				.limit(size)
				.mapToInt(l-> l.getLeagueId()).boxed()
				.collect(Collectors.toList());
	}
	
	private LeagueGroupPB.LgRankData getLgRankData(LeagueGroupRank info, int rank) {
		return LeagueGroupPB.LgRankData.newBuilder()
				.setLeagueId(info.getLeagueId())
				.setName(info.getName())
				.setRank(rank)
				.setLoss(info.getLoss())
				.setWin(info.getWin())
				.setScore(info.getJf())
				.build();
	}
	
	/**
	 * 取段位信息
	 * @param group
	 * @return
	 */
	public TierInfo getTierInfo(LeagueGroup group) {
		if(group == null) {
			return new TierInfo(1, 1, 0);
		}
    	// 下一个段位所需要积分
		Map<Integer, List<LeagueGroup>> rankMap = getTierRankMap(1);
		GroupWarTierBean currBean = LeagueGroupWarConsole.getTierByScore(group.getScore());
		int currTierId = currBean.getId();
		int rank = 0;
		// 从高段位开始查找
		for(int id : rankMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
			int index = rankMap.get(id).indexOf(group);
			if(index != -1) {
				currTierId = id;
				rank = index + 1;
				break;
			}
		}
		int nextScore = getNextTierScore(currTierId, rankMap);
		return new TierInfo(currTierId, nextScore, rank);
	}
	
	/**
	 * 下一个段位需要的积分
	 * @param currBean 当前段位
	 * @param rankMap
	 * @return
	 */
	private int getNextTierScore(int tierId, Map<Integer, List<LeagueGroup>> rankMap) {
		GroupWarTierBean currBean = LeagueGroupWarConsole.getTierById(tierId);
		// 需要积分
		if(currBean.getLimitCount() > 0 && rankMap.containsKey(currBean.getId())) {
			List<LeagueGroup> nextRank = rankMap.get(currBean.getId());
			int needScore = 1; 
			if(nextRank.size() >= currBean.getLimitCount()) {
				LeagueGroup g = nextRank.get(currBean.getLimitCount()-1);
				needScore = g.getScore() + 1;
				if(currBean.getNextRating() != 0 && currBean.getNextRating() > g.getScore()) {
					needScore = currBean.getNextRating();
				}
			}else {
				needScore = currBean.getNextRating();
			}
			return needScore;
		}
		return currBean.getNextRating();
	}
	
	/**
	 * 战队排名
	 */
	@ClientMethod(code = ServiceCode.LeagueGroupWarManager_showGroupRank)
	public void showGroupRank(int page, int tier) {
		GroupWarTierBean bean = LeagueGroupWarConsole.getTierById(tier);
		// 默认10，有限量的段位全部返回即可.
		final int pageSize = bean.getLimitCount() == 0 ? 10 : bean.getLimitCount();
		if(page < 1) {
			page = 1;
		}
		// 所有战队，参与过比赛的战队，新战队不进入
		Map<Integer, List<LeagueGroup>> rankMap = getTierRankMap(tier);
		List<LeagueGroup> rankList = rankMap.containsKey(bean.getId()) ? rankMap.get(bean.getId()) : Lists.newArrayList();
		Builder builder = LeagueGroupPB.LgGroupRankMainData.newBuilder();
    	// 下一个段位所需要积分
		int nextScore = getNextTierScore(tier, rankMap);
		builder.setNextScore(nextScore);
		//
		LBInt i = new LBInt((page-1) * pageSize);
		List<LeagueGroupPB.LgGroupRankData> ranks = Lists.newArrayList();
		rankList = rankList.stream().skip((page-1) * pageSize).limit(pageSize).collect(Collectors.toList());
		rankList.forEach(r -> ranks.add(getLgGroupRankData(r, i.increaseAndGet())));
		builder.addAllRankList(ranks);
    	sendMessage(builder.build());
	}
	
	public LeagueGroupPB.LgGroupRankData getLgGroupRankData(GroupTierRank info, int rank) {
		return LeagueGroupPB.LgGroupRankData.newBuilder()
				.setGruopId(info.getGroupId())
				.setName(info.getGroupName())
				.setScore(info.getJf())
				.setWin(info.getWinNum())
				.setLoss(info.getLossNum())
				.setLeagueId(info.getLeagueId())
				.setLeagueName(info.getLeagueName())
				.setRank(rank)
				.setTotalPrivity(0)
				.build();
	}
	
	public LeagueGroupPB.LgGroupRankData getLgGroupRankData(LeagueGroup info, int rank) {
		return LeagueGroupPB.LgGroupRankData.newBuilder()
				.setGruopId(info.getGroupId())
				.setName(info.getName())
				.setScore(info.getScore())
				.setWin(info.getWinNum())
				.setLoss(info.getLossNum())
				.setLeagueId(info.getLeagueId())
				.setLeagueName(leagueManager.getLeagueName(info.getLeagueId()))
				.setRank(rank)
				.setTotalPrivity(info.getTotalPrivity())
				.build();
	}
	
	/**
	 * 段位信息
	 * @author Jay
	 * @time:2018年5月29日 下午5:21:44
	 */
	public static class TierInfo {
		/**
		 * 当前段位
		 */
		private int tierId;
		/**
		 * 下一段位需要的积分
		 */
		private int nextScore;
		/**
		 * 段位排名
		 */
		private int rank;
		
		public TierInfo(int tierId, int nextScore, int rank) {
			super();
			this.tierId = tierId;
			this.nextScore = nextScore;
			this.rank = rank;
		}
		public int getTierId() {
			return tierId;
		}
		public int getNextScore() {
			return nextScore;
		}
		public int getRank() {
			return rank;
		}
	} 
	
	//----------------------------------------------
	/**
	 * 所有的段位排名
	 * @param starTier 开始计算的段位ID
	 * @return
	 */
	public Map<Integer, List<LeagueGroup>> getTierRankMap(int starTier) {
		GroupWarTierBean bean = LeagueGroupWarConsole.getTierById(starTier);
		List<LeagueGroup> allGroup = Lists.newArrayList();
		for(List<LeagueGroup> list : leagueGroupMap.values()) {
			allGroup.addAll(list.stream().filter(g-> !g.isNewTeam()).collect(Collectors.toList()));
		}
		// 应该被挤出的名次数，和最大被挤出的名次数.
		Map<Integer, List<LeagueGroup>> rankMap = allGroup.stream()
			.filter(g-> g.getScore() >= bean.getMinRating())
			.sorted(Comparator.comparing(LeagueGroup::getScore).reversed())
			.collect(Collectors.groupingBy(g-> LeagueGroupWarConsole.getTierByScore(g.getScore()).getId(), Collectors.toList()));
		//
		Iterator<Integer> ite = rankMap.keySet().stream().sorted(Comparator.reverseOrder()).iterator();
		while(ite.hasNext()) {
			int key = ite.next();
			GroupWarTierBean tierbean = LeagueGroupWarConsole.getTierById(key);
			if(bean.getLimitCount() <= 0) {
				continue;
			}
			List<LeagueGroup> currList = rankMap.get(key);
			if(currList.size() <= tierbean.getLimitCount()) {
				continue;
			}
			// 上一个段位的前面排名列表
			List<LeagueGroup> topList = currList.stream().skip(tierbean.getLimitCount()).collect(Collectors.toList());
			// 移除排名以外
			currList.subList(tierbean.getLimitCount(),  currList.size()).clear();;
			if(rankMap.containsKey(key - 1)) {
				List<LeagueGroup> bList = rankMap.get(key - 1);
				bList.addAll(0, topList);
			}else {
				rankMap.put(key - 1, topList);
			}
		}
		return rankMap;
	}

	/**
	 * 赛季初始化清理
	 * @param oldStatus 
	 * @param season
	 */
	public synchronized void startSeasonInit(int oldStatus, LeagueGroupSeason season) {
		if(oldStatus != 0) {
			return;
		}
		for(int key : leagueGroupMap.keySet()) {
			leagueGroupMap.get(key).forEach(g-> g.clearGroupData());
		}
	}
	
	/**
	 * 清理所有匹配状态
	 */
	public void clearStatus() {
		for(int key : leagueGroupMap.keySet()) {
			leagueGroupMap.get(key).forEach(g-> {
				g.setStatus(0);
				groupStatusChange(g);
			});
		}
	}
	
}
