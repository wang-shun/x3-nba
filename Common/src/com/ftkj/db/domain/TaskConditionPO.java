package com.ftkj.db.domain;

import java.io.Serializable;
import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

public class TaskConditionPO extends AsynchronousBatchDB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** 队伍ID*/
	private long teamId;
	/** 任务ID*/
	private int tid;
	/** 条件类型*/
	private int cid;
	/** 当前计数*/
	private int valInt;
	/** 条件参数*/
	private String valStr;
	
	public TaskConditionPO() {
		this.valStr = "";
	}

	public TaskConditionPO(long teamId, int tid, int cid) {
		super();
		this.teamId = teamId;
		this.tid = tid;
		this.cid = cid;
		this.valStr = "";
		this.valInt = 0;
	}
	
	public TaskConditionPO(long teamId, int tid, int cid, int valInt,
			String valStr) {
		super();
		this.teamId = teamId;
		this.tid = tid;
		this.cid = cid;
		this.valInt = valInt;
		this.valStr = valStr;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
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

	public int getValInt() {
		return valInt;
	}

	public void setValInt(int valInt) {
		this.valInt = valInt;
	}

	public String getValStr() {
		return valStr;
	}

	public void setValStr(String valStr) {
		this.valStr = valStr;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.tid,this.cid,this.valInt,this.valStr);
	}

	@Override
	public String getRowNames() {
		return "team_id, tid, cid, val_int, val_str";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.tid,this.cid,this.valInt,this.valStr);
    }

	@Override
	public String getTableName() {
		return "t_u_task_condition";
	}

	@Override
	public void del() {

	}

	@Override
	public String toString() {
		return "TaskCondition [tid=" + tid + ", cid=" + cid + ", valInt=" + valInt + ", valStr=" + valStr + "]";
	}


}
