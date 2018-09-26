package com.ftkj.manager.train;

import java.util.List;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * 球队训练馆数据
 *
 * @author qin.jiang
 */
public class LeagueTrain extends AsynchronousBatchDB {

    private static final long serialVersionUID = -2416604431077219130L;

    /** 联盟训练馆ID */
    private int blId;

    /** 联盟ID */
    private int leagueId; 
    
    /** 配置球馆ID */
    private Integer btId; 

    public LeagueTrain() {

    }

    public LeagueTrain(int blId, int leagueId, int btId) {
        super();
        this.blId = blId;
        this.leagueId = leagueId;
        this.btId = btId;
    }

    public int getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(int leagueId) {
        this.leagueId = leagueId;
    }
    
    public Integer getBtId() {
        return btId;
    }

    public void setBtId(Integer btId) {
        this.btId = btId;
    }

    public int getBlId() {
        return blId;
    }

    public void setBlId(int blId) {
        this.blId = blId;
    }

    
    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.blId, this.leagueId, this.btId);
    }

    @Override
    public String getRowNames() {
        return "bl_id, league_id, bt_id";
    }

    @Override
    public String getTableName() {
        return "t_u_league_train";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.blId, this.leagueId, this.btId);
    }

    @Override
    public void del() {
    }

    @Override
    public String toString() {
        return "{" +
            "\"blId\":" + blId +
            ", \"leagueId\":" + leagueId +
            ", \"btId\":" + btId + 
            '}';
    }

}

