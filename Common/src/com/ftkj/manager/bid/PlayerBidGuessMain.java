package com.ftkj.manager.bid;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author tim.huang
 * 2018年3月23日
 *
 */
public class PlayerBidGuessMain implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<Integer,PlayerBidGuessSimple> guessMaps;
	private int maxPeople;
	
	public PlayerBidGuessMain(Map<Integer, PlayerBidGuessSimple> guessMaps,
			int maxPeople) {
		super();
		this.guessMaps = guessMaps;
		this.maxPeople = maxPeople;
	}
	public Map<Integer, PlayerBidGuessSimple> getGuessMaps() {
		return guessMaps;
	}
	public int getMaxPeople() {
		return maxPeople;
	}
	
	
}
