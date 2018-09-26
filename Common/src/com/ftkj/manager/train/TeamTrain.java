package com.ftkj.manager.train;

import java.util.List;

import com.ftkj.console.ConfigConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 球队训练馆数据
 *
 * @author qin.jiang
 */
public class TeamTrain extends AsynchronousBatchDB {

    private static final long serialVersionUID = -2416604431077219130L;

    /** 训练馆球队ID */
    private long teamId;
    /** 剩余抢夺次数 */
    private int robbedCount;
    /** 抢夺次数可刷新时间(剩余时间 = 抢夺次数可刷新时间 - 当前时间) */
    private long refreshTime;
    /** 下次可抢夺时间(不存库)(剩余时间 = 下次可枪夺时间 - 当前时间) */
    private long robbedTime;
    /** 抢夺列表可刷新时间(不存库)(剩余时间 = 抢夺列表可刷新时间 - 当前时间) */
    private long refreshListTime;
    /** 训练馆抢夺总次数 */
    private long robbedTotalCount;
    /** 训练馆抢夺胜利次数 */
    private long robbedWinCount;
    /** 训练馆抢夺失败次数 */
    private long robbedFailCount;
    /** 训练馆训练次数 */
    private long trainCount; 

    public TeamTrain() {

    }

    public TeamTrain(long teamId) {
        super();
        this.teamId = teamId;
        this.robbedCount = ConfigConsole.getGlobal().trainGrabCountMax;
    }

    public long getRobbedTime() {
        return robbedTime;
    }

    public void setRobbedTime(long robbedTime) {
        this.robbedTime = robbedTime;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public int getRobbedCount() {
        return robbedCount;
    }

    public void setRobbedCount(int robbedCount) {
        this.robbedCount = robbedCount;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public long getRefreshListTime() {
        return refreshListTime;
    }

    public void setRefreshListTime(long refreshListTime) {
        this.refreshListTime = refreshListTime;
    }

    public long getRobbedTotalCount() {
        return robbedTotalCount;
    }

    public void setRobbedTotalCount(long robbedTotalCount) {
        this.robbedTotalCount = robbedTotalCount;
    }

    public long getRobbedWinCount() {
        return robbedWinCount;
    }

    public void setRobbedWinCount(long robbedWinCount) {
        this.robbedWinCount = robbedWinCount;
    }

    public long getRobbedFailCount() {
        return robbedFailCount;
    }

    public void setRobbedFailCount(long robbedFailCount) {
        this.robbedFailCount = robbedFailCount;
    }

    public long getTrainCount() {
        return trainCount;
    }

    public void setTrainCount(long trainCount) {
        this.trainCount = trainCount;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.robbedCount, this.refreshTime, this.robbedTotalCount,
            this.robbedWinCount, this.robbedFailCount, this.trainCount);
    }

    @Override
    public String getRowNames() {
        return "team_id, robbed_count, refresh_time, robbed_total_count, robbed_win_count, robbed_fail_count, train_count";
    }

    @Override
    public String getTableName() {
        return "t_u_team_train";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.robbedCount, this.refreshTime, this.robbedTotalCount,
            this.robbedWinCount, this.robbedFailCount, this.trainCount);
    }

    @Override
    public void del() {
    }

    @Override
    public String toString() {
        return "{" +
            "\"teamId\":" + teamId +
            ", \"robbedCount\":" + robbedCount +
            ", \"refreshTime\":" + refreshTime +
            ", \"robbedTime\":" + robbedTime +
            ", \"refreshListTime\":" + refreshListTime +
            ", \"robbedTotalCount\":" + robbedTotalCount +
            ", \"robbedWinCount\":" + robbedWinCount +
            ", \"robbedFailCount\":" + robbedFailCount +
            ", \"trainCount\":" + trainCount +
            '}';
    }


}

