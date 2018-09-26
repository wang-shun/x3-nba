package com.ftkj.manager.bid;

import java.io.Serializable;
import java.util.Map;

import com.ftkj.server.GameSource;

/**
 * @author tim.huang
 * 2018年3月22日
 * 球队竞价对象
 */
public class TeamGuess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long teamId;
	private int id;
	private int price;
	private String node;
	private String teamName;
	private Map<Integer,String> sbMap;
	
	public TeamGuess(long teamId, String teamName,Map<Integer,String> sbMap) {
		super();
		this.teamId = teamId;
		this.teamName = teamName;
		this.id = -1;
		this.price = 0;
		this.node = GameSource.serverName;
		this.sbMap  = sbMap;
	}

	public Map<Integer, String> getSbMap() {
		return sbMap;
	}

	public void updatePrice(int id,int price){
		this.id = id;
		this.price = price;
	}
	
	public long getTeamId() {
		return teamId;
	}

	public int getId() {
		return id;
	}

	public int getPrice() {
		return price;
	}

	public String getNode() {
		return node;
	}

	public String getTeamName() {
		return teamName;
	}

	@Override
	public String toString() {
		return "TeamGuess [teamId=" + teamId + ", price=" + price + ", node="
				+ node + ", teamName=" + teamName + "]";
	}

}
