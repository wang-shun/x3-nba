package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * 用户邮件
 * @author Jay
 * @time:2017年4月24日 下午6:11:21
 */
public class EmailPO extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 自动增长ＩＤ
	 */
	private int id;
	/**
	 * 1，系统
	 * 2，联盟
	 */
	private int type;
	private long teamId;
	// 模板ID，0的话直接显示
	private int viewId;
	private String title;
	// 参数用，分割
	private String content;
	/**
	 * 0 未读
	 * 1 已读
	 * 2 已删
	 */
	private int status;
	private String awardConfig;
	private DateTime createTime;
	
	public EmailPO() {
		this.createTime = DateTime.now();
	}
	public EmailPO(int id, long teamId, int type, int viewId, String title, String content, String propList) {
		this.id = id;
		this.teamId = teamId;
		this.type = type;
		this.viewId = viewId;
		this.title = title;
		this.content = content;
		this.awardConfig = propList;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAwardConfig() {
		return awardConfig;
	}

	public void setAwardConfig(String awardConfig) {
		this.awardConfig = awardConfig;
	}

	public int getViewId() {
		return viewId;
	}
	public void setViewId(int viewId) {
		this.viewId = viewId;
	}
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id,this.teamId,this.type,this.viewId,this.title,this.content,this.status,this.awardConfig,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "id,team_id,type,view_id,title,content,status,award_config,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id,this.teamId,this.type,this.viewId,this.title,this.content,this.status,this.awardConfig,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_email";
	}

	@Override
	public void del() {
		// 创建时间大于30天自动清除。
	}


}
