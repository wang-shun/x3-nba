package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;


public class GameDataJobRunLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2253134144612565726L;
	private int id;
	private Date runTime;
	private Date gameTime;	
	
	public GameDataJobRunLog() {}
	
	/**
	 * @return the gameTime
	 */
	public Date getGameTime() {
		return gameTime;
	}

	/**
	 * @param gameTime the gameTime to set
	 */
	public void setGameTime(Date gameTime) {
		this.gameTime = gameTime;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the runTime
	 */
	public Date getRunTime() {
		return runTime;
	}
	/**
	 * @param runTime the runTime to set
	 */
	public void setRunTime(Date runTime) {
		this.runTime = runTime;
	}

	@Override
	public String toString() {
		return "GameDataJobRunLog [gameTime=" + gameTime + ", id=" + id
				+ ", runTime=" + runTime + "]";
	}		
	
	
}
