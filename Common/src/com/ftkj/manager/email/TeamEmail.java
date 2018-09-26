package com.ftkj.manager.email;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ftkj.db.domain.EmailPO;
import com.google.common.collect.Maps;

public class TeamEmail {

	private long teamId;
	
	private Map<Integer,Email> emailMap;
	
	private AtomicInteger seq;
	
	public TeamEmail(long teamId, AtomicInteger seq, List<EmailPO> list) {
		this.teamId = teamId;
		this.seq = seq;
		if(list != null) {
			this.emailMap = list.stream().collect(Collectors.toMap(EmailPO::getId, emailPO-> new Email(emailPO)));
		}else {
			this.emailMap = Maps.newConcurrentMap();
		}
	}
	
	/**
	 * 发送邮件
	 * @param type 类型
	 * @param title 标题
	 * @param content 内容
	 * @param propList 道具列表
	 */
	public Email sendEmail(int type, int viewId, String title, String content, String propList) {
		int nid = seq.incrementAndGet();
		EmailPO emailPO = new EmailPO(nid, this.teamId, type, viewId, title, content, propList);
		Email email = new Email(emailPO);
		email.save();
		emailMap.put(nid, email);
		return email;
	}
	
	/**
	 * 删除邮件
	 * @param id
	 */
	public void deleteEmail(int id) {
		if(emailMap.containsKey(id)) {
			Email email = emailMap.remove(id);
			email.setStatus(3);
			email.del();
		}
	}
	
	public Collection<Email> getEmailList() {
		return emailMap.values();
	}
	
	public boolean checkEmail(int id) {
		return this.emailMap.containsKey(id);
	}
	
	public Email getEmail(int id) {
		return this.emailMap.get(id);
	}
	
}
