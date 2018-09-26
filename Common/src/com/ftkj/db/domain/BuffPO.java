package com.ftkj.db.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.manager.buff.TeamBuff;
import com.ftkj.util.StringUtil;


/**
 * Buff
 * @author Jay
 * @time:2017年10月30日 下午5:38:28
 */
public class BuffPO extends AsynchronousBatchDB implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * buffID
	 */
	private int id;
	/**
	 * 球队
	 */
	private long teamId;
	/**
	 * 好友
	 */
	private String params;
	/**
	 * 类型, 默认只存好友1；
	 */
	private DateTime endTime;
	
	private DateTime createTime;

	public BuffPO() {
	}
	
	public BuffPO(long teamId, TeamBuff teamBuff) {
		super();
		this.id = teamBuff.getBuffID();
		this.teamId = teamId;
		this.endTime = teamBuff.getEndTime();
		this.createTime = DateTime.now();
		this.params = teamBuff.getValues().getValueStr();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id, this.teamId, this.params, this.endTime, this.createTime);
	}

	@Override
	public String getRowNames() {
		return "id,team_id,params,end_time,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id, this.teamId, this.params, this.endTime, this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_buff";
	}

	@Override
	public void del() {
		this.endTime = GameConsole.Min_Date;
		this.save();
	}


}
