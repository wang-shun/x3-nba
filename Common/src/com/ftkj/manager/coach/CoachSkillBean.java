package com.ftkj.manager.coach;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.skill.buff.SkillBuffer;
import com.ftkj.util.excel.RowData;

import java.io.Serializable;
import java.util.List;

/**
 * @author tim.huang
 * 2017年9月18日
 * 教练技能
 */
public class CoachSkillBean extends ExcelBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private int sid;
	private String bids;
	private int count;
	
	//技能效果列表
	private List<SkillBuffer> buff;
	
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getBids() {
		return bids;
	}
	public void setBids(String bids) {
		this.bids = bids;
	}
	public void setSid(int sid) {
		this.sid = sid;
	}
	public void setBuff(List<SkillBuffer> buff) {
		this.buff = buff;
	}
	@Override
	public void initExec(RowData row) {
		
	}
	public int getSid() {
		return sid;
	}
	public List<SkillBuffer> getBuff() {
		return buff;
	}
	
	
	
	
}
