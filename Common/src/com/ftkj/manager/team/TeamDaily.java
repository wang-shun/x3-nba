package com.ftkj.manager.team;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 玩家每日数据
 * @author qin.jiang
 */
public class TeamDaily extends AsynchronousBatchDB {

    private static final long serialVersionUID = 3673984936756201188L;

    /** 球队ID*/
    private long teamId;    
    
    /** 每日交易留言状态（0:未留言, 1:已留言）*/
    private int tradeChatLMState;
    
    /** 是否已删除（0:否, 1:是）*/
    private int delete;

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getTradeChatLMState() {
        return tradeChatLMState;
    }

    public void setTradeChatLMState(int tradeChatLMState) {
        this.tradeChatLMState = tradeChatLMState;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.tradeChatLMState);
    }

    @Override
    public String getRowNames() {
        return "team_id,trade_chat_lm_state";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.tradeChatLMState);
    }

    @Override
    public String getTableName() {
      return "t_u_team_daily";
    }

    @Override
    public void del() {
       this.setDelete(1);
       save();
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }
    
    public static TeamDaily createTeamDaily(long teamId, int tradeChatLMState) {
        TeamDaily teamDaily = new TeamDaily();
        teamDaily.setTeamId(teamId);
        teamDaily.setTradeChatLMState(tradeChatLMState);
        return teamDaily;
    }


}
