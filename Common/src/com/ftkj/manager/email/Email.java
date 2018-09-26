package com.ftkj.manager.email;

import org.joda.time.DateTime;

import com.ftkj.db.domain.EmailPO;

public class Email {

	private EmailPO emailPO;
	
	public Email() {
	}

	public Email(EmailPO emailPO) {
		super();
		this.emailPO = emailPO;
	}

	public void save() {
		this.emailPO.save();
	}
	
	public void setStatus(int status) {
		this.emailPO.setStatus(status);
	}
	
	public int getId() {
		return this.emailPO.getId();
	}
	
	public int getViewId() {
		return this.emailPO.getViewId();
	}

	public long getTeamId() {
		return this.emailPO.getTeamId();
	}

	public int getType() {
		return this.emailPO.getType();
	}

	public String getTitle() {
		return this.emailPO.getTitle();
	}

	public String getContent() {
		return this.emailPO.getContent();
	}

	public DateTime getCreateTime() {
		return this.emailPO.getCreateTime();
	}

	public int getStatus() {
		return this.emailPO.getStatus();
	}

	public String getAwardConfig() {
		return this.emailPO.getAwardConfig();
	}

	public void del() {
		 this.emailPO.del();
	}
}

