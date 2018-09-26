package com.ftkj.db.domain;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;

public class NBAPKScoreBoardDetail {
	private DateTime time;
	private int id;
	private int playerId;
	private int gameId;
	private int teamId;
	private int isStarter;
	private int fgm;
	private int fga;
	private int ftm;
	private int fta;
	private int threePm;
	private int threePa;
	private int oreb;
	private int dreb;
	private int reb;
	private int ast;
	private int stl;
	private int blk;
	private int to;
	private int pf;
	private int pts;
	private int effectPoint;
	private int min;
	
	public DateTime getTime() {
		return time;
	}
	public void setTime(DateTime time) {
		this.time = time;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
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
	public int getIsStarter() {
		return isStarter;
	}
	public void setIsStarter(int isStarter) {
		this.isStarter = isStarter;
	}
	public int getFgm() {
		return fgm;
	}
	public void setFgm(int fgm) {
		this.fgm = fgm;
	}
	public int getFga() {
		return fga;
	}
	public void setFga(int fga) {
		this.fga = fga;
	}
	public int getFtm() {
		return ftm;
	}
	public void setFtm(int ftm) {
		this.ftm = ftm;
	}
	public int getFta() {
		return fta;
	}
	public void setFta(int fta) {
		this.fta = fta;
	}
	public int getThreePm() {
		return threePm;
	}
	public void setThreePm(int threePm) {
		this.threePm = threePm;
	}
	public int getThreePa() {
		return threePa;
	}
	public void setThreePa(int threePa) {
		this.threePa = threePa;
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
	public int getPts() {
		return pts;
	}
	public void setPts(int pts) {
		this.pts = pts;
	}
	public int getEffectPoint() {
		return effectPoint;
	}
	public void setEffectPoint(int effectPoint) {
		this.effectPoint = effectPoint;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	@Override
	public String toString() {
		return "NBAPKScoreBoardDetail [time=" + DateTimeUtil.getString(time) + ", id=" + id + ", playerId=" + playerId + ", gameId=" + gameId
				+ ", teamId=" + teamId + "]";
	}
	
	
}
