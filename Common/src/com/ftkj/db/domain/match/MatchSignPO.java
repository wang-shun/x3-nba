package com.ftkj.db.domain.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

public class MatchSignPO extends AsynchronousBatchDB implements Serializable {
    private static final long serialVersionUID = 1L;
    // 届数
    private int seqId;
    // 比赛类型ID
    private int matchId;
    // 报名球队
    private long teamId;
    /**
     * 球队战力，报名时需要提供；
     */
    private int teamCap;
    /**
     * 状态，0，预报名（被挤出，根据战力来计算），1正式报名
     */
    private int status;
    /**
     * 本次排名,0是未参加比赛,或者没有排名
     */
    private int rank;

    // 报名时间
    private DateTime createTime;

    public MatchSignPO() {
    }

    public MatchSignPO(int seqId, int matchId, long teamId, int teamCap) {
        super();
        this.seqId = seqId;
        this.matchId = matchId;
        this.teamId = teamId;
        this.teamCap = teamCap;
        this.createTime = DateTime.now();
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

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getTeamCap() {
        return teamCap;
    }

    public void setTeamCap(int teamCap) {
        this.teamCap = teamCap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.seqId, this.matchId, this.teamId, this.teamCap, this.status, this.rank, this.createTime);
    }

    @Override
    public String getRowNames() {
        return "seq_id, match_id, team_id, team_cap, status, rank, create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.seqId, this.matchId, this.teamId, this.teamCap, this.status, this.rank, this.createTime);
    }

    @Override
    public String getTableName() {
        return "t_u_match_sign";
    }

    @Override
    public void del() {

    }

    @Override
    public String toString() {
        return "{" +
                "\"seqId\":" + seqId +
                ", \"matchId\":" + matchId +
                ", \"teamId\":" + teamId +
                ", \"teamCap\":" + teamCap +
                ", \"status\":" + status +
                ", \"rank\":" + rank +
                ", \"createTime\":" + createTime +
                '}';
    }


}
