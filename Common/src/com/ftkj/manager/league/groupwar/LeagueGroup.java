package com.ftkj.manager.league.groupwar;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ftkj.db.domain.group.LeagueGroupPO;
import com.ftkj.enums.ELeagueGroupPos;
import com.ftkj.manager.npc.NPCBean;
import com.google.common.collect.Lists;

/**
 * 联盟战队
 * @author lin.lin
 * 
 */
public class LeagueGroup {

	private LeagueGroupPO po;
	private List<GroupTeam> teamList;
	
	/**
	 * 申请列表
	 */
	private List<LGroupApply> applyList;
	
	private boolean isNPC = false;
	
	public LeagueGroup() {
	}
	
	/**
	 * 新创建调用
	 * @param leagueId
	 * @param groupId
	 * @param name
	 */
	public LeagueGroup(int leagueId, int groupId, String name) {
		super();
		this.po = new LeagueGroupPO(leagueId, groupId, name);
		this.teamList = Lists.newArrayList();
		this.applyList = Lists.newArrayList();
		save();
	}
	
	/**
	 * 调试,创建NPC读
	 * @param leagueId
	 * @param groupId
	 * @param name
	 * @param score
	 * @param list
	 * @return
	 */
	public static LeagueGroup createNPCGroup(int leagueId, int groupId, String name, int score, List<NPCBean> list) {
		LeagueGroup t = new LeagueGroup();
		//
		t.po = new LeagueGroupPO(leagueId, groupId, name);
		t.po.setScore(score);
		t.teamList = Lists.newArrayList();
		t.applyList = Lists.newArrayList();
		t.isNPC = true;
		//
		t.addNPC(list.get(0).getNpcId(), 1);
		t.addNPC(list.get(1).getNpcId(), 0);
		t.addNPC(list.get(2).getNpcId(), 0);
		return t;
	}
	
	/**
	 * 创建NPC
	 * @param list
	 * @return
	 */
	public static LeagueGroup createNPCGroup(List<NPCBean> list, String name) {
		return createNPCGroup(0, 0, name, 0, list);
	}
	
	/**
	 * 加载调用
	 * @param po
	 * @param teamList
	 * @param applyList
	 */
	public LeagueGroup(LeagueGroupPO po, List<GroupTeam> teamList, List<LGroupApply> applyList) {
		super();
		this.po = po;
		this.teamList = teamList;
		this.applyList = applyList;
		// 保证重启后是正常状态
		if(po.getStatus() > 0) {
			po.setStatus(0);
		}
	}

	/**
	 * 胜负场次0，积分0
	 * 是否新组建的战队？进入老战队默契值默认是0
	 * @return
	 */
	public boolean isNewTeam() {
		return this.po.getWinNum() == 0 && this.po.getLossNum() == 0 && this.getScore() == 0;
	}
	
	public synchronized void addNPC(long npcId, int level) {
		GroupTeam team = new GroupTeam(this.getLeagueId(), this.getGroupId(), npcId, getNextPos(), level);
		team.setPrivity(isNewTeam() ? 100 : 0);
		this.teamList.add(team);
	}
	
	/**
	 * 加入战队
	 * @param tid
	 * @param level 1是队长，0是队员
	 */
	public synchronized void addBattleTeam(long tid, int level) {
		GroupTeam team = new GroupTeam(this.getLeagueId(), this.getGroupId(), tid, getNextPos(), level);
		team.setPrivity(isNewTeam() ? 100 : 0);
		team.save();
		this.teamList.add(team);
	}
	public void addApplyTeam(LGroupApply team) {
		this.applyList.add(team);
	}
	
	/**
	 * 取队长teamId
	 * @return
	 */
	public long getLeaderTeamId() {
		return this.teamList.stream()
				.filter(t-> t.getLevel() == 1)
				.mapToLong(t-> t.getTeamId())
				.findFirst().orElse(0L);
	}
	
	/**
	 * 根据位置取球队
	 * @param pos 位置
	 * @return 可能空
	 */
	public GroupTeam getBattleTeamByPos(int pos) {
		return this.teamList.stream()
				.filter(t-> t.getPosition() == pos)
				.findFirst().orElse(null);
	}
	
	public int getTeamSise() {
		return this.teamList.size();
	}
	
	/**
	 * 是否已在队伍内
	 * @param tid
	 * @return
	 */
	public boolean inTeam(long tid) {
		return this.teamList.stream().anyMatch(t-> t.getTeamId() == tid);
	}
	
	/**
	 * 是否在申请队列
	 * @param tid
	 * @return
	 */
	public boolean inApply(long tid) {
		return this.applyList.stream().anyMatch(t-> t.getTeamId() == tid);
	}

	/**
	 * 取空余位置
	 * @return
	 */
	public synchronized int getNextPos() {
		Set<Integer> usePos = teamList.stream().map(t-> t.getPosition()).collect(Collectors.toSet());
		for(ELeagueGroupPos pos : ELeagueGroupPos.values()) {
			if(usePos.contains(pos.getId())) {
				continue;
			}
			return pos.getId();
		}
		return ELeagueGroupPos.副将.getId();
	}
	
	/**
	 * 解散
	 */
	public void dissolve() {
		this.teamList.forEach(t-> {
			t.del();
		});
		this.del();
	}
	
	public LGroupApply getApplyTeamById(long tid) {
		return this.applyList.stream()
			.filter(a-> a.getTeamId() == tid)
			.findFirst().orElse(null);
	}
	
