package com.ftkj.manager.match;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.db.domain.match.MatchBestPO;

public class MatchBest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MatchBestPO matchBestPO;
	
	public void save() {
		this.matchBestPO.setUpdateTime(DateTime.now());
		this.matchBestPO.save();
	}

	public MatchBest(MatchBestPO matchBest) {
		super();
		this.matchBestPO = matchBest;
	}

	public int getRank() {
		return this.matchBestPO.getRank();
	}
	
	public void setRank(int rank) {
		this.matchBestPO.setRank(rank);
	}
	
	public int getMatchId() {
		return this.matchBestPO.getMatchId();
	}
	
}
