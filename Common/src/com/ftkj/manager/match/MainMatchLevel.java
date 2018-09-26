package com.ftkj.manager.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 主线赛程关卡
 */
public class MainMatchLevel extends AsynchronousBatchDB {
    private static final long serialVersionUID = -4866139704209676340L;
    private long teamId;
    /** 自增id */
    private int id;
    /** 策划配置id */
    private int resourceId;
    /** 最大星级 */
    private int star;
    /** 参与比赛总次数 */
    private int matchCount;

    public MainMatchLevel() {
    }

    public MainMatchLevel(int resourceId, int star) {
        this.resourceId = resourceId;
        this.star = star;
    }

    MainMatchLevel(long teamId, int id) {
        this.teamId = teamId;
        this.id = id;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(id, teamId, resourceId, star, matchCount);
    }

    @Override
    public String getRowNames() {
        return "id, team_id, r_id, star, m_c";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(id, teamId, resourceId, star, matchCount);
    }

    @Override
    public String getTableName() {
        return "t_u_mmatch_lev";
    }

    @Override
    public void del() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"rid\":" + resourceId +
                ", \"star\":" + star +
                ", \"mc\":" + matchCount +
                '}';
    }


}
