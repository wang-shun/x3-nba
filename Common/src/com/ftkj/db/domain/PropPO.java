package com.ftkj.db.domain;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年3月13日
 * 单个道具数据
 */
public class PropPO extends AsynchronousBatchDB {
    /** 玩家ID */
    private long teamId;
    /** 物品唯一ID */
    private int id;
    /** 物品ID */
    private int propId;
    /** 物品数量  */
    private int num;
    /** 配置  */
    private String config;
    /** 物品消失时间 */
    private DateTime endTime;
    /** 物品获得时间 */
    private DateTime createTime;

    public PropPO(int propId, int num) {
        super();
        this.propId = propId;
        this.num = num;
    }

    public PropPO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public long getTeamId() {
        return teamId;
    }

    public int getPropId() {
        return propId;
    }

    public int getNum() {
        return num;
    }

    public String getConfig() {
        return config;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.teamId, this.id, this.propId, this.num, this.config, this.endTime, this.createTime);
    }

    @Override
    public String getRowNames() {
        return "team_id,id,pid,num,config,end_time,create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.id, this.propId, this.num, this.config, this.endTime, this.createTime);
    }

    @Override
    public String getTableName() {
        return "t_u_prop";
    }

    @Override
    public void del() {
    }

    @Override
    public String toString() {
        return "{" +
                "\"propId\":" + propId +
                ", \"num\":" + num +
                ", \"et\":" + endTime +
                '}';
    }
}