	public void removeApply(long tid) {
		 this.applyList = this.applyList.stream()
			.filter(a-> a.getTeamId() != tid)
			.collect(Collectors.toList());
	}

	/**
	 * 删除
	 */
	private void del() {
		this.po.setStatus(-1);
		save();
	}

	public void save() {
		if(this.isNPC) {
			return;
		}
		this.teamList.forEach(t-> t.save());
		this.po.save();
	}

	public int getGroupId() {
		return this.po.getGroupId();
	}

	/**
	 * 退出战队
	 * @param teamId
	 */
	public synchronized void exit(long teamId) {
		GroupTeam remove = null;
		for(GroupTeam t : this.teamList) {
			if(t.getTeamId() == teamId) {
				remove = t;
				break;
			}
		}
		if(remove != null) {
			remove.del();
			this.teamList.remove(remove);
		}
		save();
	}

	/**
	 * 交换位置
	 * @param pos1
	 * @param pos2
	 */
	public void exchangePos(int pos1, int pos2) {
		 GroupTeam t1 = getBattleTeamByPos(pos1);
		 GroupTeam t2 = getBattleTeamByPos(pos2);
		if(t1 != null) {
			t1.setPosition(pos2);
			t1.save();
		}
		if(t2 != null) {
			t2.setPosition(pos1);
			t2.save();
		}
	}
	
	/**
	 * 改变队长
	 * @param tid
	 */
	public synchronized void changeLeader(long tid) {
		if(!inTeam(tid)) {
			return;
		}
		this.teamList.stream().forEach(t-> {
			if(t.getTeamId() == tid) {
				t.setLevel(1);
			}else {
				t.setLevel(0);
			}
			t.save();
		});
	}
	
	/**
	 * 准备
	 * @param teamId
	 */
	public void ready(long teamId, boolean sta) {
		this.teamList.forEach(t-> {
			if(t.getTeamId() == teamId) {
				t.setReady(sta);
			}
		});
	}

	/**
	 * 是否能参加比赛
	 * @param readyScore 不需要全部准备的分数
	 * @return
	 */
	public boolean canJoinPK(int readyScore) {
		if(this.teamList.size() < 3) {
			return false;
		}
		// 没有准备，false
		if(this.po.getScore() > readyScore && !isReadyTeam()) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * 取总的默契值
	 * @return
	 */
	public int getTotalPrivity() {
		return this.teamList.stream().mapToInt(t-> t.getPrivity()).sum();
	}

	/**
	 * 是否已全部准备
	 * @return
	 */
	private boolean isReadyTeam() {
		return this.teamList.stream().allMatch(t-> t.isReady());
	}
	
	/**
	 * 全员取消准备
	 */
	public void clearReady() {
		this.teamList.stream().forEach(t-> t.setReady(false));
	}
	
	/**
	 * 战队唯一标识<BR>
	 * 联盟战队ID； = (leagueId) * 100  + groupId;
	 * @return
	 */
	public long getLeagueGroupId() {
		return po.getLeagueGroupId();
	}
	
	public int getLeagueId() {
		return this.po.getLeagueId();
	}

	public List<GroupTeam> getTeamList() {
		return teamList;
	}
	
	public List<Long> getTeamIds() {
		return teamList.stream().mapToLong(t-> t.getTeamId()).boxed().collect(Collectors.toList());
	}
	
	public Set<Long> getApplyTeamList() {
		return this.applyList.stream().map(t-> t.getTeamId()).collect(Collectors.toSet());
	}

	public List<LGroupApply> getApplyList() {
		return applyList;
	}

	public int getTeamPrivity(long teamId) {
		if(!inTeam(teamId)) {
			return 0;
		}
		return this.teamList.stream().filter(tid-> tid.getTeamId() == teamId).findFirst().get().getPrivity();
	}
	
	public String getName() {
		return this.po.getName();
	}

	public int getScore() {
		return this.po.getScore();
	}


	public int getWinNum() {
		return this.po.getWinNum();
	}


	public int getLossNum() {
		return this.po.getLossNum();
	}


	/**
	 * 0正常状态， 1匹配， 2比赛中
	 * @return
	 */
	public int getStatus() {
		return this.po.getStatus();
	}

	public void setStatus(int status) {
		this.po.setStatus(status);
	}
	
	public void addScore(int score) {
		this.po.setScore(this.po.getScore() + score);
	}
	
	// 检查功勋值是否满100，不满则增加
	public void checkPrivityAdd(int val) {
		this.teamList.forEach(t-> {
			if(t.getPrivity() < 100) {
				int privity = t.getPrivity() + val;
				t.setPrivity(privity > 100 ? 100 : privity);
				t.save();
			} 
		});
	}

	public void addWinNum(int i) {
		this.po.setWinNum(this.po.getWinNum() + i);
		
	}

	public void addLossNum(int i) {
		this.po.setLossNum(this.po.getLossNum() + i);
	}
	
	/**
	 * 清理组队数据，赛季开始会清空.
	 */
	public synchronized void clearGroupData() {
		this.po.setLossNum(0);
		this.po.setWinNum(0);
		this.po.setScore(0);
		save();
	}

	@Override
	public String toString() {
		return "LeagueGroup [name=" + po.getName() + ", score= " + po.getScore()+"]";
	}
	
	
}
