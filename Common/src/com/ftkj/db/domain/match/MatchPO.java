package com.ftkj.db.domain.match;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import java.util.List;

public class MatchPO extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 届数
	private int seqId;
	// 比赛类型ID
	private int matchId;
	/**
	 * 比赛状态
	 * 开始报名(1),
	 * 比赛中(2),
	 * 结束(3)
	 * 报名截止(4),
	 */
	private int status;
	
	// 当前比赛轮数
	private int round;
	
	// 最大比赛轮数
	private int maxRound;
	
	// 创建时间
	private DateTime createTime;
	/**
	 * 比赛开始时间
	 */
	private DateTime matchTime;
	
	/**
	 * 比赛节点名称，某个节点创建的比赛，只会在本节点开始比赛。
	 */
	private String logicName;
	
	public MatchPO() {
		
	}
	public MatchPO(int seqId, int matchId, int status, int maxRound, DateTime matchTime, String logicName) {
		super();
		this.seqId = seqId;
		this.matchId = matchId;
		this.status = status;
		this.maxRound = maxRound;
		this.createTime = DateTime.now();
		this.matchTime = matchTime;
		this.logicName = logicName;
	}

	public int getSeqId() {
		return seqId;
	}

	public void setSeqId(int seqId) {
		this.seqId = seqId;
	}

	public int getMatchId() {
		return matchId;
	}

	public void setMatchId(int matchId) {
		this.matchId = matchId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getMaxRound() {
		return maxRound;
	}

	public void setMaxRound(int maxRound) {
		this.maxRound = maxRound;
	}
	
	public DateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(DateTime createTime) {
		this.createTime = createTime;
	}
	
	public DateTime getMatchTime() {
		return matchTime;
	}
	public void setMatchTime(DateTime matchTime) {
		this.matchTime = matchTime;
	}
	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.seqId, this.matchId, this.status, this.round, this.maxRound, this.createTime, this.matchTime, this.logicName);
	}

	@Override
	public String getRowNames() {
		return "seq_id,match_id,status,round,max_round,create_time,match_time,logic_name";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.seqId, this.matchId, this.status, this.round, this.maxRound, this.createTime, this.matchTime, this.logicName);
    }


	@Override
	public String getTableName() {
		return "t_u_match";
	}
	
	public String getLogicName() {
		return logicName;
	}
	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}
	@Override
	public void del() {
	}
	@Override
	public String toString() {
		return "MatchPO [seqId=" + seqId + ", matchId=" + matchId + ", status=" + status + ", round=" + round
				+ ", maxRound=" + maxRound + ", createTime=" + createTime + ", matchTime=" + matchTime + "]";
	}


}
