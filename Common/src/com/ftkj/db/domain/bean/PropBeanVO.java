package com.ftkj.db.domain.bean;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年3月3日
 * 道具配置
 */
public class PropBeanVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 道具ID */
    private int propId;
    /** 道具父类ID */
    private int tid;
    /** 道具分组 */
    private int groupId;
    /** 道具类型 */
    private int propType;
    /** 道具名称 */
    private String name;
    /** 道具配置 */
    private String config;
    /** 0不可用， 1可以使用 */
    private int use;
    /** 售价 */
    private int sale;
    /** 主线赛程每日掉落上限, 按组划分 */
    private int dailyMaxNum;

    public PropBeanVO() {
        super();
    }

    public PropBeanVO(int propId, int tid, int propType, String name) {
        super();
        this.propId = propId;
        this.propType = propType;
        this.name = name;
        this.tid = tid;
    }

    public int getPropId() {
        return propId;
    }

    public int getTid() {
        return tid;
    }

    public int getPropType() {
        return propType;
    }

    public String getName() {
        return name;
    }

    public String getConfig() {
        return config;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setPropType(int propType) {
        this.propType = propType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getDailyMaxNum() {
        return dailyMaxNum;
    }

    public void setDailyMaxNum(int dailyMaxNum) {
        this.dailyMaxNum = dailyMaxNum;
    }

    @Override
    public String toString() {
        return "{" +
            "\"propId\":" + propId +
            ", \"tid\":" + tid +
            ", \"groupId\":" + groupId +
            ", \"propType\":" + propType +
            ", \"name\":\"" + name + "\"" +
            ", \"config\":\"" + config + "\"" +
            ", \"use\":" + use +
            ", \"sale\":" + sale +
            ", \"dailyMaxNum\":" + dailyMaxNum +
            '}';
    }

}
