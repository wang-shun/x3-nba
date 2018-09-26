package com.ftkj.manager.gym;

import java.io.Serializable;
import java.util.List;

import com.ftkj.manager.team.TeamNode;

/**
 * @author tim.huang
 * 2017年7月6日
 * 攻击匹配的玩家信息
 */
public class ArenaAttackMatchTeam implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TeamNode node;
	private String name;
	private int arenaLevel;
	private List<ArenaMatchConstruction> cList;
	
	
	
	
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
	public List<ArenaMatchConstruction> getcList() {
		return cList;
	}
	public void setcList(List<ArenaMatchConstruction> cList) {
		this.cList = cList;
	}
	
	
	
}
