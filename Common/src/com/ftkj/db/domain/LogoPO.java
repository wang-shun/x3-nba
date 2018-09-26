package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @Description:球员头像
 * @author Jay
 * @time:2017年3月16日 下午5:14:36
 */
public class LogoPO extends AsynchronousBatchDB {

	/**
	 * ID
	 */
	private int id;
	/**
	 * 球队
	 */
	private long teamId;
	/**
	 * 球员
	 */
	private int playerId;
	/**
	 * 品质
	 */
	private int quality;
	
	/**
	 * 创建时间
	 */
	private DateTime createTime;
	
	public LogoPO() {}
	public LogoPO(int id, long teamId, int playerId, int quality) {
		super();
		this.id = id;
		this.teamId = teamId;
		this.playerId = playerId;
		this.quality = quality;
		this.createTime = DateTime.now();
	}
	
	public LogoPO(int id, long teamId, int playerId) {
		super();
		this.id = id;
		this.teamId = teamId;
		this.playerId = playerId;
		this.createTime = DateTime.now();
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
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public DateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id, this.teamId, this.playerId, this.quality, this.createTime);
	}
	@Override
	public String getRowNames() {
		return "id,team_id,player_id,quality,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id, this.teamId, this.playerId, this.quality, this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_logo";
	}
	@Override
	public void del() {
		this.playerId = -1;
		save();
	}


}
