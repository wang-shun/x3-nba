package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月2日
 * 战术
 */
public class TacticsPO extends AsynchronousBatchDB {
  
    private static final long serialVersionUID = 3584849961246059048L;
    
    /** 球队ID */
	private long teamId;
	/** 战术ID */
	private int tid;
	/** 战术等级 */
	private int level;
	/** 创建时间 */
	private DateTime createTime;
	/** 突破剩余时间 */
	private DateTime buffTime;	
	
	public TacticsPO() {
		super();
	}

	public TacticsPO(long teamId, int tid) {
		super();
		this.teamId = teamId;
		this.tid = tid;
		this.level = 1;
		this.buffTime = GameConsole.Min_Date;
		this.createTime = DateTime.now();
	}

	public TacticsPO(long teamId, int tid,int level) {
		super();
		this.teamId = teamId;
		this.tid = tid;
		this.level = level;
		this.buffTime = GameConsole.Min_Date;
		this.createTime = DateTime.now();
	}
	
	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getTeamId() {
		return teamId;
	}

	public int getTid() {
		return tid;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.tid,this.level,this.buffTime,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id,tid,level,buff_time,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.tid,this.level,this.buffTime,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_tactics";
	}

	@Override
	public void del() {

	}

	public DateTime getBuffTime() {
		return buffTime;
	}

	public void setBuffTime(DateTime buffTime) {
		this.buffTime = buffTime;
	}


}
