package com.ftkj.manager.bid;

import java.io.Serializable;
import java.util.List;

/**
 * @author tim.huang
 * 2018年3月23日
 *
 */
public class PlayerBidGuessSimple implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int playerId;
	private int maxPrice;
	private int people;
	private int group;
	private List<TeamGuess> topGuess;
	
	
	public PlayerBidGuessSimple(int id, int playerId, int maxPrice,int people,int group,List<TeamGuess> topGuess) {
		super();
		this.id = id;
		this.playerId = playerId;
		this.maxPrice = maxPrice;
		this.people = people;
		this.group = group;
		this.topGuess = topGuess;
	}
	
	
	public List<TeamGuess> getTopGuess() {
		return topGuess;
	}


	public void setTopGuess(List<TeamGuess> topGuess) {
		this.topGuess = topGuess;
	}


	public int getGroup() {
		return group;
	}


	public void setGroup(int group) {
		this.group = group;
	}


	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}
	
}
