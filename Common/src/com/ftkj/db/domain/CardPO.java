package com.ftkj.db.domain;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * 球星卡
 * @author Jay
 * @time:2017年4月11日 下午5:12:37
 */
public class CardPO  extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long teamId;
	private int playerId;
	/**
	 * 卡组
	 */
	private int type;
	/**
	 * 品质
	 */
	private int qua;
	/**
	 * 星级
	 */
	private int starLv;
	/**
	 * 星级经验
	 */
	private int exp;
	
	/**突破已经被吞噬的底薪球员数量*/
	private int costNum;
	
	private DateTime createTime;
	
	public CardPO() {
		createTime = DateTime.now();
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getPlayerId() {
		return playerId;
	}
	
	public int getQua() {
		return qua;
	}

	public void setQua(int qua) {
		this.qua = qua;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStarLv() {
		return starLv;
	}

	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	/**获取,突破已经被吞噬的底薪球员数量*/
	public int getCostNum() {
		return costNum;
	}

	/**设置,突破已经被吞噬的底薪球员数量*/
	public void setCostNum(int costNum) {
		this.costNum = costNum;
	}

	public DateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId,this.playerId,this.type,this.qua,this.starLv,this.exp,this.costNum,this.createTime);
	}

	@Override
	public String getRowNames() {
		return "team_id,player_id,type,qua,starlv,exp,cost_num,create_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,this.playerId,this.type,this.qua,this.starLv,this.exp,this.costNum,this.createTime);
    }

	@Override
	public String getTableName() {
		return "t_u_card";
	}

	@Override
	public void del() {
	}


}
