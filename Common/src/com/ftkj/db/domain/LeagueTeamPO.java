package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年5月18日
 * 联盟成员DB
 */
public class LeagueTeamPO extends AsynchronousBatchDB {

	private static final long serialVersionUID = 1L;
	 /** 联盟ID*/
	private int leagueId;
	 /** 成员球队ID*/
	private long teamId;
	 /** 成员职位等级*/
	private int level; 
	 /** 成员贡献*/
	private int score;
	 /** 成员功勋*/
	private int feats; 
     /** 成员周贡献*/
    private int weekScore;
	 /** 成员入盟时间*/
	private DateTime createTime;
	
	public LeagueTeamPO() {
	}

	public LeagueTeamPO(int leagueId, long teamId, int level,int feats) {
		super();
		this.leagueId = leagueId;
		this.teamId = teamId;
		this.level = level;
		this.createTime = DateTime.now();
		this.feats = feats;
		this.score = 0;
		this.weekScore = 0;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setFeats(int feats) {
		this.feats = feats;
	}

	public int getFeats() {
		return feats;
	}

	public int getLeagueId() {
		return leagueId;
	}

	public long getTeamId() {
		return teamId;
	}

	public int getLevel() {
		return level;
	}

	public int getScore() {
		return score;
	}

    public int getWeekScore() {
        return weekScore;
    }

    public void setWeekScore(int weekScore) {
        this.weekScore = weekScore;
    }
    
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.leagueId,this.teamId,this.level,this.score,this.feats,this.weekScore,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "league_id, team_id, level, score, feats, week_score, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.leagueId,this.teamId,this.level,this.score,this.feats,this.weekScore,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_league_team";
	}

	@Override
	public void del() {
		this.leagueId = 0;
	}


}
