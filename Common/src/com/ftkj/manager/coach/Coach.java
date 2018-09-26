package com.ftkj.manager.coach;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2017年9月18日
 * 球队教练数据 
 */
public class Coach extends AsynchronousBatchDB implements Serializable{

	private static final long serialVersionUID = 1L;
	/** 球队ID*/
	private long teamId;
	/** 教练ID*/
	private int cid;
	/** 教练基础ID*/
	private int tid;
	/** 教练开启状态*/
	private int status;
	private DateTime createTime;
	
	
	public Coach() {
	}


	public Coach(long teamId, int cid, int tid) {
		super();
		this.teamId = teamId;
		this.cid = cid;
		this.tid = tid;
		this.createTime = DateTime.now();
	}


	public void updateStatus(int status){
		this.status = status;
		this.save();
	}
	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.cid,this.tid,this.status,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, cid, tid, `status`, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.cid,this.tid,this.status,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_coach";
	}

	@Override
	public void del() {
		
	}


}
