package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author tim.huang
 * 2017年7月5日
 *
 */
public class ArenaPlayerPO extends AsynchronousBatchDB {

	private long teamId;
	private int pid;
	private int playerId;
	private int tid;
	private int grade;
	private int position;
	private DateTime createTime;
	

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public long getTeamId() {
		return teamId;
	}

	public int getPid() {
		return pid;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getTid() {
		return tid;
	}

	public int getGrade() {
		return grade;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.pid,this.playerId,this.tid,this.grade,this.position,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, pid, player_id, tid, grade, `position`, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.pid,this.playerId,this.tid,this.grade,this.position,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_arena_player";
	}

	@Override
	public void del() {
		this.playerId = 0;
		this.save();
	}


}
