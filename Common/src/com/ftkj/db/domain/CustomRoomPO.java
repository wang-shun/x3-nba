package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;

import java.util.List;

public class CustomRoomPO extends AsynchronousBatchDB {
	//房间ID
	private int roomId;
	//比赛类型
	private int pkType;
	//竞猜类型
	private int guessType;
	//获胜条件
	private int winCondition;
	//战力条件
	private int powerCondition;
	//等级条件
	private int levelCondition;
	
	//结算小节集合
	private String stepConditions;
	//结算球员位置
	private int positionCondition;
	//房间状态
	private int roomStatus;
	//房间宣言
	private String roomTip;
	
	//房间筹码
	private int roomMoney;
	//房间让分
	private int roomScore;
	
	//竞猜双方投注额
	private int homeMoney;
	private int awayMoney;
	
	//房主信息
	private String homeTeam;
	//挑战者信息
	private String awayTeam;
	
	private boolean autoStart;
	
	//-------------------------------------------------
	
	//参与投注的节点
	private String nodes;
	
	
	
	
	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getPkType() {
		return pkType;
	}

	public void setPkType(int pkType) {
		this.pkType = pkType;
	}

	public int getGuessType() {
		return guessType;
	}

	public void setGuessType(int guessType) {
		this.guessType = guessType;
	}

	public int getWinCondition() {
		return winCondition;
	}

	public void setWinCondition(int winCondition) {
		this.winCondition = winCondition;
	}

	public int getPowerCondition() {
		return powerCondition;
	}

	public void setPowerCondition(int powerCondition) {
		this.powerCondition = powerCondition;
	}

	public int getLevelCondition() {
		return levelCondition;
	}

	public void setLevelCondition(int levelCondition) {
		this.levelCondition = levelCondition;
	}

	public String getStepConditions() {
		return stepConditions;
	}

	public void setStepConditions(String stepConditions) {
		this.stepConditions = stepConditions;
	}

	public int getPositionCondition() {
		return positionCondition;
	}

	public void setPositionCondition(int positionCondition) {
		this.positionCondition = positionCondition;
	}

	public int getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(int roomStatus) {
		this.roomStatus = roomStatus;
	}

	public String getRoomTip() {
		return roomTip;
	}

	public void setRoomTip(String roomTip) {
		this.roomTip = roomTip;
	}

	public int getRoomMoney() {
		return roomMoney;
	}

	public void setRoomMoney(int roomMoney) {
		this.roomMoney = roomMoney;
	}

	public int getRoomScore() {
		return roomScore;
	}

	public void setRoomScore(int roomScore) {
		this.roomScore = roomScore;
	}

	public int getHomeMoney() {
		return homeMoney;
	}

	public void setHomeMoney(int homeMoney) {
		this.homeMoney = homeMoney;
	}

	public int getAwayMoney() {
		return awayMoney;
	}

	public void setAwayMoney(int awayMoney) {
		this.awayMoney = awayMoney;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public void setAutoStart(boolean autoStart) {
		this.autoStart = autoStart;
	}

	public String getNodes() {
		return nodes;
	}

	public void setNodes(String nodes) {
		this.nodes = nodes;
	}

	@Override
	public String getSource() {
		return null;
	}

	@Override
	public String getRowNames() {
		return null;
	}

    @Override
    public List<Object> getRowParameterList() {
        return null;
    }

	@Override
	public String getTableName() {
		return null;
	}

	@Override
	public void del() {

	}


}
