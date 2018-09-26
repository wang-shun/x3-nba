package com.ftkj.db.domain.active.base;

import com.ftkj.db.conn.dao.AsynchronousBatchDataDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * 系统活动类，DB来控制
 *
 * @author Jay
 * @time:2018年1月30日 下午5:12:53
 */
public class SystemActivePO extends AsynchronousBatchDataDB implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 区ID
     */
    private int shardId;
    private int atvId;
    private String name;
    private DateTime startTime;
    private DateTime endTime;
    /**
     * 状态：
     * 1，正常
     * 2，维护（活动维护，拒接相关影响活动数据变动的请求），该状态不影响充值和消费相关的数据选项。
     * (累计消费和累计充值，不应该由活动来控制，除非是特殊的)
     * 3，能否控制发生配置异常，自动切换成维护状态；
     */
    private int status;
    /**
     * 特殊区配置，默认空，使用excel公用配置，如有区需要特殊配置，请根据excel生成json相关配置；
     * 由相关活动类提供解析方法
     */
    private String jsonConfig = "";

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.shardId, this.atvId, this.name, this.startTime, this.endTime, this.status, this.jsonConfig);
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(this.shardId, this.atvId, this.name, this.startTime, this.endTime, this.status, this.jsonConfig);
    }

    @Override
    public String getRowNames() {
        return "shard_id, atv_id, name, start_time, end_time, status, jsonConfig";
    }

    @Override
    public String getTableName() {
        return "system_active";
    }

    @Override
    public void del() {
        this.status = 2;
        this.save();
    }

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public int getAtvId() {
        return atvId;
    }

    public void setAtvId(int atvId) {
        this.atvId = atvId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJsonConfig() {
        return jsonConfig;
    }

    public void setJsonConfig(String jsonConfig) {
        this.jsonConfig = jsonConfig;
    }

}
