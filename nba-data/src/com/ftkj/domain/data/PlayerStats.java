package com.ftkj.domain.data;

import java.io.Serializable;

public class PlayerStats implements Serializable{
	private static final long serialVersionUID = 4988173183239420486L;
	public long gamePlayerId;
	public int gameId;
	public int teamId;
	public String playerName;
	public int playerId;
	public String min;
	public String fgMA;
	public String threePMA;
	public String ftMA;
	public String oreb;
	public String dreb;
	public String reb;
	public String ast;
	public String stl;
	public String blk;
	public String to;
	public String pf;
	public String plus;
	public String pts;
	public boolean isStarter;

	public PlayerStats(){}

	public boolean is0(){
		if(getMin().equals("0") && getPts().equals("0") && getReb().equals("0") &&	
				getAst().equals("0") && getStl().equals("0") && getBlk().equals("0") && getTo().equals("0") && 
				getPf().equals("0") && getFTA().equals("0") && getThreePA().equals("0") && getFGA().equals("0")){
			return true;
		}
		return false;
	}

	//
	public String getFTM(){
		return ftMA.substring(0,ftMA.indexOf('-'));
	}
	public String getFTA(){
		return ftMA.substring(ftMA.indexOf('-')+1);
	}
	//
	public String getThreePM(){
		return threePMA.substring(0,threePMA.indexOf('-'));
	}
	public String getThreePA(){
		return threePMA.substring(threePMA.indexOf('-')+1);
	}
	//
	public String getFGM(){
		return fgMA.substring(0,fgMA.indexOf('-'));
	}
	public String getFGA(){
		return fgMA.substring(fgMA.indexOf('-')+1);
	}
	
	public long getGamePlayerId() {
		return gamePlayerId;
	}

	public void setGamePlayerId(long gamePlayerId) {
		this.gamePlayerId = gamePlayerId;
	}

	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public int getTeamId() {
		return teamId;
	}
	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getFgMA() {
		return fgMA;
	}
	public void setFgMA(String fgMA) {
		this.fgMA = fgMA;
	}
	public String getThreePMA() {
		return threePMA;
	}
	public void setThreePMA(String threePMA) {
		this.threePMA = threePMA;
	}
	public String getFtMA() {
		return ftMA;
	}
	public void setFtMA(String ftMA) {
		this.ftMA = ftMA;
	}
	public String getOreb() {
		if(oreb==null || oreb.equals(""))return "0";
		return oreb;
	}
	public void setOreb(String oreb) {
		this.oreb = oreb;
	}
	public String getDreb() {
		if(dreb==null || dreb.equals(""))return "0";
		return dreb;
	}
	public void setDreb(String dreb) {
		this.dreb = dreb;
	}
	public String getReb() {
		return reb;
	}
	public void setReb(String reb) {
		this.reb = reb;
	}
	public String getAst() {
		return ast;
	}
	public void setAst(String ast) {
		this.ast = ast;
	}
	public String getStl() {
		return stl;
	}
	public void setStl(String stl) {
		this.stl = stl;
	}
	public String getBlk() {
		return blk;
	}
	public void setBlk(String blk) {
		this.blk = blk;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getPf() {
		return pf;
	}
	public void setPf(String pf) {
		this.pf = pf;
	}
	public String getPlus() {
		return plus;
	}
	public void setPlus(String plus) {
		this.plus = plus;
	}
	public String getPts() {
		return pts;
	}
	public void setPts(String pts) {
		this.pts = pts;
	}
	public boolean isStarter() {
		return isStarter;
	}
	public void setStarter(boolean isStarter) {
		this.isStarter = isStarter;
	}

	@Override
	public String toString() {
		return "PlayerStats [ast=" + ast + ", blk=" + blk + ", dreb=" + dreb
				+ ", fgMA=" + fgMA + ", ftMA=" + ftMA + ", gameId=" + gameId
				+ ", gamePlayerId=" + gamePlayerId + ", isStarter=" + isStarter
				+ ", min=" + min + ", oreb=" + oreb + ", pf=" + pf
				+ ", playerId=" + playerId + ", playerName=" + playerName
				+ ", plus=" + plus + ", pts=" + pts + ", reb=" + reb + ", stl="
				+ stl + ", teamId=" + teamId + ", threePMA=" + threePMA
				+ ", to=" + to + "]";
	}
	
}
