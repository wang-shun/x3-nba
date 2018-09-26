package com.ftkj.db.domain;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

/**
 * @Description:好友模块
 * @author Jay
 * @time:2017年3月16日 下午5:14:36
 */
public class FriendPO extends AsynchronousBatchDB implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 球队
	 */
	private long teamId;
	/**
	 * 好友
	 */
	private long friendTeamId;
	/**
	 * 类型, 默认只存好友1；
	 */
	private int type;
	
	private DateTime createTime;
	
	/**
     * 拒绝时间
     */
	private long refusedTime;

	public FriendPO() {
	}
	
	public FriendPO(long teamId, long friendId, int type) {
		super();
		this.teamId = teamId;
		this.friendTeamId = friendId;
		this.type = type;
		this.createTime = DateTime.now();
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public long getFriendTeamId() {
		return friendTeamId;
	}

	public void setFriendTeamId(long friendTeamId) {
		this.friendTeamId = friendTeamId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId, this.friendTeamId, this.type, this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id,friend_team_id,type,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.friendTeamId, this.type, this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_friend";
	}

    @Override
	public void del() {
		this.setType(-1); // -1删除
		this.save();		
	}

    public long getRefusedTime() {
        return refusedTime;
    }

    public void setRefusedTime(long refusedTime) {
        this.refusedTime = refusedTime;
    }


}
