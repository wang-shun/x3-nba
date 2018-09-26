package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class NBATeamScore implements Serializable{

	private static final long serialVersionUID = -4240172528641895269L;
	private int gameId;
	private Date gameTime;
	private int seasonId;
	private int teamId;
	private int step[];
	private int et[];
	private int score;
	
	public NBATeamScore() {
		step=new int[4];
		et=new int[8];
	}
	
	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
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
	
	public int[] getStep() {
		return step;
	}

	public void setStep(int[] step) {
		this.step = step;
	}

	public int[] getEt() {
		return et;
	}

	public void setEt(int[] et) {
		this.et = et;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Date getGameTime() {
		return gameTime;
	}

	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}
}
