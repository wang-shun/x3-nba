package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class TaskPO extends AsynchronousBatchDB implements Serializable{


	private static final long serialVersionUID = 1L;
	
	/** 队伍ID */
	private long teamId;
	/** 任务ID */
	private int tid;
	/** 任务状态
	 *  TaskDefault(0),//任务默认
	 *  TaskOpen(1),//任务开启
	 *  TaskFinish(2),//任务完成
	 * 	TaskClose(3),//任务关闭
	 */
	private int status;	
	/** 任务创建时间*/
	private DateTime createTime;

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.tid,this.status,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, tid, `status`, create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.tid,this.status,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_task";
	}

	@Override
	public void del() {
	}

	@Override
	public String toString() {
		return "Task [tid=" + tid + ", status=" + status + "]";
	}


}
