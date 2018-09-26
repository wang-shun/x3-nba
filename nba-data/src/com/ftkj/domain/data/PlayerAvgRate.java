package com.ftkj.domain.data;

public class PlayerAvgRate implements java.io.Serializable {
	private static final long serialVersionUID = 6844356942935508635L;
	private int seasonId;
	private int gameType;
	private int teamId;
	private int playerId;
	private int playCount; //比赛数
	private int starterCount;
	private float fgm;
	private float fga;
	private float ftm;
	private float fta;
	private float pts;
	private float threePm;
	private float threePa;
	private float oreb;
	private float dreb;
	private float ast;
	private float stl;
	private float blk;
	private float to;
	private float pf;
	private float min;
	
	public PlayerAvgRate() {}
	
	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public void setStarterCount(int starterCount) {
		this.starterCount = starterCount;
	}

	public void setFga(float fga) {
		this.fga = fga;
	}

	public void setFgm(float fgm) {
		this.fgm = fgm;
	}

	public void setFtm(float ftm) {
		this.ftm = ftm;
	}

	public void setFta(float fta) {
		this.fta = fta;
	}

	public void setPts(float pts) {
		this.pts = pts;
	}

	public void setThreePm(float threePm) {
		this.threePm = threePm;
	}

	public void setThreePa(float threePa) {
		this.threePa = threePa;
	}

	public void setOreb(float oreb) {
		this.oreb = oreb;
	}

	public void setDreb(float dreb) {
		this.dreb = dreb;
	}

	public void setAst(float ast) {
		this.ast = ast;
	}

	public void setStl(float stl) {
		this.stl = stl;
	}

	public void setBlk(float blk) {
		this.blk = blk;
	}

	public void setTo(float to) {
		this.to = to;
	}

	public void setPf(float pf) {
		this.pf = pf;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getPlayCount() {
		return playCount;
	}

	public int getStarterCount() {
		return starterCount;
	}

	public float getFga() {
		return fga;
	}

	public float getFgm() {
		return fgm;
	}

	public float getFtm() {
		return ftm;
	}

	public float getFta() {
		return fta;
	}

	public float getPts() {
		return pts;
	}

	public float getThreePm() {
		return threePm;
	}

	public float getThreePa() {
		return threePa;
	}

	public float getOreb() {
		return oreb;
	}

	public float getDreb() {
		return dreb;
	}

	public float getAst() {
		return ast;
	}

	public float getStl() {
		return stl;
	}

	public float getBlk() {
		return blk;
	}

	public float getTo() {
		return to;
	}

	public float getPf() {
		return pf;
	}

	public float getMin() {
		return min;
	}
	
	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	@Override
	public String toString() {
		return "PlayerAvgRate [ast=" + ast + ", blk=" + blk + ", dreb=" + dreb
				+ ", fga=" + fga + ", fgm=" + fgm + ", fta=" + fta + ", ftm="
				+ ftm + ", gameType=" + gameType + ", min=" + min + ", oreb="
				+ oreb + ", pf=" + pf + ", playCount=" + playCount
				+ ", playerId=" + playerId + ", pts=" + pts + ", seasonId="
				+ seasonId + ", starterCount=" + starterCount + ", stl=" + stl
				+ ", teamId=" + teamId + ", threePa=" + threePa + ", threePm="
				+ threePm + ", to=" + to + "]";
	}
	
	
}
