package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class NBAPlayerScore implements Serializable{
	private static final long serialVersionUID = -255156454649224525L;
	private int gameId;
	private Date gameTime;
	private int teamId;
	private int seasonId;
	private int playerId;
	private int min;
	private int fgM;
	private int fgA;
	private int threePM;
	private int threePA;
	private int ftM;
	private int ftA;
	private int oreb;
	private int dreb;
	private int reb;
	private int ast;
	private int stl;
	private int blk;
	private int to;
	private int pf;
	private Integer effectPoint;
	private int pts;
	private boolean isStarter;
	//
	
	public NBAPlayerScore(){}

	public int getGameId() {
		return gameId;
	}

	public int getSeasonId() {
		return seasonId;
	}
	
	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
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

	public int getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	public int getMin() {
		return min;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public int getFgM() {
		return fgM;
	}
	
	public void setFgM(int fgM) {
		this.fgM = fgM;
	}
	
	public int getFgA() {
		return fgA;
	}
	
	public void setFgA(int fgA) {
		this.fgA = fgA;
	}
	
	public int getThreePM() {
		return threePM;
	}
	
	public void setThreePM(int threePM) {
		this.threePM = threePM;
	}

	public int getThreePA() {
		return threePA;
	}

	public void setThreePA(int threePA) {
		this.threePA = threePA;
	}
	
	public int getFtM() {
		return ftM;
	}
	
	public void setFtM(int ftM) {
		this.ftM = ftM;
	}
	
	public int getFtA() {
		return ftA;
	}
	
	public void setFtA(int ftA) {
		this.ftA = ftA;
	}

	public int getOreb() {
		return oreb;
	}

	public void setOreb(int oreb) {
		this.oreb = oreb;
	}

	public int getDreb() {
		return dreb;
	}

	public void setDreb(int dreb) {
		this.dreb = dreb;
	}

	public int getReb() {
		return reb;
	}

	public void setReb(int reb) {
		this.reb = reb;
	}
	
	public int getAst() {
		return ast;
	}

	public void setAst(int ast) {
		this.ast = ast;
	}

	public int getStl() {
		return stl;
	}

	public void setStl(int stl) {
		this.stl = stl;
	}

	public int getBlk() {
		return blk;
	}

	public void setBlk(int blk) {
		this.blk = blk;
	}

	public int getTo() {
		return to;
	}
	
	public void setTo(int to) {
		this.to = to;
	}

	public int getPf() {
		return pf;
	}

	public void setPf(int pf) {
		this.pf = pf;
	}

	public Integer getEffectPoint() {
		return effectPoint;
	}

	public void setEffectPoint(Integer effectPoint) {
		this.effectPoint = effectPoint;
	}

	public int getPts() {
		return pts;
	}

	public void setPts(int pts) {
		this.pts = pts;
	}

	public boolean isStarter() {
		return isStarter;
	}

	public void setStarter(boolean isStarter) {
		this.isStarter = isStarter;
	}
	
	public Date getGameTime() {
		return gameTime;
	}

	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}
	
}
