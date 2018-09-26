package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang
 * 2017年3月2日
 * 球队
 */
public class TeamPO extends AsynchronousBatchDB {

	private static final long serialVersionUID = 1L;
	
	/** 球队ID*/
	private long teamId;
	/** 玩家ID*/
	private long userId;
	/** 服务器id*/
	private int shardId;
	/** 球队等级*/
	private int level;
	/** 球队名称*/
	private String name="";
	/** 球队图标*/
	private String logo="";
	/** 球队称号*/
	private String title="";
	/** 小秘书ID*/
	private int secId;
	/** 胜场次数*/
	private int pkWin;
	/** 新手引导*/
	private String help="";
	/** 任务目标ID */
	private int taskStep;	
	/** 工资帽*/
	private int price;
	/** 购买工资帽的次数*/
	private int priceCount;	
	/** 替补位开启数量 */
	private int lineupCount;	
	/** 最后一次登录时间 */
	private DateTime lastLoginTime;
	/** 下线时间 */
	private DateTime lastOfflineTime;
	/** 创建时间 */
	private DateTime createTime;
	/** 世界聊天禁言状态 0 是正常 1是禁言   */
	private int chatStatus;
	/** 用户状态，封号等 (0 是正常) */
	private int userStatus;
	
	public int getLineupCount() {
		return lineupCount;
	}
	public void setLineupCount(int lineupCount) {
		this.lineupCount = lineupCount;
	}
	public String getHelp() {
		return help;
	}
	public void setHelp(String help) {
		this.help = help;
	}
	public int getPkWin() {
		return pkWin;
	}
	public void setPkWin(int pkWin) {
		this.pkWin = pkWin;
	}
	public DateTime getLastLoginTime() {
		return lastLoginTime;
	}
	public int getSecId() {
		return secId;
	}
	public void setSecId(int secId) {
		this.secId = secId;
	}
	public void setLastLoginTime(DateTime lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public int getPriceCount() {
		return priceCount;
	}
	public void setPriceCount(int priceCount) {
		this.priceCount = priceCount;
	}
	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setShardId(int shardId) {
		this.shardId = shardId;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public long getUserId() {
		return userId;
	}

	public int getShardId() {
		return shardId;
	}

	public int getPrice() {
		return price;
	}

	public long getTeamId() {
		return teamId;
	}

	public int getLevel() {
		return level;
	}

	public String getTitle() {
		return title;
	}

	public String getName() {
		return name;
	}

	public String getLogo() {
		return logo;
	}
	
	public int getTaskStep() {
		return taskStep;
	}
	public void setTaskStep(int taskStep) {
		this.taskStep = taskStep;
	}
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.userId,this.shardId,this.level,this.name,this.logo,this.title,this.price,
				this.priceCount,this.lineupCount,this.secId,this.help,this.taskStep,this.userStatus, this.chatStatus,this.lastLoginTime,this.lastOfflineTime,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id,user_id,shard_id,level,`name`,logo,title,price,price_count,lineup_count,sec_id,help_step,task_step,user_status,chat_status,last_login_time,last_offline_time,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.userId,this.shardId,this.level,this.name,this.logo,this.title,this.price,
            this.priceCount,this.lineupCount,this.secId,this.help,this.taskStep,this.userStatus, this.chatStatus,this.lastLoginTime,this.lastOfflineTime,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_team";
	}

	@Override
	public void del() {

	}
	
	@Override
	public String toString() {
		return "TeamPO [teamId=" + teamId + ", level=" + level + ", name=" + name + ", help=" + help + "]";
	}
	/**
	 * 如果最后下线时间在最后登录时间之前
	 * 则取最后登录时间为最后下线时间
	 * @return
	 */
	public DateTime getLastOfflineTime() {
		if(lastOfflineTime == null || lastOfflineTime.isBefore(lastLoginTime)) {
			return lastLoginTime;
		}
		return lastOfflineTime;
	}
	public void setLastOfflineTime(DateTime offlineTime) {
		this.lastOfflineTime = offlineTime;
	}
	public int getChatStatus() {
		return chatStatus;
	}
	public void setChatStatus(int chatStatus) {
		this.chatStatus = chatStatus;
	}
	public int getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(int userStatus) {
		this.userStatus = userStatus;
	}


}
