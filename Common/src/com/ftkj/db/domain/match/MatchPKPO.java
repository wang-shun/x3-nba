package com.ftkj.db.domain.match;

import com.ftkj.console.GameConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

public class MatchPKPO extends AsynchronousBatchDB {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * ＰＫ序列号
     */
    private long battleId;
    private int seqId;
    private int matchId;
    /**
     * 轮数
     */
    private int round;
    private long homeId;
    private long awayId;
    /**
     * 胜利球队ID
     */
    private long winTeamId;

    // 比分
    private int homeScore;
    // 比分
    private int awayScore;

    /**
     * 状态: 0未开始， 1已结束
     */
    private int status;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 比赛结束时间,排名用
     */
    private DateTime endTime;

    public MatchPKPO() {
    }

    public MatchPKPO(long battleId, int seqId, int matchId, int round, long home_id, long away_id) {
        super();
        this.battleId = battleId;
        this.seqId = seqId;
        this.matchId = matchId;
        this.homeId = home_id;
        this.awayId = away_id;
        this.round = round;
        this.createTime = DateTime.now();
        this.endTime = GameConsole.Max_Date;
    }

    public long getBattleId() {
        return battleId;
    }

    public void setBattleId(long battleId) {
        this.battleId = battleId;
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

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }

    public long getAwayId() {
        return awayId;
    }

    public void setAwayId(long awayId) {
        this.awayId = awayId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public long getWinTeamId() {
        return winTeamId;
    }

    public void setWinTeamId(long winTeamId) {
        this.winTeamId = winTeamId;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
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

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.battleId, this.seqId, this.matchId, this.round, this.homeId, this.awayId,
            this.winTeamId, this.homeScore, this.awayScore, this.status, this.createTime, this.endTime);
    }

    @Override
    public String getRowNames() {
        return "battle_id, seq_id, match_id, round, home_id, away_id, win_team_id, home_score, away_score, status, create_time, end_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.battleId, this.seqId, this.matchId, this.round, this.homeId, this.awayId,
            this.winTeamId, this.homeScore, this.awayScore, this.status, this.createTime, this.endTime);
    }

    @Override
    public String getTableName() {
        return "t_u_match_pk";
    }

    @Override
    public void del() {

    }

    @Override
    public String toString() {
        return "MatchPKPO [battleId=" + battleId + ", seqId=" + seqId + ", matchId=" + matchId + ", round=" + round
            + ", homeId=" + homeId + ", awayId=" + awayId + ", winTeamId=" + winTeamId + ", status=" + status + "]";
    }

}
