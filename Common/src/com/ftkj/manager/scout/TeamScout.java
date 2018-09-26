package com.ftkj.manager.scout;

import java.io.Serializable;
import java.util.List;

/**
 * @author tim.huang
 * 2017年3月22日
 * 玩家球探
 */
public class TeamScout implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 球探中球员信息 
	 */
	private List<ScoutPlayer> players;
	
	/**
	 * 当前球探信息的版本号
	 */
	private int version;
	
	
	
	public TeamScout() {
		version = -1;
	}

	public void updatePlayer(List<ScoutPlayer> players,int version){
		this.players = players;
		this.version = version;
	}

	public ScoutPlayer getPlayer(int index) {
		if(index<0 || index>=players.size()) {
			return null;
		}
		return players.get(index);
	}

	public List<ScoutPlayer> getPlayers() {
		return players;
	}

	public int getVersion() {
		return version;
	}
	
	
	
	
	
	
	
}
