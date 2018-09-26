package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * 玩家装备
 * @author Jay
 *
 */
public class EquiPO extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 唯一ID
	 */
	private int id;
	/**
	 * 装备球员类型，联合主键， 套装ID也是通过playerId来确定
	 */
	private int playerId;
	/**
	 * 专属球队，0是所有球队可用
	 */
	private int equiTeam;
	/**
	 * 装备类型
	 */
	private int type;
	/**
	 * 球队ID 
	 */
	private long teamId;
	/**
	 * 模板ID
	 */
	private int equId;
	/**
	 * 强化等级
	 */
	private int strLv;
	/**
	 * 强化祝福值（累计概率，强化成功会清空）
	 */
	private float strBless;
	/**
	 * 创建时间
	 */
	private DateTime createTime;
	/**
	 * 最后有效时间
	 */
	private DateTime endTime;
	/**
	 * 随机属性
	 */
	private String randAttr = "";

	public EquiPO() {
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

	public int getEquId() {
		return equId;
	}

	public void setEquId(int equId) {
		this.equId = equId;
	}

	public int getStrLv() {
		return strLv;
	}

	public void setStrLv(int strLv) {
		this.strLv = strLv;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	

	public float getStrBless() {
		return strBless;
	}

	public void setStrBless(float strBless) {
		this.strBless = strBless;
	}

	public int getEquiTeam() {
		return equiTeam;
	}

	public void setEquiTeam(int equiTeam) {
		this.equiTeam = equiTeam;
	}

	public String getRandAttr() {
		return randAttr;
	}

	public void setRandAttr(String randAttr) {
		this.randAttr = randAttr;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.id,this.type,this.teamId,this.equId,this.playerId,this.equiTeam,this.strLv,this.strBless,this.randAttr,this.createTime,this.endTime);
	}

	@Override
	public String getRowNames() {
		return "id,type,team_id,equi_id,player_id,equi_team,strlv,str_bless,rand_attr,create_time,end_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.id,this.type,this.teamId,this.equId,this.playerId,this.equiTeam,this.strLv,this.strBless,this.randAttr,this.createTime,this.endTime);
    }

	@Override
	public String getTableName() {
		return "t_u_equi";
	}

	@Override
	public void del() {
		// 没有删除
	}

    @Override
	public String toString() {
		return "EquiPO [id=" + id + ", playerId=" + playerId + ", equiTeam=" + equiTeam + ", type=" + type + ", teamId="
				+ teamId + ", equId=" + equId + ", strLv=" + strLv + ", strBless=" + strBless + ", createTime="
				+ createTime + ", endTime=" + endTime + ", randAttr=" + randAttr + "]";
	}


}
