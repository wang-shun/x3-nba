package com.ftkj.manager.friend;

import com.ftkj.db.domain.FriendPO;
import com.ftkj.manager.friend.bean.Friend;
import com.ftkj.server.RedisKey;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.tool.redis.JedisUtil;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TeamFriends {

	private long teamId;
	/**
	 * 好友
	 */
	private Map<Long, Friend> friendList;
	
	/**
	 * redis
	 * 黑名单
	 */
	private Map<Long, Friend> blackList;
	
	private JedisUtil redis;
	
	public final static int Friend =1;
	public final static int Black =2;
	
	
	public TeamFriends(long teamId, List<FriendPO> list) {
		this.teamId = teamId;
		friendList = Maps.newConcurrentMap();
		blackList = Maps.newConcurrentMap();
		redis = InstanceFactory.get().getInstance(JedisUtil.class);
		this.init(list);
	}
	
	private void init(List<FriendPO> list) {
		// 好友列表
		if(list == null || list.size() == 0) {
			return;
		}
		for(FriendPO po : list) {
			this.friendList.put(po.getFriendTeamId(), new Friend(po));
		}
		// 黑名单列表
		List<Friend> blackli = redis.getList(RedisKey.Team_Friend_Blask_List + this.teamId);
		if(blackli == null || blackli.size() == 0) {
			return;
		}
		for(Friend f : blackli) {
			this.friendList.put(this.teamId, f);
		}
	}

	public boolean isFriend(long friendTeamId) {
		return this.friendList.containsKey(friendTeamId);
	}
	
	public boolean isBlackName(long friendTeamId) {
		return this.blackList.containsKey(friendTeamId);
	}
	
	public int getFriendSize() {
		return friendList.size(); 
	}
	
	public int getBlackSize() {
		return blackList.size(); 
	}

	public Collection<Friend> getFriendList() {
		return this.friendList.values();
	}
	
	public Collection<Friend> getBlackList() {
		return this.blackList.values();
	}
	
	/**
	 * 添加好友
	 * @param friendId
	 * @return 
	 */
	public void addFriend(long friendId) {
		Friend f = new Friend(teamId, friendId, 1);
		f.save();
		this.friendList.put(friendId, f);
	}
	
	/**
	 * 删除好友
	 * @param friendId
	 */
	public void delFriend(long friendId) {
		this.friendList.remove(friendId).del();
	}
	
	/**
	 * 添加黑名单
	 * @param friendId
	 */
	public void addBlack(long friendId) {
		Friend f = new Friend(teamId, friendId, 2);
		this.blackList.put(friendId, f);
		List<Friend> list = this.blackList.values().stream().collect(Collectors.toList());
		redis.rpush(RedisKey.Team_Friend_Blask_List + this.teamId, list);
	}
	
	/**
	 * 删除黑名单
	 * @param friendId
	 */
	public void delBlack(long friendId) {
		this.blackList.remove(friendId).del();
		List<Friend> list = this.blackList.values().stream().collect(Collectors.toList());
		redis.rpush(RedisKey.Team_Friend_Blask_List + this.teamId, list);
	}
	
	/** 获取某个好友信息*/
	public Friend getFriend(long friendId) {
	   return getFriendList().stream().filter(friend -> friend.getFriendTeamId() == friendId).findFirst().orElse(null);
	}
}
