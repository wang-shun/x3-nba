package com.ftkj.manager.match;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.BitUtil;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 主线赛程赛区
 */
public class MainMatchDivision extends AsynchronousBatchDB {
    private static final long serialVersionUID = 7816562972925256561L;
    private long teamId;
    /** 自增id */
    private int id;
    /** 策划配置id */
    private int resourceId;
    /** 星级礼包领取状态. 按位运算 */
    private long starAwards;

    public MainMatchDivision() {
    }

    public MainMatchDivision(int resourceId) {
        this.resourceId = resourceId;
    }

    public MainMatchDivision(long teamId, int id) {
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

    public void setStarAwards(long starAwards) {
        this.starAwards = starAwards;
    }

    public boolean hasStarAward(int starId) {
        return BitUtil.hasBit(starAwards, 1 << starId);
    }

    public void addStarAward(int starId) {
        this.starAwards = BitUtil.addBit(starAwards, 1 << starId);
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(id, teamId, resourceId, starAwards);
    }

    @Override
    public String getRowNames() {
        return "id, team_id, r_id, star_awards";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(id, teamId, resourceId, starAwards);
    }

    @Override
    public String getTableName() {
        return "t_u_mmatch_div";
    }

    @Override
    public void del() {
    }


}
