package com.ftkj.manager.gym;

import java.io.Serializable;
import java.util.List;

import com.ftkj.enums.EArenaCType;
import com.ftkj.manager.team.TeamNode;
import com.ftkj.manager.team.TeamNodeInfo;

/**
 * @author tim.huang
 * 2017年7月10日
 * 球馆跨服
 */
public class TeamArenaCross implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long teamId;
	private int gold;
	private int level;
	private String name;
	private TeamNodeInfo local;
	private int defend;
	private List<TeamArenaConstruction> cList;
	
	public TeamArenaCross(long teamId){
		this.teamId = teamId;
	}
	
	public TeamArenaCross(long teamId, int gold, int level, int defend,String name,
			TeamNodeInfo local, List<TeamArenaConstruction> cList) {
		super();
		this.teamId = teamId;
		this.gold = gold;
		this.level = level;
		this.defend = defend;
		this.name = name;
		this.local = local;
		this.cList = cList;
//		this.local = new TeamNode(teamId, GameSource.serverName);
	}
	
	public boolean checkAttackConstruction(){
		return cList.stream().filter(c->c.getCurGold()>0).findFirst().isPresent();
	}

	public int getDefend() {
		return defend;
	}

	public long getTeamId() {
		return teamId;
	}

	public int getGold() {
		return gold;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public TeamNode getLocal() {
		return local;
	}

	public List<TeamArenaConstruction> getcList() {
		return cList;
	}
	
	
	public TeamArenaConstruction getTeamArenaConstruction(EArenaCType cid){
		return this.cList.stream().filter(c->c.getcId()==cid).findFirst().orElse(null);
	}
	
	
	
	
}
