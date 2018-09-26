package com.ftkj.manager.gym;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.db.domain.TeamArenaConstructionPO;
import com.ftkj.enums.EArenaCType;
import com.ftkj.enums.EPlayerGrade;

public class TeamArenaConstruction implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TeamArenaConstructionPO po;
	
	public TeamArenaConstruction(TeamArenaConstructionPO po){
		this.po = po;
	}

	public static TeamArenaConstruction createTeamArenaConstruction(long teamId,EArenaCType type,int initGold){
		 TeamArenaConstructionPO po = new TeamArenaConstructionPO();
		 po.setcId(type.getType());
		 po.setCurGold(initGold);
		 po.setMaxGold(initGold);
		 po.setPlayerGrade(0);
		 po.setPlayerId(0);
		 po.setTeamId(teamId);
		 po.setUpdateTime(DateTime.now());
		 po.save();
		 TeamArenaConstruction tc = new TeamArenaConstruction(po);
		 return tc;
	}
	
	
	public void levelup(){
		this.po.setCurGold(0);
		this.po.setMaxGold(0);
		this.po.setPlayerGrade(0);
		this.po.setPlayerId(0);
		this.po.save();
	}
	
	
	public void removePlayer(){
		this.po.setPlayerGrade(0);
		this.po.setPlayerId(0);
		this.po.save();
	}
	
	
	public void updatePlayer(ArenaPlayer player){
		this.po.setPlayerGrade(player.getGrade().ordinal());
		this.po.setPlayerId(player.getPlayerId());
		this.po.save();
	}
	
	public void updateGold(int val){
		this.po.setCurGold(this.po.getCurGold()+val);
		if(this.po.getCurGold()>this.po.getMaxGold()){
			this.po.setMaxGold(this.po.getCurGold());
		}
		this.po.save();
	}
	
	public long getTeamId(){
		return this.po.getTeamId();
	}
	
	public EArenaCType getcId() {
		return EArenaCType.getEArenaCType(this.po.getcId());
	}

	public int getCurGold() {
		return this.po.getCurGold();
	}

	public int getMaxGold() {
		return this.po.getMaxGold();
	}

	public int getPlayerId() {
		return this.po.getPlayerId();
	}

	public EPlayerGrade getPlayerGrade() {
		return EPlayerGrade.values()[this.po.getPlayerGrade()];
	}

	public DateTime getUpdateTime() {
		return this.po.getUpdateTime();
	}
	
	
	
	
}
