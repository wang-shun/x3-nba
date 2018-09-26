package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 用户邮件
 * @author Jay
 * @time:2017年4月24日 下午6:11:21
 */
public class EmailToTeamPO extends AsynchronousBatchDB {

	/**
	 * 自动增长ＩＤ
	 */
	private int id;
	private int seqId;
	private String title;
	private String content;
	private String awardConfig;
	/**
	 * 0 未发
	 * 1 已读
	 */
	private int status;
	private String remark;
	
	public EmailToTeamPO() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeqId() {
		return seqId;
	}

	public void setSeqId(int seqId) {
		this.seqId = seqId;
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

	public String getAwardConfig() {
		return awardConfig;
	}

	public void setAwardConfig(String awardConfig) {
		this.awardConfig = awardConfig;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id,this.seqId,this.title,this.content,this.status,this.awardConfig,this.remark);
	}

	@Override
	public String getRowNames() {
		return "id,seq_id,title,,content,status,award_config,remark";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id,this.seqId,this.title,this.content,this.status,this.awardConfig,this.remark);
    }

	@Override
	public String getTableName() {
		return "t_u_email_to_team";
	}

	@Override
	public void del() {

	}


}
