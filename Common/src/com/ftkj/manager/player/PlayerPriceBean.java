package com.ftkj.manager.player;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年2月16日
 * 球员配置数据
 */
public class PlayerPriceBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 唯一标识 */
    private int id;
    /** 降低百分比(1000%) */
    private int percent;
    /** 概率 */
    private int rate;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getPercent() {
        return percent;
    }
    public void setPercent(int percent) {
        this.percent = percent;
    }
    public int getRate() {
        return rate;
    }
    public void setRate(int rate) {
        this.rate = rate;
    }
}
