package com.ftkj.manager.train;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 单独配置的Npc球队训练馆数据,只配置一个球员参与训练,
 * 没有球队Id这里用NPCId作为球员Id.
 * @author mr.lei
 */
public class TrainNpcBean extends ExcelBean{

	/** npcId,这个值小于1000*/
    private int id;
    /** 训练球员id*/
    private int playerId;	
    /** 攻防cap值*/
    private int defence; 
    /** 训练位等级，只能1-10（可对应单位产出和训练位品质） */
    private int level;
    
	@Override
	public void initExec(RowData row) {
		
	}

	/** 获取npcId,这个值小于1000*/
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/** 获取训练球员id*/
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	/** 获取攻防cap值*/
	public int getDefence() {
		return defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	/** 获取训练位等级，只能1-10（可对应单位产出和训练位品质） */
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "TrainNpcBean [id=" + id + ", playerId=" + playerId
				+ ", defence=" + defence + ", level=" + level + "]";
	}
	
}
