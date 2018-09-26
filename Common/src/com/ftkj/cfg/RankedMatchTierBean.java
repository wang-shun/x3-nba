package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.util.excel.RowData;

/** 天梯赛. 层级配置 */
public class RankedMatchTierBean extends AbstractExcelBean {
    /** 层级id */
    private int id;
    /** 段位id */
    private int medalId;
    /** 上一层级 */
    private int preId;
    /** 下一层级 */
    private int nextId;
    /** 最低积分要求(包括) */
    private int minRating;
    /** 得分系数 */
    private float ratingFactor;
    /** 失败时修正系数 */
    private float failCorrectionFactor;

    @Override
    public void initExec(RowData row) {
        medalId = getInt(row, "mid");
    }

    public int getId() {
        return id;
    }

    public int getPreId() {
        return preId;
    }

    public void setPreId(int preId) {
        this.preId = preId;
    }

    public int getNextId() {
        return nextId;
    }

    public int getMedalId() {
        return medalId;
    }

    public int getMinRating() {
        return minRating;
    }

    public float getRatingFactor() {
        return ratingFactor;
    }

    public float getFailCorrectionFactor() {
        return failCorrectionFactor;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"medalId\":" + medalId +
                ", \"preId\":" + preId +
                ", \"nextId\":" + nextId +
                ", \"minRating\":" + minRating +
                ", \"ratingFactor\":" + ratingFactor +
                ", \"failCorrectionFactor\":" + failCorrectionFactor +
                '}';
    }
}
