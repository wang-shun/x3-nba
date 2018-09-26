package com.ftkj.manager.system;

import java.util.Set;

import com.ftkj.server.RedisKey;
import com.ftkj.tool.redis.JedisUtil;
import com.google.common.collect.Sets;

/**
 * 一场竞猜比赛里所有玩家竞猜的数据.
 * @author mr.lei
 */
public class NBAPlayerGuess {
	/**竞猜比赛Id*/
	private int gameId;
	/** 竞猜主队赢的球队Id集合*/
	private Set<Long> homeTeamIds;
	/** 竞猜客队赢的球队Id集合*/
	private Set<Long> awayTeamIds;
	/**用户保存竞猜主队赢的数据key值*/
	private String redis_key_gameId_home;
	/**用户保存竞猜客队赢的数据key值*/
	private String redis_key_gameId_away;
	
	public NBAPlayerGuess(int gameId) {
		this.gameId = gameId;
		this.homeTeamIds = Sets.newConcurrentHashSet();
		this.awayTeamIds = Sets.newConcurrentHashSet();
		this.redis_key_gameId_home = RedisKey.Nba_Game_Guess + gameId + "_" + 1;
		this.redis_key_gameId_away = RedisKey.Nba_Game_Guess + gameId + "_" + 2;
	}
	
	/**
	 * 初始化属性,从Redis中获取保存的所有玩家竞猜的结果数据.
	 * @param gameId
	 * @param redis
	 */
	public NBAPlayerGuess(int gameId, JedisUtil redis) {
		this.gameId = gameId;
		this.homeTeamIds = Sets.newConcurrentHashSet();
		this.awayTeamIds = Sets.newConcurrentHashSet();
		this.redis_key_gameId_home = RedisKey.Nba_Game_Guess + gameId + "_" + 1;
		this.redis_key_gameId_away = RedisKey.Nba_Game_Guess + gameId + "_" + 2;
		initGamePlayerGuessData(redis);
	}
	
	/**
	 * 初始化玩家已经竞猜的数据.
	 * @param redis
	 */
	private void initGamePlayerGuessData(JedisUtil redis){
		Set<String> smembers = redis.smembers(this.redis_key_gameId_home);
		if (smembers != null && smembers.size() > 0) {
			for (String teamIdStr : smembers) {
				this.homeTeamIds.add(Long.valueOf(teamIdStr));
			}
		}
		
		smembers = redis.smembers(this.redis_key_gameId_away);
		if (smembers != null && smembers.size() > 0) {
			for (String teamIdStr : smembers) {
				this.awayTeamIds.add(Long.valueOf(teamIdStr));
			}
		}
	}
	
	/**
	 * 玩家竞猜的主对还是客队,1主队,2客队,0没有竞猜.
	 * @param teamId
	 * @return
	 */
	public int getTeamGuessId(long teamId){
		if(this.homeTeamIds.contains(teamId)){
			return 1;
		}
		
		if(this.awayTeamIds.contains(teamId)){
			return 2;
		}
		
		return 0;
	}

	/**
	 * 保存玩家竞猜数据.
	 * @param winId		1主队赢,2客队赢
	 * @param teamId
	 */
	public void addTeam(int winId, long teamId, JedisUtil redis){
		if(winId == 1){
			this.homeTeamIds.add(teamId);
			redis.sadd(this.redis_key_gameId_home, Long.toString((teamId)));
		}else{
			this.awayTeamIds.add(teamId);
			redis.sadd(this.redis_key_gameId_away, Long.toString((teamId)));
		}
	}
	
	/**
	 * 从Redis中删除玩家竞猜数据.
	 */
	public void delNBAPlayerGuessData(JedisUtil redis) {
		redis.del(this.redis_key_gameId_away);
		redis.del(this.redis_key_gameId_home);
	}

	public Set<Long> getHomeTeamIds() {
		return homeTeamIds;
	}

	public Set<Long> getAwayTeamIds() {
		return awayTeamIds;
	}

	public int getGameId() {
		return gameId;
	}

	public String getRedis_key_gameId_home() {
		return redis_key_gameId_home;
	}

	public String getRedis_key_gameId_away() {
		return redis_key_gameId_away;
	}

}
