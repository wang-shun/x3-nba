package com.ftkj.manager.gym;

import java.io.Serializable;

import com.ftkj.manager.team.TeamNode;

/**
 * @author tim.huang
 * 2017年7月6日
 * 球馆偷取匹配玩家信息
 */
public class ArenaStealMatchTeam implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TeamNode node;
	private String name;
	private int arenaLevel;
	private int gold;
	
	
	public int getGold() {
		return gold;
	}
	public void setGold(int gold) {
		this.gold = gold;
	}
	public TeamNode getNode() {
		return node;
	}
	public void setNode(TeamNode node) {
		this.node = node;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getArenaLevel() {
		return arenaLevel;
	}
	public void setArenaLevel(int arenaLevel) {
		this.arenaLevel = arenaLevel;
	}
	
	
}
