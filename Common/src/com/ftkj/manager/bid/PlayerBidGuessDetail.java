package com.ftkj.manager.bid;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.db.domain.NBAPKScoreBoardDetail;
import com.ftkj.enums.ErrorCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author tim.huang 2018年3月22日
 *
 */
public class PlayerBidGuessDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private int playerId;
	private int group;
	private int position;
	// 显示的详情
	private NBAPKScoreBoardDetail detail;
	private int maxPrice;
	private int totalPrice;
	private Map<Long,TeamGuess> teamMaps;
	private List<TeamGuess> logTeam;
	
	public synchronized ErrorCode addTeam(TeamGuess team){
//		if(team.getPrice()<=this.maxPrice) return ErrorCode.PlayerBid_0;
		TeamGuess guess = teamMaps.get(team.getTeamId());
		int add = 0;
		if(guess != null){
			add = team.getPrice() - guess.getPrice();
		}else{
			add = team.getPrice();
		}
		totalPrice+=add;
		this.maxPrice = this.maxPrice<team.getPrice()?team.getPrice():this.maxPrice;
		this.teamMaps.put(team.getTeamId(), team);
		this.logTeam.add(team);
		return ErrorCode.Success;
	}
	
	public List<TeamGuess> getLogTeamList(){
		if(logTeam!=null && logTeam.size()>5){
			List<TeamGuess> result = Lists.newArrayList(logTeam.subList(logTeam.size()-5, logTeam.size()));
			Collections.reverse(result);
			return result;
		}else{
			List<TeamGuess> result = Lists.newArrayList();
			result.addAll(logTeam);
			Collections.reverse(result);
			return logTeam;
		}
	}
	
	public int getPeople(){
		return this.teamMaps.size();
	}
	
	public List<TeamGuess> getWinTeamList(int mod){
		if(this.teamMaps.size()<=0) {
			return Lists.newArrayList();
		}
		int count = (int)Math.ceil(this.teamMaps.size()/(mod+0f));
		if(count <=0) {
			count =1;
		}
		if(this.teamMaps.size()<=0) {
			return Lists.newArrayList();
		}
		//
		List<TeamGuess> result= this.teamMaps.values().stream().sorted((a,b)->sortedTeamGuess(a, b)).limit(count).collect(Collectors.toList());
		return result;
	}
	
	
	private int sortedTeamGuess(TeamGuess a,TeamGuess b){
		if(a.getPrice()==b.getPrice()) {
			return 0;
		}
		if(a.getPrice()>b.getPrice()) {
			return -1;
		}
		return 1;
	}
	
	public int getMaxPrice() {
		return maxPrice;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public int getId() {
		return id;
	}
	
	public int getPosition() {
		return position;
	}

	public int getPlayerId() {
		return playerId;
	}

	public NBAPKScoreBoardDetail getDetail() {
		return detail;
	}

	public int getGroup() {
		return group;
	}

	public PlayerBidGuessDetail(int id,int playerId, int group, int position,
			NBAPKScoreBoardDetail detail) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.group = group;
		this.position = position;
		this.detail = detail;
		this.teamMaps = Maps.newConcurrentMap();
		this.logTeam = Lists.newArrayList();
	}


}
