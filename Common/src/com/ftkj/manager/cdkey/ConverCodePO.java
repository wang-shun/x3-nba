package com.ftkj.manager.cdkey;

import java.util.Date;

/**
 * @author tim.huang
 * 2016年12月16日
 *
 */
public class ConverCodePO{
	
	private String id;
	private String plat;
	private long teamId;
	private String code;
	private Date createTime;
	
//	@Override
//	public String getSource() {
//		return StringUtil.formatSQL(this.id,this.plat,this.teamId,this.code,this.createTime);
//	}
//	@Override
//	public String getRowNames() {
//		return "id, plat, team_id, code, create_time";
//	}
//	@Override
//	public String getTableName() {
//		return "t_u_code";
//	}
//	@Override
//	public void del() {
//		
//	}
	
	public String getPlat() {
		return plat;
	}
	public void setPlat(String plat) {
		this.plat = plat;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}