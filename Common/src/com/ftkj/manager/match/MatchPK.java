package com.ftkj.manager.match;

import org.joda.time.DateTime;

import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.manager.team.Team;

public class MatchPK {

	
	private MatchPKPO matchPKPO;
	
	private Team homeTeam;
	private Team awayTeam;
	
	public MatchPK() {
	}

	public MatchPK(long battleId, int seq, int matchId, int round, long home_id, long away_id) {
		super();
		this.matchPKPO = new MatchPKPO(battleId, seq, matchId, round, home_id, away_id);
	}
	
	public MatchPK(MatchPKPO pkInfo) {
		this.matchPKPO = pkInfo;
	}

	public void save() {
		this.matchPKPO.save();
	}
	
	public long getBattleId() {
		return this.matchPKPO.getBattleId();
	}
	
	public long getHomeId() {
		return this.matchPKPO.getHomeId();
	}
	
	public long getAwayId() {
		return this.matchPKPO.getAwayId();
	}

	public long getWinTeamId() {
		return this.matchPKPO.getWinTeamId();
	}

	public void setWinTeamId(long winTeamId) {
		this.matchPKPO.setWinTeamId(winTeamId);
	}

	public int getHomeScore() {
		return this.matchPKPO.getHomeScore();
	}

	public void setHomeScore(int homeScore) {
		this.matchPKPO.setHomeScore(homeScore);
	}

	public int getAwayScore() {
		return this.matchPKPO.getAwayScore();
	}

	public void setAwayScore(int awayScore) {
		this.matchPKPO.setAwayScore(awayScore);
	}

	public int getStatus() {
		return this.matchPKPO.getStatus();
	}

	public void setStatus(int status) {
		this.matchPKPO.setStatus(status);
	}

	public DateTime getEndTime() {
		return this.matchPKPO.getEndTime();
	}

	public void setEndTime(DateTime endTime) {
		this.matchPKPO.setEndTime(endTime);
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	@Override
	public String toString() {
		return "MatchPK [matchPKPO=" + matchPKPO + "]";
	}
	
	
	
}
