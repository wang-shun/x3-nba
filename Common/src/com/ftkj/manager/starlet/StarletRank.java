package com.ftkj.manager.starlet;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 新秀排位赛数据
 * @author qin.jiang
 */
public class StarletRank  extends AsynchronousBatchDB{
    
    private static final long serialVersionUID = 3668883393492060865L;
    
    /** 排名 */
    private int rank;
    /** 球队ID */
    private long teamId;   
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.rank);
    }

    @Override
    public String getRowNames() {
        return "team_id, rank";
    }

    @Override
    public String getTableName() {
        return "t_u_starlet_rank";
    }

    @Override
    public void del() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId,
                this.rank);
    }

}
