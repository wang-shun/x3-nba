package com.ftkj.manager.draft;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月4日
 * 选秀玩家信息
 */
public class DraftTeam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long teamId;
	private String teamName;
	private String logo;
	private String nodeName;
	//
	private int order;
	/**
	 * 是否自动选人，超时后true
	 */
	private boolean isAuto = false;
	/**
	 * 是否已签
	 */
	private boolean isSign = false;
	
	public DraftTeam(long teamId, String teamName, String logo,
			String nodeName) {
		super();
		this.teamId = teamId;
		this.teamName = teamName;
		this.logo = logo;
		this.nodeName = nodeName;
	}
	

	public void setOrder(int order) {
		this.order = order;
	}



	public long getTeamId() {
		return teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getLogo() {
		return logo;
	}

	public String getNodeName() {
		return nodeName;
	}

	public int getOrder() {
		return order;
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	public boolean isSign() {
		return isSign;
	}

	public void setSign(boolean isSign) {
		this.isSign = isSign;
	}
	
}
